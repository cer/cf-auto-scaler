package net.chrisrichardson.cfautoscaler.backend.cep;

public class ApplicationAverage {

  private String appName;
  private String metric;
  private double average;
  
  
  public ApplicationAverage(String appName, String metric, double average) {
    this.appName = appName;
    this.metric = metric;
    this.average = average;
  }
  public String getAppName() {
    return appName;
  }
  public String getMetric() {
    return metric;
  }
  public double getAverage() {
    return average;
  }
  
  
}
