import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/database/models/transaction_model.dart';
import '../models/financial_insight_model.dart';


final financialInsightServiceProvider = Provider<FinancialInsightService>((ref) {
  return FinancialInsightService(ref);
});

class FinancialInsightService {
  final Ref ref;

  FinancialInsightService(this.ref);

  Future<FinancialInsightModel> generateInsights(List<TransactionModel> transactions) async {
    if (transactions.isEmpty) {
      return const FinancialInsightModel(
        summary: "Start tracking to see insights.",
        advice: "Add your first income or expense.",
        warning: "",
        trend: InsightTrend.stable,
      );
    }

    double totalIncome = 0;
    double totalExpense = 0;
    
    // Simple analysis window (last 30 days)
    final now = DateTime.now();
    final monthStart = DateTime(now.year, now.month, 1);
    
    for (var tx in transactions) {
      if (tx.date.isAfter(monthStart)) {
         if (tx.type == TransactionType.income) totalIncome += tx.amount;
         if (tx.type == TransactionType.expense) totalExpense += tx.amount;
      }
    }

    // 1. Trend Analysis
    InsightTrend trend = InsightTrend.stable;
    if (totalExpense > totalIncome && totalIncome > 0) trend = InsightTrend.down;
    if (totalIncome > totalExpense * 1.5) trend = InsightTrend.up;

    // 2. Summary
    String summary = "You've spent ${totalExpense.toStringAsFixed(0)} this month.";
    if (totalIncome > 0) {
      double savingsRate = ((totalIncome - totalExpense) / totalIncome) * 100;
      summary += " Savings rate: ${savingsRate.toStringAsFixed(1)}%.";
    }

    // 3. Advice
    String advice = "Keep tracking every penny.";
    if (totalExpense > totalIncome && totalIncome > 0) {
      advice = "Cut back on non-essentials to balance the books.";
    } else if (totalIncome > totalExpense * 2) {
      advice = "Great job! Consider investing the surplus.";
    }

    // 4. Warning
    String warning = "";
    if (totalExpense > totalIncome * 0.9 && totalIncome > 0) {
      warning = "⚠️ Warning: You have used >90% of your income.";
    }
    
    // Check for inactivity
    if (transactions.isNotEmpty) {
      final lastTx = transactions.first.date;
      if (now.difference(lastTx).inDays > 7) {
        warning += "\n⚠️ No activity for 7+ days. Forgot to track?";
      }
    }

    return FinancialInsightModel(
      summary: summary,
      advice: advice,
      warning: warning,
      trend: trend,
    );
  }
}
