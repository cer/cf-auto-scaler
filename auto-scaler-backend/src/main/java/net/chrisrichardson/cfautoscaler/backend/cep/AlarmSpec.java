package net.chrisrichardson.cfautoscaler.backend.cep;

public class AlarmSpec {
  
  private String metric;
  private double threshold;
  private AlarmOperator operator;
  
  public String getMetric() {
    return metric;
  }
  public void setMetric(String metric) {
    this.metric = metric;
  }
  public double getThreshold() {
    return threshold;
  }
  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }
  public AlarmOperator getOperator() {
    return operator;
  }
  public void setOperator(AlarmOperator operator) {
    this.operator = operator;
  }
  
  

}
