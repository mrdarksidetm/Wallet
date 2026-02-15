import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/design/design_controller.dart';
import 'dashboard_screen.dart';
import '../../transactions/presentation/transaction_list_screen.dart';
import '../../settings/presentation/settings_screen.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  int _currentIndex = 0;

  final List<Widget> _pages = [
    // Dashboard (Placeholder for now, we pass callback dynamically)
    const SizedBox(), 
    const TransactionListScreen(),
    const SettingsScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    // We construct Dashboard here to pass the callback using the current context state methods
    final dashboard = DashboardScreen(
      onNavigateToTransactions: () => setState(() => _currentIndex = 1),
    );

    final isLiquid = ref.watch(designControllerProvider).isLiquid;
    
    return Scaffold(
      body: _currentIndex == 0 ? dashboard : _pages[_currentIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) => setState(() => _currentIndex = index),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.dashboard_outlined),
            selectedIcon: Icon(Icons.dashboard),
            label: 'Dashboard',
          ),
          NavigationDestination(
            icon: Icon(Icons.receipt_long_outlined),
            selectedIcon: Icon(Icons.receipt_long),
            label: 'Transactions',
          ),
          NavigationDestination(
            icon: Icon(Icons.settings_outlined),
            selectedIcon: Icon(Icons.settings),
            label: 'Settings',
          ),
        ],
      ),
    );
  }
}
