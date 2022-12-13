package nahida.param;

public abstract class Param {
  private String name;

  protected Param(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public abstract Object match(String field);
}
