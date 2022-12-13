package nahida;

@FunctionalInterface
public interface Middleware {
  void call(Context ctx) throws Exception;
}
