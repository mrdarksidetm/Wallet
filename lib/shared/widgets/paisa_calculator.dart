import 'package:flutter/material.dart';

class PaisaCalculator extends StatelessWidget {
  final String amountString;
  final Function(String) onAmountChanged;
  final VoidCallback onSubmit;

  const PaisaCalculator({
    super.key,
    required this.amountString,
    required this.onAmountChanged,
    required this.onSubmit,
  });

  void _onKeyPress(String key) {
    if (key == 'C') {
      onAmountChanged('0');
    } else if (key == '<') {
      if (amountString.length > 1) {
        onAmountChanged(amountString.substring(0, amountString.length - 1));
      } else {
        onAmountChanged('0');
      }
    } else if (key == 'DONE') {
      onSubmit();
    } else {
      if (amountString == '0' && key != '.') {
        onAmountChanged(key);
      } else if (key == '.' && amountString.contains('.')) {
        return; // Prevent multiple decimals
      } else {
        // Limit decimal places to 2
        if (amountString.contains('.')) {
          final parts = amountString.split('.');
          if (parts.length > 1 && parts[1].length >= 2) {
            return;
          }
        }
        onAmountChanged(amountString + key);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Theme.of(context).colorScheme.surface,
      padding: const EdgeInsets.only(bottom: 24, top: 8),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Row(
            children: [
              _buildKey('1'), _buildKey('2'), _buildKey('3'), _buildKey('C', color: Colors.red),
            ],
          ),
          Row(
            children: [
              _buildKey('4'), _buildKey('5'), _buildKey('6'), _buildKey('<', icon: Icons.backspace_outlined),
            ],
          ),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 3,
                child: Column(
                  children: [
                    Row(children: [_buildKey('7'), _buildKey('8'), _buildKey('9')]),
                    Row(children: [_buildKey('.'), _buildKey('0'), _buildKey('00')]),
                  ],
                ),
              ),
              Expanded(
                flex: 1,
                child: _buildKey('DONE', 
                  icon: Icons.check, 
                  color: Theme.of(context).colorScheme.primary, 
                  textColor: Theme.of(context).colorScheme.onPrimary,
                  height: 120, // Taller button for DONE
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildKey(String label, {IconData? icon, Color? color, Color? textColor, double height = 60}) {
    return Expanded(
      child: Container(
        height: height,
        margin: const EdgeInsets.all(4),
        child: Material(
          color: color ?? Colors.transparent,
          borderRadius: BorderRadius.circular(16),
          clipBehavior: Clip.antiAlias,
          child: InkWell(
            onTap: () => _onKeyPress(label),
            child: Center(
              child: icon != null
                  ? Icon(icon, color: textColor ?? (color != null ? Colors.white : null))
                  : Text(
                      label,
                      style: TextStyle(
                        fontSize: label.length > 1 ? 18 : 24,
                        fontWeight: FontWeight.w500,
                        color: textColor ?? (color != null ? Colors.white : null),
                      ),
                    ),
            ),
          ),
        ),
      ),
    );
  }
}
