import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../../../core/database/providers.dart';
import '../../../../core/database/models/transaction_model.dart';

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
                  leading: CircleAvatar(
                    backgroundColor: Color(int.parse(category?.color ?? '0xFF9E9E9E')),
                    child: const Icon(Icons.category, color: Colors.white, size: 20),
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
          loading: () => const LinearProgressIndicator(),
          error: (err, _) => Padding(
            padding: const EdgeInsets.all(16),
            child: Text('Error: $err'),
          ),
        ),
      ],
    );
  }
}
