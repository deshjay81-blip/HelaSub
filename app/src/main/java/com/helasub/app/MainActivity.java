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
    
    // බ්ලොක් කරන්න ඕනේ ප්‍රධානම Ad Domains ලැයිස්තුව (තවත් වැඩි කලා)
    private final List<String> adDomains = Arrays.asList(
        "doubleclick.net", "adservice.google.com", "googlesyndication.com",
        "popads.net", "propellerads.com", "adsterra.com", "infolinks.com",
        "juicyads.com", "exoclick.com", "onclickalgo.com", "adsco.re", "ch.marketdeathly.com",
        "yandex.ru", "adnxs.com", "betweendigital.com", "skinnycrawlinglax.com", "overturncogetconcealment.com", "realizationnewestfangs.com", "cardboardcrispyrover.com", "cdn.cloudvideosa.com", "criteo.com"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        
        // Settings සකස් කිරීම
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        
        // Pop-up Ads බ්ලොක් කිරීම සඳහා වූ විශේෂ Settings දෙකක්
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false);

        // Cache එක Clear කිරීම (පරණ ඇඩ්ස් ලෝඩ් වීම වැළැක්වීමට)
        webView.clearCache(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                for (String domain : adDomains) {
                    if (url.contains(domain)) {
                        // ඇඩ් ලින්ක් එකක් අහුවුණොත් මෙතනින්ම බ්ලොක් කරයි
                        return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // පිටුව ලෝඩ් වී ඉවර වුණාට පස්සේ සයිට් එකේ තියෙන Ad placeholders මැකීමට පොඩි JS එකක් රන් කිරීම
                view.evaluateJavascript(
                    "javascript:(function() { " +
                    "var amsk = document.querySelectorAll('[id^=\"ad\"], [class^=\"ad\"], ins, iframe');" +
                    "for (var i=0; i<amsk.length; i++) { amsk[i].style.display='none'; amsk[i].innerHTML=''; }" +
                    "})()", null);
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("https://helasub.com");
        setContentView(webView);
    }

    // 🔥 මෙන්න මේකෙන් තමයි Back Button එක එබුවම කලින් පිටුවට යන්නේ!
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack(); // කලින් පිටුවට යන්න
        } else {
            super.onBackPressed(); // කලින් පිටු නැත්නම් ඇප් එක වහන්න
        }
    }
}
