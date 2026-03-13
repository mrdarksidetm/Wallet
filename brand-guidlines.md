# Brand Guidelines & Hardware Restrictions

## 1. CRITICAL Hardware Restrictions (Anti-Crash Defense)
This project is being developed on a local Windows machine with strictly **4GB of RAM**. You must actively defend against memory exhaustion (The "White Wall of Death").
* **COMMAND BAN:** You are STRICTLY FORBIDDEN from autonomously executing `./gradlew installDebug`, `./gradlew build`, or any command that triggers a background Gradle compilation. The human developer will manually trigger all builds.
* **Permitted Commands:** You may run file system checks, read files, or run syntax checks.
* **Memory Optimization:** Always check `gradle.properties` to ensure `org.gradle.jvmargs=-Xmx1024m` is set. Do not import massive libraries unless explicitly instructed.

## 2. Typography & Fonts
* **Primary Fonts:** `Google Sans` and `Google Sans Flex`.
* **Universal Fallback:** `Noto Sans` must be mapped as the explicit fallback in the `FontFamily` to prevent rendering crashes if the primary font fails to load.
* **Scale:** Strictly use Jetpack Compose Material 3 `Typography` scales (`displayLarge`, `headlineMedium`, `bodyLarge`, etc.).

## 3. UI/UX Design Language (Paisa Clone)
The app utilizes the **Google Material 3 Expressive Design Language**. It must feel premium, tactile, and responsive.
* **Corners:** Use large, friendly corner radii (16.dp to 24.dp) on standard Cards and bottom sheets.
* **Loading States:** No custom splash screens. For any database or async operation taking 200ms - 5s, strictly use the M3 `CircularProgressIndicator`.
* **Feedback:** Use `SnackbarHost` for lightweight, non-blocking success/deletion messages.

## 4. Material 3 Components (Mandatory)
Do not build custom components if an official M3 version exists. You must use:
* `SingleChoiceSegmentedButtonRow` for dual-toggles.
* `SwipeToDismissBox` for list item deletion.
* `NavigationBar` for bottom tabs.
* `OutlinedTextField` for form inputs.

## 5. Animations & Physics
* **No Instant Vanishing:** UI elements must not instantly disappear. 
* **List Animations:** Use `Modifier.animateItem()` inside `LazyColumn` so items glide into place when a transaction is deleted or added.
* **Physics:** Rely on native Jetpack Compose spring physics (`spring(dampingRatio, stiffness)`) for smooth, realistic interactions.

## 6. Color Palette
Follow M3 dynamic color logic, but hardcode premium defaults if dynamic theming is off:
* **Backgrounds:** Deep, dark surface colors for dark mode (e.g., `#121212` or very dark gray/blue).
* **Income (Positive):** Clean, vibrant Green.
* **Expense (Negative):** Soft, alert Red.
* **Cards:** Slightly elevated surface colors to stand out from the background without relying on heavy shadows.
