import 'package:isar/isar.dart';
import '../../database/models/transaction_model.dart';
import 'base_repository.dart';

class TransactionRepository extends BaseRepository<TransactionModel> {
  TransactionRepository(super.isar);

  Stream<List<TransactionModel>> watchLatest({int limit = 50}) {
    return isar.transactionModels.where().sortByDateDesc().limit(limit).watch(fireImmediately: true);
  }

  Future<List<TransactionModel>> search({
    String? query,
    DateTime? startDate,
    DateTime? endDate,
    TransactionType? type,
    List<int>? accountIds,
    List<int>? categoryIds,
  }) async {
    var q = isar.transactionModels.where().sortByDateDesc();

    if (startDate != null && endDate != null) {
      q = q.filter().dateBetween(startDate, endDate).sortByDateDesc();
    }

    if (type != null) {
      q = q.filter().typeEqualTo(type).sortByDateDesc();
    }
    
    // Note: Isar filters on links (account/category) are tricky in simple queries without links logic
    // For now, we fetch and filter in memory if IDs provided, valid for local DB size.
    // Or we use deeper link queries if needed. simpler => memory filter for MVP.
    
    var results = await q.findAll();
    
    if (query != null && query.isNotEmpty) {
      final lower = query.toLowerCase();
      results = results.where((t) => (t.note?.toLowerCase().contains(lower) ?? false)).toList();
    }
    
    if (accountIds != null && accountIds.isNotEmpty) {
      results = results.where((t) => accountIds.contains(t.account.value?.id)).toList();
    }
    
    if (categoryIds != null && categoryIds.isNotEmpty) {
      results = results.where((t) => categoryIds.contains(t.category.value?.id)).toList();
    }
    
    return results;
  }
}
