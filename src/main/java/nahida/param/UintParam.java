package nahida.param;

import java.util.regex.Pattern;

public class UintParam extends Param {
  private static final Pattern PATTERN = Pattern.compile("^/<uint:([A-Za-z_][A-Za-z_0-9]*)>$");

  private UintParam(String name) {
    super(name);
  }

  public static UintParam parse(String field) {
    var matcher = PATTERN.matcher(field);
    if (matcher.find()) {
      var name = matcher.group(1);
      return new UintParam(name);
    } else {
      return null;
    }
  }

  @Override
  public Object match(String field) {
    if (!Character.isDigit(field.codePointAt(1))) {
      return null;
    }

    try {
      return Long.valueOf(field.substring(1));
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
