import 'package:flutter/material.dart';

class LiquidTheme {
  static const double glassBlur = 10.0;
  static const double glassOpacity = 0.2;
  static const double glassBorderOpacity = 0.5;
  static const double borderRadius = 24.0;

  static BoxDecoration getGlassDecoration({
    required BuildContext context,
    double? opacity,
    Color? color,
  }) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final baseColor = color ?? (isDark ? Colors.black : Colors.white);
    
    return BoxDecoration(
      color: baseColor.withOpacity(opacity ?? glassOpacity),
      borderRadius: BorderRadius.circular(borderRadius),
      border: Border.all(
        color: Colors.white.withOpacity(glassBorderOpacity),
        width: 1.5,
      ),
      boxShadow: [
        BoxShadow(
          color: Colors.black.withOpacity(0.1),
          blurRadius: 16,
          offset: const Offset(0, 8),
        ),
      ],
    );
  }
}
