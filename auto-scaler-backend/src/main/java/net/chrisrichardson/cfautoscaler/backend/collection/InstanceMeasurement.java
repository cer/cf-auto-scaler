package net.chrisrichardson.cfautoscaler.backend.collection;

import org.apache.commons.lang.builder.ToStringBuilder;

public class InstanceMeasurement {

  private final String appName;
  private final String instanceId;
  private final double value;
  private final String metric;

  public InstanceMeasurement(String appName, String instanceId, String metric, double value) {
    this.appName = appName;
    this.instanceId = instanceId;
    this.metric = metric;
    this.value = value;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public String getAppName() {
    return appName;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public String getMetric() {
    return metric;
  }
  
  public double getValue() {
    return value;
  }
  
}
