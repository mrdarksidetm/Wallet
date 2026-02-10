import 'package:isar/isar.dart';

abstract class BaseRepository<T> {
  final Isar isar;

  BaseRepository(this.isar);

  Future<List<T>> getAll() async {
    return await isar.collection<T>().where().findAll();
  }

  Future<T?> getById(Id id) async {
    return await isar.collection<T>().get(id);
  }

  Future<Id> save(T item) async {
    return await isar.writeTxn(() async {
      return await isar.collection<T>().put(item);
    });
  }

  Future<bool> delete(Id id) async {
    return await isar.writeTxn(() async {
      return await isar.collection<T>().delete(id);
    });
  }
}
