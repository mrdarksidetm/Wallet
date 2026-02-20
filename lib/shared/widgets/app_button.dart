import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/theme/theme_provider.dart';
import 'glass_surface.dart';

class AppButton extends ConsumerWidget {
  final Widget child;
  final VoidCallback? onPressed;
  
  const AppButton({
    super.key,
    required this.child,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isLiquid = ref.watch(themeControllerProvider).isLiquid;

    if (isLiquid) {
      return GlassSurface(
        onTap: onPressed,
        borderRadius: BorderRadius.circular(100), // Pill shape for glass button
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
        child: DefaultTextStyle(
          style: Theme.of(context).textTheme.labelLarge!.copyWith(
            fontWeight: FontWeight.bold,
          ),
          child: Center(child: child),
        ),
      );
    }

    return FilledButton(
      onPressed: onPressed,
      child: child,
    );
  }
}
