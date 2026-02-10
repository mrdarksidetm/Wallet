import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:isar/isar.dart';
import 'isar_service.dart';
import 'repositories/account_repository.dart';
import 'repositories/category_repository.dart';
import 'repositories/transaction_repository.dart';
import 'services/transaction_service.dart';
import 'services/seed_service.dart';

// --- Database Provider ---
final isarServiceProvider = Provider<IsarService>((ref) => IsarService());

final isarProvider = FutureProvider<Isar>((ref) async {
  final service = ref.watch(isarServiceProvider);
  return await service.db;
});

// --- Repository Providers ---
final accountRepositoryProvider = Provider<AccountRepository>((ref) {
  final isar = ref.watch(isarProvider).value!;
  return AccountRepository(isar);
});

final categoryRepositoryProvider = Provider<CategoryRepository>((ref) {
  final isar = ref.watch(isarProvider).value!;
  return CategoryRepository(isar);
});

final transactionRepositoryProvider = Provider<TransactionRepository>((ref) {
  final isar = ref.watch(isarProvider).value!;
  return TransactionRepository(isar);
});

// --- Service Providers ---
final transactionServiceProvider = Provider<TransactionService>((ref) {
  final isar = ref.watch(isarProvider).value!;
  final trxRepo = ref.watch(transactionRepositoryProvider);
  final accRepo = ref.watch(accountRepositoryProvider);
  return TransactionService(
    isar: isar,
    transactionRepository: trxRepo,
    accountRepository: accRepo,
  );
});

final seedServiceProvider = Provider<SeedService>((ref) {
  final isar = ref.watch(isarProvider).value!;
  return SeedService(isar);
});

// --- Stream Providers ---
final accountsStreamProvider = StreamProvider<List<Account>>((ref) {
  final repo = ref.watch(accountRepositoryProvider);
  return repo.watchAll();
});

final categoriesStreamProvider = StreamProvider<List<Category>>((ref) {
  final repo = ref.watch(categoryRepositoryProvider);
  return repo.watchAll();
});

final transactionsStreamProvider = StreamProvider<List<TransactionModel>>((ref) {
  final repo = ref.watch(transactionRepositoryProvider);
  return repo.watchLatest();
});
