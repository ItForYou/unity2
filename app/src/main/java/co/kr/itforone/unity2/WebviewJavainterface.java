package co.kr.itforone.unity2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

public class WebviewJavainterface {
    MainActivity mainActivity;


    public WebviewJavainterface(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @JavascriptInterface
    public void test(String number) {

    }
}
