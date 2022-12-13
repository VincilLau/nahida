package nahida.except;

import nahida.http.Method;

public class Http405Exception extends RuntimeException {
  private Method method;
  private String path;

  public Http405Exception(Method method, String path) {
    super(String.format("The method %s is not supported for the requested URL %s.", method, path));
    this.method = method;
    this.path = path;
  }

  public Method method() {
    return method;
  }

  public String path() {
    return path;
  }
}
