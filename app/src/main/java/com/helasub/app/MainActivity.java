package com.helasub.app;

import android.content.Intent;
import android.net.Uri;
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
        "helasub.com",
        "streamtape.com", "image.tmdb.org", "dsk.strp2p.live",
        "youtube.com", "filemoon.to", "filemoon.nl",
        "vidhide.to", "vidhidepro.com", "vidhide.com",
        "voe.sx", "vudeo.co", "doodstream.com", "dood.to", "dood.ws",
        "googleusercontent.com", "googleapis.com" // Google Drive වීඩියෝ සඳහා
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // ටීවී එකේ ඔටෝ ප්ලේ වෙන්න
        
        // Pop-up ඇඩ්ස් සම්පූර්ණයෙන්ම වැළැක්වීම
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false);
        webView.clearCache(true);

        webView.setWebViewClient(new WebViewClient() {
            
            // 1. පසුබිමෙන් ලෝඩ් වෙන දේවල් (Scripts, Styles, Videos) ෆිල්ටර් කිරීම
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                
                // ලෝඩ් වෙන්න හදන ලින්ක් එක අපේ සයිට් එකේ හෝ අනුමත වීඩියෝ සර්වර් එකක එකක්දැයි බැලීම
                for (String domain : allowedVideoDomains) {
                    if (url.contains(domain)) {
                        return super.shouldInterceptRequest(view, request); // වීඩියෝ එක ප්ලේ වෙන්න ඉඩ දෙන්න
                    }
                }
                
                // අනුමත නැති අනෙක් සියලුම ඇඩ් ලින්ක්ස් මෙතනින්ම බ්ලොක් වේ
                return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
            }

            // 2. ප්‍රධාන ලින්ක් ක්ලික් කිරීම් පාලනය
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                
                // සාමාන්‍ය පිටු ඇප් එක ඇතුළෙම ඕපන් වෙන්න හැරීම
                if (url.contains("helasub.com")) {
                    return false; 
                }
                
                // වෙනත් බාහිර ලින්ක් එකක් ක්ලික් වුවහොත් ඇප් එකෙන් පිටත බ්‍රව්සර් එකකින් ඕපන් කිරීම
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
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
