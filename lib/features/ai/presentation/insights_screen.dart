import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../ai/providers/gemini_provider.dart';
import '../../../core/design/app_design.dart';

class InsightsScreen extends ConsumerWidget {
  const InsightsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final apiKeyAsync = ref.watch(geminiApiKeyProvider);
    final analysisAsync = ref.watch(geminiAnalysisProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('AI Insights'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref.read(geminiAnalysisProvider.notifier).analyze(),
            tooltip: 'Refresh Analysis',
          ),
        ],
      ),
      body: apiKeyAsync.when(
        data: (apiKey) {
          if (apiKey == null || apiKey.isEmpty) {
            return const _NoKeyView();
          }

          // Key exists, show analysis interface
          return analysisAsync.when(
            data: (content) {
              if (content == null) {
                // Initial state, auto-trigger or show start button
                return Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.auto_awesome, size: 64, color: Colors.teal),
                      const SizedBox(height: 16),
                      const Text(
                        'Unlock Financial Insights',
                        style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      const Text('Analyze your spending habits with Gemini AI'),
                      const SizedBox(height: 24),
                      ElevatedButton.icon(
                        icon: const Icon(Icons.analytics),
                        label: const Text('Analyze Now'),
                        onPressed: () => ref.read(geminiAnalysisProvider.notifier).analyze(),
                      ),
                      const SizedBox(height: 16),
                       TextButton(
                        onPressed: () => ref.read(geminiApiKeyProvider.notifier).deleteKey(),
                        child: const Text('Remove API Key', style: TextStyle(color: Colors.red)),
                      ),
                    ],
                  ),
                );
              }
              return _ResultView(content: content);
            },
            loading: () => const _LoadingView(),
            error: (err, stack) => _ErrorView(
              error: err.toString(),
              onRetry: () => ref.read(geminiAnalysisProvider.notifier).analyze(),
               onClearKey: () => ref.read(geminiApiKeyProvider.notifier).deleteKey(),
            ),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Storage Error: $err')),
      ),
    );
  }
}

class _NoKeyView extends ConsumerStatefulWidget {
  const _NoKeyView();

  @override
  ConsumerState<_NoKeyView> createState() => _NoKeyViewState();
}

class _NoKeyViewState extends ConsumerState<_NoKeyView> {
  final _controller = TextEditingController();
  bool _isLoading = false;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          const Icon(Icons.vpn_key, size: 48, color: Colors.orange),
          const SizedBox(height: 24),
          const Text(
            'Connect Gemini AI',
            style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          const Text(
            'Enter your Google Gemini API Key to enable personalized financial insights. Your key is stored securely on your device.',
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 32),
          TextField(
            controller: _controller,
            decoration: const InputDecoration(
              labelText: 'API Key',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.key),
            ),
            obscureText: true,
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _isLoading ? null : _saveKey,
            child: _isLoading
                ? const SizedBox(
                    width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                : const Text('Save Key'),
          ),
          const SizedBox(height: 16),
          TextButton(
            onPressed: () => launchUrl(Uri.parse('https://aistudio.google.com/app/apikey')),
            child: const Text('Get API Key from Google AI Studio'),
          ),
          const SizedBox(height: 24),
          const Text(
            'Privacy Note: Your transaction data (date, amount, category, note) will be sent to Google API for analysis. Do not use if you are uncomfortable with this.',
            style: TextStyle(fontSize: 12, color: Colors.grey),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Future<void> _saveKey() async {
    if (_controller.text.isEmpty) return;
    setState(() => _isLoading = true);
    await ref.read(geminiApiKeyProvider.notifier).saveKey(_controller.text.trim());
    if (mounted) setState(() => _isLoading = false);
  }
}

class _LoadingView extends StatelessWidget {
  const _LoadingView();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          const SizedBox(height: 24),
          Text(
            'Analyzing your finances...',
            style: Theme.of(context).textTheme.titleMedium,
          ),
          const SizedBox(height: 8),
          const Text('This may take a few seconds.'),
        ],
      ),
    );
  }
}

class _ResultView extends StatelessWidget {
  final String content;
  const _ResultView({required this.content});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Markdown(data: content),
    );
  }
}

class _ErrorView extends StatelessWidget {
  final String error;
  final VoidCallback onRetry;
  final VoidCallback onClearKey;

  const _ErrorView({required this.error, required this.onRetry, required this.onClearKey});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 48, color: Colors.red),
            const SizedBox(height: 16),
            Text(
              'Analysis Failed',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text(
              error.replaceAll('Exception:', ''),
              textAlign: TextAlign.center,
              style: const TextStyle(color: Colors.red),
            ),
            const SizedBox(height: 24),
            ElevatedButton.icon(
              icon: const Icon(Icons.refresh),
              label: const Text('Retry'),
              onPressed: onRetry,
            ),
             const SizedBox(height: 12),
             TextButton(
              onPressed: onClearKey, // Allow clearing key if it was invalid
              child: const Text('Change API Key'),
            ),
          ],
        ),
      ),
    );
  }
}
