package nahida.middleware;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import nahida.Middleware;

public class DateHeader {
  public static Middleware middleware() {
    return ctx -> {
      ctx.next();
      var sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      var value = sdf.format(new Date());
      ctx.addHeader("Date", value);
    };
  }
}
