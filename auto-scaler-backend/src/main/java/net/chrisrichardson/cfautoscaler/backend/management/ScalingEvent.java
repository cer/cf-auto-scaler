package net.chrisrichardson.cfautoscaler.backend.management;

import org.joda.time.DateTime;

public class ScalingEvent {

  private final int newInstances;
  private final boolean success;
  private final String message;
  private final DateTime time;

  public ScalingEvent(int newInstances, boolean success, String message) {
    this.newInstances = newInstances;
    this.success = success;
    this.message = message;
    this.time = new DateTime();
  }

  public ScalingEvent(int newInstances, boolean success) {
    this.newInstances = newInstances;
    this.success = success;
    this.message = null;
    this.time = new DateTime();
  }

  public DateTime getTime() {
    return time;
  }

  public int getNewInstances() {
    return newInstances;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  
}
