package net.chrisrichardson.cfautoscaler.backend.cep.esper;

import javax.annotation.PostConstruct;

import net.chrisrichardson.cfautoscaler.backend.cep.AlarmSource;
import net.chrisrichardson.cfautoscaler.backend.cep.AlarmSpec;
import net.chrisrichardson.cfautoscaler.backend.cep.ApplicationAlarmConsumer;

import org.springframework.stereotype.Component;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

@Component
public class Esper implements AlarmSource {

  static final int AVERAGE_WINDOW_SIZE = 30;

  public static final int EVENT_WINDOW_SIZE_IN_SECONDS = AVERAGE_WINDOW_SIZE * 3 / 2;

  private EPServiceProvider epService;
  
  private EPAdministrator epAdmin;

    
  public class AlarmListener implements UpdateListener {
    private final ApplicationAlarmConsumer applicationAlarmConsumer;

    public AlarmListener(ApplicationAlarmConsumer applicationAlarmConsumer) {
      this.applicationAlarmConsumer = applicationAlarmConsumer;
    }

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
      if (newEvents == null)
        return;
      for (EventBean event : newEvents) {
        applicationAlarmConsumer.processAlarm((String) event.get("alarmName"));
      }
    }
  }

  public class AverageListener implements UpdateListener {
    private final ApplicationAlarmConsumer applicationAlarmConsumer;
    
    public AverageListener(ApplicationAlarmConsumer applicationAlarmConsumer) {
      this.applicationAlarmConsumer = applicationAlarmConsumer;
    }
    
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
      if (newEvents == null)
        return;
      for (EventBean event : newEvents) {
        applicationAlarmConsumer.processMetric((String) event.get("metric"), (Double) event.get("average"));
      }
    }
  }

  @PostConstruct
  public void initializeEsper() {
    epService = EPServiceProviderManager.getDefaultProvider(makeConfiguration());
    epAdmin = epService.getEPAdministrator();
    initializeEngine();
    epAdmin
      .createEPL("insert into ApplicationAverage(appName, metric, average) select appName, metric, avg(value) as average" + 
          " from net.chrisrichardson.cfautoscaler.backend.collection.InstanceMeasurement.win:time_batch(" + AVERAGE_WINDOW_SIZE + "sec) group by appName, metric")
      ;

  }

  protected Configuration makeConfiguration() {
    return new Configuration();
  }

  protected void initializeEngine() {
    // subclasses can override
  }

  @Override
  public void registerAlarm(String appName, String alarmName, AlarmSpec alarmSpec) {
    epAdmin
    .createEPL("insert into net.chrisrichardson.cfautoscaler.backend.cep.Alarm(appName, alarmName) "
        + String.format(" select appName, '%s' as alarmName ", alarmName)
        + " from ApplicationAverage "
        + " match_recognize ("
        + " partition by appName, metric "
        + " measures E1.appName as appName, E1.metric as metric "
        + " after match skip past last row "
        + " pattern (E1 E2+) "
        + " interval " + EVENT_WINDOW_SIZE_IN_SECONDS + " seconds "
        + " define "
        + String.format("   E1 as E1.average %s %s & E1.appName = '%s' & E1.metric = '%s', ", alarmSpec.getOperator().asString(), alarmSpec.getThreshold(), appName, alarmSpec.getMetric())
        + String.format("   E2 as E2.average %s %s & E2.appName = '%s' & E2.metric = '%s'" , alarmSpec.getOperator().asString(), alarmSpec.getThreshold(), appName, alarmSpec.getMetric())
        + ") "
        )
        ;
  }

  @Override
  public void subscribeToAlarms(String appName, ApplicationAlarmConsumer applicationAlarmConsumer) {
    epAdmin
     .createEPL(String.format("select * from net.chrisrichardson.cfautoscaler.backend.cep.Alarm where appName='%s'", appName))
     .addListener(new AlarmListener(applicationAlarmConsumer));
    epAdmin
    .createEPL(String.format("select * from ApplicationAverage where appName='%s'", appName))
    .addListener(new AverageListener(applicationAlarmConsumer));
  }

  public void publish(Object event) {
    epService.getEPRuntime().sendEvent(event);
  }
}
