package net.chrisrichardson.cfautoscaler.backend.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import net.chrisrichardson.asyncpojos.actoids.core.ActoidContext;
import net.chrisrichardson.asyncpojos.actoids.stereotypes.Actoid;
import net.chrisrichardson.asyncpojos.futures.EnhancedFuture;
import net.chrisrichardson.asyncpojos.futures.FutureUtils;
import net.chrisrichardson.cfautoscaler.backend.cep.ApplicationAlarmConsumer;
import net.chrisrichardson.cfautoscaler.backend.cep.Esper;
import net.chrisrichardson.cfautoscaler.backend.collection.Collector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

@Actoid
@Scope("prototype")
public class ApplicationAutoscalerImpl implements ApplicationAutoscaler, ApplicationAlarmConsumer  {

  private Log logger = LogFactory.getLog(getClass());

  @Autowired
  private ActoidContext actoidContext;
  
  @Autowired
  private Esper esper;
  
  @Autowired
  private Collector collector;
  
  @Autowired
  @Qualifier("applicationScaler")
  private ApplicationScaler applicationScaler;
   
  private DateTime timeOfLastAction;
  private String appName;

  private AutoscalingPolicy autoScalingPolicy;

  private Map<String, AutoscalingRule> rules = new HashMap<String, AutoscalingRule>();
  
  private Map<String, Double> metrics = new HashMap<String, Double>();

  private int instanceCount;

  private List<ScalingEvent> scalingEvents = new ArrayList<ScalingEvent>();
  
  private AlarmTracker alarmTracker = new AlarmTracker();
  
  @Override
  public void processMetric(String metric, Double average) {
    metrics.put(metric, average);
  }
  
  @Override
  public void processAlarm(String alarmName) {
   alarmTracker.add(alarmName);
   if (!isManagementActionAllowed()) {
     logger.info("app is " + alarmName + " but we can't do anything");
     return;
   }
     
   timeOfLastAction = DateTime.now();
   
   AutoscalingRule rule = rules.get(alarmName);
   if (rule == null) {
     throw new RuntimeException("Dont know what to do alarm=" + alarmName);
   }

   applicationScaler.scale(appName, autoScalingPolicy, rule, actoidContext.self(ApplicationAutoscaler.class));
  }

  private boolean isManagementActionAllowed() {
    if (timeOfLastAction == null)
      return true;
    Interval howLongAgo = new Interval(timeOfLastAction, DateTime.now());
    long secondsAgo = howLongAgo.toDuration().getStandardSeconds();
    return secondsAgo >= autoScalingPolicy.getWaitingPeriodInSeconds();
  }

  @Override
  public void autoscaleApplication(String appName, AutoscalingPolicy autoScalingPolicy) {
    this.appName = appName;
    this.autoScalingPolicy = autoScalingPolicy;
    // FIXME We should set alarms for number of instances being outside of range
    logger.info("Autoscaling started for application: " + appName);
    esper.subscribeToAlarms(appName, actoidContext.self(ApplicationAlarmConsumer.class));
  }

  @Override
  public void updateAutoscalingPolicy(AutoscalingPolicy autoScalingPolicy) {
    // FIXME
  }

  @Override
  public void addAutoscaleRule(String ruleName, AutoscalingRule rule) {
    logger.info("Adding autoscaling rule: appName=" + appName + ", ruleName=" + ruleName);
    rules.put(ruleName, rule);
    if (rules.size() == 1) {
      collector.startCollecting(appName, actoidContext.self(ApplicationAutoscaler.class));
    }
    esper.createAutoscalingRule(appName, actoidContext.self(ApplicationAlarmConsumer.class), ruleName, rule.getAlarmSpec());
  }

  @Override
  public Future<Map<String, AutoscalingRule>> getRules() {
    Map<String, AutoscalingRule> result = new HashMap<String, AutoscalingRule>(rules);
    return FutureUtils.complete(result);
  }

  @Override
  public EnhancedFuture<AppInfo> getInfo() {
    logger.info("getInfo scaling events: " + scalingEvents.size());
    AppInfo appInfo = new AppInfo(appName, instanceCount, Collections.unmodifiableMap(metrics), Collections.unmodifiableList(scalingEvents), alarmTracker.getActiveAlarms());
    return FutureUtils.complete(appInfo);
  }

  @Override
  public void setInstanceCount(Integer instanceCount) {
      this.instanceCount = instanceCount;
  }

  @Override
  public void noteScalingSuccess(Integer newInstances) {
    logger.info("entrying noteScalingSuccess");
    scalingEvents.add(new ScalingEvent(newInstances, true));
    logger.info("noteScalingSuccess scaling events: " + scalingEvents.size());
  }

  @Override
  public void noteScalingFailure(Integer newInstances, String message) {
    scalingEvents.add(new ScalingEvent(newInstances, false, message));
    logger.info("noteScalingFailure scaling events: " + scalingEvents.size());
  }

  

}
