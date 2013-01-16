package net.chrisrichardson.cfautoscaler.backend.management;

import net.chrisrichardson.cfautoscaler.backend.cep.AlarmSpec;

public class AutoscalingRule {
  
  private int delta;
  private AlarmSpec alarmSpec;
  
  public int getDelta() {
    return delta;
  }

  public void setDelta(int delta) {
    this.delta = delta;
  }

  public void setAlarmSpec(AlarmSpec alarmSpec) {
    this.alarmSpec = alarmSpec;
  }

  public AlarmSpec getAlarmSpec() {
    return alarmSpec;
  }
  
  

}
