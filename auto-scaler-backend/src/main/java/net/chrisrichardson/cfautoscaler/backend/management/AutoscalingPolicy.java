package net.chrisrichardson.cfautoscaler.backend.management;

public class AutoscalingPolicy {
  
  private int minInstances;
  private int maxInstances;
  int waitingPeriodInSeconds;
  
  public int getMinInstances() {
    return minInstances;
  }
  public void setMinInstances(int minInstances) {
    this.minInstances = minInstances;
  }
  public int getMaxInstances() {
    return maxInstances;
  }
  public void setMaxInstances(int maxInstances) {
    this.maxInstances = maxInstances;
  }
  public int getWaitingPeriodInSeconds() {
    return waitingPeriodInSeconds;
  }
  public void setWaitingPeriodInSeconds(int waitingPeriodInSeconds) {
    this.waitingPeriodInSeconds = waitingPeriodInSeconds;
  }
  
  public int newInstanceCount(int currentInstances, int delta) {
    return Math.min(maxInstances, Math.max(minInstances, currentInstances + delta));
  }
  
}
