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
import org.junit.Test;

import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.TimerControlEvent;

public class EsperTest {

  private static final String METRIC_NAME = "cpu";
  private static final String APP_NAME = "myapp";
  private Esper esper;
  private DateTime startTime;

  @Test
  public void esperShouldProcessEvents() throws InterruptedException {
    startTime = new DateTime();

    esper = new Esper() {
      protected void initializeEngine() {
        esper.publish(new TimerControlEvent(TimerControlEvent.ClockType.CLOCK_EXTERNAL));
        timeNow(0);
      };
    };
    esper.initializeEsper();
    
    ApplicationAlarmConsumer applicationAlarmConsumer = mock(ApplicationAlarmConsumer.class);

    esper.registerAlarm(APP_NAME, "idle", new AlarmSpec(METRIC_NAME, AlarmOperator.LT, 0.3));
    esper.subscribeToAlarms(APP_NAME, applicationAlarmConsumer);

    int interval = Esper.AVERAGE_WINDOW_SIZE * 2;
    for (int time = 1; time <= interval; time++) {
      timeNow(time);
      lowCpu();
    }
    timeNow(interval + 1);

    verify(applicationAlarmConsumer, times(2)).processMetric(eq(METRIC_NAME), anyDouble());
    verify(applicationAlarmConsumer).processAlarm("idle");

    verifyNoMoreInteractions(applicationAlarmConsumer);
  }

  private void lowCpu() {
    InstanceMeasurement event = new InstanceMeasurement(APP_NAME, "1", METRIC_NAME, 0.1);
    esper.publish(event);
  }

  private void timeNow(int time) {
    esper.publish(new CurrentTimeEvent(startTime.plusSeconds(time).getMillis()));
  }

}
