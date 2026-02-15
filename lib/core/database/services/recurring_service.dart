import 'package:isar/isar.dart';
import '../models/auxiliary_models.dart';
import '../services/transaction_service.dart';

class RecurringService {
  final Isar isar;
  final TransactionService transactionService;

  RecurringService({required this.isar, required this.transactionService});

  /// Checks and processes due recurring transactions
  Future<void> checkRecurringTransactions() async {
    final now = DateTime.now();
    
    // Find active recurring entries where nextDate is in the past or today
    final dueRecurring = await isar.recurrings
        .filter()
        .isActiveEqualTo(true)
        .nextDateLessThan(now) // or equal
        .findAll();

    for (var recurring in dueRecurring) {
      await _processRecurring(recurring);
    }
  }

  Future<void> _processRecurring(Recurring recurring) async {
    final template = recurring.transaction.value;
    if (template == null) return;

    // Create the new transaction
    // We duplicate the logic from TransactionService.addTransaction but need to call it properly
    // Or we manually construct it here. Calling TransactionService is safer for atomic balance updates.
    
    try {
      final account = template.account.value;
      final category = template.category.value;
      
      if (account == null || category == null) return;

      await transactionService.addTransaction(
        amount: template.amount,
        date: DateTime.now(), // Execution date is now
        type: template.type,
        account: account,
        category: category,
        note: template.note != null ? '${template.note} (Recurring)' : '(Recurring)',
        transferAccount: template.transferAccount.value,
        tags: template.tags,
      );

      // Update next date
      await isar.writeTxn(() async {
        recurring.nextDate = _calculateNextDate(recurring.nextDate, recurring.frequency);
        if (recurring.endDate != null && recurring.nextDate.isAfter(recurring.endDate!)) {
          recurring.isActive = false;
        }
        await isar.recurrings.put(recurring);
      });

    } catch (e) {
      // Log error or handle failure (skip this one)
    }
  }

  DateTime _calculateNextDate(DateTime current, RecurrenceFrequency frequency) {
    switch (frequency) {
      case RecurrenceFrequency.daily:
        return current.add(const Duration(days: 1));
      case RecurrenceFrequency.weekly:
        return current.add(const Duration(days: 7));
      case RecurrenceFrequency.monthly:
         // Handle month overflow logic if needed, simple version for now
        return DateTime(current.year, current.month + 1, current.day);
      case RecurrenceFrequency.yearly:
        return DateTime(current.year + 1, current.month, current.day);
    }
  }
}
