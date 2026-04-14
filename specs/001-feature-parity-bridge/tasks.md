# Tasks: Feature Parity Bridge

**Feature Branch**: `001-feature-parity-bridge`  
**Status**: Draft  
**Plan**: [specs/001-feature-parity-bridge/plan.md]

## Phase 1: Setup
- [x] T001 Initialize feature branch and documentation structure
- [x] T002 Verify Compose version baseline and SDK versions (API 34)

## Phase 2: Foundational (Blocking Prerequisites)
- [x] T003 Create `CurrencyEngine` in `app/src/main/java/com/mrdarksidetm/wallet/data/domain/CurrencyEngine.kt`
- [x] T004 [P] Update `Personalization` entity or Settings to include `defaultCurrency` code
- [x] T005 Update `WalletViewModel` to expose `currencyCode` StateFlow

## Phase 3: User Story 1 - Financial Activity Visualization (Priority: P1) 🎯 MVP
**Goal**: Home screen Activity Heatmap and Expense/Income Trends line chart.
**Independent Test**: Verify charts render correctly on the Home screen using mock data.

- [x] T006 [P] [US1] Create `ActivityHeatmap.kt` component in `ui/screens/`
- [x] T007 [P] [US1] Create `TrendsChart.kt` component in `ui/screens/`
- [x] T008 [US1] Integrate `ActivityHeatmap` into `HomeScreen.kt`
- [x] T009 [US1] Integrate `TrendsChart` into `HomeScreen.kt`
- [x] T010 [US1] Map transaction density data from `WalletViewModel` to `ActivityHeatmap`
- [x] T011 [US1] Map monthly trend data from `WalletViewModel` to `TrendsChart`

## Phase 4: User Story 2 - Smart Loan Management (Priority: P2)
**Goal**: Tactical loan management with multi-line date pickers and dynamic currency.
**Independent Test**: Create a loan and verify currency symbol and date formatting.

- [ ] T012 [US2] Update `LoanScreen.kt` (Add/Edit) to use Material 3 `DatePicker`
- [ ] T013 [US2] Update `LoanItem` UI to use `CurrencyEngine.format()` for balance display
- [ ] T014 [US2] Ensure `LoanEntity` correctly persists the associated currency if different from default

## Phase 5: User Story 3 - Reactive Financial Reporting (Priority: P3)
**Goal**: Functional, reactive reports dashboard with date-range filtering.
**Independent Test**: Change date filter and verify all charts update instantly.

- [ ] T015 [US3] Overhaul `ReportsScreen.kt` with Material 3 Scaffold and TopAppBar
- [ ] T016 [US3] Implement `DateRangeFilter` bottom sheet in `ReportsScreen.kt`
- [ ] T017 [US3] Create reactive category breakdown donut chart in `ReportsScreen.kt`
- [ ] T018 [US3] Wire `ReportsScreen` to `WalletViewModel` using `collectAsState()`

## Phase 6: Polish & Cross-Cutting Concerns
- [ ] T019 Audit all UI components for hardcoded currency symbols and replace with `CurrencyEngine`
- [ ] T020 [P] Implement `CurrencyEngineTest.kt` for locale validation
- [ ] T021 Final verification against `SPEC.md` parity checklist

## Dependencies & Execution Order
1. **Phase 2 (Foundational)** must be completed before any UI work.
2. **Phase 3 (MVP)** is the primary focus for visual parity.
3. **Phase 4 & 5** can be worked on in parallel once Foundational is ready.

## Implementation Strategy
- **MVP First**: Complete Phase 2 and 3 to show immediate visual progress on the Home screen.
- **Incremental Delivery**: Deliver each User Story as a functional increment.
