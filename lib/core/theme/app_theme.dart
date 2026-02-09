import 'package:flutter/material.dart';
import 'app_colors.dart';

class AppTheme {
  static const String fontFamilyProductSans = 'ProductSans';
  static const String fontFamilySFPro = 'SFProDisplay';
  static const List<String> fontFamilyFallback = ['AppleColorEmoji'];

  static ThemeData getTheme({
    required Brightness brightness,
    required String fontFamily,
    bool isLiquid = false,
  }) {
    final colorScheme = ColorScheme.fromSeed(
      seedColor: AppColors.primary,
      brightness: brightness,
    );

    // Liquid Theme adjustments
    final scaffoldBackgroundColor = isLiquid
        ? Colors.transparent // Transparent to show background gradient
        : colorScheme.background;

    return ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: colorScheme,
      fontFamily: fontFamily,
      fontFamilyFallback: fontFamilyFallback,
      scaffoldBackgroundColor: scaffoldBackgroundColor,
      appBarTheme: AppBarTheme(
        backgroundColor: isLiquid ? Colors.transparent : null,
        elevation: 0,
        centerTitle: true,
      ),
      cardTheme: CardThemeData(
        elevation: isLiquid ? 0 : 2,
        surfaceTintColor : Colors.transparent,
        // color: isLiquid ? Colors.white.withOpacity(0.1) : null,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(24),
        ),
      ),
    );
  }
}
