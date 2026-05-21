package com.helasub.app;

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

    // 📺 Allowed Domains (අත්‍යවශ්‍ය ප්‍රධාන වෙබ් සේවා පමණි)
    private final List<String> allowedVideoDomains = Arrays.asList(
        "helasub.com", "youtube.com", "youtu.be", "tmdb.org", "image.tmdb.org"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🛠️ Full Screen Layout එකක් සකස් කිරීම
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

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
        
        // Android TV Chrome User-Agent එක මඟින් වීඩියෝ බ්ලොක් වීම් වැළැක්වීම
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; BRAVIA 4K UR3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36");
        
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // 🛑 ඇඩ්ස් රීඩිරෙක්ට් සහ Popups වැළැක්වීම
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false); 
        webView.clearCache(true);

        // 📺 [FULL SCREEN FIXED] වීඩියෝවක් Full Screen කරන විට ක්‍රියාත්මක වන කොටස
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                return false; 
            }

            // වීඩියෝව Full Screen බටන් එක එබූ විට
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                customViewCallback = callback;
                
                // Status Bar සහ Navigation Bar සඟවා සම්පූර්ණ තිරයම ලබා දීම
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
                webView.setVisibility(View.GONE);
                customViewContainer.addView(customView);
                customViewContainer.setVisibility(View.VISIBLE);
            }

            // Full Screen එකෙන් ඉවත් වන විට
            @Override
            public void onHideCustomView() {
                if (customView == null) return;

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
                customView.setVisibility(View.GONE);
                customViewContainer.removeView(customView);
                customView = null;
                customViewContainer.setVisibility(View.GONE);
                customViewCallback.onCustomViewHidden();
                webView.setVisibility(View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            // 1. පසුබිමෙන් ලෝඩ් වෙන දේවල් (Ads & Video Streams) ෆิල්ටර් කිරීම
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                
                // වීඩියෝ ස්ට්‍රීරීම්, m3u8, chunks සහ p2p ලින්ක්ස් වලට කෙලින්ම ඉඩ දීම
                if (url.startsWith("blob:") || url.contains("blob") || url.contains(".m3u8") || url.contains(".mp4") || url.contains(".ts") || url.contains(".m4s") || url.contains("stream") || url.contains("player") || url.contains("p2p") || url.contains("live")) {
                    return super.shouldInterceptRequest(view, request);
                }
                
                // අනුමත ප්‍රධාන ඩොමේන් පරීක්ෂාව
                for (String domain : allowedVideoDomains) {
                    if (url.contains(domain)) {
                        return super.shouldInterceptRequest(view, request);
                    }
                }
                
                // අනෙක් සියලුම ඇඩ් ලින්ක්ස් බ්ලොක් කිරීම
                return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
            }

            // 2. පරිශීලකයා ක්ලික් කරන ලින්ක් සහ ඔටෝ රීඩිරෙක්ට් පාලනය
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                if (url.contains("helasub.com")) {
                    return false; 
                }
                return true; // බාහිර ඇඩ්ස් රීඩිරෙක්ට් වීම් 100% ක් ලොක් කරයි
            }
        });

        webView.loadUrl("https://helasub.com");
    }

    // Back Button එක එබුවම ක්‍රියාත්මක වන ආකාරය
    @Override
    public void onBackPressed() {
        if (customView != null) {
            // වීඩියෝවක් Full screen ප්ලේ වේ නම්, Back එබූ විට Full screen එකෙන් පමණක් ඉවත් වේ
            webView.getWebChromeClient().onHideCustomView();
        } else if (webView != null && webView.canGoBack()) {
            webView.goBack(); // කලින් පිටුවට යාම
        } else {
            super.onBackPressed(); // ඇප් එකෙන් ඉවත් වීම
        }
    }
}
