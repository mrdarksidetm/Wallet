import 'package:flutter/material.dart';
import 'dart:math' as math;

/// A custom Donut Chart widget for Flutter that matches the Wallet Compose implementation.
/// Uses [CustomPainter] for native performance and zero dependencies.
class DonutChart extends StatefulWidget {
  final double income;
  final double expense;
  final String currencySymbol;

  const DonutChart({
    super.key,
    required this.income,
    required this.expense,
    this.currencySymbol = '₹',
  });

  @override
  State<DonutChart> createState() => _DonutChartState();
}

class _DonutChartState extends State<DonutChart> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );

    // Spring-like effect using a curved animation
    _animation = CurvedAnimation(
      parent: _controller,
      curve: Curves.elasticOut,
    );

    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final total = widget.income + widget.expense;
    final netBalance = widget.income - widget.expense;

    return SizedBox(
      width: 200,
      height: 200,
      child: Stack(
        alignment: Alignment.Center,
        children: [
          AnimatedBuilder(
            animation: _animation,
            builder: (context, child) {
              return CustomPaint(
                size: const Size(200, 200),
                painter: DonutChartPainter(
                  income: widget.income,
                  expense: widget.expense,
                  progress: _animation.value,
                ),
              );
            },
          ),
          Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Net Balance',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: Theme.of(context).colorScheme.onSurfaceVariant,
                    ),
              ),
              Text(
                '${widget.currencySymbol}${netBalance.toStringAsFixed(2)}',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: netBalance >= 0 ? Colors.green : Colors.red,
                    ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class DonutChartPainter extends CustomPainter {
  final double income;
  final double expense;
  final double progress;

  DonutChartPainter({
    required this.income,
    required this.expense,
    required this.progress,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final double strokeWidth = 24.0;
    final double total = income + expense;
    final Offset center = Offset(size.width / 2, size.height / 2);
    final double radius = (size.width - strokeWidth) / 2;
    final Rect rect = Rect.fromCircle(center: center, radius: radius);

    final Paint basePaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    if (total == 0) {
      canvas.drawCircle(
        center,
        radius,
        basePaint..color = Colors.grey.withOpacity(0.3),
      );
      return;
    }

    final double incomeSweep = (income / total) * 2 * math.pi * progress;
    final double expenseSweep = (expense / total) * 2 * math.pi * progress;

    // Draw Income Arc (Green)
    canvas.drawArc(
      rect,
      -math.pi / 2, // Start at 12 o'clock
      incomeSweep,
      false,
      basePaint..color = const Color(0xFF4CAF50),
    );

    // Draw Expense Arc (Red)
    canvas.drawArc(
      rect,
      -math.pi / 2 + incomeSweep,
      expenseSweep,
      false,
      basePaint..color = const Color(0xFFF44336),
    );
  }

  @override
  bool shouldRepaint(covariant DonutChartPainter oldDelegate) {
    return oldDelegate.progress != progress ||
        oldDelegate.income != income ||
        oldDelegate.expense != expense;
  }
}

/// Category Spending Data Model for Flutter
class CategorySpending {
  final String category;
  final double amount;
  final double percentage;

  CategorySpending({
    required this.category,
    required this.amount,
    required this.percentage,
  });
}

/// Spending Breakdown Widget for Flutter
class SpendingBreakdown extends StatelessWidget {
  final List<CategorySpending> spendingList;
  final String currencySymbol;

  const SpendingBreakdown({
    super.key,
    required this.spendingList,
    this.currencySymbol = '₹',
  });

  @override
  Widget build(BuildContext context) {
    if (spendingList.isEmpty) {
      return const Padding(
        padding: EdgeInsets.symmetric(vertical: 32.0),
        child: Text('No expenses recorded this month.'),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 16.0),
          child: Text(
            'Spending Breakdown',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
          ),
        ),
        ListView.separated(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: spendingList.length,
          separatorBuilder: (context, index) => const SizedBox(height: 8),
          itemBuilder: (context, index) {
            final item = spendingList[index];
            return Card(
              color: Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.3),
              elevation: 0,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            item.category,
                            style: const TextStyle(fontWeight: FontWeight.bold),
                          ),
                          const SizedBox(height: 8),
                          ClipRRect(
                            borderRadius: BorderRadius.circular(4),
                            child: LinearProgressIndicator(
                              value: item.percentage,
                              backgroundColor: Colors.grey.withOpacity(0.2),
                              color: const Color(0xFFF44336),
                              minHeight: 8,
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(width: 16),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        Text(
                          '$currencySymbol${item.amount.toStringAsFixed(2)}',
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                        Text(
                          '${(item.percentage * 100).toInt()}%',
                          style: Theme.of(context).textTheme.bodySmall,
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ],
    );
  }
}
