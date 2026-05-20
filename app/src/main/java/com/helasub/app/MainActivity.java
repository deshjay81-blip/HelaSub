package com.helasub.app;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private final List<String> adDomains = Arrays.asList(
        "doubleclick.net", "adservice.google.com", "googlesyndication.com",
        "popads.net", "propellerads.com", "adsterra.com", "infolinks.com"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                for (String domain : adDomains) {
                    if (url.contains(domain)) {
                        return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream("".getBytes()));
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        webView.loadUrl("https://helasub.com");
        setContentView(webView);
    }
}
