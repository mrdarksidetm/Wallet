import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/database/providers.dart';
import '../../../../core/design/app_design.dart';
import '../../../../core/design/design_controller.dart';
import '../../../../shared/widgets/glass_surface.dart';
import 'package:flutter_animate/flutter_animate.dart';

class BalanceCard extends ConsumerWidget {
  const BalanceCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final balanceAsync = ref.watch(totalBalanceProvider);
    final designState = ref.watch(designControllerProvider);
    final isLiquid = designState.isLiquid;

    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: _buildContainer(
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
                    fontFamily: AppDesign.fontProductSans,
                  ),
                ).animate().fade().scale(),
                loading: () => const SizedBox(
                  height: 38,
                  width: 100,
                  child: LinearProgressIndicator(),
                ),
                error: (_, __) => Text('\$0.00'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildContainer({required bool isLiquid, required Widget child}) {
    if (isLiquid) {
      return GlassSurface(
        radius: const BorderRadius.all(Radius.circular(28)),
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
      );
    }
    
    return Card(
      elevation: 2,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(28)),
      ),
      color: const Color(0xFF6750A4), // Primary Container-ish
      child: Theme(
        data: ThemeData.dark(), // Force dark text on this card for contrast if needed, or stick to M3 defaults
        child: Builder(builder: (context) {
          // Override text colors for M3 card manually if needed, or just let it inherit
          return DefaultTextStyle.merge(
             style: const TextStyle(color: Colors.white),
             child: child,
          );
        }),
      ),
    );
  }
}
