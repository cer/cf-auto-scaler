package net.chrisrichardson.cfautoscaler.backend;

import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingPolicy;

public class AutoscalingPolicyMother {

  public static AutoscalingPolicy makeAutoscalingPolicy() {
    AutoscalingPolicy autoScalingPolicy = new AutoscalingPolicy();
    autoScalingPolicy.setMinInstances(1);
    autoScalingPolicy.setMaxInstances(3);
    autoScalingPolicy.setWaitingPeriodInSeconds(10);
    return autoScalingPolicy;
  }

}
