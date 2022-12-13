package nahida.middleware;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import java.text.SimpleDateFormat;
import java.util.Date;
import nahida.Middleware;

public class Logging {
  public static Middleware middleware() {
    return ctx -> {
      var start = System.currentTimeMillis();
      ctx.next();
      var end = System.currentTimeMillis();

      var code = ctx.resp.status.code;
      var attr = Attribute.NONE();
      if (code >= 500) {
        attr = Attribute.BRIGHT_RED_TEXT();
      } else if (code >= 400) {
        attr = Attribute.BRIGHT_YELLOW_TEXT();
      } else if (code >= 300) {
        attr = Attribute.BRIGHT_MAGENTA_TEXT();
      } else if (code >= 200) {
        attr = Attribute.BRIGHT_GREEN_TEXT();
      }

      var sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
      var time = sdf.format(new Date());
      var result =
          String.format(
              "'%s' %d %3dms \"%s %s\"", ctx.ip(), code, (end - start), ctx.method(), ctx.path());

      System.out.printf(
          "%s %s\n",
          Ansi.colorize(time, Attribute.BRIGHT_CYAN_TEXT()), Ansi.colorize(result, attr));
    };
  }
}
