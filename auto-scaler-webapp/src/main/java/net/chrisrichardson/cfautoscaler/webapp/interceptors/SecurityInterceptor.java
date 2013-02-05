package net.chrisrichardson.cfautoscaler.webapp.interceptors;

import net.chrisrichardson.cfautoscaler.backend.collection.CloudFoundryCredentialsHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SecurityInterceptor extends HandlerInterceptorAdapter {

  private Log logger = LogFactory.getLog(getClass());

  @Autowired
  private CloudFoundryCredentialsHolder cloudFoundryCredentialsHolder;

  private AtomicLong delay = new AtomicLong(1);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String authorizationHeader = request.getHeader("Authorization");
    boolean allow = AuthenticationHelper.authenticate(authorizationHeader, cloudFoundryCredentialsHolder.getUserId(), cloudFoundryCredentialsHolder.getPassword());
    if (allow) {
      logger.info("successful authentication attempt");
      delay.set(1);
    } else {
      if (authorizationHeader != null) {
        logger.error("unsuccessful authentication attempt");
        long currentDelay = delay.get();
        if (currentDelay < 20 * 1000)
          delay.addAndGet(currentDelay + 1);
        TimeUnit.MILLISECONDS.sleep(currentDelay);
      } else {
        logger.info("requesting credentials");
      }
      response.setHeader("WWW-Authenticate", "Basic realm=cf autoscaler");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
    return allow;
  }

}
