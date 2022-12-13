package nahida;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import nahida.except.AbsPathException;
import nahida.http.Method;
import nahida.param.FloatParam;
import nahida.param.IntParam;
import nahida.param.Param;
import nahida.param.StrParam;
import nahida.param.UintParam;
import nahida.param.UuidParam;
import nahida.util.Path;

public class Router {
  private HashMap<Method, Middleware[]> middlewaresMap;
  private HashMap<String, Router> plains;
  private ArrayList<Entry<Param, Router>> params;

  Router() {
    plains = new HashMap<>();
    params = new ArrayList<>();
  }

  private void add(ArrayList<String> fields, int depth, Method method, Middleware[] middlewares) {
    if (depth == fields.size()) {
      if (middlewaresMap == null) {
        middlewaresMap = new HashMap<>();
      }
      middlewaresMap.put(method, middlewares);
      return;
    }

    var current = fields.get(depth);
    Param param = IntParam.parse(current);
    if (param == null) {
      param = UintParam.parse(current);
    }
    if (param == null) {
      param = FloatParam.parse(current);
    }
    if (param == null) {
      param = UuidParam.parse(current);
    }
    if (param == null) {
      param = StrParam.parse(current);
    }
    if (param != null) {
      var router = new Router();
      params.add(new SimpleEntry<Param, Router>(param, router));
      router.add(fields, depth + 1, method, middlewares);
      return;
    }

    if (!plains.containsKey(current)) {
      plains.put(current, new Router());
    }
    plains.get(current).add(fields, depth + 1, method, middlewares);
  }

  private void add(String pattern, Method method, Middleware[] middlewares)
      throws AbsPathException {
    if (middlewares.length == 0) {
      return;
    }
    var fields = Path.split(pattern);
    add(fields, 0, method, middlewares);
  }

  private Router group(ArrayList<String> fields, int depth) {
    if (depth == fields.size()) {
      return this;
    }

    var current = fields.get(depth);
    Param param = IntParam.parse(current);
    if (param == null) {
      param = UuidParam.parse(current);
    }
    if (param == null) {
      param = StrParam.parse(current);
    }
    if (param != null) {
      var router = new Router();
      params.add(new SimpleEntry<Param, Router>(param, router));
      return router.group(fields, depth + 1);
    }

    if (!plains.containsKey(current)) {
      plains.put(current, new Router());
    }
    return plains.get(current).group(fields, depth + 1);
  }

  public Router group(String pattern) throws AbsPathException {
    var fields = Path.split(pattern);
    return group(fields, 0);
  }

  private HashMap<Method, Middleware[]> route(
      ArrayList<String> fields, int depth, HashMap<String, Object> paramMap) {
    if (depth == fields.size()) {
      return middlewaresMap;
    }

    var current = fields.get(depth);
    var plain = plains.get(current);
    if (plain != null) {
      return plain.route(fields, depth + 1, paramMap);
    }

    for (var entry : params) {
      var param = entry.getKey();
      var router = entry.getValue();
      var paramValue = param.match(current);
      if (paramValue != null) {
        paramMap.put(param.name(), paramValue);
        return router.route(fields, depth + 1, paramMap);
      }
    }

    return null;
  }

  HashMap<Method, Middleware[]> route(String path, HashMap<String, Object> params)
      throws AbsPathException {
    var fields = Path.split(path);
    return route(fields, 0, params);
  }

  public Router all(String pattern, Middleware... middlewares) {
    add(pattern, Method.ALL, middlewares);
    return this;
  }

  public Router get(String pattern, Middleware... middlewares) {
    add(pattern, Method.GET, middlewares);
    return this;
  }

  public Router post(String pattern, Middleware... middlewares) {
    add(pattern, Method.POST, middlewares);
    return this;
  }

  public Router head(String pattern, Middleware... middlewares) {
    add(pattern, Method.HEAD, middlewares);
    return this;
  }

  public Router put(String pattern, Middleware... middlewares) {
    add(pattern, Method.PUT, middlewares);
    return this;
  }

  public Router patch(String pattern, Middleware... middlewares) {
    add(pattern, Method.PATCH, middlewares);
    return this;
  }

  public Router delete(String pattern, Middleware... middlewares) {
    add(pattern, Method.DELETE, middlewares);
    return this;
  }

  public Router options(String pattern, Middleware... middlewares) {
    add(pattern, Method.OPTIONS, middlewares);
    return this;
  }

  public Router connect(String pattern, Middleware... middlewares) {
    add(pattern, Method.CONNECT, middlewares);
    return this;
  }

  public Router trace(String pattern, Middleware... middlewares) {
    add(pattern, Method.TRACE, middlewares);
    return this;
  }
}
