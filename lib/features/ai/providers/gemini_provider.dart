import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/services/secure_storage_service.dart';
import '../services/gemini_service.dart';
import '../../core/database/repositories/transaction_repository.dart';

// --- API Key Management ---

final geminiApiKeyProvider = AsyncNotifierProvider<GeminiApiKeyNotifier, String?>(() {
  return GeminiApiKeyNotifier();
});

class GeminiApiKeyNotifier extends AsyncNotifier<String?> {
  @override
  Future<String?> build() async {
    final storage = ref.read(secureStorageServiceProvider);
    return await storage.getApiKey();
  }

  Future<void> saveKey(String key) async {
    state = const AsyncValue.loading();
    try {
      final storage = ref.read(secureStorageServiceProvider);
      await storage.saveApiKey(key);
      state = AsyncValue.data(key);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<void> deleteKey() async {
    state = const AsyncValue.loading();
    try {
      final storage = ref.read(secureStorageServiceProvider);
      await storage.deleteApiKey();
      state = const AsyncValue.data(null);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }
}

// --- Analysis State ---

final geminiAnalysisProvider = AsyncNotifierProvider<GeminiAnalysisNotifier, String?>(() {
  return GeminiAnalysisNotifier();
});

class GeminiAnalysisNotifier extends AsyncNotifier<String?> {
  @override
  Future<String?> build() async {
    return null; // Initial state is empty
  }

  Future<void> analyze() async {
    state = const AsyncValue.loading();
    try {
      final repository = ref.read(transactionRepositoryProvider);
      // Fetch latest 100 transactions
      final transactions = await repository.watchLatest(limit: 100).first;
      
      final service = ref.read(geminiServiceProvider);
      final result = await service.analyzeSpending(transactions);
      
      state = AsyncValue.data(result);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }
  
  void clear() {
      state = const AsyncValue.data(null);
  }
}
