package net.chrisrichardson.cfautoscaler.backend.management;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.chrisrichardson.cfautoscaler.backend.cep.esper.Esper;

import org.joda.time.DateTime;

public class AlarmTracker {

  private Map<String, DateTime> alarms = new HashMap<String, DateTime>();
  
  public void add(String alarmName) {
    alarms.put(alarmName, new DateTime());
  }
  
  public Map<String, DateTime> getActiveAlarms() {
    removeStaleAlarms();
    return Collections.unmodifiableMap(alarms);
  }

  private void removeStaleAlarms() {
    DateTime expirationCutOff = new DateTime().minusSeconds(Esper.EVENT_WINDOW_SIZE_IN_SECONDS * 3);
    for ( Iterator<Entry<String, DateTime>> it = alarms.entrySet().iterator(); it.hasNext(); ) {
        Entry<String, DateTime> entry = it.next();
        if (entry.getValue().isBefore(expirationCutOff))
          it.remove();
    }
  }

}
