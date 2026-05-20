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

### 2. Change the Application Name & Package Name
App Name: Open `app/src/main/AndroidManifest.xml` Inside the <application> tag, change the android:label value:

XML
`android:label="Your Custom App Name"`
Package Name: If you wish to change the unique bundle identifier, update the applicationId and namespace strings inside app/build.gradle:

Groovy
`android {
    namespace 'com.yourname.app'
    defaultConfig {
        applicationId "com.yourname.app"
    }
}`
Note: If you change the package name, make sure to update the folder directory path under app/src/main/java/ and the package declarations inside your Java files accordingly.

### 3. Change App Icons & TV Banner Labels
To use your custom branding, replace or add the image assets located inside the `app/src/main/res/drawable/ directory:`

Mobile App Icon (app_icon.png): A standard square image (recommended resolution: 512x512 pixels).

Android TV Banner (tv_banner.png): A rectangular horizontal image displayed on the Android TV Leanback home screen (standard resolution: 320x180 pixels or 16:9 aspect ratio).

Note: Ensure your image file names contain only lowercase letters, numbers, and underscores (_). Hyphens (-) are not allowed by the Android asset system.

## How to Build the APK Using GitHub Actions
Since the workflow pipeline is pre-configured, you don't need any local setup to download the compiled .apk:

Go to the Actions tab at the top of your repository page.

Under the actions list on the left, click Build Android APK.

Click the Run workflow dropdown menu button on the right side.

Click the green/blue Run workflow button to trigger the execution process.

Once complete (typically takes around 1-2 minutes), navigate to the Releases section of your repository to download the final app-debug.apk.

## License
This project is open-source and available under the MIT License.
