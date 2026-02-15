import 'package:isar/isar.dart';
import '../models/account.dart';
import '../models/transaction_model.dart';
import '../repositories/account_repository.dart';
import '../repositories/transaction_repository.dart';

class StatisticsService {
  final Isar isar;
  final AccountRepository accountRepository;
  final TransactionRepository transactionRepository;

  StatisticsService({
    required this.isar,
    required this.accountRepository,
    required this.transactionRepository,
  });

  Future<double> getTotalBalance() async {
    final accounts = await isar.accounts.where().findAll();
    return accounts.fold<double>(0.0, (double sum, Account account) => sum + account.balance);
  }

  Stream<double> watchTotalBalance() {
    return isar.accounts.where().watch(fireImmediately: true).map((accounts) {
      return accounts.fold<double>(0.0, (double sum, Account account) => sum + account.balance);
    });
  }

  Future<double> getMonthlyIncome(DateTime date) async {
    final start = DateTime(date.year, date.month, 1);
    final end = DateTime(date.year, date.month + 1, 0, 23, 59, 59);
    
    return await isar.transactionModels
        .filter()
        .typeEqualTo(TransactionType.income)
        .dateBetween(start, end)
        .amountProperty()
        .sum();
  }

  Future<double> getMonthlyExpense(DateTime date) async {
    final start = DateTime(date.year, date.month, 1);
    final end = DateTime(date.year, date.month + 1, 0, 23, 59, 59);

    return await isar.transactionModels
        .filter()
        .typeEqualTo(TransactionType.expense)
        .dateBetween(start, end)
        .amountProperty()
        .sum();
  }

  Stream<Map<String, double>> watchMonthlyStats() {
    final now = DateTime.now();
    final start = DateTime(now.year, now.month, 1);
    final end = DateTime(now.year, now.month + 1, 0, 23, 59, 59);

    return isar.transactionModels
        .filter()
        .dateBetween(start, end)
        .watch(fireImmediately: true)
        .map((transactions) {
      double income = 0;
      double expense = 0;
      for (var tx in transactions) {
        if (tx.type == TransactionType.income) income += tx.amount;
        if (tx.type == TransactionType.expense) expense += tx.amount;
      }
      return {'income': income, 'expense': expense};
    });
  }
}
