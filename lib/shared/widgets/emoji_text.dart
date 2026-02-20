import 'package:flutter/material.dart';

class EmojiText extends StatelessWidget {
  final String text;
  final TextStyle? style;
  final TextAlign? textAlign;

  const EmojiText(
    this.text, {
    super.key,
    this.style,
    this.textAlign,
  });

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      textAlign: textAlign,
      style: (style ?? const TextStyle()).copyWith(
        fontFamilyFallback: const ['AppleColorEmoji'],
      ),
    );
  }
}
