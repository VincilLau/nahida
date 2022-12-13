package nahida;

import java.nio.charset.Charset;
import java.util.ArrayList;
import nahida.http.Handler;
import nahida.http.Server;
import nahida.middleware.DateHeader;
import nahida.middleware.Logging;
import nahida.middleware.PathNormalization;
import nahida.middleware.Recovery;
import nahida.middleware.ServerHeader;
import nahida.util.Mounter;

public class Nahida {
  public static final String VERSION = "0.0.0";

  public String defaultMimeType = "text/plain";
  public Charset defaultCharset = Charset.defaultCharset();

  private Router router;
  private ArrayList<Middleware> global;

  Router router() {
    return router;
  }

  ArrayList<Middleware> global() {
    return global;
  }

  public Nahida() {
    router = new Router();
    global = new ArrayList<>();
  }

  public static Nahida simple() {
    var app = new Nahida();
    app.use(Logging.middleware())
        .use(Recovery.middleware())
        .use(PathNormalization.middleware())
        .use(DateHeader.middleware())
        .use(ServerHeader.middleware());
    return app;
  }

  private Handler handler() {
    return (req, resp) -> {
      var ctx = new Context(req, resp, this);
      ctx.next();
    };
  }

  public void run(String host, int port) throws Exception {
    var server = new Server();
    server.run(host, port, handler());
  }

  public void run(int port) throws Exception {
    run("localhost", port);
  }

  public Nahida use(Middleware... middlewares) {
    for (var middleware : middlewares) {
      global.add(middleware);
    }
    return this;
  }

  public Router group(String pattern) {
    return router.group(pattern);
  }

  public Nahida mount(String target, String source, int flags) {
    var mounter = new Mounter(target, source, flags);
    use(mounter.middleware());
    return this;
  }

  public Nahida mount(String target, String source) {
    mount(target, source, 0);
    return this;
  }

  public Nahida all(String pattern, Middleware... middlewares) {
    router.all(pattern, middlewares);
    return this;
  }

  public Nahida get(String pattern, Middleware... middlewares) {
    router.get(pattern, middlewares);
    return this;
  }

  public Nahida post(String pattern, Middleware... middlewares) {
    router.post(pattern, middlewares);
    return this;
  }

  public Nahida head(String pattern, Middleware... middlewares) {
    router.head(pattern, middlewares);
    return this;
  }

  public Nahida put(String pattern, Middleware... middlewares) {
    router.put(pattern, middlewares);
    return this;
  }

  public Nahida patch(String pattern, Middleware... middlewares) {
    router.patch(pattern, middlewares);
    return this;
  }

  public Nahida delete(String pattern, Middleware... middlewares) {
    router.delete(pattern, middlewares);
    return this;
  }

  public Nahida options(String pattern, Middleware... middlewares) {
    router.options(pattern, middlewares);
    return this;
  }

  public Nahida connect(String pattern, Middleware... middlewares) {
    router.connect(pattern, middlewares);
    return this;
  }

  public Nahida trace(String pattern, Middleware... middlewares) {
    router.trace(pattern, middlewares);
    return this;
  }
}
