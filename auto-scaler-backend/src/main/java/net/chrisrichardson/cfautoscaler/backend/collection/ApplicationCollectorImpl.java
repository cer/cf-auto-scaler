package net.chrisrichardson.cfautoscaler.backend.collection;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.chrisrichardson.asyncpojos.actoids.core.ActoidContext;
import net.chrisrichardson.asyncpojos.futures.FutureUtils;
import net.chrisrichardson.cfautoscaler.backend.management.ApplicationAutoscaler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.ApplicationStats;
import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.InstanceStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;

public class ApplicationCollectorImpl implements ApplicationCollector {

  private Log logger = LogFactory.getLog(ApplicationCollectorImpl.class);

  @Autowired
  @Qualifier("cloudFoundry")
  private CloudFoundry cloudFoundry;

  @Autowired
  private TaskScheduler taskScheduler;

  @Autowired
  private ActoidContext actoidContext;

  private ScheduledFuture<?> sf;

  @Autowired
  private MeasurementSink measurementSink;

  private final String appName;

  private final ApplicationAutoscaler applicationAutoscaler;

  public ApplicationCollectorImpl(String appName, ApplicationAutoscaler applicationAutoscaler) {
    this.appName = appName;
    this.applicationAutoscaler = applicationAutoscaler;
  }

  public void startCollecting() {
    Assert.notNull(appName);
    logger.info("Starting to collect: " + appName);
    final ApplicationCollector s = actoidContext.self(ApplicationCollector.class);
    sf = taskScheduler.scheduleAtFixedRate(new Runnable() {

      public void run() {
        s.gatherMetrics();
      }
    }, 5 * 1000);
  }

  public void stopCollecting() {
    logger.info("stopping to collect: " + appName);

    sf.cancel(true);
  }

  public void gatherMetrics() {

    // What happens if we are deleted

    Future<CloudApplication> applicationFuture = cloudFoundry.getApplication(appName);
    Future<ApplicationStats> appStatsFuture = cloudFoundry.getApplicationInstances(appName);

    // FIXME - replace with callback (except that we need a timeout)

    try {
      FutureUtils.await(TimeUnit.MILLISECONDS, 800, applicationFuture, appStatsFuture);
    } catch (Exception e) {
      logger.error("error gathering metrics", e);
      return;
    }

    CloudApplication app = FutureUtils.get(applicationFuture);
    ApplicationStats appStats = FutureUtils.get(appStatsFuture);

    applicationAutoscaler.setInstanceCount(app.getInstances());
    // Notify someone if we are stopped

    for (InstanceStats is : appStats.getRecords()) {
      if (is.getState().equals("RUNNING")) {
        InstanceMeasurement icu = new InstanceMeasurement(appName, is.getId(), "cpu", is.getUsage().getCpu());
        measurementSink.publish(icu);
      }
    }

  }

}
