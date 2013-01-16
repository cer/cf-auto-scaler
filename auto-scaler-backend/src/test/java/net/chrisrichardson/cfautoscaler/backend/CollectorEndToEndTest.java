package net.chrisrichardson.cfautoscaler.backend;

import java.util.concurrent.TimeUnit;

import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingManager;
import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingPolicy;
import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingRule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/appctx/*.xml")
public class CollectorEndToEndTest {
  
  @Autowired
  private AutoscalingManager autoscalingManager;
  
  @Test
  public void collectorShouldPollForApplications() throws Exception {
    AutoscalingPolicy autoScalingPolicy = AutoscalingPolicyMother.makeAutoscalingPolicy();
    autoscalingManager.autoscaleApplication("available-restaurant-webapp", autoScalingPolicy);
    
    AutoscalingRule rule = AutoscalingRuleMother.makeAutoscalingRule();
    autoscalingManager.addAutoscaleRule("available-restaurant-webapp", "idle", rule);
    TimeUnit.SECONDS.sleep(1000);
  }

}
