package nahida.except;

public class Http400Exception extends RuntimeException {
  private Exception src;

  public Http400Exception(Exception src) {
    super(src.toString());
    this.src = src;
  }

  public Exception src() {
    return src;
  }
}
