package net.chrisrichardson.cfautoscaler.backend.management;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

public class AppInfo {

  private final String appName;
  private final Map<String, Double> metrics;
  private final int instanceCount;
  private final List<ScalingEvent> scalingEvents;
  private final Map<String, DateTime> activeAlarms;

  public AppInfo(String appName, int instanceCount, Map<String, Double> metrics, List<ScalingEvent> scalingEvents, Map<String, DateTime> activeAlarms) {
    this.appName = appName;
    this.instanceCount = instanceCount;
    this.metrics = metrics;
    this.scalingEvents = scalingEvents;
    this.activeAlarms = activeAlarms;
  }

  public String getAppName() {
    return appName;
  }

  public int getInstanceCount() {
    return instanceCount;
  }
  
  public Map<String, Double> getMetrics() {
    return metrics;
  }

  public List<ScalingEvent> getScalingEvents() {
    return scalingEvents;
  }
  
  public Map<String, DateTime> getActiveAlarms() {
    return activeAlarms;
  }
}
