package cenergy.central.com.pwb_store.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.PromotionPaymentAdapter;
import cenergy.central.com.pwb_store.manager.bus.event.PromotionItemBus;
import cenergy.central.com.pwb_store.model.Promotion;
import cenergy.central.com.pwb_store.model.PromotionDetailText;
import cenergy.central.com.pwb_store.model.PromotionItem;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class PromotionPaymentFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PromotionPaymentAdapter mPaymentAdapter;
    private Promotion mPromotion;
    private GridLayoutManager mLayoutManager;

    public PromotionPaymentFragment() {
        super();
    }

    @Subscribe
    public void onEvent(PromotionItemBus promotionItemBus){

    }

    @SuppressWarnings("unused")
    public static PromotionPaymentFragment newInstance() {
        PromotionPaymentFragment fragment = new PromotionPaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View rootView = inflater.inflate(R.layout.fragment_promotion_payment, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        PromotionDetailText promotionDetailText = new PromotionDetailText("<html>\n" +
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
                "</html>\n", PromotionDetailText.MODE_DESCRIPTION);
        List<PromotionItem> promotionItems = new ArrayList<>();
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));
        promotionItems.add(new PromotionItem("รับประสบการณ์เหนือจินตนาการกับ FUJIFILM เมื่อแบ่งจ่ายด้วยบัตรเครดิต SCB นาน 10 เดือน", "ระยะเวลาโปรโมชั่น 17/07/2560 ถึง 23/07/2560","", promotionDetailText));

        mPromotion = new Promotion(promotionItems);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);

        mPaymentAdapter = new PromotionPaymentAdapter(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mPaymentAdapter.getSpanSize());
        mPaymentAdapter.setPromotionPayment(mPromotion);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mPaymentAdapter);
        mRecyclerView.setNestedScrollingEnabled(true);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
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
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
