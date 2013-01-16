package net.chrisrichardson.cfautoscaler.backend;

import net.chrisrichardson.cfautoscaler.backend.cep.AlarmOperator;
import net.chrisrichardson.cfautoscaler.backend.cep.AlarmSpec;
import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingRule;

public class AutoscalingRuleMother {

  public static AutoscalingRule makeAutoscalingRule() {
    AutoscalingRule rule = new AutoscalingRule();
    rule.setDelta(-1);
    AlarmSpec alarmSpec = new AlarmSpec();
    alarmSpec.setMetric("cpu");
    alarmSpec.setOperator(AlarmOperator.LT);
    alarmSpec.setThreshold(30);
    rule.setAlarmSpec(alarmSpec);
    return rule;
  }

}
