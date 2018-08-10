package cenergy.central.com.pwb_store.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.ProductDetailFragment;
import cenergy.central.com.pwb_store.fragment.WebViewFragment;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.bus.event.OverviewBus;
import cenergy.central.com.pwb_store.manager.bus.event.PromotionItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.RecommendBus;
import cenergy.central.com.pwb_store.manager.bus.event.SpecDaoBus;
import cenergy.central.com.pwb_store.manager.bus.event.UpdateBageBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.ExtensionProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetailAvailableOption;
import cenergy.central.com.pwb_store.model.ProductDetailAvailableOptionItem;
import cenergy.central.com.pwb_store.model.ProductDetailDao;
import cenergy.central.com.pwb_store.model.ProductDetailImage;
import cenergy.central.com.pwb_store.model.ProductDetailImageItem;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDetailOption;
import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;
import cenergy.central.com.pwb_store.model.ProductDetailPromotion;
import cenergy.central.com.pwb_store.model.ProductRelatedList;
import cenergy.central.com.pwb_store.model.Recommend;
import cenergy.central.com.pwb_store.model.SpecDao;
import cenergy.central.com.pwb_store.model.SpecItem;
import cenergy.central.com.pwb_store.model.TheOneCardProductDetail;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyCompareView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by napabhat on 7/19/2017 AD.
 */

public class ProductDetailActivity extends AppCompatActivity implements PowerBuyCompareView.OnClickListener {
    private static final String TAG = ProductDetailActivity.class.getSimpleName();

    public static final String ARG_PRODUCT_ID = "ARG_PRODUCT_ID";
    public static final String ARG_PRODUCT_SKU = "ARG_PRODUCT_SKU";
    public static final String ARG_IS_BARCODE = "ARG_IS_BARCODE";

    Toolbar mToolbar;
    PowerBuyCompareView mBuyCompareView;

    private ProductDetail mProductDetail;
    private Recommend mRecommend;
    private SpecDao mSpecDao;
    private ProgressDialog mProgressDialog;
    private String productId;
    private boolean isBarcode;
    private Realm mRealm;
    private ProductDetailDao mProductDetailDao;

