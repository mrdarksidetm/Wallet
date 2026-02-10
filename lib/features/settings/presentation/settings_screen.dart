import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/design/app_design.dart';
import '../../../../core/design/design_controller.dart';
import '../../../../shared/widgets/app_icon.dart';
import '../../../../shared/widgets/emoji_text.dart';
import '../../../../shared/widgets/glass_surface.dart';

class SettingsScreen extends ConsumerWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final designState = ref.watch(designControllerProvider);
    final controller = ref.read(designControllerProvider.notifier);
    final isLiquid = designState.isLiquid;

    // Helper for Section Headers
    Widget sectionHeader(String title) {
      return Padding(
        padding: const EdgeInsets.fromLTRB(16, 24, 16, 8),
        child: Text(
          title,
          style: Theme.of(context).textTheme.titleSmall?.copyWith(
            color: Theme.of(context).colorScheme.primary,
            fontWeight: FontWeight.bold,
          ),
        ),
      );
    }

    // Helper for Settings Tile
    Widget settingsTile({
      required Widget title,
      Widget? subtitle,
      Widget? trailing,
      VoidCallback? onTap,
    }) {
      final tile = ListTile(
        title: title,
        subtitle: subtitle,
        trailing: trailing,
        onTap: onTap,
        shape: AppDesign.shapeMedium,
      );

      if (isLiquid) {
        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
          child: GlassSurface(
            padding: EdgeInsets.zero,
            borderRadius: BorderRadius.circular(16),
            child: tile,
          ),
        );
      }
      return tile;
    }

    return Scaffold(
      extendBodyBehindAppBar: isLiquid,
      appBar: AppBar(
        title: const Text('Settings'),
        backgroundColor: isLiquid ? Colors.transparent : null,
      ),
      body: Stack(
        children: [
          // Background for Liquid Mode
          if (isLiquid)
            Container(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: Theme.of(context).brightness == Brightness.dark
                      ? [const Color(0xFF2E0249), const Color(0xFF570A57)]
                      : [const Color(0xFFE0C3FC), const Color(0xFF8EC5FC)],
                ),
              ),
            ),
          
          // Content
          ListView(
            padding: EdgeInsets.only(
              top: isLiquid ? kToolbarHeight + 32 : 16,
              bottom: 32,
            ),
            children: [
              sectionHeader('APPEARANCE'),
              settingsTile(
                title: const Text('Theme Mode'),
                trailing: DropdownButton<ThemeMode>(
                  value: designState.themeMode,
                  underline: const SizedBox(),
                  items: const [
                    DropdownMenuItem(value: ThemeMode.system, child: Text('System')),
                    DropdownMenuItem(value: ThemeMode.light, child: Text('Light')),
                    DropdownMenuItem(value: ThemeMode.dark, child: Text('Dark')),
                  ],
                  onChanged: (mode) => controller.setThemeMode(mode!),
                ),
              ),
              settingsTile(
                title: const Text('Font Family'),
                trailing: DropdownButton<String>(
                  value: designState.fontFamily,
                  underline: const SizedBox(),
                  items: const [
                     DropdownMenuItem(
                      value: AppDesign.fontProductSans, 
                      child: Text('Product Sans', style: TextStyle(fontFamily: AppDesign.fontProductSans))
                    ),
                    DropdownMenuItem(
                      value: AppDesign.fontSFPro, 
                      child: Text('SF Pro', style: TextStyle(fontFamily: AppDesign.fontSFPro))
                    ),
                  ],
                  onChanged: (font) => controller.setFontFamily(font!),
                ),
              ),
              settingsTile(
                title: const Text('Liquid Glass'),
                subtitle: const Text('Enable futuristic overlay'),
                trailing: Switch(
                  value: designState.isLiquid,
                  onChanged: controller.toggleLiquid,
                ),
              ),

              sectionHeader('DESIGN SYSTEM'),
              settingsTile(
                title: const Text('Material 3 Expressive'),
                subtitle: const Text('Default Design Language'),
                trailing: const AppIcon(Icons.check_circle, cupertinoIcon: CupertinoIcons.check_mark_circled, size: 20),
              ),
              settingsTile(
                title: const Text('Animation Intensity'),
                trailing: const Text('Normal'), 
              ),

              sectionHeader('EMOJI'),
              settingsTile(
                title: const Text('Emoji Pack'),
                subtitle: const EmojiText('Locked to Apple Emojis 🍎'),
                trailing: const Icon(Icons.lock, size: 16),
              ),

              sectionHeader('DATA'),
              settingsTile(
                title: const Text('Backup Data'),
                onTap: () {},
                trailing: const AppIcon(Icons.cloud_upload, cupertinoIcon: CupertinoIcons.cloud_upload),
              ),
              settingsTile(
                title: const Text('Restore Data'),
                onTap: () {},
                trailing: const AppIcon(Icons.cloud_download, cupertinoIcon: CupertinoIcons.cloud_download),
              ),

              sectionHeader('ABOUT'),
              settingsTile(
                title: const Text('Version'),
                trailing: const Text('1.0.0 (Build 1)'),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
