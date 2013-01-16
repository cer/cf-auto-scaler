package net.chrisrichardson.cfautoscaler.backend;

import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscalingRuleResource;

public class AutoscalingRuleResourceMother {

	public static AutoscalingRuleResource makeAutoscalingRuleResource() {
		AutoscalingRuleResource rule = new AutoscalingRuleResource();
		rule.setName("idle");
		rule.setRule(AutoscalingRuleMother.makeAutoscalingRule());
		return rule;
	}

}

