package nahida.except;

public class AbsPathException extends RuntimeException {
  private String path;

  public AbsPathException(String path) {
    super(String.format("The path '%s' is not an absolute path", path));
    this.path = path;
  }

  public String path() {
    return path;
  }
}
