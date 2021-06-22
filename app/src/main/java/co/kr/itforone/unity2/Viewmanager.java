package co.kr.itforone.unity2;

import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Viewmanager extends WebViewClient {
    MainActivity mainActivity;
    Activity activity;
    public Viewmanager(MainActivity mainActivity, Activity activity) {
        this.activity = activity;
        this.mainActivity = mainActivity;
    }
    private Viewmanager(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        view.loadUrl(url);

        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);
    }
}
