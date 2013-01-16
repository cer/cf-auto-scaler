package net.chrisrichardson.cfautoscaler.backend;

import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscaledApplicationResource;

public class AutoscaledAppResourceMother {

	public static AutoscaledApplicationResource makeAutoscaledAppResource() {
		String name = "foo";
		return makeAutoscaledAppResource(name);
	}

	public static AutoscaledApplicationResource makeAutoscaledAppResource(
			String name) {
		AutoscaledApplicationResource aar = new AutoscaledApplicationResource();
		aar.setAppName(name);
		aar.setAutoScalingPolicy(AutoscalingPolicyMother
				.makeAutoscalingPolicy());
		return aar;
	}

}
