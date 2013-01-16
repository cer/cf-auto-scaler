package net.chrisrichardson.cfautoscaler.backend.management;

import org.cloudfoundry.client.lib.CloudApplication;


public interface ApplicationScaler {

  void scale(String appName, AutoscalingPolicy autoScalingPolicy, AutoscalingRule rule, ApplicationAutoscaler appAutoscaler);

  void scale(CloudApplication success, AutoscalingPolicy autoScalingPolicy, AutoscalingRule rule, ApplicationAutoscaler appAutoscaler);

}
