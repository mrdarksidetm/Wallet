import 'package:isar/isar.dart';
import '../../database/models/transaction_model.dart';
import 'base_repository.dart';

class TransactionRepository extends BaseRepository<TransactionModel> {
  TransactionRepository(super.isar);

  Stream<List<TransactionModel>> watchLatest({int limit = 50}) {
    return isar.transactionModels.where().sortByDateDesc().limit(limit).watch(fireImmediately: true);
  }
  
  // NOTE: Business logic for saving transactions (updating balance) is in Service,
  // but this repository handles the raw specific queries.
}
