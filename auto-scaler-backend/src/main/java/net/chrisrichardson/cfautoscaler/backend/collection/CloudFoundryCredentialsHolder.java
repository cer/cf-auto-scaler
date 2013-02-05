package net.chrisrichardson.cfautoscaler.backend.collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CloudFoundryCredentialsHolder {

  @Value("${cloud.foundry.email}")
  private String userId;

  @Value("${cloud.foundry.password}")
  private String password;

  public String getUserId() {
    return userId;
  }

  public String getPassword() {
    return password;
  }
}