package com.helasub.app;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    // 📺 Allowed Domains (අත්‍යවශ්‍ය ප්‍රධාන වෙබ් සේවා පමණි)
    private final List<String> allowedVideoDomains = Arrays.asList(
        "helasub.com", "youtube.com", "youtu.be", "tmdb.org", "image.tmdb.org"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        
        // 🛠️ ප්‍රධාන සෙටින්ග්ස්
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        
        // Android TV Chrome User-Agent එක
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; BRAVIA 4K UR3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36");
        
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // 🛑 ඇඩ්ස් රීඩිරෙක්ට් වැළැක්වීම
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false); 
        
        webView.clearCache(true);

        // Popups සම්පූර්ණයෙන්ම Kill කිරීම
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                return false; 
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            
            // 1. පසුබිමෙන් ලෝඩ් වෙන දේවල් (Ads & Video Streams) ෆිල්ටර් කිරීම
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                
                // 🔥 [SUPER FIX] වීඩියෝ ස්ට්‍රීමිං වලට අදාළ ඕනෑම ලින්ක් එකක් හෝ ෆයිල් format එකක් නම් බ්ලොක් නොකර කෙලින්ම ඉඩ දෙන්න
                if (url.startsWith("blob:") || 
                    url.contains("blob") || 
                    url.contains(".m3u8") || 
                    url.contains(".mp4") || 
                    url.contains(".ts") || 
                    url.contains(".m4s") || 
                    url.contains("stream") || 
                    url.contains("player") || 
                    url.contains("p2p") || 
                    url.contains("live")) {
                    return super.shouldInterceptRequest(view, request);
                }
                
                // අනුමත ප්‍රධාන ඩොමේන් පරීක්ෂාව (HelaSub සහ TMDB පින්තූර සඳහා)
                for (String domain : allowedVideoDomains) {
                    if (url.contains(domain)) {
                        return super.shouldInterceptRequest(view, request);
                    }
                }
                
                // ඉහත කිසිම එකකට අයිති නැති අනෙක් සියලුම රහසිගත ඇඩ් ලින්ක්ස් මෙතනින් බ්ලොක් වේ
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
        setContentView(webView);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
