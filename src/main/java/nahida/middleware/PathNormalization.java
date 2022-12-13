package nahida.middleware;

import nahida.Middleware;
import nahida.util.Path;

public class PathNormalization {
  public static Middleware middleware() {
    return ctx -> {
      ctx.req.path = Path.normalize(ctx.path());
      ctx.next();
    };
  }
}
