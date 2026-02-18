/// Sanitizes a string by removing invalid UTF-16 surrogate pairs.
///
/// Lone surrogates (0xD800-0xDFFF) that are not part of a valid pair
/// are replaced with the Unicode replacement character (U+FFFD).
/// This prevents rendering crashes from malformed contact data.
String sanitizeString(String value) {
  if (value.isEmpty) return value;
  final buffer = StringBuffer();
  for (int i = 0; i < value.length; i++) {
    final codeUnit = value.codeUnitAt(i);
    // Check for lone surrogates (0xD800-0xDFFF)
    if (codeUnit >= 0xD800 && codeUnit <= 0xDFFF) {
      // High surrogate (0xD800-0xDBFF) - check if followed by low surrogate
      if (codeUnit <= 0xDBFF && i + 1 < value.length) {
        final nextCodeUnit = value.codeUnitAt(i + 1);
        if (nextCodeUnit >= 0xDC00 && nextCodeUnit <= 0xDFFF) {
          // Valid surrogate pair - keep both
          buffer.writeCharCode(codeUnit);
          buffer.writeCharCode(nextCodeUnit);
          i++; // Skip the next code unit
          continue;
        }
      }
      // Lone surrogate - replace with replacement character
      buffer.write('\uFFFD');
    } else {
      buffer.writeCharCode(codeUnit);
    }
  }
  return buffer.toString();
}
