package net.chrisrichardson.cfautoscaler.backend.management;

import net.chrisrichardson.asyncpojos.actoids.core.ActoidContext;
import net.chrisrichardson.asyncpojos.actoids.stereotypes.Actoid;
import net.chrisrichardson.asyncpojos.futures.CompletionCallback;
import net.chrisrichardson.asyncpojos.futures.Outcome;
import net.chrisrichardson.asyncpojos.futures.SuccessCallback;
import net.chrisrichardson.cfautoscaler.backend.collection.CloudFoundry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.CloudApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Actoid
public class ApplicationScalerImpl implements ApplicationScaler {

  private Log logger = LogFactory.getLog(getClass());

  @Autowired
  private ActoidContext actoidContext;

  @Autowired
  @Qualifier("cloudFoundry")
  private CloudFoundry cloudFoundry;

  @Override
  public void scale(String appName, final AutoscalingPolicy autoScalingPolicy, final AutoscalingRule rule, final ApplicationAutoscaler appAutoscaler) {
    final ApplicationScaler self = actoidContext.self(ApplicationScaler.class);
    cloudFoundry.getApplication(appName).addSuccessCallback(new SuccessCallback<CloudApplication>() {

      @Override
      public void onSuccess(CloudApplication app) {
        self.scale(app, autoScalingPolicy, rule, appAutoscaler);
      }
    });
  }

  @Override
  public void scale(CloudApplication app, AutoscalingPolicy autoScalingPolicy, AutoscalingRule rule, final ApplicationAutoscaler appAutoscaler) {
    String appName = app.getName();
    final int currentInstances = app.getInstances();
    final int newInstances = autoScalingPolicy.newInstanceCount(currentInstances, rule.getDelta());
    if (currentInstances == newInstances) {
      if (rule.getDelta() > 0)
        logger.info("application " + appName + " already at maximum");
      else
        logger.info("application " + appName + " already at minimum");
    } else {
      logger.info("scaling " + appName + " by " + (newInstances - currentInstances));
      cloudFoundry.scale(appName, newInstances).addCompletionCallback(new CompletionCallback<Void>() {
        
        @Override
        public void onCompletion(Outcome<Void> outcome) {
          logger.info("in completion callback: " + outcome.isSuccessful());
          try {
            if (outcome.isSuccessful())
              appAutoscaler.noteScalingSuccess(newInstances);
            else {
              logger.error("autoscaling failed", outcome.failure);
              appAutoscaler.noteScalingFailure(newInstances, outcome.failure.getMessage());
            }
          } catch (Throwable t) {
            logger.error("weird error", t);
            throw new RuntimeException(t);
          }
          logger.info("leaving completion callback: " + outcome.isSuccessful());
        }
      });
    }
  }

}
