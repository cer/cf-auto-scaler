package net.chrisrichardson.cfautoscaler.backend.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import net.chrisrichardson.asyncpojos.actoids.core.ActoidContext;
import net.chrisrichardson.asyncpojos.actoids.stereotypes.Actoid;
import net.chrisrichardson.asyncpojos.futures.EnhancedFuture;
import net.chrisrichardson.asyncpojos.futures.FutureUtils;

import org.springframework.beans.factory.annotation.Autowired;

@Actoid
public class AutoscalingManagerImpl implements AutoscalingManager {

  @Autowired
  private ActoidContext actoidContext;
  
  private Map<String, ApplicationAutoscaler> applicationAutoscalers = new HashMap<String, ApplicationAutoscaler>();
  
  @Override
  public void autoscaleApplication(String appName, AutoscalingPolicy autoScalingPolicy) {
    ApplicationAutoscaler aap = applicationAutoscalers.get(appName);
    if (aap == null) {
      aap = actoidContext.makeActoid(ApplicationAutoscaler.class);
      applicationAutoscalers.put(appName, aap);
      aap.autoscaleApplication(appName, autoScalingPolicy);
    } else {
      aap.updateAutoscalingPolicy(autoScalingPolicy);
    }
    
  }

  @Override
  public void addAutoscaleRule(String appName, String ruleName, AutoscalingRule rule) {
    ApplicationAutoscaler aap = applicationAutoscalers.get(appName);
    aap.addAutoscaleRule(ruleName, rule);
  }

  @Override
  public Future<List<String>> getAppNames() {
    List<String> value = new ArrayList<String>(applicationAutoscalers.keySet());
    return FutureUtils.complete(value);
  }

  @Override
  public Future<Map<String, AutoscalingRule>> getRules(String appName) {
    return applicationAutoscalers.get(appName).getRules();
  }

  @Override
  public EnhancedFuture<AppInfo> getApp(String appName) {
    ApplicationAutoscaler aap = applicationAutoscalers.get(appName);
    return aap.getInfo();
  }

}
