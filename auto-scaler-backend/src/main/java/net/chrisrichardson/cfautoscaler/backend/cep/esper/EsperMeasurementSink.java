package net.chrisrichardson.cfautoscaler.backend.cep.esper;

import net.chrisrichardson.cfautoscaler.backend.collection.InstanceMeasurement;
import net.chrisrichardson.cfautoscaler.backend.collection.MeasurementSink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EsperMeasurementSink implements MeasurementSink {

  @Autowired
  private Esper esper;
  
  public void publish(InstanceMeasurement icu) {
    esper.publish(icu);
  }

}
