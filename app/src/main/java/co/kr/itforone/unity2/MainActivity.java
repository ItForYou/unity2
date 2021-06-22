package co.kr.itforone.unity2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.mwebview)    WebView mwebview;
    //static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    static final int PERMISSION_REQUEST_CODE = 1;
    private long backPrssedTime = 0;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    WebSettings settings;

    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private boolean hasPermissions(String[] permissions){

        // 퍼미션 확인해
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
            if(result!= PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)) {
                    Toast.makeText(getApplicationContext(), "권한을 허용하지 않으면 앱을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);


        settings = mwebview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);//웹에서 파일 접근 여부
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);//웹에서 view port 사용여부
        settings.setUserAgentString(settings.getUserAgentString() + "unity");
        settings.setTextZoom(100);

        mwebview.addJavascriptInterface(new WebviewJavainterface(this),"Android");
        mwebview.setWebViewClient(new Viewmanager(this,this));
        mwebview.setWebChromeClient(new WebchromeClient(this, this));

        mwebview.loadUrl(getString(R.string.home));

    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPrssedTime;

        if(!mwebview.canGoBack() || mwebview.getUrl().contains("all_contact") ) {
            if (0 <= intervalTime && 2000 >= intervalTime) {
                finishAndRemoveTask();
            } else {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            mwebview.goBack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //광고 글쓰기 주소 검색

            case WebchromeClient.FILECHOOSER_LOLLIPOP_REQ_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (resultCode == RESULT_OK ) {
                        if (data != null) {
                            //String dataString = data.getDataString();
                            //  ClipData clipData = data.getClipData();
                            List<Uri> matisse = Matisse.obtainResult(data);
                            //   Log.d("mselected_stcrop",  matisse.get(0).toString());
                            CropImage.activity(matisse.get(0))
                                    .setAspectRatio(1,1)//가로 세로 1:1로 자르기 기능 * 1:1 4:3 16:9로 정해져 있어요
                                    .setCropShape(CropImageView.CropShape.OVAL)
                                    .start(this);
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } else {
                        try {
                            if (filePathCallbackLollipop != null) {
                                filePathCallbackLollipop.onReceiveValue(null);
                                filePathCallbackLollipop = null;

                            }
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result != null) {
                    Uri resultUri = result.getUri();
                    Uri[] arr_Uri = new Uri[1];
                    arr_Uri[0] = resultUri;
                    filePathCallbackLollipop.onReceiveValue(arr_Uri);
                    filePathCallbackLollipop = null;
                } else {
                    try {
                        if (filePathCallbackLollipop != null) {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

    public void set_filePathCallbackLollipop(ValueCallback<Uri[]> filePathCallbackLollipop){
        this.filePathCallbackLollipop = filePathCallbackLollipop;
    }
}