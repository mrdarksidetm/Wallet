class FinancialInsightModel {
  final String summary;
  final String advice;
  final String warning;
  final InsightTrend trend;

  const FinancialInsightModel({
    required this.summary,
    required this.advice,
    required this.warning,
    required this.trend,
  });
}

enum InsightTrend { up, down, stable }
