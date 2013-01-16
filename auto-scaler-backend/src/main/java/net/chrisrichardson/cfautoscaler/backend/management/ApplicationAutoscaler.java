package net.chrisrichardson.cfautoscaler.backend.management;

import java.util.Map;
import java.util.concurrent.Future;

import net.chrisrichardson.asyncpojos.futures.EnhancedFuture;


public interface ApplicationAutoscaler {
  void autoscaleApplication(String appName, AutoscalingPolicy autoScalingPolicy);

  void updateAutoscalingPolicy(AutoscalingPolicy autoScalingPolicy);

  void addAutoscaleRule(String ruleName, AutoscalingRule rule);

  Future<Map<String, AutoscalingRule>> getRules();

  EnhancedFuture<AppInfo> getInfo();

  void setInstanceCount(Integer instances);

  void noteScalingSuccess(Integer newInstances);

  void noteScalingFailure(Integer newInstances, String message);

}