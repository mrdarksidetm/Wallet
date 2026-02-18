import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/design/app_design.dart';
import 'core/design/design_controller.dart';
import 'core/database/providers.dart';
import 'features/dashboard/presentation/home_screen.dart';

void main() {
  runZonedGuarded(() async {
    WidgetsFlutterBinding.ensureInitialized();
    // Initialize error UI early
    runApp(const InitializationApp());
    
    try {
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
    } catch (e, stack) {
      runApp(ErrorApp(error: e, stack: stack));
    }
  }, (error, stack) {
    runApp(ErrorApp(error: error, stack: stack));
  });
}

class InitializationApp extends StatelessWidget {
  const InitializationApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      ),
    );
  }
}

class ErrorApp extends StatelessWidget {
  final Object error;
  final StackTrace stack;
  const ErrorApp({super.key, required this.error, required this.stack});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        backgroundColor: Colors.red.shade900,
        body: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: SingleChildScrollView(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Initialization Error',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    error.toString(),
                    style: const TextStyle(color: Colors.white, fontSize: 16),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    stack.toString(),
                    style: const TextStyle(
                      color: Colors.white70,
                      fontSize: 12,
                      fontFamily: 'monospace',
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
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
