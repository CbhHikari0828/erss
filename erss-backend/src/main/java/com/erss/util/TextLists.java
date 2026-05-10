package com.erss.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TextLists {
  private TextLists() {
  }

  public static List<String> split(String value) {
    if (value == null || value.isBlank()) {
      return List.of();
    }
    return Arrays.stream(value.split(","))
      .map(String::trim)
      .filter(item -> !item.isBlank())
      .toList();
  }

  public static String join(List<String> values) {
    if (values == null || values.isEmpty()) {
      return "";
    }
    return values.stream()
      .filter(item -> item != null && !item.isBlank())
      .map(String::trim)
      .collect(Collectors.joining(","));
  }
}
