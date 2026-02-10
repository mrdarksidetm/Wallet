import 'package:isar/isar.dart';
import 'account.dart';
import 'category.dart';

part 'transaction_model.g.dart'; // Naming as transaction_model to avoid conflict with generic names

@collection
class TransactionModel {
  Id id = Isar.autoIncrement;

  double amount = 0.0;

  @Index()
  late DateTime date;

  String? note;

  @Enumerated(EnumType.name)
  late TransactionType type;

  // Relations using IsarLink
  final account = IsarLink<Account>();
  
  final category = IsarLink<Category>();
  
  // For transfers only
  final transferAccount = IsarLink<Account>();

  List<String> tags = [];

  // For recurring instances
  bool isRecurringInstance = false;
  int? originalRecurringId;

  late DateTime createdAt;

  late DateTime updatedAt;

  bool isDeleted = false;
}

enum TransactionType {
  income,
  expense,
  transfer,
}
