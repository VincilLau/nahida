package nahida.param;

import java.util.regex.Pattern;

public class UuidParam extends Param {
  private static final Pattern PATTERN = Pattern.compile("^/<uuid:([A-Za-z_][A-Za-z_0-9]*)>$");
  private static final Pattern UUID_PATTERN =
      Pattern.compile("^/[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$");

  private UuidParam(String name) {
    super(name);
  }

  public static UuidParam parse(String field) {
    var matcher = PATTERN.matcher(field);
    if (matcher.find()) {
      var name = matcher.group(1);
      return new UuidParam(name);
    } else {
      return null;
    }
  }

  @Override
  public Object match(String field) {
    var lower = field.toLowerCase();
    var matcher = UUID_PATTERN.matcher(lower);
    if (matcher.find()) {
      return lower.substring(1);
    }
    return null;
  }
}
