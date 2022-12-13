package nahida.util;

import java.util.ArrayList;
import nahida.except.AbsPathException;

public class Path {
  public static ArrayList<String> split(String path) throws AbsPathException {
    if (path.isEmpty() || path.codePointAt(0) != '/') {
      throw new AbsPathException(path);
    }

    path += '/';

    var fields = new ArrayList<String>();
    var len = path.codePointCount(0, path.length());
    var start = 0;

    for (var i = 1; i < len; i++) {
      if (path.codePointAt(i) == '/') {
        var field = path.substring(start, i);
        if (field.equals("/") || field.equals("/.")) {
        } else if (field.equals("/..")) {
          if (!fields.isEmpty()) {
            fields.remove(fields.size() - 1);
          }
        } else {
          fields.add(field);
        }
        start = i;
      }
    }

    return fields;
  }

  public static String normalize(String path) throws AbsPathException {
    var fields = split(path);
    if (fields.isEmpty()) {
      return "/";
    }

    var builder = new StringBuffer();
    for (var field : fields) {
      builder.append(field);
    }
    return builder.toString();
  }
}
