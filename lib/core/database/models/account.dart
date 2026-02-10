import 'package:isar/isar.dart';

part 'account.g.dart';

@collection
class Account {
  Id id = Isar.autoIncrement;

  late String name;

  @Enumerated(EnumType.name)
  late AccountType type;

  double balance = 0.0;

  late String color; // Hex string

  late String icon; // IconData codepoint or asset path

  late DateTime createdAt;

  late DateTime updatedAt;
  
  // Soft delete for future-proofing
  bool isDeleted = false;
}

enum AccountType {
  cash,
  bank,
  creditCard,
  wallet,
  investment,
  other,
}
