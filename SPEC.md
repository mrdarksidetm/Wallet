# 📜 Wallet (Compose) Design Specification (SPEC.md)

This is the **Single Source of Truth** for the Project Wallet (Jetpack Compose) version. 🏦

---

## 💎 1. App Identity

*   **Project Name**: Wallet (Jetpack Compose) 🏦
*   **Package Name**: `com.darkside.wallet` 📦
*   **Version**: 1.0.0 ("The Native Stitch") 💎
*   **Target SDK**: Android 15 (API 35) 🤖
*   **Design Language**: Material 3 Expressive (Editorial Style) 🎨
*   **Core Mandate**: Offline-first, Privacy-centric, High-Performance (60-120 FPS). ⚡

---

## 📊 2. Data Model (Room Entities)

### **🏦 2.1 AccountEntity**
- **id**: String (UUID, Primary Key) 🔑
- **name**: String (Index) 📝
- **type**: String (e.g., Cash, Bank, CreditCard) 💳
- **initialBalance**: double 💵

### **💸 2.2 TransactionEntity**
- **id**: String (UUID, Primary Key) 🔑
- **amount**: double 💵
- **date**: Long (Timestamp) 📅
- **type**: String (Income, Expense, Transfer) 🔄
- **note**: String 📝
- **category**: String (Category name/slug) 📂
- **accountId**: String (ForeignKey to AccountEntity) 🔗
- **isArchived**: Boolean ✅

### **💹 2.3 Future Entities (Parity Map)**
*Planned for parity with Wallet-Flutter:*
- **CategoryEntity**: UUID-based category management.
- **BudgetEntity**: Monthly spending limits.
- **GoalEntity**: Savings targets tracking.
- **PersonEntity**: Links for loans and bill splitting.

---

## 🎨 3. Design System (Material 3)

### **📐 Layout Geometry**
- **Border Radius**: 32dp (Bottom Sheets), 24dp (Large Cards), 16dp (Dialogs). 📏
- **Padding**: 24dp (Standard Outer Margin), 16dp (Internal Spacing). 📏
- **Visual Style**: "Stitch" depth with subtle gradients and glassy surfaces. 🧵

### **🎭 Typography & Icons**
- **Font**: `Google Sans Flex` (Variable weight). ✒️
- **Icons**: `Material Icons Extended` (Rounded style). 🎭
- **Performance**: Native Compose `Canvas` for all visualizations. ⛸️

---

## ✅ 4. Feature Implementation Status

### **Core**
- [x] Room Database Integration (Offline-first). ✅
- [x] UUID-based Primary Keys (Sync-ready). ✅
- [x] Basic Transaction CRUD. ✅
- [x] Account-aware balance calculations. ✅
- [x] Local Crash Log Export (Zero-Network). ✅
- [x] Safe Runtime Fixer (UI Error Boundary). ✅

### **Home & UI**
- [x] Material 3 Home Screen with "Stitch" style. ✅
- [x] Balance Hero Section. ✅
- [x] Interactive Overview Grid (8-card layout). ✅
- [x] **New**: PRO/Premium visual badges. ✅

### **Parity Backlog (To-Do)**
- [x] Advanced Charts (Line/Donut parity). 📊
- [x] Bill Splitter & People integration (PeopleScreen finalized). 👥
- [x] Goals & Budgets modules. 🎯
- [x] Recurring transactions engine. 🔄
- [x] CSV Export/Import logic. 📂
- [x] JSON Export/Import logic (Full Portability). 📂
- [x] Native Performance Optimization (Lifecycle-aware). ⚡
- [x] Native Android Splash Screen. 🚀

---

## 🛠️ 5. Technical Context

- **Architecture**: MVVM with Unidirectional Data Flow (UDF). 🏛️
- **State Management**: Kotlin StateFlow & CollectAsStateWithLifecycle. ⚡
- **Database**: Room (SQLite) with Coroutines & Flow. 💾
- **DI**: Manual Injection (ViewModelProviders) for efficiency. ⚙️
- **Platform**: Antigravity Migration Readiness (CLI v1.0.0 verified). 🚀

---
*SPEC maintained by the Wallet Core Team.* 💼
