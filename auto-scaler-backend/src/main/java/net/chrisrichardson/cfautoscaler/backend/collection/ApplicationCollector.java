package net.chrisrichardson.cfautoscaler.backend.collection;

public interface ApplicationCollector {

  void startCollecting();

  void stopCollecting();

  void gatherMetrics();

}
