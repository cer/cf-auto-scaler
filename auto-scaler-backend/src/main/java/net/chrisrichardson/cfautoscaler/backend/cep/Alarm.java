package net.chrisrichardson.cfautoscaler.backend.cep;

public class Alarm {
  private String appName;
  private String alarmName;
  
  public Alarm() {
  }
  
  public Alarm(String appName, String alarmName) {
    super();
    this.appName = appName;
    this.alarmName = alarmName;
  }

  public String getAppName() {
    return appName;
  }
  public void setAppName(String appName) {
    this.appName = appName;
  }
  public String getAlarmName() {
    return alarmName;
  }
  public void setAlarmName(String alarmName) {
    this.alarmName = alarmName;
  }
}
