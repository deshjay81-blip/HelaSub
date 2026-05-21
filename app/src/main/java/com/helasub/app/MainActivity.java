package com.helasub.app;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    // 📺 වීඩියෝ ප්ලේ වෙන්න අනිවාර්යයෙන්ම ඉඩ දිය යුතු (Allowed) සර්වර්ස් ලැයිස්තුව
    private final List<String> allowedVideoDomains = Arrays.asList(
        "helasub.com", "strp2p.live",
        "dsk.strp2p.live", "youtube.com", "streamta.pe",
        "image.tmdb.org", "filemoon.to", "filemoon.nl",
        "vidhide.to", "vidhidepro.com", "vidhide.com",
        "voe.sx", "vudeo.co", "doodstream.com", "dood.to", "dood.ws",
        "googleusercontent.com", "googleapis.com"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // TV එකේ වීඩියෝ ඔටෝ ප්ලේ වීමට
        
        // Pop-up සහ අලුත් Windows ඕපන් වීම සම්පූර්ණයෙන්ම වැළැක්වීම
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false);
        webView.clearCache(true);

        webView.setWebViewClient(new WebViewClient() {
            
            // 1. පසුබිමෙන් ලෝඩ් වෙන Scripts, Images සහ Video Chunks (blob) ෆිල්ටර් කිරීම
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                
                // 🔥 1. ලින්ක් එක blob: එකක් නම් කිසිම බාධාවකින් තොරව වීඩියෝව ප්ලේ වෙන්න ඉඩ දෙන්න
                if (url.startsWith("blob:")) {
                    return super.shouldInterceptRequest(view, request);
                }
                
                // 2. අනුමත වීඩියෝ ඩොමේන් එකක්දැයි පරීක්ෂා කිරීම
                for (String domain : allowedVideoDomains) {
                    if (url.contains(domain)) {
                        return super.shouldInterceptRequest(view, request);
                    }
                }
                
                // අනුමත නැති අනෙක් සියලුම ඇඩ් ලින්ක්ස් මෙතනින්ම බ්ලොක් වේ
                return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
            }

            // 2. 🛑 රීඩිරෙක්ට් සහ බාහිර ලින්ක්ස් ක්ලික් කිරීම් සම්පූර්ණයෙන්ම බ්ලොක් කිරීම
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                
                // helasub.com සයිට් එක ඇතුළේ පිටු විතරක් ඇප් එක ඇතුළේ සාමාන්‍ය පරිදි ලෝඩ් වෙන්න හැරීම
                if (url.contains("helasub.com")) {
                    return false; 
                }
                
                // 🔥 helasub.com නොවන වෙනත් ඕනෑම ඇඩ් රීඩිරෙක්ට් එකක් හෝ බාහිර ඇප්/බ්‍රව්සර් ලින්ක් එකක් 
                // මෙතනින්ම Kill කරලා දමයි (True කිරීමෙන් රීඩිරෙක්ට් වීම නවතී)
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
