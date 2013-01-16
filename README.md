Cloud Foundry Autoscaler
========================

This is a simple autoscaler for Cloud Foundry. Currently, it monitors CPU utilization (by getting instance stats) and adjusts the number of instances. One day it will make autoscaling decisions based on metrics published by the application.

In order to deploy the autoscaler in your environment, you need to set two environment variables:

    vmc env-add cf-auto-scaler cloud_foundry_email=<Your email address>
    vmc env-add cf-auto-scaler cloud_foundry_password=<Your Password>

The autoscaler exposes a HATEOAS-style REST API for configuring autoscaling for your applications. Here is an example request on the root URL:

	GET http://cf-auto-scaler.cloudfoundry.com
    ->
    {"links":[
    	{"rel":"autoscaledapps",
    	 "href":"http://cf-auto-scaler.cloudfoundry.com/autoscaledapps"}
    ]}

The autoscaler works as follows. First you specify that an application should be auto scaled. You supply the follow parameters (attributes of AutoscalingPolicy):

* minInstances - minimum number of instances
* maxInstances - max number of instances
* waitingPeriodInSeconds - time interval between adjusting the number of instances

You use the href specified by the 'autoscaledapps' relationship to manage applications.

Here is an example request that creates an autoscaled application:

    POST <URL specified by the 'autoscaledapps' relationship>
    Content-type: application/json
    
    {"appName":"vertx-clock",
     "autoScalingPolicy": 
     		{"waitingPeriodInSeconds":10,
     		  "minInstances":1,
     		  "maxInstances":3}
    }

This request returns the URL of the resource of the now autoscaled application.

A GET on that URL returns a resource like this:

    {"name":"vertx-clock",
     "links":[
         {"rel":"self",
           "href":"http://cf-auto-scaler.cloudfoundry.com/autoscaledapps/vertx-clock"},
         {"rel":"rules",
            "href":"http://cf-auto-scaler.cloudfoundry.com/autoscaledapps/vertx-clock/rules"}
      ]
    }

Note the 'rules' relation. That's the URL you use to manage the application's autoscaling rules.

Once you have enabled auto scaling for an application you can then define one more auto scaling rules. Each rule consists of the following (attributes of AutoscalingRule):

* delta - how many instances to scale up/down by when the alarm is triggered
* alarmSpec - describes the alarm

An alarm consists the following attributes

* metric - currently only 'cpu' is supported
* threshold - e.g. 30 (as in 30%)
* Operator - either LT or GT

Here is an example request:

    POST <url from the 'rules' relationship of an autoscaledapp resource>
    Content-type: application/json
    
    {"name":"idle",
     "rule":{"delta":-1,
       "alarmSpec":{"metric":"cpu","threshold":30.0,"operator":"LT"}},
       "links":[]}

The autoscaler will then monitor the application  and if the threshold is breached for a certain period of time then the number of instances will be adjusted within the constraints of the application's scaling policy.
