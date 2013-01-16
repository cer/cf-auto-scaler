package net.chrisrichardson.cfautoscaler.backend.cep;

public enum AlarmOperator {
  LT {
    @Override
    String asString() {
     return "<";
    }
  }, GT
 {
    @Override
    String asString() {
      return ">";
    }
  };
  abstract String asString();
}
