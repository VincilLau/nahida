package nahida.param;

import java.util.regex.Pattern;

public class StrParam extends Param {
  private static final Pattern PATTERN_1 = Pattern.compile("^/<str:([A-Za-z_][A-Za-z_0-9]*)>$");
  private static final Pattern PATTERN_2 = Pattern.compile("^/<([A-Za-z_][A-Za-z_0-9]*)>$");

  private StrParam(String name) {
    super(name);
  }

  public static StrParam parse(String field) {
    var matcher = PATTERN_1.matcher(field);
    if (matcher.find()) {
      var name = matcher.group(1);
      return new StrParam(name);
    }

    matcher = PATTERN_2.matcher(field);
    if (matcher.find()) {
      var name = matcher.group(1);
      return new StrParam(name);
    } else {
      return null;
    }
  }

  @Override
  public Object match(String field) {
    if (field.length() == 1) {
      return null;
    }
    return field.substring(1);
  }
}
