import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

// --- State ---
class DesignState {
  final ThemeMode themeMode;
  final bool isLiquid;
  final String fontFamily;
  
  // Locked Settings
  final bool isExpressive = true;
  final String emojiFamily = 'AppleColorEmoji';

  const DesignState({
    required this.themeMode,
    required this.isLiquid,
    required this.fontFamily,
  });

  factory DesignState.initial() {
    return const DesignState(
      themeMode: ThemeMode.system,
      isLiquid: false,
      fontFamily: 'ProductSans', // Default font
    );
  }

  DesignState copyWith({
    ThemeMode? themeMode,
    bool? isLiquid,
    String? fontFamily,
  }) {
    return DesignState(
      themeMode: themeMode ?? this.themeMode,
      isLiquid: isLiquid ?? this.isLiquid,
      fontFamily: fontFamily ?? this.fontFamily,
    );
  }
}

// --- Provider ---
final designControllerProvider = StateNotifierProvider<DesignController, DesignState>((ref) {
  return DesignController();
});

class DesignController extends StateNotifier<DesignState> {
  static const _keyThemeMode = 'design_theme_mode';
  static const _keyIsLiquid = 'design_is_liquid';
  static const _keyFontFamily = 'design_font_family';

  DesignController() : super(DesignState.initial()) {
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    
    final modeIndex = prefs.getInt(_keyThemeMode) ?? 0;
    final isLiquid = prefs.getBool(_keyIsLiquid) ?? false;
    final fontFamily = prefs.getString(_keyFontFamily) ?? 'ProductSans';

    ThemeMode mode;
    switch (modeIndex) {
      case 1: mode = ThemeMode.light; break;
      case 2: mode = ThemeMode.dark; break;
      default: mode = ThemeMode.system;
    }

    state = state.copyWith(
      themeMode: mode,
      isLiquid: isLiquid,
      fontFamily: fontFamily,
    );
  }

  Future<void> setThemeMode(ThemeMode mode) async {
    state = state.copyWith(themeMode: mode);
    final prefs = await SharedPreferences.getInstance();
    int index = 0; // system
    if (mode == ThemeMode.light) index = 1;
    if (mode == ThemeMode.dark) index = 2;
    await prefs.setInt(_keyThemeMode, index);
  }

  Future<void> toggleLiquid(bool enabled) async {
    state = state.copyWith(isLiquid: enabled);
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_keyIsLiquid, enabled);
  }

  Future<void> setFontFamily(String family) async {
    state = state.copyWith(fontFamily: family);
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyFontFamily, family);
  }
}
