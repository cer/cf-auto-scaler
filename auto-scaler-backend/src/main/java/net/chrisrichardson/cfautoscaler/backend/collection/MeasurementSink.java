package net.chrisrichardson.cfautoscaler.backend.collection;

public interface MeasurementSink {

  void publish(InstanceMeasurement icu);

}
