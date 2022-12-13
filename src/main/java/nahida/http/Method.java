package nahida.http;

public class Method {
  public static final Method ALL = new Method("");
  public static final Method GET = new Method("GET");
  public static final Method POST = new Method("POST");
  public static final Method HEAD = new Method("HEAD");
  public static final Method PUT = new Method("PUT");
  public static final Method PATCH = new Method("PATCH");
  public static final Method DELETE = new Method("DELETE");
  public static final Method OPTIONS = new Method("OPTIONS");
  public static final Method CONNECT = new Method("CONNECT");
  public static final Method TRACE = new Method("TRACE");

  private String text;

  Method(String text) {
    this.text = text.toUpperCase();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!getClass().equals(obj.getClass())) {
      return false;
    }

    var other = (Method) obj;
    return text.equals(other.text);
  }

  @Override
  public int hashCode() {
    return text.hashCode();
  }

  @Override
  public String toString() {
    return text;
  }
}
