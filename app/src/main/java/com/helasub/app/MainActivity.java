package com.helasub.app;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View customView;
    private LinearLayout errorLayout; // No Internet වෙනුවට පෙන්වන Layout එක

    private final List<String> allowedVideoDomains = Arrays.asList(
        "helasub.com", "youtube.com", "youtu.be", "tmdb.org", "image.tmdb.org"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🛠️ Main Layout එක සකස් කිරීම
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // WebView එක
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Full Screen වීඩියෝ Container එක
        customViewContainer = new FrameLayout(this);
        customViewContainer.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        customViewContainer.setVisibility(View.GONE);

        // 🔥 Modern Black No-Internet Screen එකක් කෝඩ් එකෙන්ම නිර්මාණය කිරීම
        createErrorLayout();

        mainLayout.addView(webView);
        mainLayout.addView(customViewContainer);
        mainLayout.addView(errorLayout); // Error layout එක එකතු කිරීම
        setContentView(mainLayout);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        
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
                
                // 🔥 [FIX] Portrait එකේ හිටියත්, Full Screen කරපු ගමන් බලෙන් Landscape කිරීම
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
                // 🔥 [FIX] Full screen එකෙන් අයින් වුණාම ආපහු ෆෝන් එකේ සාමාන්‍ය Rotation එකට ඉඩ දීම
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

            // 🛑 [NEW FIX] ඉන්ටර්නෙට් නැති වුණොත් කැත Error එක වෙනුවට අපේ Screen එක පෙන්වීම
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });

        webView.loadUrl("https://helasub.com");
    }

    // 🖼️ No Internet Layout එක කෝඩ් එකෙන්ම ඩිසයින් කිරීම (Black, Red & White Theme)
    private void createErrorLayout() {
        errorLayout = new LinearLayout(this);
        errorLayout.setOrientation(LinearLayout.VERTICAL);
        errorLayout.setGravity(Gravity.CENTER);
        errorLayout.setBackgroundColor(Color.parseColor("#121212")); // Dark Black Background
        errorLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        errorLayout.setVisibility(View.GONE);

        // 🛑 Error Message Title
        TextView tvErrorTitle = new TextView(this);
        tvErrorTitle.setText("Connection Failed");
        tvErrorTitle.setTextColor(Color.parseColor("#E50914")); // Netflix Red Color
        tvErrorTitle.setTextSize(24);
        tvErrorTitle.setPadding(0, 0, 0, 10);
        errorLayout.addView(tvErrorTitle);

        // 💬 Sub Message
        TextView tvErrorSub = new TextView(this);
        tvErrorSub.setText("Please check your internet connection and try again.");
        tvErrorSub.setTextColor(Color.WHITE);
        tvErrorSub.setTextSize(16);
        tvErrorSub.setPadding(0, 0, 0, 30);
        errorLayout.addView(tvErrorSub);

        // 🔄 Modern Retry Button
        Button btnRetry = new Button(this);
        btnRetry.setText("RETRY");
        btnRetry.setBackgroundColor(Color.parseColor("#E50914"));
        btnRetry.setTextColor(Color.WHITE);
        btnRetry.setPadding(40, 20, 40, 20);
        
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.reload(); // සයිට් එක ආයෙත් ලෝඩ් කරන්න ට්‍රයි කිරීම
            }
        });
        errorLayout.addView(btnRetry);
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
