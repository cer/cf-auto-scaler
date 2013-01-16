package net.chrisrichardson.cfautoscaler.webapp.resources;

import java.util.List;
import java.util.Map;

import net.chrisrichardson.cfautoscaler.backend.management.ScalingEvent;

import org.joda.time.DateTime;
import org.springframework.hateoas.ResourceSupport;

public class AutoscaledAppResource extends ResourceSupport {
  
  private String name;
  private Map<String, Double> metrics;
  private int instanceCount;
  private List<ScalingEvent> scalingEvents;
  private Map<String, DateTime> activeAlarms;

  public AutoscaledAppResource(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setMetrics(Map<String, Double> metrics) {
    this.metrics = metrics;
  }
  
  public Map<String, Double> getMetrics() {
    return metrics;
  }

  public void setInstanceCount(int instanceCount) {
    this.instanceCount = instanceCount;
  }
  
  public int getInstanceCount() {
    return instanceCount;
  }

  public void setScalingEvents(List<ScalingEvent> scalingEvents) {
    this.scalingEvents = scalingEvents;
  }
  
  public List<ScalingEvent> getScalingEvents() {
    return scalingEvents;
  }

  public void setActiveAlarms(Map<String, DateTime> activeAlarms) {
    this.activeAlarms = activeAlarms;
  }
  
  public Map<String, DateTime> getActiveAlarms() {
    return activeAlarms;
  }
  
}
