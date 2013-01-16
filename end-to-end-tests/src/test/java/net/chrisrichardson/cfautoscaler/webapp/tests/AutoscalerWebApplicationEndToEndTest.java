package net.chrisrichardson.cfautoscaler.webapp.tests;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import net.chrisrichardson.cfautoscaler.backend.AutoscaledAppResourceMother;
import net.chrisrichardson.cfautoscaler.backend.AutoscalingRuleResourceMother;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscaledApplicationResource;
import net.chrisrichardson.cfautoscaler.webapp.resources.AutoscalingRuleResource;

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
    RestTemplate rt = new RestTemplate();
    LinkExtractor ex = new LinkExtractor();
    MultiValueMap<String, Link> r1 = rt.execute("http://cf-auto-scaler.cloudfoundry.com", HttpMethod.GET, ex, ex);
    String autoscaledAppsHref = getSingletonRelHref(r1, "autoscaledapps");

    AutoscaledApplicationResource aar = AutoscaledAppResourceMother.makeAutoscaledAppResource("vertx-clock");

    URI autoscaledVertxClockHref = rt.postForLocation(autoscaledAppsHref, aar);

    MultiValueMap<String, Link> vertxClockResource = rt.execute(autoscaledVertxClockHref, HttpMethod.GET, ex, ex);
    String rulesLink = getSingletonRelHref(vertxClockResource, "rules");

    AutoscalingRuleResource rule = AutoscalingRuleResourceMother.makeAutoscalingRuleResource();
    URI autoscaledVertxClockRule = rt.postForLocation(rulesLink, rule);

    System.out.println(autoscaledVertxClockRule);

  }

  private String getSingletonRelHref(MultiValueMap<String, Link> vertxClockResource, String relName2) {
    List<Link> links2 = vertxClockResource.get(relName2);
    Assert.assertEquals(1, links2.size());
    return links2.get(0).getHref();
  }

}
