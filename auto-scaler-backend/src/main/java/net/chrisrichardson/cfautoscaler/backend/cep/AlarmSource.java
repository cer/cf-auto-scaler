package net.chrisrichardson.cfautoscaler.backend.cep;


public interface AlarmSource {

  public void registerAlarm(String appName, String alarmName, AlarmSpec alarmSpec);

  public void subscribeToAlarms(String appName, ApplicationAlarmConsumer applicationAlarmConsumer);

}