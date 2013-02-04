package net.chrisrichardson.cfautoscaler.webapp.tests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import net.chrisrichardson.cfautoscaler.backend.AutoscaledAppResourceMother;
import net.chrisrichardson.cfautoscaler.backend.AutoscalingRuleResourceMother;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscaledApplicationResource;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscalingRuleResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class AutoscalerWebApplicationEndToEndTest {

  private Log logger = LogFactory.getLog(getClass());

  private String testAppName;
  private RestTemplate restTemplate;
  private LinkExtractor linkExtractor;
  private String autoscaledAppsUrl;
  private CloudFoundryClient cloudFoundryClient;
  private String autoscalerUrl;

  class LinkExtractor implements RequestCallback, ResponseExtractor<MultiValueMap<String, Link>> {

    private ObjectMapper mapper = new ObjectMapper();

    public void doWithRequest(ClientHttpRequest request) throws IOException {
      request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }

    @SuppressWarnings("rawtypes")
    public MultiValueMap<String, Link> extractData(ClientHttpResponse response) throws IOException {
      MultiValueMap<String, Link> links = new LinkedMultiValueMap<String, Link>();
      Map m = mapper.readValue(response.getBody(), Map.class);
      Object o = m.get("links");
      if (o instanceof List) {
        for (Object lnk : (List) o) {
          if (lnk instanceof Map) {
            Map lnkmap = (Map) lnk;
            String href = String.format("%s", lnkmap.get("href"));
            String rel = String.format("%s", lnkmap.get("rel"));
            links.add(rel, new Link(href, rel));
          }
        }
      }
      return links;
    }

  }

  @Test
  public void test() throws Exception {
    testAppName = System.getProperty("test.app.name");
    Assert.assertNotNull("Please specify -Dtest.app.name=", testAppName);

    autoscalerUrl = System.getProperty("autoscaler.url");

    restTemplate = new RestTemplate();
    linkExtractor = new LinkExtractor();

    loginToCloudFoundry();

    autoscaledAppsUrl = getAutoscaledAppUrl();

    URI appUrl = createAutoscaledApp();

    createAutoscalingRule(appUrl);

    createIdleInstancesForTestApplication();

    assertApplicationIsAutoscaled(appUrl);

  }

  private void assertApplicationIsAutoscaled(URI appUrl) throws InterruptedException {
    int initialScalingEvents = getScalingEvents(appUrl).size();
    for (int i = 0; i < 10; i++) {
      List<Object> scalingEvents = getScalingEvents(appUrl);
      if (scalingEvents.size() > initialScalingEvents)
        return;
      logger.info("Waiting for scaling event " + (i + 1) + " of " + 10);
      TimeUnit.SECONDS.sleep(15);
    }
    Assert.fail("no scaling event after 150 seconds");
  }

  private List<Object> getScalingEvents(URI appUrl) {
    Map<String, Object> appInfo = restTemplate.getForObject(appUrl, Map.class);
    return (List<Object> ) appInfo.get("scalingEvents");
  }

  private void createIdleInstancesForTestApplication() throws MalformedURLException {
    cloudFoundryClient.updateApplicationInstances(testAppName, 4);
  }

  private void loginToCloudFoundry() throws MalformedURLException {
    String userId = System.getProperty("cloud.foundry.email");
    String password = System.getProperty("cloud.foundry.password");
    Assert.assertNotNull("Please specify -Dcloud.foundry.email=", userId);
    Assert.assertNotNull("Please specify -Dcloud.foundry.password=", password);

    cloudFoundryClient = new CloudFoundryClient(userId, password, "http://api.cloudfoundry.com");
    cloudFoundryClient.login();
  }

  private void createAutoscalingRule(URI appUrl) {
    MultiValueMap<String, Link> appResource = restTemplate.execute(appUrl, HttpMethod.GET, linkExtractor, linkExtractor);
    String rulesLink = getSingletonRelHref(appResource, "rules");
    AutoscalingRuleResource rule = AutoscalingRuleResourceMother.makeAutoscalingRuleResource();
    restTemplate.postForLocation(rulesLink, rule);
  }

  private URI createAutoscaledApp() {
    AutoscaledApplicationResource aar = AutoscaledAppResourceMother.makeAutoscaledAppResource("vertx-clock");

    return restTemplate.postForLocation(autoscaledAppsUrl, aar);
  }

  private String getAutoscaledAppUrl() {
    MultiValueMap<String, Link> r1 = restTemplate.execute(autoscalerUrl, HttpMethod.GET, linkExtractor, linkExtractor);
    return getSingletonRelHref(r1, "autoscaledapps");
  }

  private String getSingletonRelHref(MultiValueMap<String, Link> appResource, String relName) {
    List<Link> links2 = appResource.get(relName);
    Assert.assertEquals(1, links2.size());
    return links2.get(0).getHref();
  }

}
