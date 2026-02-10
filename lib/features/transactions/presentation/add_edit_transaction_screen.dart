import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../../core/database/providers.dart';
import '../../../core/database/models/transaction_model.dart';
import '../../../core/database/models/account.dart';
import '../../../core/database/models/category.dart';

class AddEditTransactionScreen extends ConsumerStatefulWidget {
  final TransactionModel? transaction;

  const AddEditTransactionScreen({super.key, this.transaction});

  @override
  ConsumerState<AddEditTransactionScreen> createState() => _AddEditTransactionScreenState();
}

class _AddEditTransactionScreenState extends ConsumerState<AddEditTransactionScreen> {
  final _formKey = GlobalKey<FormState>();
  late double _amount;
  late DateTime _date;
  late TransactionType _type;
  String? _note;
  Account? _selectedAccount;
  Category? _selectedCategory;
  Account? _selectedTransferAccount;

  @override
  void initState() {
    super.initState();
    if (widget.transaction != null) {
      _amount = widget.transaction!.amount;
      _date = widget.transaction!.date;
      _type = widget.transaction!.type;
      _note = widget.transaction!.note;
      _selectedAccount = widget.transaction!.account.value;
      _selectedCategory = widget.transaction!.category.value;
      _selectedTransferAccount = widget.transaction!.transferAccount.value;
    } else {
      _amount = 0.0;
      _date = DateTime.now();
      _type = TransactionType.expense;
    }
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    _formKey.currentState!.save();

    if (_selectedAccount == null || _selectedCategory == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select account and category')),
      );
      return;
    }

    final service = ref.read(transactionServiceProvider);

    try {
      if (widget.transaction == null) {
        await service.addTransaction(
          amount: _amount,
          date: _date,
          type: _type,
          account: _selectedAccount!,
          category: _selectedCategory!,
          note: _note,
          transferAccount: _selectedTransferAccount,
        );
      } else {
        final updatedTx = TransactionModel()
          ..id = widget.transaction!.id
          ..amount = _amount
          ..date = _date
          ..type = _type
          ..note = _note
          ..createdAt = widget.transaction!.createdAt;
        
        updatedTx.account.value = _selectedAccount;
        updatedTx.category.value = _selectedCategory;
        updatedTx.transferAccount.value = _selectedTransferAccount;

        await service.updateTransaction(widget.transaction!, updatedTx);
      }
      if (mounted) Navigator.pop(context);
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    }
  }

  Future<void> _delete() async {
    if (widget.transaction == null) return;
    
    final service = ref.read(transactionServiceProvider);
    await service.deleteTransaction(widget.transaction!);
    if (mounted) Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    final accountsAsync = ref.watch(accountsStreamProvider);
    final categoriesAsync = ref.watch(categoriesStreamProvider);

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.transaction == null ? 'Add Transaction' : 'Edit Transaction'),
        actions: [
          if (widget.transaction != null)
            IconButton(
              icon: const Icon(Icons.delete),
              onPressed: _delete,
            ),
        ],
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // Type Selector
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: TransactionType.values.map((type) {
                return ChoiceChip(
                  label: Text(type.name.toUpperCase()),
                  selected: _type == type,
                  onSelected: (selected) {
                    if (selected) setState(() => _type = type);
                  },
                );
              }).toList(),
            ),
            const SizedBox(height: 16),
            // Amount
            TextFormField(
              initialValue: _amount == 0 ? '' : _amount.toString(),
              decoration: const InputDecoration(labelText: 'Amount', prefixText: '\$'),
              keyboardType: TextInputType.number,
              validator: (val) => (val == null || double.tryParse(val) == null || double.parse(val) <= 0) ? 'Enter valid amount' : null,
              onSaved: (val) => _amount = double.parse(val!),
            ),
            const SizedBox(height: 16),
            // Account
            accountsAsync.when(
              data: (accounts) => DropdownButtonFormField<Account>(
                value: accounts.any((a) => a.id == _selectedAccount?.id) 
                    ? accounts.firstWhere((a) => a.id == _selectedAccount?.id)
                    : null,
                items: accounts.map((a) => DropdownMenuItem(value: a, child: Text('${a.name} (\$${a.balance.toStringAsFixed(2)})'))).toList(),
                onChanged: (val) => setState(() => _selectedAccount = val),
                decoration: const InputDecoration(labelText: 'Account'),
              ),
              loading: () => const LinearProgressIndicator(),
              error: (err, stack) => Text('Error loading accounts'),
            ),
            if (_type == TransactionType.transfer) ...[
              const SizedBox(height: 16),
              accountsAsync.when(
                data: (accounts) => DropdownButtonFormField<Account>(
                  value: accounts.any((a) => a.id == _selectedTransferAccount?.id) 
                      ? accounts.firstWhere((a) => a.id == _selectedTransferAccount?.id)
                      : null,
                  items: accounts.map((a) => DropdownMenuItem(value: a, child: Text('${a.name} (\$${a.balance.toStringAsFixed(2)})'))).toList(),
                  onChanged: (val) => setState(() => _selectedTransferAccount = val),
                  decoration: const InputDecoration(labelText: 'To Account'),
                ),
                loading: () => const LinearProgressIndicator(),
                error: (err, stack) => Text('Error loading accounts'),
              ),
            ],
            const SizedBox(height: 16),
            // Category
            categoriesAsync.when(
              data: (categories) {
                final filtered = categories.where((c) => _type == TransactionType.transfer ? true : c.type.name == _type.name).toList();
                return DropdownButtonFormField<Category>(
                  value: filtered.any((Category c) => c.id == _selectedCategory?.id) 
                      ? filtered.firstWhere((Category c) => c.id == _selectedCategory?.id)
                      : null,
                  items: filtered.map<DropdownMenuItem<Category>>((Category c) => DropdownMenuItem<Category>(value: c, child: Text(c.name))).toList(),
                  onChanged: (val) => setState(() => _selectedCategory = val),
                  decoration: const InputDecoration(labelText: 'Category'),
                );
              },
              loading: () => const LinearProgressIndicator(),
              error: (err, stack) => Text('Error loading categories'),
            ),
            const SizedBox(height: 16),
            // Date
            ListTile(
              title: const Text('Date'),
              subtitle: Text(DateFormat.yMMMd().format(_date)),
              onTap: () async {
                final picked = await showDatePicker(
                  context: context,
                  initialDate: _date,
                  firstDate: DateTime(2000),
                  lastDate: DateTime(2100),
                );
                if (picked != null) setState(() => _date = picked);
              },
            ),
            const SizedBox(height: 16),
            // Note
            TextFormField(
              initialValue: _note,
              decoration: const InputDecoration(labelText: 'Note'),
              onSaved: (val) => _note = val,
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: _save,
              child: const Text('Save Transaction'),
            ),
          ],
        ),
      ),
    );
  }
}
