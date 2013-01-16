package net.chrisrichardson.cfautoscaler.backend;

import java.io.PrintWriter;

import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscaledApplicationResource;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscalingRuleResource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class JsonExamplesTest {

	@Test
	public void generateAutoscalingPolicy() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		PrintWriter pw = new PrintWriter(System.out);
		AutoscaledApplicationResource aar = AutoscaledAppResourceMother.makeAutoscaledAppResource();
		objectMapper.writeValue(pw, aar);
	}

	@Test
	public void generateAutoscalingRule() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		PrintWriter pw = new PrintWriter(System.out);
		AutoscalingRuleResource rule = AutoscalingRuleResourceMother.makeAutoscalingRuleResource();
		objectMapper.writeValue(pw, rule);
		pw.flush();
	}

}
