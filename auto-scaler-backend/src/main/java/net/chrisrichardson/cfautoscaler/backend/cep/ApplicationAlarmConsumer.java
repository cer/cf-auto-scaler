package net.chrisrichardson.cfautoscaler.backend.cep;

public interface ApplicationAlarmConsumer {

  void processAlarm(String alarmName);

  void processMetric(String metric, Double average);

}
