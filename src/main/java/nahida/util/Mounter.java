package nahida.util;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import nahida.Context;
import nahida.Middleware;
import nahida.MountFlags;
import nahida.except.Http403Exception;
import nahida.except.Http404Exception;
import nahida.except.Http405Exception;
import nahida.http.Method;

public class Mounter {
  private String target;
  private String source;
  private int flags;

  public Mounter(String target, String source, int flags) {
    this.target = Path.normalize(target);
    this.source = source;
    this.flags = flags;
  }

  public Middleware middleware() {
    return ctx -> {
      try {
        mapping(ctx);
      } catch (AccessDeniedException e) {
        throw new Http403Exception(ctx.path());
      } catch (NoSuchFileException e) {
        throw new Http404Exception(ctx.path());
      }
    };
  }

  private void mapping(Context ctx) throws Exception {
    var targetPath = Path.normalize(ctx.path());
    if (!targetPath.startsWith(target)) {
      ctx.next();
      return;
    }

    if (!ctx.method().equals(Method.GET) && !ctx.method().equals(Method.HEAD)) {
      throw new Http405Exception(ctx.method(), targetPath);
    }

    var sourcePath = source + "/" + targetPath.substring(target.length());
    sendMappedFile(ctx, targetPath, sourcePath);
  }

  private void sendMappedFile(Context ctx, String targetPath, String sourcePath) throws Exception {
    var file = new File(sourcePath);
    if (!file.exists()) {
      throw new Http404Exception(targetPath);
    }
    if (file.isDirectory()) {
      if ((flags & MountFlags.NO_INDEX) != 0) {
        if ((flags & MountFlags.NO_DIR) != 0) {
          throw new Http404Exception(targetPath);
        }
        sendIndex(ctx, targetPath, sourcePath);
        return;
      }
      var indexPath = sourcePath + "/index.html";
      if ((new File(indexPath)).exists()) {
        ctx.sendFile(indexPath);
        return;
      }
      if ((flags & MountFlags.NO_DIR) != 0) {
        throw new Http404Exception(targetPath);
      }
      sendIndex(ctx, targetPath, sourcePath);
      return;
    }
    ctx.sendFile(sourcePath);
  }

  private void sendIndex(Context ctx, String targetPath, String sourcePath) {
    var builder = new StringBuilder();
    var title = String.format("Directory listing for %s", targetPath);
    builder.append("<!DOCTYPE html>\n");
    builder.append("<head>\n");
    builder.append(String.format("    <title>%s</title>\n", title));
    builder.append("    <style>\n");
    builder.append("        li {\n");
    builder.append("            font-size: 20px;\n");
    builder.append("            padding: 3px;\n");
    builder.append("        }\n");
    builder.append("    </style>\n");
    builder.append("</head>\n");
    builder.append("<body>\n");
    builder.append(String.format("    <h1>%s</h1>\n", title));
    builder.append("    <hr/>\n");
    builder.append("    <ul>\n");
    builder.append(
        String.format(
            "        <li><a href=\"%s\">..</a></li>\n", Path.normalize(targetPath + "/..")));
    var files = (new File(sourcePath)).listFiles();
    if (files == null) {
      files = new File[0];
    }
    for (var file : files) {
      var name = file.getName();
      if (file.isDirectory()) {
        name += '/';
      }
      builder.append(
          String.format(
              "        <li><a href=\"%s\">%s</a></li>\n",
              Path.normalize(targetPath + "/" + name), name));
    }
    builder.append("    </ul>\n");
    builder.append("    <hr/>\n");
    builder.append("</body>\n");
    ctx.html(builder.toString());
  }
}
