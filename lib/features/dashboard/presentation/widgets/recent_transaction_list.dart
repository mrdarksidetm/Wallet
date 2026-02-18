import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../../../core/database/providers.dart';
import '../../../../core/database/models/transaction_model.dart';
import '../../../../core/design/widgets/skeleton_widgets.dart';

class RecentTransactions extends ConsumerWidget {
  final VoidCallback onSeeAll;

  const RecentTransactions({super.key, required this.onSeeAll});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final transactionsAsync = ref.watch(transactionsStreamProvider);

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'Recent Transactions',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              TextButton(
                onPressed: onSeeAll,
                child: const Text('See All'),
              ),
            ],
          ),
        ),
        transactionsAsync.when(
          data: (transactions) {
            final recent = transactions.take(5).toList();
            if (recent.isEmpty) {
              return const Padding(
                padding: EdgeInsets.all(16.0),
                child: Text('No recent transactions'),
              );
            }
            return ListView.separated(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: recent.length,
              separatorBuilder: (_, __) => const Divider(height: 1, indent: 16, endIndent: 16),
              itemBuilder: (context, index) {
                final tx = recent[index];
                final category = tx.category.value;
                  return ListTile(
                    leading: Hero(
                      tag: 'cat_icon_${tx.id}',
                      child: CircleAvatar(
                        backgroundColor: Color(int.parse(category?.color ?? '0xFF9E9E9E')),
                        child: const Icon(Icons.category, color: Colors.white, size: 20),
                      ),
                    ),
                    title: Text(category?.name ?? 'Unknown'),
                    subtitle: Text(DateFormat.yMMMd().format(tx.date)),
                    trailing: Text(
                      '${tx.type == TransactionType.expense ? '-' : '+'}\$${tx.amount.toStringAsFixed(2)}',
                      style: TextStyle(
                        color: tx.type == TransactionType.expense ? Colors.red : Colors.green,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  );
              },
            );
          },
          loading: () => const SkeletonList(itemCount: 5, itemHeight: 72),
          error: (err, _) => Container(
            margin: const EdgeInsets.all(16),
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.red.withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: Colors.red.withOpacity(0.3)),
            ),
            child: Row(
              children: [
                const Icon(Icons.error_outline, color: Colors.red),
                const SizedBox(width: 12),
                Expanded(child: Text('Error: $err', style: const TextStyle(color: Colors.red))),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
