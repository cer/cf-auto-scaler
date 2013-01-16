package net.chrisrichardson.cfautoscaler.backend.collection;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Future;

import net.chrisrichardson.asyncpojos.actoids.stereotypes.Actoid;
import net.chrisrichardson.asyncpojos.futures.EnhancedFuture;
import net.chrisrichardson.asyncpojos.futures.FutureUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.ApplicationStats;
import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Actoid
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CloudFoundryImpl implements CloudFoundry {

  private Log logger = LogFactory.getLog(CloudFoundryImpl.class);
  
  @Value("${cloud.foundry.email}")
  private String userId;

  @Value("${cloud.foundry.password}")
  private String password;

  private CloudFoundryClient client;

  private void ensureCloudFoundryClient() {
    if (client == null)
      try {
        logger.info("Logging in with " + userId + ", " + password);
        client = new CloudFoundryClient(userId, password, "http://api.cloudfoundry.com");
        client.login();
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
  }

  @Override
  public EnhancedFuture<List<CloudApplication>> getApplications() {
    ensureCloudFoundryClient();
    return FutureUtils.complete(client.getApplications());
  }

  @Override
  public Future<ApplicationStats> getApplicationInstances(String name) {
    ensureCloudFoundryClient();
    return FutureUtils.complete(client.getApplicationStats(name));
  }

  @Override
  public EnhancedFuture<CloudApplication> getApplication(String name) {
    ensureCloudFoundryClient();
    return FutureUtils.complete(client.getApplication(name));
  }

  @Override
  public EnhancedFuture<Void> scale(String appName, int instances) {
    ensureCloudFoundryClient();
    try {
      client.updateApplicationInstances(appName, instances);
      logger.info("Updated instances for " + appName +  " to " + instances);
      return FutureUtils.complete(null);
    } catch (Exception e) {
      return FutureUtils.fail(e);
    }
  }
}
