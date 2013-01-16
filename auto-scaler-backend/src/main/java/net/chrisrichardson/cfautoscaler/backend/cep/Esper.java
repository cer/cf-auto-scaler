package net.chrisrichardson.cfautoscaler.backend.cep;

import javax.annotation.PostConstruct;

import net.chrisrichardson.cfautoscaler.backend.collection.InstanceMeasurement;

import org.springframework.stereotype.Component;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

@Component
public class Esper {

  public static final int EVENT_WINDOW_SIZE_IN_SECONDS = 15;

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
    epService = EPServiceProviderManager.getDefaultProvider();

    epAdmin = epService.getEPAdministrator();
    
    epAdmin
      .createEPL("insert into net.chrisrichardson.cfautoscaler.backend.cep.ApplicationAverage(appName, metric, average) select appName, metric, avg(value) as average from net.chrisrichardson.cfautoscaler.backend.collection.InstanceMeasurement.win:time_batch(30 sec) group by appName, metric")
      ;
    epAdmin
      .createEPL("select appName, instanceId, metric, avg(value) as avg from net.chrisrichardson.cfautoscaler.backend.collection.InstanceMeasurement.win:time(30 sec) group by appName, instanceId, metric")
      //.addListener(new InstanceAverageListener())
      ;

  }

  public void createAutoscalingRule(String appName, ApplicationAlarmConsumer applicationAlarmConsumer, String alarmName, AlarmSpec alarmSpec) {
    epAdmin
    .createEPL("insert into net.chrisrichardson.cfautoscaler.backend.cep.Alarm(appName, alarmName) "
        + String.format(" select appName, '%s' as alarmName ", alarmName)
        + " from net.chrisrichardson.cfautoscaler.backend.cep.ApplicationAverage "
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

  public void subscribeToAlarms(String appName, ApplicationAlarmConsumer applicationAlarmConsumer) {
    epAdmin
     .createEPL(String.format("select * from net.chrisrichardson.cfautoscaler.backend.cep.Alarm where appName='%s'", appName))
     .addListener(new AlarmListener(applicationAlarmConsumer));
    epAdmin
    .createEPL(String.format("select * from net.chrisrichardson.cfautoscaler.backend.cep.ApplicationAverage where appName='%s'", appName))
    .addListener(new AverageListener(applicationAlarmConsumer));
  }

  public void publish(InstanceMeasurement icu) {
    epService.getEPRuntime().sendEvent(icu);
  }
}
