import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../theme/app_theme.dart';

// --- State ---
class ThemeState {
  final ThemeMode themeMode;
  final String fontFamily;
  final bool isLiquid;

  const ThemeState({
    required this.themeMode,
    required this.fontFamily,
    required this.isLiquid,
  });

  factory ThemeState.initial() {
    return const ThemeState(
      themeMode: ThemeMode.system,
      fontFamily: AppTheme.fontFamilySFPro,
      isLiquid: false,
    );
  }

  ThemeState copyWith({
    ThemeMode? themeMode,
    String? fontFamily,
    bool? isLiquid,
  }) {
    return ThemeState(
      themeMode: themeMode ?? this.themeMode,
      fontFamily: fontFamily ?? this.fontFamily,
      isLiquid: isLiquid ?? this.isLiquid,
    );
  }
}

// --- Provider ---
final themeProvider = StateNotifierProvider<ThemeNotifier, ThemeState>((ref) {
  return ThemeNotifier();
});

class ThemeNotifier extends StateNotifier<ThemeState> {
  final _storage = const FlutterSecureStorage();

  static const _keyThemeMode = 'theme_mode';
  static const _keyFontFamily = 'font_family';
  static const _keyIsLiquid = 'is_liquid';

  ThemeNotifier() : super(ThemeState.initial()) {
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final modeStr = await _storage.read(key: _keyThemeMode);
    final fontStr = await _storage.read(key: _keyFontFamily);
    final liquidStr = await _storage.read(key: _keyIsLiquid);

    ThemeMode mode = ThemeMode.system;
    if (modeStr == 'light') mode = ThemeMode.light;
    if (modeStr == 'dark') mode = ThemeMode.dark;

    final String font = fontStr ?? AppTheme.fontFamilySFPro;
    final bool isLiquid = liquidStr == 'true';

    state = state.copyWith(
      themeMode: mode,
      fontFamily: font,
      isLiquid: isLiquid,
    );
  }

  Future<void> setThemeMode(ThemeMode mode) async {
    state = state.copyWith(themeMode: mode);
    await _storage.write(key: _keyThemeMode, value: mode.name);
  }

  Future<void> setFontFamily(String family) async {
    state = state.copyWith(fontFamily: family);
    await _storage.write(key: _keyFontFamily, value: family);
  }

  Future<void> toggleLiquid(bool isLiquid) async {
    state = state.copyWith(isLiquid: isLiquid);
    await _storage.write(key: _keyIsLiquid, value: isLiquid.toString());
  }
}
