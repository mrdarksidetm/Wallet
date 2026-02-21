import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../core/database/providers.dart';
import '../../../core/database/models/transaction_model.dart';

class StatisticsScreen extends ConsumerStatefulWidget {
  const StatisticsScreen({super.key});

  @override
  ConsumerState<StatisticsScreen> createState() => _StatisticsScreenState();
}

class _StatisticsScreenState extends ConsumerState<StatisticsScreen> {
  int _touchedIndex = -1;

  @override
  Widget build(BuildContext context) {
    final transactionsAsync = ref.watch(transactionsStreamProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Statistics')),
      body: transactionsAsync.when(
        data: (transactions) {
          if (transactions.isEmpty) return const Center(child: Text('Not enough data.'));
          
          final expenses = transactions.where((t) => t.type == TransactionType.expense).toList();
          if (expenses.isEmpty) return const Center(child: Text('No expenses to analyze.'));

          // Group by category
          final Map<String, double> categorySums = {};
          final Map<String, Color> categoryColors = {};
          
          for (final tx in expenses) {
            final cat = tx.category.value;
            if (cat != null) {
              categorySums[cat.name] = (categorySums[cat.name] ?? 0) + tx.amount;
              categoryColors[cat.name] = Color(int.parse(cat.color));
            }
          }

          final sortedKeys = categorySums.keys.toList()..sort((a,b) => categorySums[b]!.compareTo(categorySums[a]!));

          return ListView(
            padding: const EdgeInsets.all(24),
            children: [
              Text('Spending Breakdown', style: Theme.of(context).textTheme.titleLarge),
              const SizedBox(height: 32),
              SizedBox(
                height: 300,
                child: PieChart(
                  PieChartData(
                    pieTouchData: PieTouchData(
                      touchCallback: (FlTouchEvent event, pieTouchResponse) {
                        setState(() {
                          if (!event.isInterestedForInteractions ||
                              pieTouchResponse == null ||
                              pieTouchResponse.touchedSection == null) {
                            _touchedIndex = -1;
                            return;
                          }
                          _touchedIndex = pieTouchResponse.touchedSection!.touchedSectionIndex;
                        });
                      },
                    ),
                    borderData: FlBorderData(show: false),
                    sectionsSpace: 2,
                    centerSpaceRadius: 60,
                    sections: List.generate(sortedKeys.length, (i) {
                      final isTouched = i == _touchedIndex;
                      final fontSize = isTouched ? 22.0 : 14.0;
                      final radius = isTouched ? 70.0 : 60.0;
                      final name = sortedKeys[i];
                      final value = categorySums[name]!;
                      
                      return PieChartSectionData(
                        color: categoryColors[name],
                        value: value,
                        title: '\$${value.toStringAsFixed(0)}',
                        radius: radius,
                        titleStyle: TextStyle(
                          fontSize: fontSize,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                      );
                    }),
                  ),
                ).animate().scale(duration: 600.ms, curve: Curves.easeOutBack),
              ),
              const SizedBox(height: 48),
              Text('Top Categories', style: Theme.of(context).textTheme.titleLarge),
              const SizedBox(height: 16),
              ...sortedKeys.map((name) {
                final value = categorySums[name]!;
                return ListTile(
                  leading: CircleAvatar(backgroundColor: categoryColors[name]),
                  title: Text(name),
                  trailing: Text('\$${value.toStringAsFixed(2)}', style: const TextStyle(fontWeight: FontWeight.bold)),
                ).animate().fade().slideX();
              }),
            ],
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, _) => Center(child: Text('Error: $err')),
      ),
    );
  }
}
