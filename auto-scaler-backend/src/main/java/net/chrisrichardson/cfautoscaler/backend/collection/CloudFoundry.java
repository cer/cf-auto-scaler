package net.chrisrichardson.cfautoscaler.backend.collection;

import java.util.List;
import java.util.concurrent.Future;

import net.chrisrichardson.asyncpojos.futures.EnhancedFuture;

import org.cloudfoundry.client.lib.ApplicationStats;
import org.cloudfoundry.client.lib.CloudApplication;


public interface CloudFoundry {

  EnhancedFuture<List<CloudApplication>> getApplications();

  Future<ApplicationStats> getApplicationInstances(String name);

  EnhancedFuture<CloudApplication> getApplication(String name);

  EnhancedFuture<Void> scale(String appName, int instances);

}
