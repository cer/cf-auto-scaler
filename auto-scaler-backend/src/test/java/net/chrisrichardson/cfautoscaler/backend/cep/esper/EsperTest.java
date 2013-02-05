package net.chrisrichardson.cfautoscaler.backend.cep.esper;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import net.chrisrichardson.cfautoscaler.backend.cep.AlarmOperator;
import net.chrisrichardson.cfautoscaler.backend.cep.AlarmSpec;
import net.chrisrichardson.cfautoscaler.backend.cep.ApplicationAlarmConsumer;
import net.chrisrichardson.cfautoscaler.backend.collection.InstanceMeasurement;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.TimerControlEvent;

public class EsperTest {

  private static final String METRIC_NAME = "cpu";
  private static final String APP_NAME = "myapp";
  private Esper esper;
  private DateTime startTime;
  private ApplicationAlarmConsumer applicationAlarmConsumer;

  @Before
  public void initialize() {
    startTime = new DateTime();

    esper = new Esper() {
      protected void initializeEngine() {
        esper.publish(new TimerControlEvent(TimerControlEvent.ClockType.CLOCK_EXTERNAL));
        timeNow(0);
      };
    };
    esper.initializeEsper();
    applicationAlarmConsumer = mock(ApplicationAlarmConsumer.class);
  }

  @Test
  public void esperShouldGenerateIdleAlarm() throws InterruptedException {

    String alarmName = "idle";
   
    registerAlarmAndSubscribe(alarmName, AlarmOperator.LT, 0.3);

    publishMeasurements(0.1);

    verifyAlarmGenerated(alarmName);
  }

  @Test
  public void esperShouldGenerateBusyAlarm() throws InterruptedException {
    
    String alarmName = "busy";
    registerAlarmAndSubscribe(alarmName, AlarmOperator.GT, 0.8);
    
    publishMeasurements(0.9);
    
    verifyAlarmGenerated(alarmName);
  }

  @Test
  public void esperShouldNotGenerateAnyAlarms() throws InterruptedException {
    
    registerAlarmAndSubscribe("idle", AlarmOperator.LT, 0.3);
    registerAlarmAndSubscribe("busy", AlarmOperator.GT, 0.8);
    
    publishMeasurements(0.5);
    
    verify(applicationAlarmConsumer, times(4)).processMetric(eq(METRIC_NAME), anyDouble());
    verifyNoMoreInteractions(applicationAlarmConsumer);
  }
  
  private void verifyAlarmGenerated(String alarmName) {
    verify(applicationAlarmConsumer, times(2)).processMetric(eq(METRIC_NAME), anyDouble());
    verify(applicationAlarmConsumer).processAlarm(alarmName);
    verifyNoMoreInteractions(applicationAlarmConsumer);
  }

  

  private void registerAlarmAndSubscribe(String alarmName, AlarmOperator operator, double threshold) {
    esper.registerAlarm(APP_NAME, alarmName, new AlarmSpec(METRIC_NAME, operator, threshold));
    esper.subscribeToAlarms(APP_NAME, applicationAlarmConsumer);
  }

  private void publishMeasurements(double value) {
    int interval = Esper.AVERAGE_WINDOW_SIZE * 2;
    for (int time = 1; time <= interval; time++) {
      timeNow(time);
      cpu(value);
    }
    timeNow(interval + 1);
  }

  private void cpu(double value) {
    InstanceMeasurement event = new InstanceMeasurement(APP_NAME, "1", METRIC_NAME, value);
    esper.publish(event);
  }

  private void timeNow(int time) {
    esper.publish(new CurrentTimeEvent(startTime.plusSeconds(time).getMillis()));
  }

}
