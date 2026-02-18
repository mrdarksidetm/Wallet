import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

final secureStorageServiceProvider = Provider<SecureStorageService>((ref) {
  return SecureStorageService();
});

class SecureStorageService {
  final FlutterSecureStorage _storage;
  static const String _apiKeyKey = 'gemini_api_key';

  SecureStorageService() : _storage = const FlutterSecureStorage();

  /// Saves the API key securely.
  Future<void> saveApiKey(String key) async {
    await _storage.write(key: _apiKeyKey, value: key);
  }

  /// Retrieves the API key. Returns null if not found.
  Future<String?> getApiKey() async {
    return await _storage.read(key: _apiKeyKey);
  }

  /// Deletes the API key.
  Future<void> deleteApiKey() async {
    await _storage.delete(key: _apiKeyKey);
  }

  /// Checks if an API key exists.
  Future<bool> hasApiKey() async {
    final key = await getApiKey();
    return key != null && key.isNotEmpty;
  }
}
