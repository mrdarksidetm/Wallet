# Implementation Plan: Feature Parity Bridge

**Branch**: `001-feature-parity-bridge` | **Date**: 2026-04-04 | **Spec**: [specs/001-feature-parity-bridge/spec.md]

## Summary
Port missing v1.5.0 features from the Flutter repository to the Jetpack Compose repository to achieve 100% parity. This includes high-impact UI (Charts/Heatmaps) and core logic (Dynamic Currency).

## Technical Context
- **Tech Stack**: Jetpack Compose (Material 3), Room DB, Kotlin Coroutines/Flow.
- **Charts**: Custom `Canvas` implementations to maintain the 60-120 FPS target and avoid APK bloat.
- **Currency**: Mapping Flutter's `intl` based `CurrencyEngine` to JVM's `java.util.Currency` and `java.text.NumberFormat`.

## Constitution Check
- **Rule I (MVVM)**: All state will be managed in `WalletViewModel` as `StateFlow`. ✅
- **Rule II (Sync)**: `SPEC.md` already updated. ✅
- **Rule III (Local)**: Room persistence maintained. ✅
- **Rule IV (Performance)**: Canvas used for all charts. ✅

## Project Structure

### Documentation
- `specs/001-feature-parity-bridge/spec.md` (Functional Requirements)
- `specs/001-feature-parity-bridge/plan.md` (This file)

### Source Code
- `app/src/main/java/com/mrdarksidetm/wallet/data/domain/CurrencyEngine.kt` (Port of Flutter service)
- `app/src/main/java/com/mrdarksidetm/wallet/ui/screens/HeatmapScreen.kt` (Compose implementation)
- `app/src/main/java/com/mrdarksidetm/wallet/ui/screens/TrendsChart.kt` (Compose implementation)
- `app/src/main/java/com/mrdarksidetm/wallet/ui/ReportsScreen.kt` (Overhaul)

## Implementation Strategy

### Phase 1: Core Engine & Data
1. **Currency Engine**: Create `CurrencyEngine.kt` in `data/domain`. Use `java.text.NumberFormat.getCurrencyInstance(Locale)` to replicate Flutter's localized formatting (e.g., Lakh/Crore for INR).
2. **ViewModel Integration**: Update `WalletViewModel` to expose a `currencyCode` StateFlow from the personalization settings.

### Phase 2: Home Screen Visuals (Canvas)
1. **Activity Heatmap**:
   - Create `ActivityHeatmap.kt`.
   - Calculate transaction density per day for the last 90 days.
   - Use `drawRect` with alpha-blended `MaterialTheme.colorScheme.primary` for intensity.
2. **Trends Line Chart**:
   - Create `ExpenseTrendsChart.kt`.
   - Aggregate daily income/expense for the current month.
   - Use `Path` and `drawPath` for the line, and `drawPoints` for the nodes.
   - Add a subtle gradient fill under the line.

### Phase 3: Reports Dashboard Overhaul
1. **Reactive Reports**:
   - Update `ReportsScreen.kt` to use `collectAsState()` on filtered transaction flows.
   - Add a `DateRangeFilter` bottom sheet.
   - Implement category-wise donut charts using `drawArc`.

### Phase 4: UX Refinement
1. **Loan Page**: Update `LoanScreen.kt` to support multi-line date pickers using `DatePicker` from Material 3 Compose.
2. **Dynamic UI**: Ensure all currency displays in the app call `CurrencyEngine.format(amount, currencyCode)`.

## Verification Plan

### Automated Tests
- **Unit Test**: `CurrencyEngineTest` to verify formatting for INR, USD, and EUR.
- **Unit Test**: `WalletViewModelTest` to verify filtered report flows update correctly on date range change.

### Manual Verification
- Verify Heatmap squares align with transaction dates.
- Verify Line Chart updates when a new transaction is added.
- Verify the Reports page correctly reflects the "Last 30 Days" filter.
