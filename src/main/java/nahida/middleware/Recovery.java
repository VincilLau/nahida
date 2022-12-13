package nahida.middleware;

import nahida.Middleware;
import nahida.except.Http400Exception;
import nahida.except.Http403Exception;
import nahida.except.Http404Exception;
import nahida.except.Http405Exception;
import nahida.http.Status;

public class Recovery {
  public static Middleware middleware() {
    return ctx -> {
      try {
        ctx.next();
      } catch (Http400Exception e) {
        var html = String.format("<h1>%s</h1>", e.toString());
        ctx.html(Status.BAD_REQUEST, html);
      } catch (Http403Exception e) {
        var html = String.format("<h1>%s</h1>", e.toString());
        ctx.html(Status.FORBIDDEN, html);
      } catch (Http404Exception e) {
        var html = String.format("<h1>%s</h1>", e.toString());
        ctx.html(Status.NOT_FOUND, html);
      } catch (Http405Exception e) {
        var html = String.format("<h1>%s</h1>", e.toString());
        ctx.html(Status.METHOD_NOT_ALLOWED, html);
      } catch (Exception e) {
        var html = String.format("<h1>%s</h1>", e.toString());
        ctx.html(Status.INTERNAL_SERVER_ERROR, html);
      }
    };
  }
}
