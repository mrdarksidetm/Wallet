import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/design/app_design.dart';
import 'core/design/design_controller.dart';
import 'core/database/providers.dart';
import 'features/dashboard/presentation/home_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  final container = ProviderContainer();
  // Wait for Isar to initialize
  await container.read(isarProvider.future);
  // Run seed defaults
  await container.read(seedServiceProvider).seedDefaults();
  // Check recurring transactions
  await container.read(recurringServiceProvider).checkRecurringTransactions();

  runApp(
    UncontrolledProviderScope(
      container: container,
      child: const WalletApp(),
    ),
  );
}

class WalletApp extends ConsumerWidget {
  const WalletApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final designState = ref.watch(designControllerProvider);

    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Wallet',
      theme: AppDesign.getThemeData(
        brightness: Brightness.light,
        fontFamily: designState.fontFamily,
      ),
      darkTheme: AppDesign.getThemeData(
        brightness: Brightness.dark,
        fontFamily: designState.fontFamily,
      ),
      themeMode: designState.themeMode,
      home: const HomeScreen(),
    );
  }
}
