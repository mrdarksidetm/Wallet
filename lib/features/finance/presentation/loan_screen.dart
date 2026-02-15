import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/database/providers.dart';
import '../../../core/database/models/auxiliary_models.dart';

class LoanScreen extends ConsumerWidget {
  const LoanScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final loansStream = ref.watch(loanRepositoryProvider).watchAll();

    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Loans'),
          bottom: const TabBar(
            tabs: [Tab(text: 'Lent'), Tab(text: 'Borrowed')],
          ),
        ),
        body: StreamBuilder<List<Loan>>(
          stream: loansStream,
          builder: (context, snapshot) {
            if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());
            final loans = snapshot.data!;
            
            return TabBarView(
              children: [
                _LoanList(loans: loans.where((l) => l.type == LoanType.lent).toList(), ref: ref),
                _LoanList(loans: loans.where((l) => l.type == LoanType.borrowed).toList(), ref: ref),
              ],
            );
          },
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () => _showAddLoanDialog(context, ref),
          child: const Icon(Icons.add),
        ),
      ),
    );
  }

  void _showAddLoanDialog(BuildContext context, WidgetRef ref) {
    // Implementation omitted for brevity, similar to Budget but with Person selection (mocked or text input for now)
    // Providing basic impl:
    showDialog(context: context, builder: (_) => const AddLoanDialog());
  }
}

class _LoanList extends StatelessWidget {
  final List<Loan> loans;
  final WidgetRef ref;
  const _LoanList({required this.loans, required this.ref});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: loans.length,
      itemBuilder: (context, index) {
        final loan = loans[index];
        return ListTile(
          title: Text(loan.person.value?.name ?? 'Unknown'),
          subtitle: Text('\$${loan.amount} - ${loan.isPaid ? 'Paid' : 'Unpaid'}'),
          trailing: Checkbox(
            value: loan.isPaid,
            onChanged: (val) {
              ref.read(loanServiceProvider).markAsPaid(loan, val!);
            },
          ),
        );
      },
    );
  }
}

class AddLoanDialog extends ConsumerStatefulWidget {
  const AddLoanDialog({super.key});

  @override
  ConsumerState<AddLoanDialog> createState() => _AddLoanDialogState();
}

class _AddLoanDialogState extends ConsumerState<AddLoanDialog> {
  final _formKey = GlobalKey<FormState>();
  String _personName = '';
  double _amount = 0;
  LoanType _type = LoanType.lent;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('New Loan'),
      content: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextFormField(
              decoration: const InputDecoration(labelText: 'Person Name'),
              onSaved: (val) => _personName = val!,
            ),
            TextFormField(
              decoration: const InputDecoration(labelText: 'Amount'),
              keyboardType: TextInputType.number,
              onSaved: (val) => _amount = double.parse(val!),
            ),
            DropdownButtonFormField<LoanType>(
              value: _type,
              items: LoanType.values.map((t) => DropdownMenuItem(value: t, child: Text(t.name))).toList(),
              onChanged: (val) => setState(() => _type = val!),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
        ElevatedButton(
          onPressed: () async {
            if (_formKey.currentState!.validate()) {
              _formKey.currentState!.save();
              // Create a temporary person for now since Person CRUD is not full
              final person = Person()..name = _personName..createdAt = DateTime.now()..updatedAt = DateTime.now();
              await ref.read(isarProvider).value!.writeTxn(() async {
                await ref.read(isarProvider).value!.persons.put(person);
              });
              
              await ref.read(loanServiceProvider).addLoan(
                person: person,
                amount: _amount,
                type: _type,
              );
              if (mounted) Navigator.pop(context);
            }
          },
          child: const Text('Save'),
        ),
      ],
    );
  }
}
