# Feature Specification: Feature Parity Bridge

**Feature Branch**: `001-feature-parity-bridge`  
**Created**: 2026-04-04  
**Status**: Draft  
**Input**: User description: "Bring the Jetpack Compose version to 100% feature parity with the Flutter version (v1.5.0), including Activity Heatmap, Line Charts, Dynamic Currency, and Reactive Reports Dashboard."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Financial Activity Visualization (Priority: P1)
As a user, I want to see a visual heatmap of my spending activity and a trend line chart on my home screen so that I can quickly understand my financial health trends.

**Why this priority**: High visual impact and core parity with the "editorial" style of the Flutter version.
**Independent Test**: Verify that the `HomeScreen` displays a functional Activity Heatmap and a Line Chart using Compose Canvas.

---

### User Story 2 - Smart Loan Management (Priority: P2)
As a user, I want a more tactile loan management screen with multi-line date pickers and dynamic currency support so that I can manage my debts precisely.

**Why this priority**: Matches the improved UX of the Flutter v1.5.0 version.
**Independent Test**: Create a loan with a specific currency symbol and verify it persists and displays correctly in the Loan list.

---

### User Story 3 - Reactive Financial Reporting (Priority: P3)
As a user, I want a functional, reactive reports dashboard with date-range filtering so that I can analyze my spending over specific periods.

**Why this priority**: Replaces the "dead" or static reports screen with a modern, reactive implementation.
**Independent Test**: Apply a date filter (e.g., "Last 30 Days") on the Reports page and verify the charts and lists update instantly.

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: Implement a custom **Activity Heatmap** component using Jetpack Compose Canvas.
- **FR-002**: Implement an **Expense/Income Trends line chart** on the HomeScreen.
- **FR-003**: Replace hardcoded currency symbols with a dynamic `CurrencyEngine` (mapping to the existing Room `Personalization` or Settings).
- **FR-004**: Overhaul `ReportsScreen` with Material 3 components and reactive `StateFlow` streams.
- **FR-005**: Implement multi-line date pickers in the `AddEditLoanPage`.

### Key Entities
- **CurrencyConfig**: Shared configuration for dynamic symbols.
- **ReportData**: Aggregated stream of transactions for the dashboard.

## Success Criteria *(mandatory)*
- **SC-001**: 100% feature parity with Flutter v1.5.0.
- **SC-002**: Maintain 60-120 FPS during chart rendering.
- **SC-003**: Zero hardcoded currency symbols in the UI.

## Assumptions
- **Native Canvas**: All charts will be drawn manually using `androidx.compose.ui.graphics.Canvas` to avoid external dependencies.
- **Shared SPEC**: This specification aligns with the shared `SPEC.md` across repositories.
