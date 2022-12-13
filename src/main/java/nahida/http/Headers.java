package nahida.http;

import java.util.ArrayList;
import nahida.util.MultiMap;

public class Headers extends MultiMap<String> {
  @Override
  public boolean contains(String name) {
    name = capitalizeName(name);
    return nameValues.containsKey(name);
  }

  @Override
  public String get(String name) {
    name = capitalizeName(name);
    var values = nameValues.get(name);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

  @Override
  public ArrayList<String> getAll(String name) {
    name = capitalizeName(name);
    return nameValues.get(name);
  }

  @Override
  public void add(String name, String value) {
    name = capitalizeName(name);
    var values = nameValues.get(name);
    if (values == null) {
      values = new ArrayList<>();
      values.add(value);
      nameValues.put(name, values);
      return;
    }
    values.add(value);
  }

  @Override
  public void remove(String name) {
    name = capitalizeName(name);
    nameValues.remove(name);
  }

  private static String capitalizeName(String name) {
    var lowerName = name.toLowerCase();
    var builder = new StringBuilder();
    builder.append(Character.toUpperCase(lowerName.charAt(0)));

    for (var i = 1; i < lowerName.length(); i++) {
      if ((lowerName.charAt(i - 1) == '-')) {
        builder.append(Character.toUpperCase(lowerName.charAt(i)));
      } else {
        builder.append(lowerName.charAt(i));
      }
    }

    return builder.toString();
  }
}
