package nahida.except;

public class Http403Exception extends RuntimeException {
  private String path;

  public Http403Exception(String path) {
    super(String.format("You don't have permission to access %s on this server.", path));
    this.path = path;
  }

  public String path() {
    return path;
  }
}
