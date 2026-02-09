import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/theme/app_theme.dart';
import 'core/providers/theme_provider.dart';
import 'features/settings/presentation/theme_settings_screen.dart';

void main() {
  runApp(const ProviderScope(child: WalletApp()));
}

class WalletApp extends ConsumerWidget {
  const WalletApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeState = ref.watch(themeProvider);

    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Wallet',
      theme: AppTheme.getTheme(
        brightness: Brightness.light,
        fontFamily: themeState.fontFamily,
        isLiquid: themeState.isLiquid,
      ),
      darkTheme: AppTheme.getTheme(
        brightness: Brightness.dark,
        fontFamily: themeState.fontFamily,
        isLiquid: themeState.isLiquid,
      ),
      themeMode: themeState.themeMode,
      home: const ThemeSettingsScreen(),
    );
  }
}
