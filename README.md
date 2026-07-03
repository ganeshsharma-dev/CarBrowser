# 🚗 CarBrowser
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-GPLv3-red.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/ganeshsharma-dev/CarBrowser?include_prereleases)](https://github.com/ganeshsharma-dev/CarBrowser/releases)
[![Downloads](https://img.shields.io/github/downloads/ganeshsharma-dev/CarBrowser/total)](https://github.com/ganeshsharma-dev/CarBrowser/releases)

**CarBrowser** is a lightweight, modern, and privacy-focused web browser specifically designed for use in automotive environments (Android Auto / Automotive OS). It features a "Safety-First" design with large touch targets and a clean interface.

---

## ✨ Key Features

- 🍿 **Media-Optimized:** Watch YouTube and other media sites with automatic session restoration—never lose your video position again.
- 🎨 **Modern UI:** A beautiful Red theme matching automotive aesthetics with Material 3 design components and "little rounded" corners.
- 🛡️ **Privacy First:** No trackers, no analytics, and no data harvesting. Your browsing history stays on your device.
- 🚗 **Car-Ready:** Large buttons and high-contrast UI elements designed for quick interaction while parked.
- 📑 **Tab Management:** Smooth handling of multiple tabs with a dedicated tab manager.
- 🔖 **Smart Bookmarks:** Save your favorite sites and pin them to the customizable Start Page.
- 🌓 **Dynamic Dark Mode:** Automatic darkening of web content to reduce glare during night driving.

---

## 📸 Screenshots

<div align="center">
  <img width="44%" src="docs/screenshots/litemode.gif" alt="Car Browser light mode live screenshot" />
  <img width="44%" src="docs/screenshots/darkmode.gif" alt="Car Browser dark mode live screenshot" />
</div>

---

## 🚀 Installation

You can download the latest stable APK from the [Releases](https://github.com/ganeshsharma-dev/CarBrowser/releases) page.

1. Download the `CarBrowser-x.y.z.apk`.
2. Sideload it onto your Android device.
3. If using with Android Auto, ensure you have enabled **Unknown Sources** in Android Auto Developer Settings.

#### 🛠️ How to Enable Unknown Sources
1. Open **Android Auto Settings** on your phone.
2. Scroll to the bottom and tap the **Version** section 10 times until Developer Mode is active.
3. Tap the three-dot menu (⋮) in the top-right corner -> **Developer settings**.
4. Check the **Unknown sources** box.

---

## 🛠 Tech Stack

- **Language:** Kotlin
- **Networking:** OkHttp 5
- **UI:** XML Layouts with ViewBinding & Material 3
- **Engine:** Android WebView (Chromium-based)

---

## 📦 Automated Builds

This project uses **GitHub Actions** to automatically build and release APKs.
- Every **Push to main** triggers a build check.
- Every **Release Tag** (`v*`) automatically generates a signed Release APK.

---

## 🤝 Contributing

Contributions are welcome! If you have a feature request or found a bug:
1. Open an [Issue](https://github.com/ganeshsharma-dev/CarBrowser/issues).
2. Fork the repository and submit a Pull Request.

## 📄 License

This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.

---

*Developed with ❤️ by [Ganesh Sharma](https://github.com/ganeshsharma-dev)*