    final Callback<ProductDetail> CALLBACK_PRODUCT_DETAIL = new Callback<ProductDetail>() {
        @Override
        public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
            if (response.isSuccessful()){
                mProductDetail = response.body();
                if (mProductDetail != null){
                    ExtensionProductDetail extensionProductDetail = mProductDetail.getExtensionProductDetail();
                    if (extensionProductDetail.getSpecItems() != null){
                        mSpecDao = new SpecDao(extensionProductDetail.getSpecItems());
                        mProductDetail.setSpecDao(mSpecDao);
                    }

                    //mRecommend = new Recommend(mProductDetail.getProductRelatedLists());

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction
                            .replace(R.id.container, ProductDetailFragment.newInstance(mProductDetail, mRecommend))
                            .commit();
                    mProgressDialog.dismiss();
                }else {
                    MockData();
                    mProgressDialog.dismiss();
                }
            }else {
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                showAlertDialog(error.getErrorMessage(), false);
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<ProductDetail> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };

//    final Callback<ProductDetail> CALLBACK_BARCODE = new Callback<ProductDetail>() {
//        @Override
//        public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
//            if (response.isSuccessful()){
//                mProductDetail = response.body();
//                if (mProductDetail != null){
//                    List<SpecItem> specItems = new ArrayList<>();
//                    specItems.add(new SpecItem("Instant Film","Fujifilm Instant Color “Instax mini”"));
//                    specItems.add(new SpecItem("Picture size","62x46mm"));
//                    specItems.add(new SpecItem("Shutter","Shutter speed : 1/60 sec"));
//                    specItems.add(new SpecItem("Exposure Control","Manual Switching System (LED indecator in exposure meter)"));
//                    specItems.add(new SpecItem("Flash","Constant firing flash (automatic light adjustment)\n" +
//                            "Recycle time : 0.2 sec. to 6 sec. (when using new batteries), Effective flash\n" +
//                            "range : 0.6m - 2.7m"));
//                    specItems.add(new SpecItem("Display Screen","3.0 Inches."));
//
//                    mSpecDao = new SpecDao(specItems);
//                    mProductDetail.setSpecDao(mSpecDao);
//                    //mRecommend = new Recommend(mProductDetail.getProductRelatedLists());
//
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction
//                            .replace(R.id.container, ProductDetailFragment.newInstance(mProductDetail, mRecommend))
//                            .commit();
//                    mProgressDialog.dismiss();
//                }else {
//                    MockData();
//                    mProgressDialog.dismiss();
//                }
//            }else {
//                APIError error = APIErrorUtils.parseError(response);
//                Log.e(TAG, "onResponse: " + error.getErrorMessage());
//                showAlertDialog(error.getErrorMessage(), false);
//                mProgressDialog.dismiss();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ProductDetail> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//            mProgressDialog.dismiss();
//        }
//    };

    final Callback<ProductDetailDao> CALLBACK_BARCODE = new Callback<ProductDetailDao>() {
        @Override
        public void onResponse(Call<ProductDetailDao> call, Response<ProductDetailDao> response) {
            if (response.isSuccessful()){
                mProductDetailDao = response.body();
                if (mProductDetailDao != null){
                    for (ProductDetail productDetail : mProductDetailDao.getProductDetails()){
                        mProductDetail = productDetail;
                    }
                }
                if (mProductDetail != null){
//                    List<SpecItem> specItems = new ArrayList<>();
////                    specItems.add(new SpecItem("Instant Film","Fujifilm Instant Color “Instax mini”"));
////                    specItems.add(new SpecItem("Picture size","62x46mm"));
////                    specItems.add(new SpecItem("Shutter","Shutter speed : 1/60 sec"));
////                    specItems.add(new SpecItem("Exposure Control","Manual Switching System (LED indecator in exposure meter)"));
////                    specItems.add(new SpecItem("Flash","Constant firing flash (automatic light adjustment)\n" +
////                            "Recycle time : 0.2 sec. to 6 sec. (when using new batteries), Effective flash\n" +
////                            "range : 0.6m - 2.7m"));
////                    specItems.add(new SpecItem("Display Screen","3.0 Inches."));
//
//                    specItems.add(new SpecItem("",""));
//                    specItems.add(new SpecItem("",""));
//                    specItems.add(new SpecItem("",""));
//                    specItems.add(new SpecItem("",""));
//                    specItems.add(new SpecItem("",""));
//                    specItems.add(new SpecItem("",""));
//                    mSpecDao = new SpecDao(specItems);
//                    mProductDetail.setSpecDao(mSpecDao);
                    //mRecommend = new Recommend(mProductDetail.getProductRelatedLists());
                    ExtensionProductDetail extensionProductDetail = mProductDetail.getExtensionProductDetail();
                    if (extensionProductDetail.getSpecItems() != null){
                        mSpecDao = new SpecDao(extensionProductDetail.getSpecItems());
                        mProductDetail.setSpecDao(mSpecDao);
                    }
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction
                            .replace(R.id.container, ProductDetailFragment.newInstance(mProductDetail, mRecommend))
                            .commit();
                    mProgressDialog.dismiss();
                }else {
                    //MockData();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction
                            .replace(R.id.container, ProductDetailFragment.newInstance(mProductDetail, mRecommend))
                            .commit();
                    mProgressDialog.dismiss();
                }
            }else {
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                showAlertDialog(error.getErrorMessage(), false);
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<ProductDetailDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };


    @Subscribe
    public void onEvent(PromotionItemBus promotionItemBus){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.ARG_WEB_URL, promotionItemBus.getPromotionItem().getPromotionDetailText().getHtml());
        intent.putExtra(WebViewActivity.ARG_MODE, WebViewFragment.MODE_HTML);
        intent.putExtra(WebViewActivity.ARG_TITLE, "Web");

        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(promotionItemBus.getView(), 0, 0, promotionItemBus.getView().getWidth(), promotionItemBus.getView().getHeight())
                        .toBundle());
    }

    @Subscribe
    public void onEvent(OverviewBus overviewBus){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.ARG_WEB_URL, overviewBus.getReviewDetailText().getHtml());
        intent.putExtra(WebViewActivity.ARG_MODE, WebViewFragment.MODE_HTML);
        intent.putExtra(WebViewActivity.ARG_TITLE, "Web");

        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(overviewBus.getView(), 0, 0, overviewBus.getView().getWidth(), overviewBus.getView().getHeight())
                        .toBundle());
    }

    @Subscribe
    public void onEvent(SpecDaoBus specDaoBus){
        if (specDaoBus.getSpecDao().getSpecItems().size() == 0){
            showAlertDialog("ไม่มีข้อมูล", false);
        }else {
            Intent intent = new Intent(this, SpecActivity.class);
            intent.putExtra(SpecActivity.ARG_SPEC_DAO, specDaoBus.getSpecDao());
            intent.putExtra(SpecActivity.ARG_PRODUCT_ID, productId);
            ActivityCompat.startActivity(this, intent,
                    ActivityOptionsCompat
                            .makeScaleUpAnimation(specDaoBus.getView(), 0, 0, specDaoBus.getView().getWidth(), specDaoBus.getView().getHeight())
                            .toBundle());
        }

    }

    @Subscribe
    public void onEvent(UpdateBageBus updateBageBus){
        if (updateBageBus.isUpdate()){
//            long count = RealmController.with(this).getCount();
            long count = RealmController.with(this).getCompareProduts().size();
            mBuyCompareView.updateCartCount((int) count);
        }
    }

    @Subscribe
    public void onEvent(RecommendBus recommendBus){
        Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, recommendBus.getRelatedList().getProductId());
        intent.putExtra(ProductDetailActivity.ARG_IS_BARCODE, false);
        ActivityCompat.startActivity(ProductDetailActivity.this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(mToolbar, 0, 0, mToolbar.getWidth(), mToolbar.getHeight())
                        .toBundle());
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            productId = extras.getString(ARG_PRODUCT_SKU);
//            productId = "231839";
            isBarcode = extras.getBoolean(ARG_IS_BARCODE);
        }

        initView();
