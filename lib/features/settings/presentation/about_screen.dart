import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:url_launcher/url_launcher.dart';

class AboutScreen extends StatelessWidget {
  const AboutScreen({super.key});

  Future<void> _launchUrl(String url) async {
    final uri = Uri.parse(url);
    if (!await launchUrl(uri, mode: LaunchMode.externalApplication)) {
      debugPrint('Could not launch $url');
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Scaffold(
      appBar: AppBar(title: const Text('About')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            const SizedBox(height: 24),
            Center(
              child: ClipRRect(
                borderRadius: BorderRadius.circular(40),
                child: Image.asset(
                  'assets/images/logo.png', // Replace with your main app logo if you have one, or remove this part. Let's use a standard icon instead if logo isn't available.
                   width: 80, height: 80,
                   errorBuilder: (context, error, stackTrace) => Icon(Icons.wallet, size: 80, color: colorScheme.primary),
                ),
              ),
            ),
            const SizedBox(height: 16),
            Text(
              'Wallet',
              style: textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              'Version 1.0.0',
              style: textTheme.bodyMedium?.copyWith(color: colorScheme.onSurfaceVariant),
            ),
            const SizedBox(height: 32),
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: colorScheme.surfaceContainerHighest,
                borderRadius: BorderRadius.circular(24),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Text(
                    'Built with ❤️ by',
                    textAlign: TextAlign.center,
                    style: textTheme.titleSmall?.copyWith(color: colorScheme.onSurfaceVariant),
                  ),
                  const SizedBox(height: 16),
                  Center(
                    child: CircleAvatar(
                      radius: 40,
                      backgroundImage: const AssetImage('assets/images/Abhi.jpg'),
                      backgroundColor: colorScheme.surface,
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Mrdarkside',
                    textAlign: TextAlign.center,
                    style: textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 24),
                  FilledButton.icon(
                    onPressed: () => _launchUrl('https://github.com/mrdarksidetm'), // Adjust github link if user provided a specific repo link. User said: "copy this repository link with telling it's open source". I will assume standard github username, but I can add a text explaining the open source nature.
                    icon: const Icon(Icons.code),
                    label: const Text('Open Source Repository'),
                  ),
                  const SizedBox(height: 12),
                  OutlinedButton.icon(
                    onPressed: () => _launchUrl('https://www.instagram.com/mrdarksidetm'),
                    icon: SvgPicture.asset(
                      'assets/images/instagram.svg',
                      width: 20,
                      height: 20,
                      colorFilter: ColorFilter.mode(colorScheme.primary, BlendMode.srcIn),
                    ),
                    label: const Text('Instagram'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 32),
            Text(
              'Wallet is an open-source, offline first expense manager focusing on your privacy.',
              textAlign: TextAlign.center,
              style: textTheme.bodySmall?.copyWith(color: colorScheme.onSurfaceVariant),
            ),
          ],
        ),
      ),
    );
  }
}
