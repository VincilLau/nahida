package nahida;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import nahida.except.Http400Exception;
import nahida.except.Http403Exception;
import nahida.except.Http404Exception;
import nahida.except.Http405Exception;
import nahida.http.Method;
import nahida.http.Request;
import nahida.http.Response;
import nahida.http.Status;
import nahida.util.MimeType;
import nahida.util.MultiMap;

public class Context {
  public Request req;
  public Response resp;
  public Nahida app;
  public HashMap<String, Object> state;

  private Middleware[] local;
  private int globalIndex;
  private int localIndex;

  Context(Request req, Response resp, Nahida app) {
    this.req = req;
    this.resp = resp;
    this.app = app;
    state = new HashMap<>();
    globalIndex = 0;
    localIndex = -1;
  }

  public void next() throws Exception {
    if (globalIndex != -1) {
      if (globalIndex < app.global().size()) {
        var middleware = app.global().get(globalIndex);
        globalIndex++;
        middleware.call(this);
        return;
      }
      globalIndex = -1;
    }

    if (localIndex != -1) {
      if (localIndex < local.length) {
        var middleware = local[localIndex];
        localIndex++;
        middleware.call(this);
        return;
      }
      return;
    }

    var middlewares = app.router().route(req.path, req.params);
    if (middlewares == null) {
      throw new Http404Exception(req.path);
    }
    local = middlewares.get(req.method);
    if (local == null) {
      local = middlewares.get(Method.ALL);
    }
    if (local == null) {
      throw new Http405Exception(req.method, req.path);
    }

    localIndex = 1;
    local[0].call(this);
  }

  public String ip() {
    return req.ip;
  }

  public Method method() {
    return req.method;
  }

  public String url() {
    return req.url;
  }

  public String path() {
    return req.path;
  }

  public MultiMap<String> headers() {
    return req.headers;
  }

  public String content() {
    return req.path;
  }

  public Object param(String name) {
    return req.params.get(name);
  }

