import 'package:google_generative_ai/google_generative_ai.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/database/models/transaction_model.dart';
import '../../core/services/secure_storage_service.dart';

final geminiServiceProvider = Provider<GeminiService>((ref) {
  final storage = ref.watch(secureStorageServiceProvider);
  return GeminiService(storage);
});

class GeminiService {
  final SecureStorageService _storage;
  static const String _modelName = 'gemini-1.5-flash';

  GeminiService(this._storage);

  Future<String> analyzeSpending(List<TransactionModel> transactions) async {
    final apiKey = await _storage.getApiKey();
    if (apiKey == null || apiKey.isEmpty) {
      throw Exception('API Key not found. Please add it in Settings.');
    }

    if (transactions.isEmpty) {
      return "No transactions found to analyze. Add some spending data first.";
    }

    try {
      final model = GenerativeModel(
        model: _modelName,
        apiKey: apiKey,
        generationConfig: GenerationConfig(
          temperature: 0.7,
          maxOutputTokens: 800,
        ),
        systemInstruction: Content.text(
          "You are a professional financial advisor. Provide structured, actionable advice.",
        ),
      );

      // Limit to last 100 transactions to save tokens/latency
      final recentTransactions = transactions.take(100).toList();
      
      final prompt = _buildPrompt(recentTransactions);
      final content = [Content.text(prompt)];
      
      final response = await model.generateContent(content);
      
      final text = response.text;
      if (text == null || text.isEmpty) {
        throw Exception('Received empty response from AI.');
      }
      
      return text;
    } on GenerativeAIException catch (e) {
      if (e.message.contains('API key')) {
        throw Exception('Invalid API Key. Please check your settings.');
      }
      throw Exception('AI Error: ${e.message}');
    } catch (e) {
      throw Exception('Failed to connect to AI service: $e');
    }
  }

  String _buildPrompt(List<TransactionModel> transactions) {
    final buffer = StringBuffer();
    buffer.writeln("Analyze the following financial transactions:");
    buffer.writeln("Date | Type | Amount | Category | Note");
    buffer.writeln("---|---|---|---|---");

    for (var tx in transactions) {
      final date = tx.date.toIso8601String().split('T')[0];
      final type = tx.type.name.toUpperCase();
      final category = tx.category.value?.name ?? 'Uncategorized';
      final note = tx.note ?? '';
      buffer.writeln("$date | $type | ${tx.amount} | $category | $note");
    }

    buffer.writeln("\nProvide a response in Markdown format with these exact sections:");
    buffer.writeln("## 1. Spending Summary");
    buffer.writeln("## 2. Budget Advice");
    buffer.writeln("## 3. Saving Tips");
    buffer.writeln("## 4. Risk Warning");
    buffer.writeln("Keep it concise and actionable. Do not reveal raw data.");
    
    return buffer.toString();
  }
}
