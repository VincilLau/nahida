package nahida.except;

public class Http404Exception extends RuntimeException {
  private String path;

  public Http404Exception(String path) {
    super(String.format("The requested URL %s was not found on this server.", path));
    this.path = path;
  }

  public String path() {
    return path;
  }
}
