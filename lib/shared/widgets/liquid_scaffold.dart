import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/providers/theme_provider.dart';
import '../../core/theme/app_colors.dart';

class LiquidScaffold extends ConsumerWidget {
  final Widget body;
  final PreferredSizeWidget? appBar;
  final Widget? floatingActionButton;
  final Widget? bottomNavigationBar;

  const LiquidScaffold({
    super.key,
    required this.body,
    this.appBar,
    this.floatingActionButton,
    this.bottomNavigationBar,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeState = ref.watch(themeProvider);
    final isLiquid = themeState.isLiquid;
    final isDark = Theme.of(context).brightness == Brightness.dark;

    if (!isLiquid) {
      return Scaffold(
        appBar: appBar,
        body: body,
        floatingActionButton: floatingActionButton,
        bottomNavigationBar: bottomNavigationBar,
      );
    }

    // Liquid Glass Background
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: appBar,
      body: Stack(
        children: [
          // Background Gradient
          Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: isDark
                    ? AppColors.liquidUnknown
                    : [const Color(0xFFE0C3FC), const Color(0xFF8EC5FC)],
              ),
            ),
          ),
          // Content
          SafeArea(child: body),
        ],
      ),
      floatingActionButton: floatingActionButton,
      bottomNavigationBar: bottomNavigationBar,
    );
  }
}
