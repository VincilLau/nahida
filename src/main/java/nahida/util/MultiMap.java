package nahida.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class MultiMap<T> implements Iterable<Entry<String, ArrayList<T>>> {
  protected HashMap<String, ArrayList<T>> nameValues;

  public MultiMap() {
    nameValues = new HashMap<>();
  }

  public boolean isEmpty() {
    return nameValues.isEmpty();
  }

  public int size() {
    return nameValues.size();
  }

  public int count() {
    var result = 0;
    for (var v : nameValues.values()) {
      result += v.size();
    }
    return result;
  }

  public boolean contains(String name) {
    return nameValues.containsKey(name);
  }

  public T get(String name) {
    var values = nameValues.get(name);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

  public ArrayList<T> getAll(String name) {
    return nameValues.get(name);
  }

  public Set<String> names() {
    return nameValues.keySet();
  }

  public Set<Entry<String, ArrayList<T>>> entries() {
    return nameValues.entrySet();
  }

  public void add(String name, T value) {
    var values = nameValues.get(name);
    if (values == null) {
      values = new ArrayList<>();
      values.add(value);
      nameValues.put(name, values);
      return;
    }
    values.add(value);
  }

  public void remove(String name) {
    nameValues.remove(name);
  }

  public void clear() {
    nameValues.clear();
  }

  @Override
  public Iterator<Entry<String, ArrayList<T>>> iterator() {
    return nameValues.entrySet().iterator();
  }
}
