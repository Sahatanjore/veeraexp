# VEERA EXP — starter scaffold

This is a **working starting point**, not the finished app from the spec.
It compiles, runs, and shows a real (Room-backed) balance on screen —
everything past that still needs to be built.

## What's implemented
- Gradle project (Kotlin DSL, AGP 8.5, Kotlin 1.9.24, Room via KSP)
- GitHub Actions CI (`.github/workflows/android-build.yml`) — lint, unit
  tests, debug + release APK build, optional signing
- Room database with 5 entities: `Transaction`, `Category`,
  `InvestmentGoal`, `Budget`, `AppSettings`
- DAOs for all of the above, including the SQL for monthly sums and
  per-category expense totals
- `FinanceRepository` — the single place that enforces:
  - `balance = openingBalance + income - expense - goalTransfers`
  - goal contributions/withdrawals as explicit `GOAL_TRANSFER`
    transactions (per spec section 8 — goals never silently touch
    the spendable balance)
- Default category seed data (Tamil-friendly icon keys, matches spec
  section 7 exactly)
- English + Tamil string resources (`values/` and `values-ta/`)
- **Bottom navigation** with 5 real tabs (Home, Transactions, Goals,
  SAHA, Settings) — `MainActivity`
- **Home dashboard** — live balance, this-month income/expense, a
  data-derived SAHA summary line, and a recent-transactions list, all
  backed by Room Flows (section 2)
- **Quick Add** — bottom sheet for Income/Expense that writes a real
  `Transaction` row, with category dropdown, note, payment method,
  and vibration + toast feedback on save (section 4)
- **Transactions screen** — full list, live search by note, swipe-left
  to delete with an undo snackbar shell (section 6; sort/date/category
  filters and swipe-right edit/duplicate are the next increment)
- **Goals screen** — create a goal, add money to it (explicit
  balance→goal transfer via the repository), progress bar per goal
  (section 8)
- **Settings screen** — opening balance (editable anytime, section 3),
  dark mode (system/light/dark), language (English/Tamil), sound/
  vibration/floating-quick-add toggles — all persisted to
  `AppSettings` and reflected live

## What's NOT built yet (still just the spec)
- SAHA rule-based insight set beyond the one summary line, and the
  SAHA chat UI (section 11) — the `SahaFragment` currently shows a
  real balance-derived line as a placeholder for the fuller engine
- Applying the saved dark-mode/language settings at app startup
  (they're saved correctly; wiring `AppCompatDelegate` /
  `AppCompatDelegate.setApplicationLocales` to read them on launch is
  the next step)
- Category management screen (add/edit/delete/icon/color — section 7;
  DAO support already exists via `CategoryDao.softDelete`)
- Budget screen and category-budget progress bars (section 9; `Budget`
  entity + DAO exist, no UI yet)
- PDF report generation and in-app viewer (section 12)
- Receipt capture (camera/gallery) and compression (section 13)
- App lock — PIN + biometric (section 14; `security-crypto` and
  `biometric` deps are already in `build.gradle.kts`)
- Theme engine (15+ themes) and the animation set in section 16
- Floating quick-add overlay (section 5)
- JSON/CSV backup and restore (section 21)
- Undo-on-swipe-delete currently shows a snackbar but doesn't yet
  restore the deleted row — add before shipping
- Room migrations beyond version 1 (add these as soon as the schema
  changes — `fallbackToDestructiveMigration()` was deliberately left
  out so a bad migration fails loudly instead of wiping data)

## Opening this project
1. Unzip, open the root folder in Android Studio (Koala or newer).
2. Let Gradle sync — first sync downloads the Gradle 8.7 distribution
   and dependencies, so it needs network access.
3. Run on an emulator or device (minSdk 24 / Android 7.0+).

## CI
Push to `main` or `develop` to trigger `.github/workflows/android-build.yml`.
To get signed release APKs, add these repo secrets: `SIGNING_KEY`
(base64-encoded keystore), `KEY_ALIAS`, `KEY_STORE_PASSWORD`,
`KEY_PASSWORD`. Without them, remove the signing step or it will fail
on pushes to `main`.
