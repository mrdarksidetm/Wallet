import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:wallet/core/database/models/transaction_model.dart';
import 'package:wallet/features/insights/services/financial_insight_service.dart';
import 'package:wallet/features/insights/models/financial_insight_model.dart';

void main() {
  group('FinancialInsightService', () {
    late FinancialInsightService service;
    late ProviderContainer container;

    setUp(() {
      container = ProviderContainer();
      service = container.read(financialInsightServiceProvider);
    });

    tearDown(() {
      container.dispose();
    });

    test('generateInsights returns default message for empty transactions', () async {
      final result = await service.generateInsights([]);
      expect(result.summary, contains("Start tracking"));
      expect(result.trend, InsightTrend.stable);
    });

    test('generateInsights detects high spending warning', () async {
      final now = DateTime.now();
      final income = TransactionModel()
        ..amount = 1000
        ..type = TransactionType.income
        ..date = now;
      
      final expense = TransactionModel()
        ..amount = 950
        ..type = TransactionType.expense
        ..date = now;

      final result = await service.generateInsights([income, expense]);
      
      expect(result.warning, contains("Warning"));
      expect(result.warning, contains(">90%"));
    });

    test('generateInsights detects savings advice', () async {
      final now = DateTime.now();
      final income = TransactionModel()
        ..amount = 2000
        ..type = TransactionType.income
        ..date = now;
      
      final expense = TransactionModel()
        ..amount = 500
        ..type = TransactionType.expense
        ..date = now;

      final result = await service.generateInsights([income, expense]);
      
      expect(result.advice, contains("Great job"));
      expect(result.summary, contains("Savings rate"));
    });

     test('generateInsights detects inactivity warning', () async {
      final oldDate = DateTime.now().subtract(const Duration(days: 8));
      final tx = TransactionModel()
        ..amount = 100
        ..type = TransactionType.expense
        ..date = oldDate;

      final result = await service.generateInsights([tx]);
      expect(result.warning, contains("No activity"));
    });
  });
}
