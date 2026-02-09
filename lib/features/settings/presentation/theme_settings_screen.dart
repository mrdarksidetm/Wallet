import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/providers/theme_provider.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/widgets/liquid_scaffold.dart';
import '../../../../shared/widgets/liquid_container.dart';

class ThemeSettingsScreen extends ConsumerWidget {
  const ThemeSettingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeState = ref.watch(themeProvider);
    final notifier = ref.read(themeProvider.notifier);

    return LiquidScaffold(
      appBar: AppBar(
        title: const Text('Theme Settings'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Theme Mode
          _buildSectionTitle(context, 'Theme Mode'),
          const SizedBox(height: 8),
          _buildSegmentedControl(
            context: context,
            groupValue: themeState.themeMode,
            onValueChanged: notifier.setThemeMode,
            children: {
              ThemeMode.system: 'System',
              ThemeMode.light: 'Light',
              ThemeMode.dark: 'Dark',
            },
          ),
          const SizedBox(height: 24),

          // Liquid Style
          _buildSectionTitle(context, 'Aesthetic'),
          const SizedBox(height: 8),
          SwitchListTile(
            title: const Text('Liquid Glass Style'),
            subtitle: const Text('Enable futuristic glassmorphism'),
            value: themeState.isLiquid,
            onChanged: notifier.toggleLiquid,
          ),
          const SizedBox(height: 24),

          // Font Family
          _buildSectionTitle(context, 'Typography'),
          const SizedBox(height: 8),
          RadioListTile<String>(
            title: const Text('Product Sans', style: TextStyle(fontFamily: AppTheme.fontFamilyProductSans)),
            value: AppTheme.fontFamilyProductSans,
            groupValue: themeState.fontFamily,
            onChanged: (val) => notifier.setFontFamily(val!),
          ),
          RadioListTile<String>(
            title: const Text('SF Pro Display', style: TextStyle(fontFamily: AppTheme.fontFamilySFPro)),
            value: AppTheme.fontFamilySFPro,
            groupValue: themeState.fontFamily,
            onChanged: (val) => notifier.setFontFamily(val!),
          ),
          
          const SizedBox(height: 24),
          // Preview Area
          _buildSectionTitle(context, 'Preview'),
          const SizedBox(height: 8),
          LiquidContainer(
            height: 150,
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  'This is a glass container',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 8),
                const Text('🔥 🚀 💰'),
              ],
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildSectionTitle(BuildContext context, String title) {
    return Text(
      title,
      style: Theme.of(context).textTheme.titleMedium?.copyWith(
            fontWeight: FontWeight.bold,
            color: Theme.of(context).colorScheme.primary,
          ),
    );
  }

  Widget _buildSegmentedControl<T>({
    required BuildContext context,
    required T groupValue,
    required Function(T) onValueChanged,
    required Map<T, String> children,
  }) {
    // A simple row of buttons to simulate segmented control without external package dep for now
    return Row(
      children: children.entries.map((entry) {
        final isSelected = entry.key == groupValue;
        return Expanded(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4.0),
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: isSelected ? Theme.of(context).colorScheme.primary : null,
                foregroundColor: isSelected ? Theme.of(context).colorScheme.onPrimary : null,
              ),
              onPressed: () => onValueChanged(entry.key),
              child: Text(entry.value),
            ),
          ),
        );
      }).toList(),
    );
  }
}
