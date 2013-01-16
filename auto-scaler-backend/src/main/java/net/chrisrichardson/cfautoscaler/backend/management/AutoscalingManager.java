package net.chrisrichardson.cfautoscaler.backend.management;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import net.chrisrichardson.asyncpojos.futures.EnhancedFuture;


public interface AutoscalingManager {

  void autoscaleApplication(String appName, AutoscalingPolicy autoScalingPolicy);

  void addAutoscaleRule(String appName, String ruleName, AutoscalingRule rule);

  Future<List<String>> getAppNames();
  Future<Map<String, AutoscalingRule>> getRules(String appName);

  EnhancedFuture<AppInfo> getApp(String appName);

}