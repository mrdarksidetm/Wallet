import 'package:flutter/material.dart';

class PaisaCard extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;
  final Color? color;
  final double elevation;
  final double borderRadius;
  final VoidCallback? onTap;

  const PaisaCard({
    super.key,
    required this.child,
    this.padding = const EdgeInsets.all(16.0),
    this.margin = const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
    this.color,
    this.elevation = 0, // Paisa mostly uses flat, tonal cards
    this.borderRadius = 28.0, // High border radius is standard in M3/Paisa
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    Widget cardChild = Padding(
      padding: padding ?? EdgeInsets.zero,
      child: child,
    );

    if (onTap != null) {
      cardChild = InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(borderRadius),
        child: cardChild,
      );
    }

    return Card(
      elevation: elevation,
      margin: margin,
      color: color ?? theme.colorScheme.surfaceContainerHighest, // Standard M3 tonal color
      shadowColor: Colors.transparent,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(borderRadius),
      ),
      clipBehavior: Clip.antiAlias,
      child: cardChild,
    );
  }
}
