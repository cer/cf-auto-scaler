package net.chrisrichardson.cfautoscaler.backend.config;

import net.chrisrichardson.asyncpojos.actoids.core.ActoidSystem;
import net.chrisrichardson.asyncpojos.actoids.pooled.PooledActoidFactory;
import net.chrisrichardson.cfautoscaler.backend.collection.CloudFoundry;
import net.chrisrichardson.cfautoscaler.backend.management.ApplicationScaler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoscalerConfig {
  
  @Bean
  public CloudFoundry cloudFoundry(ActoidSystem actoidSystem) {
    return new PooledActoidFactory<CloudFoundry>(CloudFoundry.class, actoidSystem, "cloudFoundryImpl", 20).make();
  }

  @Bean
  public ApplicationScaler applicationScaler(ActoidSystem actoidSystem) {
    return new PooledActoidFactory<ApplicationScaler>(ApplicationScaler.class, actoidSystem, "applicationScalerImpl", 20).make();
  }
}
