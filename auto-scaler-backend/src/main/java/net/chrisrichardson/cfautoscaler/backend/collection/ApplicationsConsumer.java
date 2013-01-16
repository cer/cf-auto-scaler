package net.chrisrichardson.cfautoscaler.backend.collection;

import java.util.List;

import org.cloudfoundry.client.lib.CloudApplication;

public interface ApplicationsConsumer {
  void consume(List<CloudApplication> applications);
}
