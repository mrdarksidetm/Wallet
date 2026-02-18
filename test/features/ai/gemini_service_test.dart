import 'package:flutter_test/flutter_test.dart';
import 'package:wallet/core/database/models/transaction_model.dart';
import 'package:wallet/core/database/models/category.dart';
import 'package:wallet/core/database/models/account.dart';
import 'package:wallet/core/services/secure_storage_service.dart';
import 'package:wallet/features/ai/services/gemini_service.dart';
import 'package:isar/isar.dart';

// Manual Mock for SecureStorageService
class MockSecureStorageService extends SecureStorageService {
  String? _apiKey;

  @override
  Future<String?> getApiKey() async => _apiKey;

  @override
  Future<void> saveApiKey(String key) async => _apiKey = key;
}

void main() {
  group('GeminiService', () {
    late GeminiService geminiService;
    late MockSecureStorageService mockStorage;

    setUp(() {
      mockStorage = MockSecureStorageService();
      geminiService = GeminiService(mockStorage);
    });

    test('analyzeSpending throws exception if API key is missing', () async {
      // Analyze with empty transactions (API key check happens first)
      await expectLater(
        () => geminiService.analyzeSpending([]),
        throwsA(isA<Exception>().having((e) => e.toString(), 'message', contains('API Key not found'))),
      );
    });

    test('analyzeSpending returns valid message if transaction list is empty', () async {
      await mockStorage.saveApiKey('fake_key');
      final result = await geminiService.analyzeSpending([]);
      expect(result, contains('No transactions found'));
    });

    // We cannot easily test the actual API call without mocking GenerativeModel (which is a 3rd party class)
    // or making GeminiService wrapper injectable.
    // However, we verified the pre-checks.
  });
}
