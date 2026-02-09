import 'dart:ui';
import 'package:flutter/material.dart';
import '../../core/theme/liquid_theme.dart';

class LiquidContainer extends StatelessWidget {
  final Widget child;
  final double? width;
  final double? height;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;
  final VoidCallback? onTap;

  const LiquidContainer({
    super.key,
    required this.child,
    this.width,
    this.height,
    this.padding,
    this.margin,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      height: height,
      margin: margin,
      child: ClipRRect(
        borderRadius: BorderRadius.circular(LiquidTheme.borderRadius),
        child: BackdropFilter(
          filter: ImageFilter.blur(
            sigmaX: LiquidTheme.glassBlur,
            sigmaY: LiquidTheme.glassBlur,
          ),
          child: GestureDetector(
            onTap: onTap,
            child: Container(
              padding: padding,
              decoration: LiquidTheme.getGlassDecoration(context: context),
              child: child,
            ),
          ),
        ),
      ),
    );
  }
}
