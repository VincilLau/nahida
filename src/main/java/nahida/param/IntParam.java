package nahida.param;

import java.util.regex.Pattern;

public class IntParam extends Param {
  private static final Pattern PATTERN = Pattern.compile("^/<int:([A-Za-z_][A-Za-z_0-9]*)>$");

  private IntParam(String name) {
    super(name);
  }

  public static IntParam parse(String field) {
    var matcher = PATTERN.matcher(field);
    if (matcher.find()) {
      var name = matcher.group(1);
      return new IntParam(name);
    } else {
      return null;
    }
  }

  @Override
  public Object match(String field) {
    try {
      return Long.valueOf(field.substring(1));
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
