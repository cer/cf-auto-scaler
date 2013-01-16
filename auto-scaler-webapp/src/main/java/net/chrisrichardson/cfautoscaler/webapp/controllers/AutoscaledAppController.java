package net.chrisrichardson.cfautoscaler.webapp.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import net.chrisrichardson.cfautoscaler.backend.management.AppInfo;
import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingManager;
import net.chrisrichardson.cfautoscaler.backend.management.AutoscalingRule;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscaledAppResource;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscaledApplicationResource;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscalingRuleResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(value = "/autoscaledapps")
public class AutoscaledAppController {

  @Autowired
  private AutoscalingManager autoscalingManager;

  @RequestMapping(method = RequestMethod.GET)
  public HttpEntity<Resources<AutoscaledAppResource>> getAutoscaledApps() throws Exception {

    List<String> autoscaledAppNames = autoscalingManager.getAppNames().get(10, TimeUnit.MILLISECONDS);
    List<AutoscaledAppResource> result = new ArrayList<AutoscaledAppResource>(autoscaledAppNames.size());
    Collection<Link> links = new ArrayList<Link>();
    links.add(linkTo(AutoscaledAppController.class).withSelfRel());

    for (String appName : autoscaledAppNames) {
      AutoscaledAppResource ar = new AutoscaledAppResource(appName);
      ar.add(linkTo(AutoscaledAppController.class).slash(appName).withSelfRel());
      ar.add(linkTo(AutoscaledAppController.class).slash(appName).slash("rules").withRel("rules"));
      links.add(linkTo(AutoscaledAppController.class).slash(appName).withRel("autoscaledApp"));
      result.add(ar);
    }
    return new HttpEntity<Resources<AutoscaledAppResource>>(new Resources<AutoscaledAppResource>(result, links));
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public void autoscaleApplication(@RequestBody AutoscaledApplicationResource autoscaledApplicationResource,
      UriComponentsBuilder builder, HttpServletResponse response) {
    autoscalingManager.autoscaleApplication(autoscaledApplicationResource.getAppName(),
        autoscaledApplicationResource.getAutoScalingPolicy());
    String uriString = builder.path("/autoscaledapps/{appName}").buildAndExpand(autoscaledApplicationResource.getAppName()).toUriString();
    response.setHeader("location", uriString);
  }

  @RequestMapping(value = "/{appName}", method = RequestMethod.GET)
  public HttpEntity<AutoscaledAppResource> get(@PathVariable String appName) throws InterruptedException, ExecutionException {
    AppInfo app = autoscalingManager.getApp(appName).get();
    AutoscaledAppResource ar = new AutoscaledAppResource(appName);
    ar.setMetrics(app.getMetrics());
    ar.setInstanceCount(app.getInstanceCount());
    ar.setScalingEvents(app.getScalingEvents());
    ar.setActiveAlarms(app.getActiveAlarms());
    ar.add(linkTo(AutoscaledAppController.class).slash(appName).withSelfRel());
    ar.add(linkTo(AutoscaledAppController.class).slash(appName).slash("rules").withRel("rules"));
    return new HttpEntity<AutoscaledAppResource>(ar);
  }

  @RequestMapping(value = "/{appName}/rules", method = RequestMethod.GET)
  public HttpEntity<Resources<AutoscalingRuleResource>> getRules(@PathVariable String appName) throws Exception {
    Map<String, AutoscalingRule> namedRules = autoscalingManager.getRules(appName).get(10, TimeUnit.MILLISECONDS);
    Collection<Link> links = new ArrayList<Link>();
    for (Map.Entry<String, AutoscalingRule> namedRule : namedRules.entrySet()) {
      AutoscalingRuleResource rr = new AutoscalingRuleResource();
      rr.setName(namedRule.getKey());
      rr.setRule(namedRule.getValue());
      rr.add(linkTo(AutoscaledAppController.class).slash(appName).slash("rules").slash(namedRule.getKey()).withSelfRel());
      links.add(linkTo(AutoscaledAppController.class).slash(appName).slash("rules").slash(namedRule.getKey()).withRel("rule"));
    }
    List<AutoscalingRuleResource> rules = new ArrayList<AutoscalingRuleResource>(namedRules.size());
    return new HttpEntity<Resources<AutoscalingRuleResource>>(new Resources<AutoscalingRuleResource>(rules, links));
  }

  @RequestMapping(value = "/{appName}/rules", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public void createRule(@PathVariable String appName, @RequestBody AutoscalingRuleResource rule, UriComponentsBuilder builder,
      HttpServletResponse response) {
    autoscalingManager.addAutoscaleRule(appName, rule.getName(), rule.getRule());
    String uriString = builder.path("/autoscaledapps/{appName}/rules/{ruleName}").buildAndExpand(appName, rule.getName()).toUriString();
    response.setHeader("location", uriString);
  }

  @RequestMapping(value = "/{appName}/rules/{ruleName}", method = RequestMethod.GET)
  public HttpEntity<AutoscalingRuleResource> getRule(@PathVariable String appName, @PathVariable String ruleName) {
    // FIXME - implement me
    AutoscalingRuleResource rule = new AutoscalingRuleResource();
    rule.setName(ruleName);
    return new HttpEntity<AutoscalingRuleResource>(rule);
  }

}
