package net.chrisrichardson.cfautoscaler.webapp.resources;

import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingRule;

import org.springframework.hateoas.ResourceSupport;

public class AutoscalingRuleResource extends ResourceSupport {
  private String name;
  private AutoscalingRule rule;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AutoscalingRule getRule() {
    return rule;
  }

  public void setRule(AutoscalingRule rule) {
    this.rule = rule;
  }

}
