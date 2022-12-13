package nahida.http;

public class Status {
  public static final Status CONTINUE = new Status(100, "Continue");
  public static final Status SWITCHING_PROTOCOLS = new Status(101, "Switching Protocols");
  public static final Status EARLY_HINTS = new Status(103, "Early Hints");

  public static final Status OK = new Status(200, "OK");
  public static final Status CREATED = new Status(201, "Created");
  public static final Status ACCEPTED = new Status(202, "Accepted");
  public static final Status NON_AUTHORITATIVE_INFORMATION =
      new Status(203, "Non-Authoritative Information");
  public static final Status NO_CONTENT = new Status(204, "No Content");
  public static final Status RESET_CONTENT = new Status(205, "Reset Content");
  public static final Status PARTIAL_CONTENT = new Status(206, "Partial Content");

  public static final Status MULTIPLE_CHOICE = new Status(300, "Multiple Choice");
  public static final Status MOVED_PERMANENTLY = new Status(301, "Moved Permanently");
  public static final Status FOUND = new Status(302, "Found");
  public static final Status SEE_OTHER = new Status(303, "See Other");
  public static final Status NOT_MODIFIED = new Status(304, "Not Modified");
  public static final Status TEMPORARY_REDIRECT = new Status(307, "Temporary Redirect");
  public static final Status PERMANENT_REDIRECT = new Status(308, "Permanent Redirect");

  public static final Status BAD_REQUEST = new Status(400, "Bad Request");
  public static final Status UNAUTHORIZED = new Status(401, "Unauthorized");
  public static final Status FORBIDDEN = new Status(403, "Forbidden");
  public static final Status NOT_FOUND = new Status(404, "Not Found");
  public static final Status METHOD_NOT_ALLOWED = new Status(405, "Method Not Allowed");
  public static final Status NOT_ACCEPTABLE = new Status(406, "Not Acceptable");
  public static final Status PROXY_AUTHENTICATION_REQUIRED =
      new Status(407, "Proxy Authentication Required");
  public static final Status REQUEST_TIMEOUT = new Status(408, "Request Timeout");
  public static final Status CONFLICT = new Status(409, "Conflict");
  public static final Status GONE = new Status(410, "GONE");
  public static final Status LENGTH_REQUIRED = new Status(411, "Length Required");
  public static final Status PRECONDITION_FAILED = new Status(412, "Precondition Failed");
  public static final Status PAYLOAD_TOO_LARGE = new Status(413, "Payload Too Large");
  public static final Status URI_TOO_LONG = new Status(414, "URI Too Long");
  public static final Status UNSUPPORTED_MEDIA_TYPE = new Status(415, "Unsupported Media Type");
  public static final Status RANGE_NOT_SATISFIABLE = new Status(416, "Range Not Satisfiable");
  public static final Status EXPECTATION_FAILED = new Status(417, "Expectation Failed");
  public static final Status IM_A_TEAPOT = new Status(418, "I'm a teapot");
  public static final Status UPGRADE_REQUIRED = new Status(426, "Upgrade Required");
  public static final Status PRECONDITION_REQUIRED = new Status(428, "Precondition Required");
  public static final Status TOO_MANY_REQUESTS = new Status(429, "Too Many Requests");
  public static final Status REQUEST_HEADER_FIELDS_TOO_LARGE =
      new Status(431, "Request Header Fields Too Large");
  public static final Status UNAVAILABLE_FOR_LEGAL_REASONS =
      new Status(451, "Unavailable For Legal Reasons");

  public static final Status INTERNAL_SERVER_ERROR = new Status(500, "Internal Server Error");
  public static final Status NOT_IMPLEMENTED = new Status(501, "Not Implemented");
  public static final Status BAD_GATEWAY = new Status(502, "Bad Gateway");
  public static final Status SERVICE_UNAVAILABLE = new Status(503, "Service Unavailable");
  public static final Status GATEWAY_TIMEOUT = new Status(504, "Gateway Timeout");
  public static final Status HTTP_VERSION_NOT_SUPPORTED =
      new Status(505, "HTTP Version Not Supported");
  public static final Status VARIANT_ALSO_NEGOTIATES = new Status(506, "Variant Also Negotiates");
  public static final Status NOT_EXTENDED = new Status(510, "Not Extended");
  public static final Status NETWORK_AUTHENTICATION_REQUIRED =
      new Status(511, "Network Authentication Required");

  public int code;
  public String reason;

  private Status(int code, String reason) {
    this.code = code;
    this.reason = reason;
  }
}
