import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/theme/theme_provider.dart';

class AppIcon extends ConsumerWidget {
  final IconData materialIcon;
  final IconData cupertinoIcon;
  final Color? color;
  final double? size;

  const AppIcon(
    this.materialIcon, {
    super.key,
    required this.cupertinoIcon,
    this.color,
    this.size,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isLiquid = ref.watch(themeControllerProvider).isLiquid;
    
    // Animate the switch? For now simple switch.
    return Icon(
      isLiquid ? cupertinoIcon : materialIcon,
      color: color,
      size: size,
    );
  }
}
