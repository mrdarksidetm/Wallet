import 'package:isar/isar.dart';
import 'package:path_provider/path_provider.dart';
import 'models/account.dart';
import 'models/category.dart';
import 'models/transaction_model.dart';
import 'models/auxiliary_models.dart';

class IsarService {
  late Future<Isar> db;

  IsarService() {
    db = openDB();
  }

  Future<Isar> openDB() async {
    if (Isar.instanceNames.isEmpty) {
      final dir = await getApplicationDocumentsDirectory();
      return await Isar.open(
        [
          AccountSchema,
          CategorySchema,
          TransactionModelSchema,
          PersonSchema,
          PlaceSchema,
          BudgetSchema,
          LoanSchema,
          GoalSchema,
          RecurringSchema,
          LabelSchema,
        ],
        directory: dir.path,
        inspector: true,
      );
    }
    return Future.value(Isar.getInstance());
  }
}
