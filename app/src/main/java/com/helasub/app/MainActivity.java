package com.helasub.app;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View customView;

    private final List<String> allowedVideoDomains = Arrays.asList(
        "helasub.com", "youtube.com", "youtu.be", "tmdb.org", "image.tmdb.org"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hardware Acceleration ඔන් කිරීම (ස්ක්‍රෝල් එක ස්මූත් වීමට)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );

        // 💡 [NEW FEATURE] ෆිල්ම් එක ප්ලේ වෙද්දී ස්ක්‍රීන් එක SLEEP/OFF වීම වැළැක්වීම
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Sony Side Sense සහ Notch අයිනේ තියෙන දේවල් full screen එකේදී හැංගීමට (Android 9 සහ ඉහළ)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = 
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        super.onCreate(savedInstanceState);

        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        customViewContainer = new FrameLayout(this);
        customViewContainer.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        customViewContainer.setVisibility(View.GONE);

        mainLayout.addView(webView);
        mainLayout.addView(customViewContainer);
        setContentView(mainLayout);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; BRAVIA 4K UR3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36");
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false); 

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                return false; 
            }

            // 📺 වීඩියෝව Full Screen බටන් එක එබූ විට
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                customViewCallback = callback;
                
                // Full Screen කරපු ගමන් බලෙන් Landscape කිරීම
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                
                // 💡 [NEW FEATURE] Immersive Mode එක ඔන් කිරීම (Back, Home, Recent සහ Sony Side Sense සැඟවීම)
                getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // යට බටන් 3 හැංගීම
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // උඩ ස්ටේටස් බාර් එක හැංගීම
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // ස්ක්‍රීන් එක උඩින් ඇද්දත් බටන්ස් ස්ථිරවම හැංගී තිබීම
                );

                webView.setVisibility(View.GONE);
                customViewContainer.addView(customView);
                customViewContainer.setVisibility(View.VISIBLE);
            }

            // Full Screen එකෙන් ඉවත් වන විට (නැවත සාමාන්‍ය තත්වයට පත් කිරීම)
            @Override
            public void onHideCustomView() {
                if (customView == null) return;

                // Immersive Mode එක අයින් කර සාමාන්‍ය බටන් ටික ආපහු පෙන්වීම
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                
                customView.setVisibility(View.GONE);
                customViewContainer.removeView(customView);
                customView = null;
                customViewContainer.setVisibility(View.GONE);
                customViewCallback.onCustomViewHidden();
                webView.setVisibility(View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                if (url.startsWith("blob:") || url.contains("blob") || url.contains(".m3u8") || url.contains(".mp4") || url.contains(".ts") || url.contains(".m4s") || url.contains("stream") || url.contains("player") || url.contains("p2p") || url.contains("live")) {
                    return super.shouldInterceptRequest(view, request);
                }
                for (String domain : allowedVideoDomains) {
                    if (url.contains(domain)) {
                        return super.shouldInterceptRequest(view, request);
                    }
                }
                return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                if (url.contains("helasub.com")) {
                    return false; 
                }
                return true; 
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(
                    "javascript:(function() { " +
                    "var style = document.createElement('style'); " +
                    "style.innerHTML = '* { animation: none !important; transition: none !important; text-shadow: none !important; box-shadow: none !important; } " +
                    "img { will-change: auto !important; image-rendering: auto !important; } " +
                    ".sl-slider-wrapper, .carousel, .g-recaptcha { will-change: auto !important; }'; " +
                    "document.head.appendChild(style); " +
                    "})()", null);
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("https://helasub.com");
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.clearCache(true);
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            webView.getWebChromeClient().onHideCustomView();
        } else if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
