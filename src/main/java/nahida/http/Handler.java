package nahida.http;

@FunctionalInterface
public interface Handler {
  void handle(Request req, Response resp) throws Exception;
}
