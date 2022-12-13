package nahida.http;

import io.netty.handler.codec.http.FullHttpRequest;
import java.net.URI;
import java.util.HashMap;
import nahida.Cookie;
import nahida.util.MultiMap;

public class Request {
  public String ip;
  public Method method;
  public String url;
  public String path;
  public HashMap<String, Object> params;
  public MultiMap<String> queries;
  public Headers headers;
  public MultiMap<Cookie> cookies;
  public byte[] content;

  private Request() {
    params = new HashMap<>();
    queries = new MultiMap<String>();
    headers = new Headers();
  }

  Request(FullHttpRequest req, String ip) throws Exception {
    this();

    this.ip = ip;
    method = new Method(req.method().toString());

    url = req.uri();
    var uri = new URI(url);
    path = uri.getPath();
    parseQueries(uri.getQuery());

    for (var header : req.headers().entries()) {
      headers.add(header.getKey(), header.getValue());
    }

    cookies = parseCookies(req.headers().get("Cookie"));

    var len = req.content().writerIndex();
    content = new byte[len];
    req.content().readerIndex(0);
    req.content().readBytes(content);
  }

  private void parseQueries(String query) {
    if (query == null) {
      return;
    }

    query += '&';

    var nameBuilder = new StringBuilder();
    var valueBuilder = new StringBuilder();
    var parsingName = true;
    var len = query.codePointCount(0, query.length());
    for (int i = 0; i < len; i++) {
      var cp = query.codePointAt(i);
      if (parsingName) {
        if (cp == '=') {
          parsingName = false;
          continue;
        }
        nameBuilder.appendCodePoint(cp);
        continue;
      }

      if (cp == '&') {
        var name = nameBuilder.toString();
        var value = valueBuilder.toString();
        nameBuilder.setLength(0);
        valueBuilder.setLength(0);
        queries.add(name, value);
        parsingName = true;
        continue;
      }
      valueBuilder.appendCodePoint(cp);
    }
  }

  public static MultiMap<Cookie> parseCookies(String headerValue) {
    var cookies = new MultiMap<Cookie>();
    if (headerValue == null || headerValue.isEmpty()) {
      return cookies;
    }

    headerValue += "; ";

    var len = headerValue.codePointCount(0, headerValue.length());
    var parsingName = true;
    var nameBuilder = new StringBuilder();
    var valueBuilder = new StringBuilder();

    for (var i = 0; i < len; i++) {
      var cp = headerValue.codePointAt(i);
      if (parsingName) {
        if (cp == '=') {
          parsingName = false;
        } else {
          nameBuilder.appendCodePoint(cp);
        }
        continue;
      }

      if (cp == ';') {
        var name = nameBuilder.toString();
        var value = valueBuilder.toString();
        nameBuilder.setLength(0);
        valueBuilder.setLength(0);
        cookies.add(name, new Cookie(name, value));
        parsingName = true;
        continue;
      }

      if (cp == ' ') {
        continue;
      }

      valueBuilder.appendCodePoint(cp);
    }
    return cookies;
  }
}
