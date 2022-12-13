package nahida.middleware;

import nahida.Nahida;
import nahida.Middleware;

public class ServerHeader {
  public static Middleware middleware() {
    return ctx -> {
      ctx.next();
      var value = "Nahida/" + Nahida.VERSION;
      ctx.addHeader("Server", value);
    };
  }
}
