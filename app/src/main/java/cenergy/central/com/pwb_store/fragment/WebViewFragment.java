package cenergy.central.com.pwb_store.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.WebViewBackBus;
import cenergy.central.com.pwb_store.manager.bus.event.WebViewEndedBus;
import cenergy.central.com.pwb_store.manager.bus.event.WebViewTitleBus;
import cenergy.central.com.pwb_store.utils.DialogUtils;


/**
 * Created by nuuneoi on 11/16/2014.
 * Modified by C.Thawanapong on 8/24/2016 AD.
 * Email chavit.t@th.zalora.com
 */
@SuppressWarnings("unused")
public class WebViewFragment extends Fragment {
    public static final int MODE_URL = 0;
    public static final int MODE_HTML = 1;
    public static final int MODE_ADD_CARD = 2;
    public static final int MODE_PAYMENT = 3;
    private static final String TAG = "WebViewFragment";

    private static final String ARG_WEB_URL = "ARG_WEB_URL";
    private static final String ARG_MODE = "ARG_MODE";
    private static final String ARG_TITLE = "ARG_TITLE";
    //View Members
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    //Callback Members
    final WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress < 100 && mProgressBar.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            mProgressBar.setProgress(newProgress);

            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };
    @BindView(R.id.web_view)
    WebView mWebView;
    //Data Members
    private String mWebUrl;
    private String mTitle;
    private int mMode;
    private ProgressDialog mProgressDialog;
    final WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onPageStarted: url = " + url);
            super.onPageStarted(view, url, favicon);
            switch (mMode) {
                case MODE_PAYMENT:
                    showProgressDialog();
                    break;
                default:
                    break;
            }
            EventBus.getDefault().post(new WebViewTitleBus(mTitle, url));
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onPageFinished: url = " + url);
            switch (mMode) {
                case MODE_PAYMENT:
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    break;
                default:
                    break;
            }
            super.onPageFinished(view, url);
            EventBus.getDefault().post(new WebViewTitleBus(mTitle, url));
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(Contextor.getInstance().getContext().getString(R.string.error_ssl_cert_invalid));
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String lastPathSegment;
            switch (mMode) {
                case MODE_HTML:
                case MODE_URL:
                    if (url.equals(mWebUrl)) {
                        view.loadUrl(url);
                    }else {
                        view.loadUrl(url);
                    }
                case MODE_ADD_CARD:
                    lastPathSegment = url.substring(url.lastIndexOf('/') + 1);
                    if (lastPathSegment.contains("success")) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else if (lastPathSegment.contains("error")) {
                        String errorString = lastPathSegment.substring(lastPathSegment.lastIndexOf("=") + 1);
                        try {
                            showAlertDialog(URLDecoder.decode(errorString, "UTF-8"));
                        } catch (UnsupportedEncodingException ex) {
                            showAlertDialog(errorString);
                        }
                    }
                    break;
                case MODE_PAYMENT:
                    lastPathSegment = url.substring(url.lastIndexOf('?') + 1);
                    if (lastPathSegment.contains("id")) {
                        String orderId = lastPathSegment.substring(lastPathSegment.lastIndexOf("=") + 1);
//                        Intent intent = new Intent(getContext(), AddressActivity.class);
//                        intent.putExtra(AddressActivity.ARG_ORDER_ID, orderId);
//                        getActivity().setResult(Activity.RESULT_OK, intent);
//                        getActivity().finish();
                    } else if (lastPathSegment.contains("error")) {
                        String errorString = lastPathSegment.substring(lastPathSegment.lastIndexOf("=") + 1);
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        try {
                            showAlertDialog(URLDecoder.decode(errorString, "UTF-8"));
                        } catch (UnsupportedEncodingException ex) {
                            showAlertDialog(errorString);
                        }
                    } else if (lastPathSegment.contains("message")) {
                        String errorString = lastPathSegment.substring(lastPathSegment.lastIndexOf("=") + 1);
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        try {
                            showAlertDialog(URLDecoder.decode(errorString, "UTF-8"));
                        } catch (UnsupportedEncodingException ex) {
                            showAlertDialog(errorString);
                        }
                    } else {
                        view.loadUrl(url);
                    }
                    break;
            }

            return true;
        }
    };

    public WebViewFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static WebViewFragment newInstance(String webUrl, int mode, String title) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WEB_URL, webUrl);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onEvent(WebViewBackBus webViewBackBus) {
        if (webViewBackBus.isBackPressed()) {
            if (isWebViewCanGoBack()) {
                mWebView.goBack();
            } else {
                EventBus.getDefault().post(new WebViewEndedBus(true));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        if (getArguments() != null) {
            mWebUrl = getArguments().getString(ARG_WEB_URL);
            mMode = getArguments().getInt(ARG_MODE);
            mTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.setProgressTintList(ColorStateList.valueOf(Color.DKGRAY));
        } else {
            mProgressBar.getProgressDrawable().setColorFilter(Color.DKGRAY, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.loadUrl(mWebUrl);

        String internalFilePath = "file:///android_asset/html/";

        switch (mMode) {
            case MODE_URL:
                mWebView.loadUrl("<html>"
                        +
                "<div class=\"col-xs-12 col-sm-12 col-md-12 col-lg-12 tab-product-detail-wrapper\">\n" +
                        "      <section class=\"ckeditor ckeditor-productdetail\">\n" +
                        "  <div class=\"-wrapper\">\n" +
                        "    <h2>Product details of (REFURBISHED)IPHONE 5S 16GB (Gold)</h2>\n" +
                        "\n" +
                        "<p><strong>(REFURBISHED)IPHONE 5S 16GB (Gold)</strong><br>\n" +
                        "<br>\n" +
                        "หากคุณเป็นสาApple วกของสินค้า Apple แบรนด์ดัง แล้วละก็ คงพลาดไม่ได้กับ Apple iPhone 5s ที่มาพร้อมความสามารถที่หลากหลาย ให้คุณได้ตื่นตาประหลาดใจอย่างที่สุด ไม่ว่ารูปแบบไลฟ์สไตล์ของคุณจะเป็นเช่นไร มือถือระบบ IOS ที่มากประสิทธิภาพเครื่องนี้ จะสามารถตอบโจทย์การใช้งานสมาร์ทโฟนได้อย่างลงตัว<br>\n" +
                        "<br>\n" +
                        "<strong>คุณสมบัติ</strong><br>\n" +
                        "<br>\n" +
                        "<strong>รูปแบบการประมวลผลที่รวดเร็ว</strong><br>\n" +
                        "ด้วยชิบเซ็ต A7 ที่ทำงานประสานกับระบบปฏิบัติการ iOS7 จะทำให้คุณสามารถที่จะประมวลข้อมูลต่างๆอย่างลื่นไหล ไม่มีสะดุด เพราะชิบเซ็ต A7 ของ - Apple iPhone 5s เครื่องนี้มีการประมวลผลแบบ 64-bit นอกจากนี้ยังมี M7 coprocessor ผู้ช่วยการประมวลผลชั้นยอด ซึ่งทำให้คุณสามารถทำงาน และใช้งานเครื่องได้อย่างเต็มประสิทธิภาพ รวดเร็วไม่มีสะดุดเลยทีเดียว<br>\n" +
                        "<br>\n" +
                        "<strong>การใช้งานและรักษาข้อมูลที่ปลอดภัยขึ้น</strong><br>\n" +
                        "Apple iPhone 5s มาพร้อมกับความสามารถในการระบุตัวตนผู้ใช้แบบลายนิ้วมือ ที่ผู้ใช้ต้องทำก็แค่วางนิ้วไปที่ปุ่ม home ของเครื่อง ระบบการระบุตัวตนด้วยรอยนิ้วมือที่ซ่อนอยู่ในปุ่นดังกล่าว ก็จะทำการยืนยันตัวตนของคุณ ให้คุณอุ่นใจกับข้อมูลสำคัญที่มากคุณค่าของคุณว่าจะปลอดภัยอย่างแน่นอน<br>\n" +
                        "<br>\n" +
                        "<strong>การติดต่อที่ใกล้ขึ้นแม้ระยะทางจะห่างไกล</strong><br>\n" +
                        "หากคุณไม่สามารถอยู่เพื่อติดต่อผู้ที่คุณต้องการติดต่อด้วย สิ่งที่คุณต้องทำก็คือการเปิดฟังก์ชั่น Facetime ของ Apple iPhone 5s ซึ่งมาพร้อมกับการบันทึกและส่งต่อภาพแบบ HD<br>\n" +
                        "<br>\n" +
                        "<strong>Specifications</strong><br>\n" +
                        "<br>\n" +
                        "<strong>General</strong><br>\n" +
                        "- 2G Network : GSM 850 / 900 / 1800 / 1900 - all models<br>\n" +
                        "- CDMA 800 / 1700 / 1900 / 2100 - A1533 (CDMA), A1453<br>\n" +
                        "- 3G Network : HSDPA 850 / 900 / 1700 / 1900 / 2100 - A1533 (GSM), A1453 CDMA2000 1xEV-DO - A1533 (CDMA), A1453<br>\n" +
                        "- HSDPA 850 / 900 / 1900 / 2100 - A1457, A1530<br>\n" +
                        "- 4G Network : LTE- all models<br>\n" +
                        "- SIM : Nano-SIM<br>\n" +
                        "<br>\n" +
                        "<strong>Body</strong><br>\n" +
                        "- Dimensions : 123.8 x 58.6 x 7.6 mm (4.87 x 2.31 x 0.30 in)<br>\n" +
                        "- Weight : 112 g (3.95 oz)<br>\n" +
                        "- Fingerprint sensor (Touch ID)<br>\n" +
                        "<br>\n" +
                        "<strong>Display</strong><br>\n" +
                        "- Type : LED-backlit IPS LCD, capacitive touchscreen, 16M colors<br>\n" +
                        "- Size : 640 x 1136 pixels, 4.0 inches (~326 ppi pixel density)<br>\n" +
                        "- Multitouch : Yes<br>\n" +
                        "- Protection : Corning Gorilla Glass, oleophobic coating<br>\n" +
                        "<br>\n" +
                        "<strong>Sound</strong><br>\n" +
                        "- Alert types : Vibration, proprietary ringtones<br>\n" +
                        "- Loudspeaker : Yes<br>\n" +
                        "- 3.5mm jack : Yes<br>\n" +
                        "<br>\n" +
                        "<strong>Memory</strong><br>\n" +
                        "- Card slot : No<br>\n" +
                        "- Internal<br>\n" +
                        "- 16/32/64 GB storage, 1 GB RAM DDR3<br>\n" +
                        "<br>\n" +
                        "<strong>Data</strong><br>\n" +
                        "- GPRS : Yes<br>\n" +
                        "- EDGE : Yes<br>\n" +
                        "- Speed : DC-HSDPA, 42 Mbps; HSDPA, 21 Mbps; HSUPA, 5.76 Mbps, LTE, 100 Mbps; EV-DO Rev. A, up to 3.1 Mbps<br>\n" +
                        "- WLAN : Wi-Fi 802.11 a/b/g/n, dual-band, Wi-Fi hotspot<br>\n" +
                        "- Bluetooth : Yes, v4.0 with A2DP<br>\n" +
                        "- USB : Yes, v2.0<br>\n" +
                        "<br>\n" +
                        "<strong>Camera</strong><br>\n" +
                        "- Primary : 8 MP, 3264x2448 pixels, autofocus, dual-LED (True Tone) flash, check quality<br>\n" +
                        "- Features : 1/3'' sensor size, 1.5 ?m pixel size, simultaneous HD video and image recording, touch focus, geo-tagging, face detection, HDR panorama, HDR photo<br>\n" +
                        "- Video : Yes, 1080p@30fps, 720p@120fps, video stabilization, check quality<br>\n" +
                        "- Secondary : Yes, 1.2 MP, 720p@30fps, face detection, FaceTime over Wi-Fi or Cellular<br>\n" +
                        "<br>\n" +
                        "- OS : iOS 7, upgradable to iOS 7.0.3<br>\n" +
                        "- Chipset : Apple A7<br>\n" +
                        "- CPU : Dual-core 1.3 GHz Cyclone (ARM v8-based)<br>\n" +
                        "- GPU : PowerVR G6430 (quad-core graphics)<br>\n" +
                        "- Sensors : Accelerometer, gyro, proximity, compass<br>\n" +
                        "- Messaging : iMessage, SMS (threaded view), MMS, Email, Push Email<br>\n" +
                        "- Browser : HTML (Safari)<br>\n" +
                        "- Radio : No<br>\n" +
                        "- GPS : Yes, with A-GPS support and GLONASS<br>\n" +
                        "- Java : No<br>\n" +
                        "- Colors : Space Gray, White/Silver, Gold<br>\n" +
                        "- Active noise cancellation with dedicated mic<br>\n" +
                        "- AirDrop file sharing<br>\n" +
                        "- Siri natural language commands and dictation<br>\n" +
                        "- iCloud cloud service<br>\n" +
                        "- iCloud Keychain<br>\n" +
                        "- Twitter and Facebook integration<br>\n" +
                        "- TV-out<br>\n" +
                        "- Maps<br>\n" +
                        "- iBooks PDF reader<br>\n" +
                        "- Audio/video player/editor<br>\n" +
                        "- Organizer<br>\n" +
                        "- Document viewer/editor ((Word, Excel, PowerPoint)<br>\n" +
                        "- Image viewer/editor<br>\n" +
                        "- Voice memo/dial/command<br>\n" +
                        "- Predictive text input Battery - Li-Po 1560 mAh battery (5.92 Wh)</p>\n" +
                        "\n" +
                        "                  <div class=\"attributes-heading\">ตัวกรองสี</div>\n" +
                        "                                            <img src=\"https://s2.tctcdn.com/attribute/2/BodyPart_edca64d2-1d4a-4efc-977c-489cd8a101f6.jpg\" alt=\"ชมพู\"><br>\n" +
                        "                                            <div class=\"attributes-heading\">สีเฉพาะสินค้า</div>\n" +
                        "                                            <img src=\"https://s0.tctcdn.com/attribute/3/BodyPart_b16b09aa-a14d-434c-a3a5-27025c14f530.jpg\" alt=\"Light Blue [ #ADD8E6 ]\"><br>\n" +
                        "                                            <div class=\"attributes-heading\">วัสดุของถุงมือ</div>\n" +
                        "                                            ผ้า<br>\n" +
                        "                                            <div class=\"attributes-heading\">รุ่นสินค้า</div>\n" +
                        "                                            เอสอี<br>\n" +
                        "                                            <div class=\"attributes-heading\">ขนาดถุงมือ</div>\n" +
                        "                                            S<br>\n" +
                        "                                            <div class=\"attributes-heading\">ขนาดของถุงมือ</div>\n" +
                        "                                            30<br>\n" +
                        "                                          </div>\n" +
                        "</section>\n" +
                        "    </div>\n" +
                        "</html>\n");
                break;

            case MODE_HTML:
                mWebView.loadDataWithBaseURL(internalFilePath, mWebUrl, "text/html", "UTF-8", null);
                break;

            case MODE_ADD_CARD:
                mWebView.loadUrl(mWebUrl);
                break;

            case MODE_PAYMENT:
                mWebView.loadUrl(mWebUrl);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    /*
         * Save Instance State Here
         */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putString(ARG_WEB_URL, mWebUrl);
        outState.putInt(ARG_MODE, mMode);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        mWebUrl = savedInstanceState.getString(ARG_WEB_URL);
        mMode = savedInstanceState.getInt(ARG_MODE);
    }

    private boolean isWebViewCanGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });

        builder.show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(getContext());
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }
}
