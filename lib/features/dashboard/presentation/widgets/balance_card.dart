import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:wallet/core/theme/theme_provider.dart';
import '../../../../core/database/providers.dart';
import '../../../../shared/widgets/glass_surface.dart';
import '../../../../shared/widgets/paisa_card.dart';
import 'package:flutter_animate/flutter_animate.dart';

class BalanceCard extends ConsumerWidget {
  const BalanceCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final balanceAsync = ref.watch(totalBalanceProvider);
    final themeState = ref.watch(themeControllerProvider);
    final isLiquid = themeState.isLiquid;

    return _buildContainer(
      context: context,
      isLiquid: isLiquid,
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Total Balance',
              style: TextStyle(
                color: isLiquid ? Colors.white70 : null,
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
            const SizedBox(height: 8),
            balanceAsync.when(
              data: (balance) => Text(
                '\$${balance.toStringAsFixed(2)}',
                style: TextStyle(
                  color: isLiquid ? Colors.white : null,
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                ),
              ).animate().fade().scale(),
              loading: () => const SizedBox(
                height: 38,
                width: 100,
                child: LinearProgressIndicator(),
              ),
              error: (_, __) => const Text('\$0.00'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContainer({required BuildContext context, required bool isLiquid, required Widget child}) {
    if (isLiquid) {
      return Padding(
        padding: const EdgeInsets.all(16.0),
        child: RepaintBoundary(
          child: GlassSurface(
            borderRadius: const BorderRadius.all(Radius.circular(28)),
            child: Container(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    Colors.white.withOpacity(0.1),
                    Colors.white.withOpacity(0.05),
                  ],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
              ),
              child: child,
            ),
          ),
        ),
      );
    }
    
    return PaisaCard(
      color: Theme.of(context).colorScheme.primary, // Primary solid color for Balance Card
      child: Theme(
        data: ThemeData.dark(), // Force dark text on this card for contrast
        child: Builder(builder: (newContext) {
          return DefaultTextStyle.merge(
             style: const TextStyle(color: Colors.white),
             child: child,
          );
        }),
      ),
    );
  }
}

