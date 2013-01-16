package net.chrisrichardson.cfautoscaler.backend.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.chrisrichardson.asyncpojos.actoids.core.ActoidContext;
import net.chrisrichardson.asyncpojos.actoids.stereotypes.Actoid;
import net.chrisrichardson.asyncpojos.futures.SuccessCallback;
import net.chrisrichardson.cfautoscaler.backend.management.ApplicationAutoscaler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.CloudApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Actoid
public class CollectorImpl implements Collector, ApplicationsConsumer {

  private Log logger = LogFactory.getLog(getClass());

  @Autowired
  private ActoidContext actoidContext;

  @Autowired
  @Qualifier("cloudFoundry")
  private CloudFoundry cloudFoundry;
  
  private Map<String, ApplicationCollector> appCollectors = new HashMap<String, ApplicationCollector>();

  @Override
  public void pollForApplications() {
    final ApplicationsConsumer self = actoidContext.self(ApplicationsConsumer.class);
    cloudFoundry.getApplications().addSuccessCallback(new SuccessCallback<List<CloudApplication>>() {
      
      @Override
      public void onSuccess(List<CloudApplication> apps) {
        self.consume(apps);
      }
    });
  }

  @Override
  public void consume(List<CloudApplication> applications) {
    Map<String, CloudApplication> appsMap = listToMap(applications);
    removeDeletedApplications(appsMap);
  }

  private Map<String, CloudApplication> listToMap(List<CloudApplication> applications) {
    Map<String, CloudApplication> result = new HashMap<String, CloudApplication>();
    for (CloudApplication cloudApplication : applications) {
      result.put(cloudApplication.getName(), cloudApplication);
    }
    return result;
  }

  private void removeDeletedApplications(Map<String, CloudApplication> appsMap) {
    Set<String> namesOfAppsToRemove = new HashSet<String>(appCollectors.keySet());
    namesOfAppsToRemove.removeAll(appsMap.keySet());
    if (!namesOfAppsToRemove.isEmpty())
      logger.info("Apps to stop monitoring: " + namesOfAppsToRemove);
    for (String appName : namesOfAppsToRemove) {
      ApplicationCollector appc = appCollectors.get(appName);
      appCollectors.remove(appName);
      appc.stopCollecting();
    }
  }

  @Override
  public void startCollecting(String appName, ApplicationAutoscaler applicationAutoscaler) {
    ApplicationCollector collector = actoidContext.actoidFor(new ApplicationCollectorImpl(appName, applicationAutoscaler), ApplicationCollector.class);
    appCollectors.put(appName, collector);
    collector.startCollecting();
    
  }
  
}
