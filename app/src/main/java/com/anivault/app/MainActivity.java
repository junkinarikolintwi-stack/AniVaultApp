package com.anivault.app;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import java.util.Arrays;
import java.util.List;
public class MainActivity extends Activity {
    private WebView webView;
    private ProgressBar progressBar;
    private View fullscreenView;
    private FrameLayout container;
    private static final String SITE_URL = "https://momkjhhhh.blogspot.com/?m=1";
    private static final List<String> ALLOW = Arrays.asList(
        "blogspot.com","blogger.com","bp.blogspot.com",
        "streamwish","filemoon","moonplayer","voe.sx","voe.la",
        "dood","ds2play","streamtape","stp.network","mp4upload",
        "videovard","vidhide","uqload","sendvid","viewsb","hqq",
        "ok.ru","odnoklassniki","sibnet","mixdrop","fembed",
        "jwplatform","jwpcdn","akamaized","cloudfront","fastly",
        "fonts.googleapis","fonts.gstatic","image.tmdb","tmdb.org",
        "cdnjs","jsdelivr","youtube","youtu.be"
    );
    private static final List<String> BLOCK = Arrays.asList(
        "doubleclick","googlesyndication","googleadservices",
        "google-analytics","adservice","pagead",
        "facebook.net","connect.facebook","amazon-adsystem",
        "adnxs","rubiconproject","openx.net","pubmatic",
        "criteo","taboola","outbrain","mgid","popads",
        "popcash","hilltopads","propellerads","trafficjunky",
        "exoclick","juicyads","trafficstars","hotjar","clarity.ms"
    );
    @SuppressLint({"SetJavaScriptEnabled","ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        container = new FrameLayout(this);
        setContentView(container);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, 6));
        container.addView(progressBar);
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        container.addView(webView);
        setup();
        webView.loadUrl(SITE_URL);
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void setup() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setBuiltInZoomControls(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String host = r.getUrl().getHost();
                if (host == null) return true;
                host = host.toLowerCase().replace("www.", "");
                for (String a : ALLOW) if (host.contains(a) || a.contains(host)) return false;
                android.util.Log.d("AniVault", "BLOCKED: " + r.getUrl());
                return true;
            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                String host = r.getUrl().getHost();
                if (host == null) return null;
                host = host.toLowerCase();
                for (String b : BLOCK)
                    if (host.contains(b))
                        return new WebResourceResponse("text/plain","utf-8",
                            new java.io.ByteArrayInputStream("".getBytes()));
                return null;
            }
            @Override public void onPageStarted(WebView v,String u,Bitmap f){
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override public void onPageFinished(WebView v,String u){
                progressBar.setVisibility(View.GONE);
                v.evaluateJavascript("window.open=function(){return null;}", null);
            }
            @Override public void onReceivedSslError(WebView v,SslErrorHandler h,SslError e){
                h.proceed();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView v,boolean d,boolean u,android.os.Message m){
                return false;
            }
            @Override public void onShowCustomView(View view,CustomViewCallback cb){
                if(fullscreenView!=null){cb.onCustomViewHidden();return;}
                fullscreenView=view;
                container.addView(fullscreenView,new FrameLayout.LayoutParams(-1,-1));
                webView.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            @Override public void onHideCustomView(){
                if(fullscreenView==null)return;
                container.removeView(fullscreenView);fullscreenView=null;
                webView.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            @Override public void onPermissionRequest(PermissionRequest r){r.grant(r.getResources());}
            @Override public void onProgressChanged(WebView v,int p){
                progressBar.setProgress(p);
                if(p==100)progressBar.setVisibility(View.GONE);
            }
        });
    }
    @Override public void onBackPressed(){
        if(webView.canGoBack())webView.goBack(); else super.onBackPressed();
    }
    @Override protected void onResume(){super.onResume();webView.onResume();}
    @Override protected void onPause(){super.onPause();webView.onPause();}
    @Override protected void onDestroy(){super.onDestroy();webView.destroy();}
}