  public Object defaultParam(String name, String defaultValue) {
    var value = param(name);
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

  public String query(String name) {
    return req.queries.get(name);
  }

  public ArrayList<String> queryAll(String name) {
    return req.queries.getAll(name);
  }

  public String defaultQuery(String name, String defaultValue) {
    var value = query(name);
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

  public String header(String name) {
    return req.headers.get(name);
  }

  public String defaultHeader(String name, String defaultValue) {
    var value = header(name);
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

  public ArrayList<String> headerAll(String name) {
    return req.headers.getAll(name);
  }

  public Cookie cookie(String name) {
    return req.cookies.get(name);
  }

  public ArrayList<Cookie> cookieAll(String name) {
    return req.cookies.getAll(name);
  }

  public <T> T bindParam(Class<T> type) throws Http400Exception {
    try {
      var mapper = new ObjectMapper();
      return mapper.convertValue(req.params, type);
    } catch (Exception e) {
      throw new Http400Exception(e);
    }
  }

  public <T> T bindQuery(Class<T> type) throws Http400Exception {
    try {
      var mapper = new ObjectMapper();
      var kvMap = new HashMap<String, String>();
      for (var name : req.queries.names()) {
        kvMap.put(name, req.queries.get(name));
      }
      return mapper.convertValue(kvMap, type);
    } catch (Exception e) {
      throw new Http400Exception(e);
    }
  }

  public <T> T bindHeader(Class<T> type) throws Http400Exception {
    try {
      var mapper = new ObjectMapper();
      var kvMap = new HashMap<String, String>();
      for (var name : req.headers.names()) {
        kvMap.put(name, req.headers.get(name));
      }
      return mapper.convertValue(kvMap, type);
    } catch (Exception e) {
      throw new Http400Exception(e);
    }
  }

  public <T> T bindJson(Class<T> type) throws Http400Exception {
    try {
      var mapper = new JsonMapper();
      return mapper.readValue(req.content, type);
    } catch (Exception e) {
      throw new Http400Exception(e);
    }
  }

  public void setStatus(Status status) {
    resp.status = status;
  }

  public void addHeader(String name, String value) {
    resp.headers.add(name, value);
  }

  public void addCookie(Cookie cookie) {
    resp.cookies.add(cookie);
  }

  public void send(Status status, byte[] content, String contentType) {
    resp.status = status;
    resp.headers.add("Content-Type", contentType);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void send(byte[] content, String contentType) {
    resp.headers.add("Content-Type", contentType);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void send(Status status, String text, Charset charset, String contentType) {
    resp.status = status;
    resp.headers.add("Content-Type", contentType);
    var content = text.getBytes(charset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void send(String text, Charset charset, String contentType) {
    resp.headers.add("Content-Type", contentType);
    var content = text.getBytes(charset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void send(String text, String contentType) {
    resp.headers.add("Content-Type", contentType);
    var content = text.getBytes(app.defaultCharset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void send(String text) {
    resp.headers.add("Content-Type", "text/plain");
    var content = text.getBytes(app.defaultCharset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void send(Status status, String text, String contentType) {
    resp.status = status;
    resp.headers.add("Content-Type", contentType);
    var content = text.getBytes(app.defaultCharset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void send(Status status, String text) {
    resp.status = status;
    resp.headers.add("Content-Type", "text/plain");
    var content = text.getBytes(app.defaultCharset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void html(Status status, String text, Charset charset) {
    resp.status = status;
    resp.headers.add("Content-Type", "text/html");
    var content = text.getBytes(charset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void html(String text, Charset charset) {
    resp.headers.add("Content-Type", "text/html");
    var content = text.getBytes(charset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void html(Status status, String text) {
    resp.status = status;
    resp.headers.add("Content-Type", "text/html");
    var content = text.getBytes(app.defaultCharset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void html(String text) {
    resp.headers.add("Content-Type", "text/html");
    var content = text.getBytes(app.defaultCharset);
    if (req.method.equals(Method.HEAD)) {
      addHeader("Content-Length", Integer.toString(content.length));
    } else {
      resp.content = content;
    }
  }

  public void json(Status status, Object obj) throws Exception {
    resp.status = status;
    var mapper = new JsonMapper();
    var bytes = mapper.writeValueAsBytes(obj);
    send(bytes, "application/json");
  }

  public void json(Object obj) throws Exception {
    var mapper = new JsonMapper();
    var bytes = mapper.writeValueAsBytes(obj);
    send(bytes, "application/json");
  }

  public void redirect(Status status, String url) {
    resp.status = status;
    resp.headers.add("Location", url);
    html(
        String.format(
            "The URL %s has been redirected to <a href=\"%s\">%s</a>", req.path, url, url));
  }

  public void redirect(String url) {
    redirect(Status.FOUND, url);
  }

  public void sendFile(String path, String contentType) throws Exception {
    try {
      if (contentType != null) {
        addHeader("Content-Type", contentType);
      }
      if (req.method.equals(Method.HEAD)) {
        var fileSize = (new File(path)).length();
        addHeader("Content-Length", Long.toString(fileSize));
      } else {
        resp.content = Files.readAllBytes(Paths.get(path));
      }
    } catch (AccessDeniedException e) {
      throw new Http403Exception(req.path);
    } catch (NoSuchFileException e) {
      throw new Http404Exception(req.path);
    }
  }

  public void sendFile(Status status, String path, String contentType) throws Exception {
    resp.status = status;
    sendFile(path, contentType);
  }

  public void sendFile(Status status, String path) throws Exception {
    resp.status = status;
    var contentType = MimeType.guess(path);
    if (contentType == null) {
      contentType = app.defaultMimeType;
    }
    sendFile(path, contentType);
  }

  public void sendFile(String path) throws Exception {
    var contentType = MimeType.guess(path);
    if (contentType == null) {
      contentType = app.defaultMimeType;
    }
    sendFile(path, contentType);
  }
}
