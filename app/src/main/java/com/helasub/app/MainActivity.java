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

    // 📺 Allowed Domains (වීඩියෝ, පින්තූර සහ ට්‍රැකර්ස් නොවන අත්‍යවශ්‍ය සේවා)
    private final List<String> allowedVideoDomains = Arrays.asList(
        "helasub.com", "strp2p", "live", "p2p",
        "streamtape", "filemoon", "vidhide", "voe", "vudeo", "dood",
        "googleusercontent", "googleapis", "youtube.com", "youtu.be",
        "tmdb.org", "image.tmdb.org", "jwplayer", "video"
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
        
        // 🔥 [NEW] වීඩියෝ ප්ලේයර්ස් වලට ඇප් එක බ්‍රව්සර් එකක් වගේ පෙන්වීමට Custom User-Agent එකක් දීම
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; BRAVIA 4K UR3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36");
        
        // 📺 වීඩියෝ ඔටෝ ප්ලේ සහ මික්ස්ඩ් කන්ටෙන්ට්ස් වලට ඉඩ දීම
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // 🛑 ඇඩ්ස් සහ පොප්-අප්ස් බ්‍රව්සර් එකට රීඩිරෙක්ට් වීම වැළැක්වීම
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false); 
        
        webView.clearCache(true);

        // JS වලින් බලෙන් අලුත් Window එකක් (ඇඩ් එකක්) ඕපන් කරන එක ලොක් කිරීම
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
                
                // වීඩියෝ ස්ට්‍රීම් එකක් (blob:) හෝ static files, m3u8 වැනි දේ නම් කෙලින්ම ඉඩ දෙන්න
                if (url.startsWith("blob:") || url.contains("blob") || url.contains(".m3u8") || url.contains(".mp4")) {
                    return super.shouldInterceptRequest(view, request);
                }
                
                // අනුමත වීඩියෝ, TMDB පින්තූර සහ අපේ සයිට් එකේ ඩොමේන් පරීක්ෂාව
                for (String domain : allowedVideoDomains) {
                    if (url.contains(domain)) {
                        return super.shouldInterceptRequest(view, request);
                    }
                }
                
                // ඇඩ්ස් ඔක්කොම මෙතනින් බ්ලොක් වේ
                return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
            }

            // 2. පරිශීලකයා ක්ලික් කරන ලින්ක් සහ ඔටෝ රීඩිරෙක්ට් පාලනය
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                
                // අපේ සයිට් එක ඇතුළේ නම් විතරක් ඇප් එක ඇතුළෙම යන්න දෙන්න
                if (url.contains("helasub.com")) {
                    return false; 
                }
                
                // අන් සියලුම බාහිර ඇඩ් රීඩිරෙක්ට් කිරීම් බ්‍රව්සර් එකට යන්න නොදී මෙතනින්ම බ්ලොක් කරයි!
                return true;
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
