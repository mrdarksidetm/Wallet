import 'package:flutter/material.dart';
import '../../../../core/theme/typography.dart';
import 'widgets/balance_card.dart';
import 'widgets/overview_cards.dart';
import 'widgets/recent_transaction_list.dart';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/services/greeting_service.dart';
import 'package:flutter_animate/flutter_animate.dart';

class DashboardScreen extends ConsumerWidget {
  final VoidCallback onNavigateToTransactions;

  const DashboardScreen({super.key, required this.onNavigateToTransactions});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final greeting = ref.watch(greetingServiceProvider).getGreeting();

    return SafeArea(
      bottom: false,
      child: CustomScrollView(
        slivers: [
        SliverAppBar(
          expandedHeight: 60,
          floating: true,
          backgroundColor: Colors.transparent,
          flexibleSpace: FlexibleSpaceBar(
            titlePadding: const EdgeInsets.only(left: 16, bottom: 16),
             title: Text(
              greeting,
              style: TextStyle(
                fontFamily: AppTypography.fontFamily,
                fontWeight: FontWeight.bold,
                fontSize: 16,
                color: Theme.of(context).colorScheme.onSurface,
              ),
            ),
          ),
        ),
        SliverToBoxAdapter(
          child: const BalanceCard().animate().fade(duration: 600.ms, curve: Curves.easeOutQuad).slideY(begin: 0.1, end: 0),
        ),
        SliverPadding(
          padding: const EdgeInsets.symmetric(vertical: 16),
          sliver: SliverToBoxAdapter(
            child: const OverviewCards().animate(delay: 100.ms).fade(duration: 600.ms).slideY(begin: 0.1, end: 0),
          ),
        ),
        SliverToBoxAdapter(
          child: RecentTransactions(onSeeAll: onNavigateToTransactions)
              .animate(delay: 200.ms)
              .fade(duration: 600.ms)
              .slideY(begin: 0.1, end: 0),
        ),
        const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
      ],
      ),
    );
  }
}
