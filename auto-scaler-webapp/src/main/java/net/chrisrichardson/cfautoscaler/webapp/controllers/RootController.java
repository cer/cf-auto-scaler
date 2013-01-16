package net.chrisrichardson.cfautoscaler.webapp.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoScalerResource;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RootController {
  
  @RequestMapping(value="/", method=RequestMethod.GET)
  public HttpEntity<AutoScalerResource> discover() {
    AutoScalerResource asr = new AutoScalerResource();
    asr.add(linkTo(AutoscaledAppController.class).withRel("autoscaledapps"));
    return new HttpEntity<AutoScalerResource>(asr);
  }

}
