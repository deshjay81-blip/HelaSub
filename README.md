# Web-to-App Android Native Studio (Android TV & Mobile Support)

This is a lightweight (under 3MB) Android Native WebView application built using Java. It is optimized to run smoothly on both **Android TV** (Android Leanback) and **Mobile devices**, featuring an **Inbuilt Ad-Blocker** and custom back-button behavior.

With integrated **GitHub Actions**, you can compile and generate the production-ready APK directly in the cloud without installing Android Studio or setting up a local development environment.

---

## Features
* 📺 **Dual Compatibility:** Works seamlessly on Android TV (Landscape forced) and mobile phones.
* 🛡️ **Inbuilt Ad-Blocker:** Intercepts and blocks popular ad network domains (Adsterra, PopAds, Google Ads, etc.) dynamically.
* 🔄 **Back Button Fixed:** Overridden `onBackPressed` handler keeps the application from closing when navigating internal web pages.
* 📦 **Cloud Compilation:** Automated APK generation via GitHub Actions workflows.

---

## How to Customize This Project

You can easily modify this project to point to your own website, change the application name, or update the icons. Follow the instructions below:

### 1. Change the Website URL & Ad Domains
Open `app/src/main/java/com/helasub/app/MainActivity.java` and modify the following sections:
* **Website URL:** Look for `webView.loadUrl("https://helasub.com");` near the bottom of the `onCreate` method and replace the link with your domain.
* **Ad Blocklist:** To add more ad domains to block, update the `adDomains` array list at the top of the class:
  ```java
  private final List<String> adDomains = Arrays.asList(
      "doubleclick.net", "adsterra.com", "your-target-ad-domain.com"
  );
