package nahida;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Cookie {
  public String name;
  public String value;

  public String path;
  public String domain;
  public Date expires;

  public Integer maxAge;
  public boolean secure;
  public boolean httpOnly;
  public String sameSite;

  public Cookie(String name, String value) {
    this.name = name;
    this.value = value;
    secure = false;
    httpOnly = false;
  }

  public Cookie(String name) {
    this(name, null);
  }

  public static Builder builder(String name) {
    return new Builder(name);
  }

  public static class Builder {
    private Cookie cookie;

    Builder(String name) {
      cookie = new Cookie(name);
    }

    public Cookie build() {
      return cookie;
    }

    public Builder value(String value) {
      cookie.value = value;
      return this;
    }

    public Builder path(String path) {
      cookie.path = path;
      return this;
    }

    public Builder domain(String domain) {
      cookie.domain = domain;
      return this;
    }

    public Builder expires(Date expires) {
      cookie.expires = expires;
      return this;
    }

    public Builder maxAge(int maxAge) {
      cookie.maxAge = maxAge;
      return this;
    }

    public Builder secure(boolean secure) {
      cookie.secure = secure;
      return this;
    }

    public Builder httpOnly(boolean httpOnly) {
      cookie.httpOnly = httpOnly;
      return this;
    }

    public Builder sameSite(String sameSite) {
      cookie.sameSite = sameSite;
      return this;
    }
  }

  @Override
  public String toString() {
    var builder = new StringBuffer();
    builder.append(name);
    if (value != null) {
      builder.append('=');
      builder.append(value);
    }
    if (path != null) {
      builder.append("; Path=");
      builder.append(path);
    }
    if (domain != null) {
      builder.append("; Domain=");
      builder.append(domain);
    }
    if (expires != null) {
      builder.append("; Expires=");
      var sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      builder.append(sdf.format(expires));
    }
    if (maxAge != null) {
      builder.append("; Max-Age=");
      builder.append(maxAge);
    }
    if (secure) {
      builder.append("; Secure");
    }
    if (httpOnly) {
      builder.append("; HttpOnly");
    }
    if (sameSite != null) {
      builder.append("; SameSite=");
      builder.append(sameSite);
    }
    return builder.toString();
  }
}
