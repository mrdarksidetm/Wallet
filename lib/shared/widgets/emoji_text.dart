import 'package:flutter/material.dart';
import '../../core/design/app_design.dart';

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
        fontFamily: AppDesign.fontEmoji,
        fontFamilyFallback: [AppDesign.fontEmoji],
      ),
    );
  }
}
