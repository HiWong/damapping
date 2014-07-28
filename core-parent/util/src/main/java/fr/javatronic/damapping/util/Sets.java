package fr.javatronic.damapping.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Sets -
 *
 * @author Sébastien Lesaint
 */
public final class Sets {
  private Sets() {
    // prevents instantiation
  }

  public static <T> Set<T> of() {
    return new HashSet<T>();
  }

  public static <T> Set<T> of(T item) {
    HashSet<T> res = new HashSet<T>();
    res.add(item);
    return res;
  }

  public static <T> Set<T> of(T item1, T item2) {
    HashSet<T> res = new HashSet<T>();
    res.add(item1);
    res.add(item2);
    return res;
  }

  public static <T> Set<T> of(T item1, T item2, T item3) {
    HashSet<T> res = new HashSet<T>();
    res.add(item1);
    res.add(item2);
    res.add(item3);
    return res;
  }

  public static <T> Set<T> of(T item1, T item2, T item3, T item4) {
    HashSet<T> res = new HashSet<T>();
    res.add(item1);
    res.add(item2);
    res.add(item3);
    res.add(item4);
    return res;
  }

  @SafeVarargs
  public static <T> Set<T> of(T... items) {
    if (items.length == 0) {
      return Collections.emptySet();
    }

    Set<T> res = new HashSet<T>(items.length);
    for (T item : items) {
      res.add(item);
    }
    return res;
  }

  public static <T> Set<T> copyOf(Collection<T> items) {
    return new HashSet<T>(items);
  }
}
