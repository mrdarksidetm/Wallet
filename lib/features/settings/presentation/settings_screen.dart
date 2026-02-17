import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/design/design_controller.dart';
import '../../../core/database/providers.dart';
import '../../finance/presentation/budget_screen.dart';
import '../../finance/presentation/loan_screen.dart';
import '../../finance/presentation/goal_screen.dart';
import '../../finance/presentation/recurring_transaction_screen.dart';

class SettingsScreen extends ConsumerWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final designState = ref.watch(designControllerProvider);
    final designController = ref.read(designControllerProvider.notifier);

    return Scaffold(
      appBar: AppBar(title: const Text('Settings')),
      body: ListView(
        children: [
          const _SectionHeader(title: 'Appearance'),
          SwitchListTile(
            title: const Text('Liquid Glass Effect'),
            value: designState.isLiquid,
            onChanged: (val) => designController.toggleLiquid(val),
          ),
          ListTile(
            title: const Text('Theme Mode'),
            trailing: DropdownButton<ThemeMode>(
              value: designState.themeMode,
              onChanged: (val) {
                if (val != null) designController.setThemeMode(val);
              },
              items: const [
                DropdownMenuItem(value: ThemeMode.system, child: Text('System')),
                DropdownMenuItem(value: ThemeMode.light, child: Text('Light')),
                DropdownMenuItem(value: ThemeMode.dark, child: Text('Dark')),
              ],
            ),
          ),
           ListTile(
            title: const Text('Font Family'),
            trailing: DropdownButton<String>(
              value: designState.fontFamily,
              onChanged: (val) {
                if (val != null) designController.setFontFamily(val);
              },
              items: const [
                DropdownMenuItem(value: 'ProductSans', child: Text('Product Sans')),
                DropdownMenuItem(value: 'SFProDisplay', child: Text('SF Pro')),
              ],
            ),
          ),
          
          const _SectionHeader(title: 'Finance'),
          ListTile(
            leading: const Icon(Icons.pie_chart),
            title: const Text('Budgets'),
            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const BudgetScreen())),
          ),
          ListTile(
            leading: const Icon(Icons.handshake),
            title: const Text('Loans'),
            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const LoanScreen())),
          ),
          ListTile(
            leading: const Icon(Icons.flag),
            title: const Text('Goals'),
            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const GoalScreen())),
          ),
          ListTile(
            leading: const Icon(Icons.repeat),
            title: const Text('Recurring Transactions'),
            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const RecurringTransactionScreen())),
          ),

          const _SectionHeader(title: 'Data'),
          ListTile(
            leading: const Icon(Icons.download),
            title: const Text('Export CSV'),
            onTap: () async {
              try {
                await ref.read(csvServiceProvider).exportTransactions();
              } catch (e) {
                ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Export failed: $e')));
              }
            },
          ),
          ListTile(
            leading: const Icon(Icons.delete_forever, color: Colors.red),
            title: const Text('Reset Data', style: TextStyle(color: Colors.red)),
            onTap: () {
               // Show confirmation dialog logic here (omitted for brevity)
               ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Data Reset not implemented for safety reasons yet.')));
            },
          ),
        ],
      ),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String title;
  const _SectionHeader({required this.title});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 24, 16, 8),
      child: Text(
        title,
        style: TextStyle(
          color: Theme.of(context).colorScheme.primary,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }
}
