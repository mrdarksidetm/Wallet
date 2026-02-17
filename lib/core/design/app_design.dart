import 'package:flutter/material.dart';

class AppDesign {
  // --- Constants ---
  static const String fontProductSans = 'ProductSans';
  static const String fontSFPro = 'SFProDisplay';
  static const String fontEmoji = 'AppleColorEmoji';

  // --- M3 Duration & Curves ---
  static const Duration durationShort = Duration(milliseconds: 200);
  static const Duration durationStandard = Duration(milliseconds: 300);
  static const Duration durationLong = Duration(milliseconds: 500);
  static const Curve curveStandard = Curves.easeOutCubic;

  // --- M3 Shapes ---
  static const ShapeBorder shapeSmall = RoundedRectangleBorder(
    borderRadius: BorderRadius.all(Radius.circular(12)),
  );
  static const ShapeBorder shapeMedium = RoundedRectangleBorder(
    borderRadius: BorderRadius.all(Radius.circular(16)),
  );
  static const ShapeBorder shapeLarge = RoundedRectangleBorder(
    borderRadius: BorderRadius.all(Radius.circular(28)),
  );

  // --- Theme Generator ---
  static ThemeData getThemeData({
    required Brightness brightness,
    required String fontFamily,
  }) {
    // Expressive Seed Color (Violet/Purple base)
    const seedColor = Color(0xFF6750A4); 

    final colorScheme = ColorScheme.fromSeed(
      seedColor: seedColor,
      brightness: brightness,
      dynamicSchemeVariant: DynamicSchemeVariant.fidelity, // Expressive fidelity
    );

    return ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: colorScheme,
      fontFamily: fontFamily,
      fontFamilyFallback: const [fontEmoji],
      
      // Card
      // cardTheme: CardTheme(
      //   elevation: 2,
      //   shape: shapeMedium,
      //   clipBehavior: Clip.antiAlias,
      //   margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      // ),

      // List Tile
      listTileTheme: const ListTileThemeData(
        shape: shapeMedium,
        contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      ),
    );
  }
}
