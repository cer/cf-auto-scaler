package net.chrisrichardson.cfautoscaler.webapp.resources;

import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingPolicy;

public class AutoscaledApplicationResource {

  private String appName;
  private AutoscalingPolicy autoScalingPolicy;
  
  public String getAppName() {
    return appName;
  }
  public void setAppName(String appName) {
    this.appName = appName;
  }
  public AutoscalingPolicy getAutoScalingPolicy() {
    return autoScalingPolicy;
  }
  public void setAutoScalingPolicy(AutoscalingPolicy autoScalingPolicy) {
    this.autoScalingPolicy = autoScalingPolicy;
  }
  
  
}
