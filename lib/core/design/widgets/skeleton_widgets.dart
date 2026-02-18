import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';

class SkeletonCard extends StatelessWidget {
  final double height;
  final double width;
  final BorderRadius? borderRadius;

  const SkeletonCard({
    super.key,
    this.height = 100,
    this.width = double.infinity,
    this.borderRadius,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: height,
      width: width,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainerHighest.withOpacity(0.3),
        borderRadius: borderRadius ?? BorderRadius.circular(16),
      ),
    ).animate(onPlay: (controller) => controller.repeat())
     .shimmer(duration: 1200.ms, color: Theme.of(context).colorScheme.surface.withOpacity(0.8));
  }
}

class SkeletonList extends StatelessWidget {
  final int itemCount;
  final double itemHeight;

  const SkeletonList({super.key, this.itemCount = 5, this.itemHeight = 72});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: List.generate(itemCount, (index) {
        return const Padding(
          padding: EdgeInsets.symmetric(vertical: 8, horizontal: 16),
          child: Row(
            children: [
              SkeletonCard(height: 40, width: 40, borderRadius: BorderRadius.all(Radius.circular(20))),
              SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                  SkeletonCard(height: 16, width: 120),
                    SizedBox(height: 8),
                  SkeletonCard(height: 12, width: 80),
                  ],
                ),
              ),
              SizedBox(width: 16),
              SkeletonCard(height: 16, width: 60),
            ],
          ),
        );
      }),
    );
  }
}
