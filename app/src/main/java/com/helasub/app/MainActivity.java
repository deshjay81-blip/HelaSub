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

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        
        // Settings සකස් කිරීම
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        
        // Pop-up වළක්වන Settings
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false);
        webView.clearCache(true);

        webView.setWebViewClient(new WebViewClient() {
            
            // 1. මේකෙන් පසුබිමෙන් ලෝඩ් වෙන Scripts/Images (Ads) ඔක්කොම බ්ලොක් කරනවා helasub.com නොවේ නම්
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                
                // ලින්ක් එකේ helasub.com තියෙනවාද කියලා විතරක් බලයි
                if (url.contains("helasub.com")) {
                    return super.shouldInterceptRequest(view, request); // සාමාන්‍ය පරිදි ලෝඩ් වෙන්න හරින්න
                }
                
                // helasub.com නැති හැමදේම (ඇඩ්ස්, ට්‍රැකර්ස්) මෙතනින්ම බ්ලොක් කරලා හිස් response එකක් යවයි
                return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
            }

            // 2. මේකෙන් පරිශීලකයා ක්ලික් කරන ප්‍රධාන ලින්ක් (Navigation) ෆිල්ටර් කරනවා
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                
                if (url.contains("helasub.com")) {
                    return false; // ඇප් එක ඇතුළෙම පිටුව ලෝඩ් වෙන්න දෙන්න
                }
                
                // වැරදීමකින්වත් වෙනත් සයිට් එකක ලින්ක් එකක් ක්ලික් වුණොත්, ඇප් එක ඇතුළේ ලෝඩ් වෙන්නේ නැතුව 
                // ෆෝන් එකේ/ටීවී එකේ තියෙන සාමාන්‍ය බ්‍රව්සර් එකකින් (Chrome වගේ) ඕපන් කරන්න කියලා එලියට තල්ලු කරයි.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }
        });

        webView.loadUrl("https://helasub.com");
        setContentView(webView);
    }

    // Back Button එක එබුවම කලින් පිටුවට යාම
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
