package nahida.http;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.ArrayList;
import nahida.Cookie;

public class Response {
  public Status status;
  public Headers headers;
  public ArrayList<Cookie> cookies;
  public byte[] content;

  Response() {
    status = Status.OK;
    headers = new Headers();
    cookies = new ArrayList<>();
  }

  DefaultFullHttpResponse ToDefaultFullHttpResponse() {
    var resp =
        new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, new HttpResponseStatus(status.code, status.reason));
    for (var header : headers) {
      resp.headers().add(header.getKey(), header.getValue());
    }

    for (var cookie : cookies) {
      resp.headers().add("Set-Cookie", cookie.toString());
    }

    int len = 0;
    if (content != null) {
      len = content.length;
      resp.content().writeBytes(content);
    }
    if (!resp.headers().contains("Content-Length")) {
      resp.headers().add("Content-Length", Integer.toString(len));
    }

    return resp;
  }
}
