package net.chrisrichardson.cfautoscaler.backend.cep;

public enum AlarmOperator {
  LT {
    @Override
    public String asString() {
     return "<";
    }
  }, GT
 {
    @Override
    public String asString() {
      return ">";
    }
  };
  public abstract String asString();
}
