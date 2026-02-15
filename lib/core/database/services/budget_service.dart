import 'package:isar/isar.dart';
import '../models/auxiliary_models.dart';
import '../models/category.dart';
import '../repositories/finance_repositories.dart';

class BudgetService {
  final Isar isar;
  final BudgetRepository budgetRepository;

  BudgetService({required this.isar, required this.budgetRepository});

  Future<void> addBudget({
    required double amount,
    required Category category,
    required BudgetPeriod period,
    required DateTime startDate,
    required DateTime endDate,
  }) async {
    await isar.writeTxn(() async {
      final budget = Budget()
        ..amount = amount
        ..period = period
        ..startDate = startDate
        ..endDate = endDate
        ..createdAt = DateTime.now()
        ..updatedAt = DateTime.now();
      
      budget.category.value = category;
      
      await isar.budgets.put(budget);
      await budget.category.save();
    });
  }

  Future<void> deleteBudget(Id id) async {
    await budgetRepository.delete(id);
  }

  // Calculate spent amount for a budget
  Future<double> getSpentAmount(Budget budget) async {
    // This requires querying transactions for the category within the budget period
    // We assume period is within start/end dates
    // For simplicity in this milestone, we sum all expenses in that category for the period
    final categoryId = budget.category.value?.id;
    if (categoryId == null) return 0.0;
    
    // We would technically query TransactionRepository here, but we can do a direct query for speed
    // import model to avoid circular dependency issues if possible, or pass it in.
    // However, clean architecture says we should probably use a provider or repository.
    // For now, assume we can access transactions collection via Isar.
    
    // Note: To properly implement this, we need to import TransactionModel and TransactionType
    // which effectively links this service to Transactions.
    return 0.0; // Placeholder until we link the Transaction Repo query logic or move this logic to a higher level use case
  }
}
