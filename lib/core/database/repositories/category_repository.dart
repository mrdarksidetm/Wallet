import 'package:isar/isar.dart';
import '../../database/models/category.dart';
import 'base_repository.dart';

class CategoryRepository extends BaseRepository<Category> {
  CategoryRepository(super.isar);

  Stream<List<Category>> watchAll() {
    return isar.categories.where().sortByName().watch(fireImmediately: true);
  }
  
  Stream<List<Category>> watchByType(CategoryType type) {
    return isar.categories.filter().typeEqualTo(type).sortByName().watch(fireImmediately: true);
  }
}