//        MockData();

        if (savedInstanceState == null){
            showProgressDialog();
            if (!isBarcode) {
//                HttpManagerMagentoOld.getInstance().getProductService().getProductDetailMagento(productId, UserInfoManager.getInstance().getUserId(),
//                        getString(R.string.product_detail)).enqueue(CALLBACK_PRODUCT_DETAIL);
                retrieveProduct(productId);
            } else {
//                HttpManagerMagentoOld.getInstance().getProductService().getSearchBarcodeMagento("in_stores", UserInfoManager.getInstance().getUserId(),
//                        "finset", "barcode", productId, "eq", "name", 10, 1).enqueue(CALLBACK_BARCODE);
                retrieveProductFromBarcode(mIntent.getStringExtra(ARG_PRODUCT_ID));
            }
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.container, ProductDetailFragment.newInstance(mProductDetail, mRecommend))
                    .commit();
        }
    }

    // region retrieve product
    private void retrieveProductFromBarcode(String barcode) {
        HttpManagerMagento.Companion.getInstance().getProductFromBarcode("barcode", barcode, "eq", "name", 10, 1, new ApiResponseCallback<Product>() {
            @Override
            public void success(@Nullable Product response) {
                if (response != null){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, ProductDetailFragment.newInstance(response)).commit();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void failure(@NotNull APIError error) {
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
                showAlertDialog(error.getErrorUserMessage(), false);
                mProgressDialog.dismiss();
            }
        });
    }

    private void retrieveProduct(String sku) {
        HttpManagerMagento.Companion.getInstance().retrieveProductDetail(sku,
                getString(R.string.product_detail), new ApiResponseCallback<Product>() {
                    @Override
                    public void success(@Nullable Product response) {
                        if (response != null){
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.container, ProductDetailFragment.newInstance(response)).commit();
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void failure(@NotNull APIError error) {
                        Log.e(TAG, "onResponse: " + error.getErrorMessage());
                        showAlertDialog(error.getErrorUserMessage(), false);
                        mProgressDialog.dismiss();
                    }
                });
    }
    // end region

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mBuyCompareView = findViewById(R.id.button_compare);
        ImageView searchImageView = findViewById(R.id.img_search);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, SearchActivity.class);
                ActivityCompat.startActivity(ProductDetailActivity.this, intent,
                        ActivityOptionsCompat
                                .makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight())
                                .toBundle());
            }
        });


        //get realm instance
        this.mRealm = RealmController.with(this).getRealm();
        int count = RealmController.with(this).getCompareProduts().size();

        mBuyCompareView.setListener(this);
        mBuyCompareView.updateCartCount(count);
    }

    private void MockData() {
        List<ProductDetailImageItem> productDetailImageItems = new ArrayList<>();
        productDetailImageItems.add(new ProductDetailImageItem("1", "http://www.mx7.com/i/004/aOc9VL.png"));
        productDetailImageItems.add(new ProductDetailImageItem("2", "http://www.mx7.com/i/2d4/o5IzWu.png"));
        productDetailImageItems.add(new ProductDetailImageItem("3", "http://www.mx7.com/i/0d5/aNtSTA.png"));
        productDetailImageItems.add(new ProductDetailImageItem("4", "http://www.mx7.com/i/004/aOc9VL.png"));
        productDetailImageItems.add(new ProductDetailImageItem("5", "http://www.mx7.com/i/004/aOc9VL.png"));

        ProductDetailImage productDetailImage = new ProductDetailImage(4, productDetailImageItems);

        TheOneCardProductDetail theOneCardProductDetail = new TheOneCardProductDetail(7000, 2500);

        List<ProductDetailPromotion> productDetailPromotionList = new ArrayList<>();
        productDetailPromotionList.add(new ProductDetailPromotion("1", "http://www.uppic.org/image-4E3E_57D7C9D3.jpg"));
        productDetailPromotionList.add(new ProductDetailPromotion("2", "http://www.uppic.org/image-4391_57D7C9D3.jpg"));
        productDetailPromotionList.add(new ProductDetailPromotion("3", "http://www.uppic.org/image-4E3E_57D7C9D3.jpg"));

//        ProductDetailTextFooter productDetailTextFooter = new ProductDetailTextFooter("<html>\n" +
//                "<div class=\"col-xs-12 col-sm-12 col-md-12 col-lg-12 tab-product-detail-wrapper\">\n" +
//                "      <section class=\"ckeditor ckeditor-productdetail\">\n" +
//                "  <div class=\"-wrapper\">\n" +
//                "    <h2>ProductList details of (REFURBISHED)IPHONE 5S 16GB (Gold)</h2>\n" +
//                "\n" +
//                "<p><strong>(REFURBISHED)IPHONE 5S 16GB (Gold)</strong><br>\n" +
//                "<br>\n" +
//                "หากคุณเป็นสาApple วกของสินค้า Apple แบรนด์ดัง แล้วละก็ คงพลาดไม่ได้กับ Apple iPhone 5s ที่มาพร้อมความสามารถที่หลากหลาย ให้คุณได้ตื่นตาประหลาดใจอย่างที่สุด ไม่ว่ารูปแบบไลฟ์สไตล์ของคุณจะเป็นเช่นไร มือถือระบบ IOS ที่มากประสิทธิภาพเครื่องนี้ จะสามารถตอบโจทย์การใช้งานสมาร์ทโฟนได้อย่างลงตัว<br>\n" +
//                "<br>\n" +
//                "<strong>คุณสมบัติ</strong><br>\n" +
//                "<br>\n" +
//                "<strong>รูปแบบการประมวลผลที่รวดเร็ว</strong><br>\n" +
//                "ด้วยชิบเซ็ต A7 ที่ทำงานประสานกับระบบปฏิบัติการ iOS7 จะทำให้คุณสามารถที่จะประมวลข้อมูลต่างๆอย่างลื่นไหล ไม่มีสะดุด เพราะชิบเซ็ต A7 ของ - Apple iPhone 5s เครื่องนี้มีการประมวลผลแบบ 64-bit นอกจากนี้ยังมี M7 coprocessor ผู้ช่วยการประมวลผลชั้นยอด ซึ่งทำให้คุณสามารถทำงาน และใช้งานเครื่องได้อย่างเต็มประสิทธิภาพ รวดเร็วไม่มีสะดุดเลยทีเดียว<br>\n" +
//                "<br>\n" +
//                "<strong>การใช้งานและรักษาข้อมูลที่ปลอดภัยขึ้น</strong><br>\n" +
//                "Apple iPhone 5s มาพร้อมกับความสามารถในการระบุตัวตนผู้ใช้แบบลายนิ้วมือ ที่ผู้ใช้ต้องทำก็แค่วางนิ้วไปที่ปุ่ม home ของเครื่อง ระบบการระบุตัวตนด้วยรอยนิ้วมือที่ซ่อนอยู่ในปุ่นดังกล่าว ก็จะทำการยืนยันตัวตนของคุณ ให้คุณอุ่นใจกับข้อมูลสำคัญที่มากคุณค่าของคุณว่าจะปลอดภัยอย่างแน่นอน<br>\n" +
//                "<br>\n" +
//                "<strong>การติดต่อที่ใกล้ขึ้นแม้ระยะทางจะห่างไกล</strong><br>\n" +
//                "หากคุณไม่สามารถอยู่เพื่อติดต่อผู้ที่คุณต้องการติดต่อด้วย สิ่งที่คุณต้องทำก็คือการเปิดฟังก์ชั่น Facetime ของ Apple iPhone 5s ซึ่งมาพร้อมกับการบันทึกและส่งต่อภาพแบบ HD<br>\n" +
//                "<br>\n" +
//                "<strong>Specifications</strong><br>\n" +
//                "<br>\n" +
//                "<strong>General</strong><br>\n" +
//                "- 2G Network : GSM 850 / 900 / 1800 / 1900 - all models<br>\n" +
//                "- CDMA 800 / 1700 / 1900 / 2100 - A1533 (CDMA), A1453<br>\n" +
//                "- 3G Network : HSDPA 850 / 900 / 1700 / 1900 / 2100 - A1533 (GSM), A1453 CDMA2000 1xEV-DO - A1533 (CDMA), A1453<br>\n" +
//                "- HSDPA 850 / 900 / 1900 / 2100 - A1457, A1530<br>\n" +
//                "- 4G Network : LTE- all models<br>\n" +
//                "- SIM : Nano-SIM<br>\n" +
//                "<br>\n" +
//                "<strong>Body</strong><br>\n" +
//                "- Dimensions : 123.8 x 58.6 x 7.6 mm (4.87 x 2.31 x 0.30 in)<br>\n" +
//                "- Weight : 112 g (3.95 oz)<br>\n" +
//                "- Fingerprint sensor (Touch ID)<br>\n" +
//                "<br>\n" +
//                "<strong>Display</strong><br>\n" +
//                "- Type : LED-backlit IPS LCD, capacitive touchscreen, 16M colors<br>\n" +
//                "- Size : 640 x 1136 pixels, 4.0 inches (~326 ppi pixel density)<br>\n" +
//                "- Multitouch : Yes<br>\n" +
//                "- Protection : Corning Gorilla Glass, oleophobic coating<br>\n" +
//                "<br>\n" +
//                "<strong>Sound</strong><br>\n" +
//                "- Alert types : Vibration, proprietary ringtones<br>\n" +
//                "- Loudspeaker : Yes<br>\n" +
//                "- 3.5mm jack : Yes<br>\n" +
//                "<br>\n" +
//                "<strong>Memory</strong><br>\n" +
//                "- Card slot : No<br>\n" +
//                "- Internal<br>\n" +
//                "- 16/32/64 GB storage, 1 GB RAM DDR3<br>\n" +
//                "<br>\n" +
//                "<strong>Data</strong><br>\n" +
//                "- GPRS : Yes<br>\n" +
//                "- EDGE : Yes<br>\n" +
//                "- Speed : DC-HSDPA, 42 Mbps; HSDPA, 21 Mbps; HSUPA, 5.76 Mbps, LTE, 100 Mbps; EV-DO Rev. A, up to 3.1 Mbps<br>\n" +
//                "- WLAN : Wi-Fi 802.11 a/b/g/n, dual-band, Wi-Fi hotspot<br>\n" +
//                "- Bluetooth : Yes, v4.0 with A2DP<br>\n" +
//                "- USB : Yes, v2.0<br>\n" +
//                "<br>\n" +
//                "<strong>Camera</strong><br>\n" +
//                "- Primary : 8 MP, 3264x2448 pixels, autofocus, dual-LED (True Tone) flash, check quality<br>\n" +
//                "- Features : 1/3'' sensor size, 1.5 ?m pixel size, simultaneous HD video and image recording, touch focus, geo-tagging, face detection, HDR panorama, HDR photo<br>\n" +
//                "- Video : Yes, 1080p@30fps, 720p@120fps, video stabilization, check quality<br>\n" +
//                "- Secondary : Yes, 1.2 MP, 720p@30fps, face detection, FaceTime over Wi-Fi or Cellular<br>\n" +
//                "<br>\n" +
//                "- OS : iOS 7, upgradable to iOS 7.0.3<br>\n" +
//                "- Chipset : Apple A7<br>\n" +
//                "- CPU : Dual-core 1.3 GHz Cyclone (ARM v8-based)<br>\n" +
//                "- GPU : PowerVR G6430 (quad-core graphics)<br>\n" +
//                "- Sensors : Accelerometer, gyro, proximity, compass<br>\n" +
//                "- Messaging : iMessage, SMS (threaded view), MMS, Email, Push Email<br>\n" +
//                "- Browser : HTML (Safari)<br>\n" +
//                "- Radio : No<br>\n" +
//                "- GPS : Yes, with A-GPS support and GLONASS<br>\n" +
//                "- Java : No<br>\n" +
//                "- Colors : Space Gray, White/Silver, Gold<br>\n" +
//                "- Active noise cancellation with dedicated mic<br>\n" +
//                "- AirDrop file sharing<br>\n" +
//                "- Siri natural language commands and dictation<br>\n" +
//                "- iCloud cloud service<br>\n" +
//                "- iCloud Keychain<br>\n" +
//                "- Twitter and Facebook integration<br>\n" +
//                "- TV-out<br>\n" +
//                "- Maps<br>\n" +
//                "- iBooks PDF reader<br>\n" +
//                "- Audio/video player/editor<br>\n" +
//                "- Organizer<br>\n" +
//                "- Document viewer/editor ((Word, Excel, PowerPoint)<br>\n" +
//                "- Image viewer/editor<br>\n" +
//                "- Voice memo/dial/command<br>\n" +
//                "- Predictive text input Battery - Li-Po 1560 mAh battery (5.92 Wh)</p>\n" +
//                "\n" +
//                "                  <div class=\"attributes-heading\">ตัวกรองสี</div>\n" +
//                "                                            <img src=\"https://s2.tctcdn.com/attribute/2/BodyPart_edca64d2-1d4a-4efc-977c-489cd8a101f6.jpg\" alt=\"ชมพู\"><br>\n" +
//                "                                            <div class=\"attributes-heading\">สีเฉพาะสินค้า</div>\n" +
//                "                                            <img src=\"https://s0.tctcdn.com/attribute/3/BodyPart_b16b09aa-a14d-434c-a3a5-27025c14f530.jpg\" alt=\"Light Blue [ #ADD8E6 ]\"><br>\n" +
//                "                                            <div class=\"attributes-heading\">วัสดุของถุงมือ</div>\n" +
//                "                                            ผ้า<br>\n" +
//                "                                            <div class=\"attributes-heading\">รุ่นสินค้า</div>\n" +
//                "                                            เอสอี<br>\n" +
//                "                                            <div class=\"attributes-heading\">ขนาดถุงมือ</div>\n" +
//                "                                            S<br>\n" +
//                "                                            <div class=\"attributes-heading\">ขนาดของถุงมือ</div>\n" +
//                "                                            30<br>\n" +
//                "                                          </div>\n" +
//                "</section>\n" +
//                "    </div>\n" +
//                "</html>\n", ProductDetailTextFooter.MODE_DESCRIPTION);

//        List<ProductDetailItemDescriptionItem> productDetailItemDescriptionItemList = new ArrayList<>();
//        productDetailItemDescriptionItemList.add(new ProductDetailItemDescriptionItem("รายละเอียดสินค้า", "สำหรับผู้ที่ชื่นชอบในการถ่ายภาพและมีความอาร์ตอยู่ในตัว คุณคงไม่อยากพลาดกล้องสุดกิ๊บเก๋ที่จะให้คุณได้ถ่ายภาพแบบเก๋ๆ อย่าง Fujifilm Instax mini 8 ถ่าย ภาพปุ๊บ ได้รูปทันที ด้วยกล้อง Instax จาก Fujifilm ที่จะมาทำให้คุณได้ถ่ายภาพความทรงจำอันแสนประทับใจในสไตล์ของคุณเองได้เร่ง ด่วนในทันที", productDetailTextFooter));
//        productDetailItemDescriptionItemList.add(new ProductDetailItemDescriptionItem("การจัดส่งและการเปลี่ยนสินค้า", "สำหรับผู้ที่ชื่นชอบในการถ่ายภาพและมีความอาร์ตอยู่ในตัว คุณคงไม่อยากพลาดกล้องสุดกิ๊บเก๋ที่จะให้คุณได้ถ่ายภาพแบบเก๋ๆ อย่าง Fujifilm Instax mini 8 ถ่าย ภาพปุ๊บ ได้รูปทันที ด้วยกล้อง Instax จาก Fujifilm ที่จะมาทำให้คุณได้ถ่ายภาพความทรงจำอันแสนประทับใจในสไตล์ของคุณเองได้เร่ง ด่วนในทันที", productDetailTextFooter));
//        productDetailItemDescriptionItemList.add(new ProductDetailItemDescriptionItem("การรับประกันสินค้า", "สำหรับผู้ที่ชื่นชอบในการถ่ายภาพและมีความอาร์ตอยู่ในตัว คุณคงไม่อยากพลาดกล้องสุดกิ๊บเก๋ที่จะให้คุณได้ถ่ายภาพแบบเก๋ๆ อย่าง Fujifilm Instax mini 8 ถ่าย ภาพปุ๊บ ได้รูปทันที ด้วยกล้อง Instax จาก Fujifilm ที่จะมาทำให้คุณได้ถ่ายภาพความทรงจำอันแสนประทับใจในสไตล์ของคุณเองได้เร่ง ด่วนในทันที", productDetailTextFooter));
//
//        ProductDetailItemDescription productDetailItemDescription = new ProductDetailItemDescription(productDetailItemDescriptionItemList);

        List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemListSize1 = new ArrayList<>();
        productDetailAvailableOptionItemListSize1.add(new ProductDetailAvailableOptionItem(1, "s", "S", "http://www.clipartkid.com/images/378/upper-case-s-clipart-panda-free-clipart-images-XcnAg3-clipart."));
        productDetailAvailableOptionItemListSize1.add(new ProductDetailAvailableOptionItem(2, "m", "M", "https://static.rappad.co/api/file/6TAG5jivSPukZUX0OrWs"));
        List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemListSize2 = new ArrayList<>();
        productDetailAvailableOptionItemListSize2.add(new ProductDetailAvailableOptionItem(3, "l", "L", "http://www.lucatenneriello.com/wp/wp-content/uploads/2014/05/L.jpg"));
        productDetailAvailableOptionItemListSize2.add(new ProductDetailAvailableOptionItem(4, "xl", "XL", "http://www.xl.co.id/thumbnail-xl-facebook-350.jpg"));
        List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemListSize3 = new ArrayList<>();
        productDetailAvailableOptionItemListSize3.add(new ProductDetailAvailableOptionItem(3, "l", "L", "http://www.lucatenneriello.com/wp/wp-content/uploads/2014/05/L.jpg"));
        productDetailAvailableOptionItemListSize3.add(new ProductDetailAvailableOptionItem(4, "xl", "XL", "http://www.xl.co.id/thumbnail-xl-facebook-350.jpg"));

        ProductDetailAvailableOption productDetailAvailableOptionListSize1 = new ProductDetailAvailableOption(2, "size", "Size", 2, productDetailAvailableOptionItemListSize1);
        ProductDetailAvailableOption productDetailAvailableOptionListSize2 = new ProductDetailAvailableOption(2, "size", "Size", 2, productDetailAvailableOptionItemListSize2);
        ProductDetailAvailableOption productDetailAvailableOptionListSize3 = new ProductDetailAvailableOption(2, "size", "Size", 2, productDetailAvailableOptionItemListSize3);

        List<ProductDetailOptionItem> productDetailOptionItemListColor = new ArrayList<>();
        productDetailOptionItemListColor.add(new ProductDetailOptionItem(1, "gold", "Gold", "http://www.mx7.com/i/025/EHDbUk.jpg", productDetailAvailableOptionListSize1));
        productDetailOptionItemListColor.add(new ProductDetailOptionItem(2, "gray", "Gray", "http://www.mx7.com/i/2e9/5XJL2W.png", productDetailAvailableOptionListSize2));
        productDetailOptionItemListColor.add(new ProductDetailOptionItem(3, "rose_gold", "RoseGold", "http://www.mx7.com/i/24a/T6AUYF.png", productDetailAvailableOptionListSize3));

        List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemListColor1 = new ArrayList<>();
        productDetailAvailableOptionItemListColor1.add(new ProductDetailAvailableOptionItem(1, "gold", "Gold", "http://www.mx7.com/i/025/EHDbUk.jpg"));
        List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemListColor2 = new ArrayList<>();
        productDetailAvailableOptionItemListColor2.add(new ProductDetailAvailableOptionItem(1, "gray", "Gray", "http://www.mx7.com/i/2e9/5XJL2W.png"));
        List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemListColor3 = new ArrayList<>();
        productDetailAvailableOptionItemListColor3.add(new ProductDetailAvailableOptionItem(1, "rose_gold", "RoseGold", "http://www.mx7.com/i/24a/T6AUYF.png"));

        ProductDetailAvailableOption productDetailAvailableOptionListColor1 = new ProductDetailAvailableOption(1, "color", "Color", 1, productDetailAvailableOptionItemListColor1);
        ProductDetailAvailableOption productDetailAvailableOptionListColor2 = new ProductDetailAvailableOption(2, "color", "Color", 1, productDetailAvailableOptionItemListColor2);
        ProductDetailAvailableOption productDetailAvailableOptionListColor3 = new ProductDetailAvailableOption(3, "color", "Color", 1, productDetailAvailableOptionItemListColor3);

        List<ProductDetailOptionItem> productDetailOptionItemListSize = new ArrayList<>();
        productDetailOptionItemListSize.add(new ProductDetailOptionItem(1, "s", "S", "http://www.clipartkid.com/images/378/upper-case-s-clipart-panda-free-clipart-images-XcnAg3-clipart.", productDetailAvailableOptionListColor1));
        productDetailOptionItemListSize.add(new ProductDetailOptionItem(2, "m", "M", "https://static.rappad.co/api/file/6TAG5jivSPukZUX0OrWs", productDetailAvailableOptionListColor1));
        productDetailOptionItemListSize.add(new ProductDetailOptionItem(3, "l", "L", "http://www.lucatenneriello.com/wp/wp-content/uploads/2014/05/L.jpg", productDetailAvailableOptionListColor2));
        productDetailOptionItemListSize.add(new ProductDetailOptionItem(4, "xl", "XL", "http://www.xl.co.id/thumbnail-xl-facebook-350.jpg", productDetailAvailableOptionListColor2));

        ProductDetailOption productDetailOption = new ProductDetailOption(1, "color", "Color", 2, productDetailOptionItemListColor);

        List<SpecItem> specItems = new ArrayList<>();
        specItems.add(new SpecItem("Instant Film","Fujifilm Instant Color “Instax mini”"));
        specItems.add(new SpecItem("Picture size","62x46mm"));
        specItems.add(new SpecItem("Shutter","Shutter speed : 1/60 sec"));
        specItems.add(new SpecItem("Exposure Control","Manual Switching System (LED indecator in exposure meter)"));
        specItems.add(new SpecItem("Flash","Constant firing flash (automatic light adjustment)\n" +
                "Recycle time : 0.2 sec. to 6 sec. (when using new batteries), Effective flash\n" +
                "range : 0.6m - 2.7m"));
        specItems.add(new SpecItem("Display Screen","3.0 Inches."));

        mSpecDao = new SpecDao(specItems);


        mProductDetail = new ProductDetail("1", "FUJI", "Fuji instax white", "CJ 1234567", "B2S", productDetailImage, 5, 3500, 2290, 1, 1, 5,
                theOneCardProductDetail, productDetailPromotionList, productDetailOption, mSpecDao);

        List<ProductRelatedList> productLists = new ArrayList<>();
        productLists.add(new ProductRelatedList("http://www.mx7.com/i/004/aOc9VL.png",
                "iPhone SE", "GPS นำทาง 800 Mhz ขนาด 7 นิ้ว", 100, 2500, "123456"));
        productLists.add(new ProductRelatedList("http://www.mx7.com/i/0e5/oFp5mm.png",
                "iPhone SE", "Besta GPS C5 PLUS ขนาด 7 นิ้ว (black)", 4590, 2390, "123456"));
        productLists.add(new ProductRelatedList("http://www.mx7.com/i/1cb/TL8yVR.png",
                "Lenovo", "Canon EOS 750D WiFi NFC + EF-S 18-55 mm", 0, 31990, "123456"));
        productLists.add(new ProductRelatedList("http://www.mx7.com/i/215/WAY0dD.png",
                "EPSON", "Canon Power Shot SX610 HS Digital Camera (Black)", 0, 22790, "123456"));
        productLists.add(new ProductRelatedList("http://www.mx7.com/i/004/aOc9VL.png",
                "iPhone SE", "GPS นำทาง 800 Mhz ขนาด 7 นิ้ว", 0, 2590, "123456"));
        productLists.add(new ProductRelatedList("http://www.mx7.com/i/0e5/oFp5mm.png",
                "iPhone SE", "Canon Power Shot SX610 HS Digital Camera (Black)", 0, 22790, "123456"));
        mRecommend = new Recommend(productLists);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        long count = RealmController.with(this).getCompareProduts().size();
        mBuyCompareView.updateCartCount((int) count);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_PRODUCT_ID, productId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        productId = savedInstanceState.getString(ARG_PRODUCT_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onShoppingBagClick(View view) {
        Intent intent = new Intent(this, CompareActivity.class);
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                        .toBundle());
    }

    private void showAlertDialog(String message, final boolean shouldCloseActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (shouldCloseActivity) finish();
                    }
                });

        builder.show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }
}
