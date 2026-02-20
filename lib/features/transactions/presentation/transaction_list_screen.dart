import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../../core/database/providers.dart';
import '../../../core/database/models/transaction_model.dart';
import '../../search/presentation/search_screen.dart';
import '../../../shared/widgets/paisa_list_tile.dart';
import 'add_edit_transaction_screen.dart';

class TransactionListScreen extends ConsumerWidget {
  const TransactionListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final transactionsAsync = ref.watch(transactionsStreamProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Transactions'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const SearchScreen())),
          ),
        ],
      ),
      body: transactionsAsync.when(
        data: (transactions) {
          if (transactions.isEmpty) {
            return const Center(child: Text('No transactions yet.'));
          }

          return ListView.builder(
            itemCount: transactions.length,
            itemBuilder: (context, index) {
              final tx = transactions[index];
              final account = tx.account.value;
              final category = tx.category.value;

              return PaisaListTile(
                title: category?.name ?? 'Unknown',
                subtitle: '${account?.name ?? 'Unknown'} • ${DateFormat.yMMMd().format(tx.date)}',
                amount: '${tx.type == TransactionType.expense ? '-' : '+'}${tx.amount.toStringAsFixed(2)}',
                amountColor: tx.type == TransactionType.expense ? Colors.red : Colors.green,
                icon: Icons.category, // Replace with dynamic icon if available in Category model
                iconColor: Colors.white,
                iconBackgroundColor: Color(int.parse(category?.color ?? '0xFF9E9E9E')),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => AddEditTransactionScreen(transaction: tx),
                    ),
                  );
                },
              );
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
      floatingActionButton: Hero(
        tag: 'fab_add',
        child: FloatingActionButton(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const AddEditTransactionScreen(),
            ),
          );
        },
        child: const Icon(Icons.add),
        ),
      ),
    );
  }
}
