package nahida.param;

import java.util.regex.Pattern;

public class FloatParam extends Param {
  private static final Pattern PATTERN = Pattern.compile("^/<float:([A-Za-z_][A-Za-z_0-9]*)>$");

  private FloatParam(String name) {
    super(name);
  }

  public static FloatParam parse(String field) {
    var matcher = PATTERN.matcher(field);
    if (matcher.find()) {
      var name = matcher.group(1);
      return new FloatParam(name);
    } else {
      return null;
    }
  }

  @Override
  public Object match(String field) {
    try {
      return Double.valueOf(field.substring(1));
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
