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

    private final List<String> allowedVideoDomains = Arrays.asList(
        "helasub.com", "youtube.com", "youtu.be", "tmdb.org", "image.tmdb.org"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🔥 [TV FIX] ඇප් එක ඇතුළේ ග්‍රැෆික්ස් සහ ස්ක්‍රෝල් එක ස්මූත් කරන්න Hardware Acceleration ඔන් කිරීම
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );

        super.onCreate(savedInstanceState);

        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // 🔥 [TV FIX] ටීවී එකේ රැම් එක මදි වෙලා ක්‍රෑෂ් වීම වැළැක්වීමට Layer Type එක Hardware කිරීම
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
        
        // 🔥 [TV FIX] ස්ක්‍රෝල් එකේදී පින්තූර ලැග් නොවී වේගයෙන් ලෝඩ් වීමට සෙටින්ග්ස්
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccess(true);
        
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; BRAVIA 4K UR3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36");
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false); 
        webView.clearCache(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                return false; 
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                customViewCallback = callback;
                
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
                webView.setVisibility(View.GONE);
                customViewContainer.addView(customView);
                customViewContainer.setVisibility(View.VISIBLE);
            }

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
        });

        webView.loadUrl("https://helasub.com");
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
