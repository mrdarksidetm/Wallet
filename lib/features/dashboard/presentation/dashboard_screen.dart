import 'package:flutter/material.dart';
import '../../../../core/design/app_design.dart';
import 'widgets/balance_card.dart';
import 'widgets/overview_cards.dart';
import 'widgets/recent_transaction_list.dart';

class DashboardScreen extends StatelessWidget {
  final VoidCallback onNavigateToTransactions;

  const DashboardScreen({super.key, required this.onNavigateToTransactions});

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      slivers: [
        const SliverAppBar(
          expandedHeight: 60,
          floating: true,
          backgroundColor: Colors.transparent,
          title: Text(
            'Good Morning', // Dynamic greeting could be added here
            style: TextStyle(
              fontFamily: AppDesign.fontProductSans,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        const SliverToBoxAdapter(child: BalanceCard()),
        const SliverPadding(
          padding: EdgeInsets.symmetric(vertical: 16),
          sliver: SliverToBoxAdapter(child: OverviewCards()),
        ),
        SliverToBoxAdapter(
          child: RecentTransactions(onSeeAll: onNavigateToTransactions),
        ),
        const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
      ],
    );
  }
}
