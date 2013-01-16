package net.chrisrichardson.cfautoscaler.backend.collection;

import net.chrisrichardson.cfautoscaler.backend.management.ApplicationAutoscaler;

public interface Collector {
  
  void pollForApplications();

  void startCollecting(String appName, ApplicationAutoscaler applicationAutoscaler);

}
