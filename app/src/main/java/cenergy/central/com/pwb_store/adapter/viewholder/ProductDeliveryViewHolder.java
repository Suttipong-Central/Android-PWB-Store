package cenergy.central.com.pwb_store.adapter.viewholder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.HttpManagerHDL;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.manager.bus.event.BookTimeBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.AddCompare;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.Shipping;
import cenergy.central.com.pwb_store.model.ShippingDao;
import cenergy.central.com.pwb_store.model.ShippingItem;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.model.TimeSlotItem;
import cenergy.central.com.pwb_store.model.request.CartDataRequest;
import cenergy.central.com.pwb_store.model.request.CustomDetailRequest;
import cenergy.central.com.pwb_store.model.request.HDLRequest;
import cenergy.central.com.pwb_store.model.request.PeriodRequest;
import cenergy.central.com.pwb_store.model.request.ShippingRequest;
import cenergy.central.com.pwb_store.model.request.SkuDataRequest;
import cenergy.central.com.pwb_store.model.response.HDLResponse;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.utils.APIErrorHDLUtils;
import cenergy.central.com.pwb_store.utils.CommonMethod;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.CalendarViewCustom;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductDeliveryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CalendarViewCustom.OnItemClickListener {
    private static final String TAG = ProductDeliveryViewHolder.class.getSimpleName();

    @BindView(R.id.layout_delivery)
    LinearLayout mDelivery;

    @BindView(R.id.custom_calendar)
    CalendarViewCustom mCalendarView;

    @BindView(R.id.card_view_add_time)
    CardView mCardView;

    private Context mContext;
    private Realm mRealm;
    private RealmResults<AddCompare> results;
    private List<CartDataRequest> cartDataRequests = new ArrayList<>();
    private HDLRequest mHDLRequest;
    private HDLResponse mHDLResponse;
    private StoreList storeList;
    private List<TimeSlotItem> timeSlotItems = new ArrayList<>();
    private List<TimeSlotItem> timeSlotItemsA = new ArrayList<>();
    private List<TimeSlotItem> timeSlotItemsB = new ArrayList<>();
    private List<TimeSlotItem> timeSlotItemsC = new ArrayList<>();
    private List<TimeSlotItem> timeSlotItemsD = new ArrayList<>();
    private List<TimeSlotItem> timeSlotItemsE = new ArrayList<>();
    private List<TimeSlotItem> timeSlotItemsF = new ArrayList<>();
    //Time Slot Not RIS
    private ShippingRequest mShippingRequest;
    private ShippingDao mShippingDao;
    private List<SkuDataRequest> mSkuDataRequests = new ArrayList<>();
    private PeriodRequest mPeriodRequest;
    private CustomDetailRequest mCustomDetailRequest;
    private List<ShippingItem> shippingItems = new ArrayList<>();
    private List<ShippingItem> shippingItemsA = new ArrayList<>();
    private List<ShippingItem> shippingItemsB = new ArrayList<>();
    private List<ShippingItem> shippingItemsC = new ArrayList<>();
    private List<ShippingItem> shippingItemsD = new ArrayList<>();
    private List<ShippingItem> shippingItemsE = new ArrayList<>();
    private List<ShippingItem> shippingItemsF = new ArrayList<>();
    private final AWS4Signer signer = new AWS4Signer();
    private Request<?> aws;
    private ProgressDialog mProgressDialog;
    public String[] NextPreWeekday;
    public String[] monthPreday;
    private String sunday;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private ProductDetail mProductDetail;
//    final Callback<HDLResponse> CALLBACK_HDL = new Callback<HDLResponse>() {
//        @Override
//        public void onResponse(Call<HDLResponse> call, Response<HDLResponse> response) {
//            if (response.isSuccessful()) {
//                mProgressDialog.dismiss();
//                mHDLResponse = response.body();
//                createData(mHDLResponse);
//            } else {
//                APIError error = APIErrorUtils.parseError(response);
//                Log.e(TAG, "onResponse: " + error.getErrorUserMessage());
//                showAlertDialog(error.getErrorUserMessage());
//                mProgressDialog.dismiss();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<HDLResponse> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//            mProgressDialog.dismiss();
//        }
//    };

    final Callback<ShippingDao> CALLBACK_SHIPPING = new Callback<ShippingDao>() {
        @Override
        public void onResponse(Call<ShippingDao> call, Response<ShippingDao> response) {
            if (response.isSuccessful()) {
                mShippingDao = response.body();
                createShippingData(mShippingDao);
                mProgressDialog.dismiss();
            } else {
                APIError error = APIErrorHDLUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorUserMessage());
                showAlertDialog(error.getErrorUserMessage());
                mProgressDialog.dismiss();

            }
        }

        @Override
        public void onFailure(Call<ShippingDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
        }
    };

    public ProductDeliveryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mRealm = RealmController.getInstance().getRealm();
    }

    public void setViewHolder(Context context, ProductDetail productDetail) {
        this.mContext = context;
        this.mProductDetail = productDetail;
        mCardView.setOnClickListener(this);
        showProgressDialog();
        //getData(getMonth());
        getDataSlot(getMonth());
        mCalendarView.setListener(this);
        //getMonth();
//        NextPreWeekday = mCalendarView.getWeekDay();

    }

    public String getMonth() {

        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String month;
        month = format.format(now.getTime());

        return month;

    }

//    public void getData(String mount) {
//        results = RealmController.getInstance().getCompares();
//        for (AddCompare addCompare : results) {
//            for (int i = 0; i < results.size(); i++) {
//                i++;
//                cartDataRequests.add(new CartDataRequest(i, addCompare.getBarcode(), addCompare.getProductSku(),
//                        1, "1", "00991", "", ""));
//            }
//        }
//        if (results.size() > 0) {
//            storeList = UserInfoManager.getInstance().loadStore();
//            mHDLRequest = new HDLRequest(storeList.getSubDistrictName(), storeList.getDistrictName(),
//                    storeList.getProvinceName(), storeList.getPostCode(), mount, cartDataRequests);
//
//            HttpManagerHDL.getInstance().getHDLService().checkTimeSlot(UserInfoManager.getInstance().getUserToken(),
//                    "application/json",
//                    mHDLRequest).enqueue(CALLBACK_HDL);
//        }else {
//            storeList = UserInfoManager.getInstance().loadStore();
//            cartDataRequests.add(new CartDataRequest(1, mProductDetail.getBarcode(), mProductDetail.getSku(),
//                    1, "1", "00991", "", ""));
//            mHDLRequest = new HDLRequest(storeList.getSubDistrictName(), storeList.getDistrictName(),
//                    storeList.getProvinceName(), storeList.getPostCode(), mount, cartDataRequests);
//
//            HttpManagerHDL.getInstance().getHDLService().checkTimeSlot(UserInfoManager.getInstance().getUserToken(),
//                    "application/json",
//                    mHDLRequest).enqueue(CALLBACK_HDL);
//        }
//
//    }

    public void getDataSlot(String mount) {
        results = RealmController.getInstance().getCompares();
        storeList = UserInfoManager.getInstance().loadStore();
        if (results.size() > 0) {
            for (AddCompare addCompare : results) {
                for (int i = 0; i < results.size(); i++) {
                    i++;
                    mSkuDataRequests.add(new SkuDataRequest(addCompare.getBarcode(), 1, addCompare.getProductSku(), 1, storeList.getStoreId(), ""));
                }
            }
            String[] separated = mount.split("-");
            String year = separated[0];
            String month = separated[1];
            mPeriodRequest = new PeriodRequest(Integer.parseInt(year), Integer.parseInt(month));
            mCustomDetailRequest = new CustomDetailRequest("1", "", "");
            mShippingRequest = new ShippingRequest(storeList.getPostCode(), mSkuDataRequests, mPeriodRequest, mCustomDetailRequest);

//            HttpManagerHDL.getInstance().getHDLService().checkTimeSlot(UserInfoManager.getInstance().getUserToken(),
//                    "application/json",
//                    mHDLRequest).enqueue(CALLBACK_HDL);
            HttpManagerHDL.getInstance().getHDLService().checkShippingTime(
                    mShippingRequest).enqueue(CALLBACK_SHIPPING);

        } else {
            mSkuDataRequests.add(new SkuDataRequest(mProductDetail.getExtensionProductDetail().getProductDetailStores().get(0).getBarCode()
                    , 1, mProductDetail.getSku(), 1, storeList.getStoreId(), ""));
            String[] separated = mount.split("-");
            String year = separated[0];
            String month = separated[1];
            mPeriodRequest = new PeriodRequest(Integer.parseInt(year), Integer.parseInt(month));
            mCustomDetailRequest = new CustomDetailRequest("1", "", "");
            mShippingRequest = new ShippingRequest(storeList.getPostCode(), mSkuDataRequests, mPeriodRequest, mCustomDetailRequest);

            HttpManagerHDL.getInstance().getHDLService().checkShippingTime(
                    mShippingRequest).enqueue(CALLBACK_SHIPPING);
        }

    }

//    private void createData(HDLResponse hdlResponse) {
//        if (NextPreWeekday == null) {
//            NextPreWeekday = mCalendarView.getWeekDay();
//        }
//        sunday = NextPreWeekday[0];
//        monday = NextPreWeekday[1];
//        tuesday = NextPreWeekday[2];
//        wednesday = NextPreWeekday[3];
//        thursday = NextPreWeekday[4];
//        friday = NextPreWeekday[5];
//        saturday = NextPreWeekday[6];
//        for (Delivery delivery : hdlResponse.getDeliveryList()) {
//            String deliveryDate = delivery.getDeliveryDate();
//
//            if (sunday.equalsIgnoreCase(deliveryDate)) {
//                for (TimeSlotItem timeSlotItem : delivery.getTimeSlotItems()) {
//                    String slotTime = timeSlotItem.getSlotLabel();
//                    switch (slotTime) {
//                        case "09:00-09:30":
//                            timeSlotItemsA.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "11:00-11:30":
//                            timeSlotItemsB.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "13:00-13:30":
//                            timeSlotItemsC.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "15:00-15:30":
//                            timeSlotItemsD.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "17:00-17:30":
//                            timeSlotItemsE.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "19:00-19:30":
//                            timeSlotItemsF.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        default:
//                            timeSlotItemsA.add(new TimeSlotItem(1, "FULL", 0));
//                            timeSlotItemsB.add(new TimeSlotItem(2, "FULL", 0));
//                            timeSlotItemsC.add(new TimeSlotItem(3, "FULL", 0));
//                            timeSlotItemsD.add(new TimeSlotItem(6, "FULL", 0));
//                            timeSlotItemsE.add(new TimeSlotItem(7, "FULL", 0));
//                            timeSlotItemsF.add(new TimeSlotItem(8, "FULL", 0));
//                    }
//                }
//
//            } else if (monday.equalsIgnoreCase(deliveryDate)) {
//                for (TimeSlotItem timeSlotItem : delivery.getTimeSlotItems()) {
//                    String slotTime = timeSlotItem.getSlotLabel();
//                    switch (slotTime) {
//                        case "09:00-09:30":
//                            timeSlotItemsA.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "11:00-11:30":
//                            timeSlotItemsB.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "13:00-13:30":
//                            timeSlotItemsC.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "15:00-15:30":
//                            timeSlotItemsD.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "17:00-17:30":
//                            timeSlotItemsE.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "19:00-19:30":
//                            timeSlotItemsF.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        default:
//                            timeSlotItemsA.add(new TimeSlotItem(1, "FULL", 0));
//                            timeSlotItemsB.add(new TimeSlotItem(2, "FULL", 0));
//                            timeSlotItemsC.add(new TimeSlotItem(3, "FULL", 0));
//                            timeSlotItemsD.add(new TimeSlotItem(6, "FULL", 0));
//                            timeSlotItemsE.add(new TimeSlotItem(7, "FULL", 0));
//                            timeSlotItemsF.add(new TimeSlotItem(8, "FULL", 0));
//                    }
//                }
//
//            }
//            if (tuesday.equalsIgnoreCase(deliveryDate)) {
//                for (TimeSlotItem timeSlotItem : delivery.getTimeSlotItems()) {
//                    String slotTime = timeSlotItem.getSlotLabel();
//                    switch (slotTime) {
//                        case "09:00-09:30":
//                            timeSlotItemsA.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "11:00-11:30":
//                            timeSlotItemsB.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "13:00-13:30":
//                            timeSlotItemsC.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "15:00-15:30":
//                            timeSlotItemsD.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "17:00-17:30":
//                            timeSlotItemsE.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "19:00-19:30":
//                            timeSlotItemsF.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        default:
//                            timeSlotItemsA.add(new TimeSlotItem(1, "FULL", 0));
//                            timeSlotItemsB.add(new TimeSlotItem(2, "FULL", 0));
//                            timeSlotItemsC.add(new TimeSlotItem(3, "FULL", 0));
//                            timeSlotItemsD.add(new TimeSlotItem(6, "FULL", 0));
//                            timeSlotItemsE.add(new TimeSlotItem(7, "FULL", 0));
//                            timeSlotItemsF.add(new TimeSlotItem(8, "FULL", 0));
//                    }
//                }
//            } else if (wednesday.equalsIgnoreCase(deliveryDate)) {
//                for (TimeSlotItem timeSlotItem : delivery.getTimeSlotItems()) {
//                    String slotTime = timeSlotItem.getSlotLabel();
//                    switch (slotTime) {
//                        case "09:00-09:30":
//                            timeSlotItemsA.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "11:00-11:30":
//                            timeSlotItemsB.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "13:00-13:30":
//                            timeSlotItemsC.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "15:00-15:30":
//                            timeSlotItemsD.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "17:00-17:30":
//                            timeSlotItemsE.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "19:00-19:30":
//                            timeSlotItemsF.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        default:
//                            timeSlotItemsA.add(new TimeSlotItem(1, "FULL", 0));
//                            timeSlotItemsB.add(new TimeSlotItem(2, "FULL", 0));
//                            timeSlotItemsC.add(new TimeSlotItem(3, "FULL", 0));
//                            timeSlotItemsD.add(new TimeSlotItem(6, "FULL", 0));
//                            timeSlotItemsE.add(new TimeSlotItem(7, "FULL", 0));
//                            timeSlotItemsF.add(new TimeSlotItem(8, "FULL", 0));
//                    }
//                }
//            } else if (thursday.equalsIgnoreCase(deliveryDate)) {
//                for (TimeSlotItem timeSlotItem : delivery.getTimeSlotItems()) {
//                    String slotTime = timeSlotItem.getSlotLabel();
//                    switch (slotTime) {
//                        case "09:00-09:30":
//                            timeSlotItemsA.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "11:00-11:30":
//                            timeSlotItemsB.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "13:00-13:30":
//                            timeSlotItemsC.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "15:00-15:30":
//                            timeSlotItemsD.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "17:00-17:30":
//                            timeSlotItemsE.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "19:00-19:30":
//                            timeSlotItemsF.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        default:
//                            timeSlotItemsA.add(new TimeSlotItem(1, "FULL", 0));
//                            timeSlotItemsB.add(new TimeSlotItem(2, "FULL", 0));
//                            timeSlotItemsC.add(new TimeSlotItem(3, "FULL", 0));
//                            timeSlotItemsD.add(new TimeSlotItem(6, "FULL", 0));
//                            timeSlotItemsE.add(new TimeSlotItem(7, "FULL", 0));
//                            timeSlotItemsF.add(new TimeSlotItem(8, "FULL", 0));
//                    }
//                }
//            } else if (friday.equalsIgnoreCase(deliveryDate)) {
//                for (TimeSlotItem timeSlotItem : delivery.getTimeSlotItems()) {
//                    String slotTime = timeSlotItem.getSlotLabel();
//                    switch (slotTime) {
//                        case "09:00-09:30":
//                            timeSlotItemsA.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "11:00-11:30":
//                            timeSlotItemsB.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "13:00-13:30":
//                            timeSlotItemsC.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "15:00-15:30":
//                            timeSlotItemsD.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "17:00-17:30":
//                            timeSlotItemsE.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "19:00-19:30":
//                            timeSlotItemsF.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        default:
//                            timeSlotItemsA.add(new TimeSlotItem(1, "FULL", 0));
//                            timeSlotItemsB.add(new TimeSlotItem(2, "FULL", 0));
//                            timeSlotItemsC.add(new TimeSlotItem(3, "FULL", 0));
//                            timeSlotItemsD.add(new TimeSlotItem(6, "FULL", 0));
//                            timeSlotItemsE.add(new TimeSlotItem(7, "FULL", 0));
//                            timeSlotItemsF.add(new TimeSlotItem(8, "FULL", 0));
//                    }
//                }
//            } else if (saturday.equalsIgnoreCase(deliveryDate)) {
//                for (TimeSlotItem timeSlotItem : delivery.getTimeSlotItems()) {
//                    String slotTime = timeSlotItem.getSlotLabel();
//                    switch (slotTime) {
//                        case "09:00-09:30":
//                            timeSlotItemsA.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "11:00-11:30":
//                            timeSlotItemsB.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "13:00-13:30":
//                            timeSlotItemsC.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "15:00-15:30":
//                            timeSlotItemsD.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "17:00-17:30":
//                            timeSlotItemsE.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        case "19:00-19:30":
//                            timeSlotItemsF.add(new TimeSlotItem(timeSlotItem.getSlotId(), timeSlotItem.getSlotLabel(), timeSlotItem.getAvailabel()));
//                            break;
//                        default:
//                            timeSlotItemsA.add(new TimeSlotItem(1, "FULL", 0));
//                            timeSlotItemsB.add(new TimeSlotItem(2, "FULL", 0));
//                            timeSlotItemsC.add(new TimeSlotItem(3, "FULL", 0));
//                            timeSlotItemsD.add(new TimeSlotItem(6, "FULL", 0));
//                            timeSlotItemsE.add(new TimeSlotItem(7, "FULL", 0));
//                            timeSlotItemsF.add(new TimeSlotItem(8, "FULL", 0));
//                    }
//                }
//
//            }
//        }
//
//        for (TimeSlotItem timeSlotItemA : timeSlotItemsA) {
//            timeSlotItems.add(new TimeSlotItem(timeSlotItemA.getSlotId(), timeSlotItemA.getSlotLabel(), timeSlotItemA.getAvailabel()));
//        }
//
//        for (TimeSlotItem timeSlotItemB : timeSlotItemsB) {
//            timeSlotItems.add(new TimeSlotItem(timeSlotItemB.getSlotId(), timeSlotItemB.getSlotLabel(), timeSlotItemB.getAvailabel()));
//        }
//
//        for (TimeSlotItem timeSlotItemC : timeSlotItemsC) {
//            timeSlotItems.add(new TimeSlotItem(timeSlotItemC.getSlotId(), timeSlotItemC.getSlotLabel(), timeSlotItemC.getAvailabel()));
//        }
//
//        for (TimeSlotItem timeSlotItemD : timeSlotItemsD) {
//            timeSlotItems.add(new TimeSlotItem(timeSlotItemD.getSlotId(), timeSlotItemD.getSlotLabel(), timeSlotItemD.getAvailabel()));
//        }
//
//        for (TimeSlotItem timeSlotItemE : timeSlotItemsE) {
//            timeSlotItems.add(new TimeSlotItem(timeSlotItemE.getSlotId(), timeSlotItemE.getSlotLabel(), timeSlotItemE.getAvailabel()));
//        }
//
//        for (TimeSlotItem timeSlotItemF : timeSlotItemsF) {
//            timeSlotItems.add(new TimeSlotItem(timeSlotItemF.getSlotId(), timeSlotItemF.getSlotLabel(), timeSlotItemF.getAvailabel()));
//        }
//
//        mCalendarView.setTimeSlotItem(timeSlotItems);
//
//    }

    private void createShippingData(ShippingDao shippingDao) {
        if (NextPreWeekday == null) {
            NextPreWeekday = mCalendarView.getWeekDay();
            monthPreday = mCalendarView.getMonthDay();
            Log.d(TAG, "day of month:" + monthPreday.toString());
        }
        sunday = NextPreWeekday[0];
        monday = NextPreWeekday[1];
        tuesday = NextPreWeekday[2];
        wednesday = NextPreWeekday[3];
        thursday = NextPreWeekday[4];
        friday = NextPreWeekday[5];
        saturday = NextPreWeekday[6];
        for (Shipping shipping : shippingDao.getShippings()) {
            String shippingDate = shipping.getShippingDate();
            if (sunday.equalsIgnoreCase(shippingDate)) {
                for (ShippingItem shippingItem : shipping.getShippingItems()) {
                    String slotTime = shippingItem.getDescription();
                    switch (slotTime) {
                        case "09:00-09:30":
                            shippingItemsA.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "11:00-11:30":
                            shippingItemsB.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "13:00-13:30":
                            shippingItemsC.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "15:00-15:30":
                            shippingItemsD.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "17:00-17:30":
                            shippingItemsE.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "19:00-19:30":
                            shippingItemsF.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        default:
                            shippingItemsA.add(new ShippingItem(1, "FULL"));
                            shippingItemsB.add(new ShippingItem(2, "FULL"));
                            shippingItemsC.add(new ShippingItem(3, "FULL"));
                            shippingItemsD.add(new ShippingItem(6, "FULL"));
                            shippingItemsE.add(new ShippingItem(7, "FULL"));
                            shippingItemsF.add(new ShippingItem(8, "FULL"));
                    }
                }

            } else if (monday.equalsIgnoreCase(shippingDate)) {
                for (ShippingItem shippingItem : shipping.getShippingItems()) {
                    String slotTime = shippingItem.getDescription();
                    switch (slotTime) {
                        case "09:00-09:30":
                            shippingItemsA.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "11:00-11:30":
                            shippingItemsB.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "13:00-13:30":
                            shippingItemsC.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "15:00-15:30":
                            shippingItemsD.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "17:00-17:30":
                            shippingItemsE.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "19:00-19:30":
                            shippingItemsF.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        default:
                            shippingItemsA.add(new ShippingItem(1, "FULL"));
                            shippingItemsB.add(new ShippingItem(2, "FULL"));
                            shippingItemsC.add(new ShippingItem(3, "FULL"));
                            shippingItemsD.add(new ShippingItem(6, "FULL"));
                            shippingItemsE.add(new ShippingItem(7, "FULL"));
                            shippingItemsF.add(new ShippingItem(8, "FULL"));
                    }
                }

            }
            if (tuesday.equalsIgnoreCase(shippingDate)) {
                for (ShippingItem shippingItem : shipping.getShippingItems()) {
                    String slotTime = shippingItem.getDescription();
                    switch (slotTime) {
                        case "09:00-09:30":
                            shippingItemsA.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "11:00-11:30":
                            shippingItemsB.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "13:00-13:30":
                            shippingItemsC.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "15:00-15:30":
                            shippingItemsD.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "17:00-17:30":
                            shippingItemsE.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "19:00-19:30":
                            shippingItemsF.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        default:
                            shippingItemsA.add(new ShippingItem(1, "FULL"));
                            shippingItemsB.add(new ShippingItem(2, "FULL"));
                            shippingItemsC.add(new ShippingItem(3, "FULL"));
                            shippingItemsD.add(new ShippingItem(6, "FULL"));
                            shippingItemsE.add(new ShippingItem(7, "FULL"));
                            shippingItemsF.add(new ShippingItem(8, "FULL"));
                    }
                }
            } else if (wednesday.equalsIgnoreCase(shippingDate)) {
                for (ShippingItem shippingItem : shipping.getShippingItems()) {
                    String slotTime = shippingItem.getDescription();
                    switch (slotTime) {
                        case "09:00-09:30":
                            shippingItemsA.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "11:00-11:30":
                            shippingItemsB.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "13:00-13:30":
                            shippingItemsC.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "15:00-15:30":
                            shippingItemsD.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "17:00-17:30":
                            shippingItemsE.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "19:00-19:30":
                            shippingItemsF.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        default:
                            shippingItemsA.add(new ShippingItem(1, "FULL"));
                            shippingItemsB.add(new ShippingItem(2, "FULL"));
                            shippingItemsC.add(new ShippingItem(3, "FULL"));
                            shippingItemsD.add(new ShippingItem(6, "FULL"));
                            shippingItemsE.add(new ShippingItem(7, "FULL"));
                            shippingItemsF.add(new ShippingItem(8, "FULL"));
                    }
                }
            } else if (thursday.equalsIgnoreCase(shippingDate)) {
                for (ShippingItem shippingItem : shipping.getShippingItems()) {
                    String slotTime = shippingItem.getDescription();
                    switch (slotTime) {
                        case "09:00-09:30":
                            shippingItemsA.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "11:00-11:30":
                            shippingItemsB.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "13:00-13:30":
                            shippingItemsC.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "15:00-15:30":
                            shippingItemsD.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "17:00-17:30":
                            shippingItemsE.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "19:00-19:30":
                            shippingItemsF.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        default:
                            shippingItemsA.add(new ShippingItem(1, "FULL"));
                            shippingItemsB.add(new ShippingItem(2, "FULL"));
                            shippingItemsC.add(new ShippingItem(3, "FULL"));
                            shippingItemsD.add(new ShippingItem(6, "FULL"));
                            shippingItemsE.add(new ShippingItem(7, "FULL"));
                            shippingItemsF.add(new ShippingItem(8, "FULL"));
                    }
                }
            } else if (friday.equalsIgnoreCase(shippingDate)) {
                for (ShippingItem shippingItem : shipping.getShippingItems()) {
                    String slotTime = shippingItem.getDescription();
                    switch (slotTime) {
                        case "09:00-09:30":
                            shippingItemsA.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "11:00-11:30":
                            shippingItemsB.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "13:00-13:30":
                            shippingItemsC.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "15:00-15:30":
                            shippingItemsD.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "17:00-17:30":
                            shippingItemsE.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "19:00-19:30":
                            shippingItemsF.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        default:
                            shippingItemsA.add(new ShippingItem(1, "FULL"));
                            shippingItemsB.add(new ShippingItem(2, "FULL"));
                            shippingItemsC.add(new ShippingItem(3, "FULL"));
                            shippingItemsD.add(new ShippingItem(6, "FULL"));
                            shippingItemsE.add(new ShippingItem(7, "FULL"));
                            shippingItemsF.add(new ShippingItem(8, "FULL"));
                    }
                }
            } else if (saturday.equalsIgnoreCase(shippingDate)) {
                for (ShippingItem shippingItem : shipping.getShippingItems()) {
                    String slotTime = shippingItem.getDescription();
                    switch (slotTime) {
                        case "09:00-09:30":
                            shippingItemsA.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "11:00-11:30":
                            shippingItemsB.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "13:00-13:30":
                            shippingItemsC.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "15:00-15:30":
                            shippingItemsD.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "17:00-17:30":
                            shippingItemsE.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        case "19:00-19:30":
                            shippingItemsF.add(new ShippingItem(shippingItem.getId(), shippingItem.getDescription()));
                            break;
                        default:
                            shippingItemsA.add(new ShippingItem(1, "FULL"));
                            shippingItemsB.add(new ShippingItem(2, "FULL"));
                            shippingItemsC.add(new ShippingItem(3, "FULL"));
                            shippingItemsD.add(new ShippingItem(6, "FULL"));
                            shippingItemsE.add(new ShippingItem(7, "FULL"));
                            shippingItemsF.add(new ShippingItem(8, "FULL"));
                    }
                }
            }
        }

        for (ShippingItem shippingItemA : shippingItemsA) {
            shippingItems.add(new ShippingItem(shippingItemA.getId(), shippingItemA.getDescription()));
        }

        for (ShippingItem shippingItemB : shippingItemsB) {
            shippingItems.add(new ShippingItem(shippingItemB.getId(), shippingItemB.getDescription()));
        }

        for (ShippingItem shippingItemC : shippingItemsC) {
            shippingItems.add(new ShippingItem(shippingItemC.getId(), shippingItemC.getDescription()));
        }

        for (ShippingItem shippingItemD : shippingItemsD) {
            shippingItems.add(new ShippingItem(shippingItemD.getId(), shippingItemD.getDescription()));
        }

        for (ShippingItem shippingItemE : shippingItemsE) {
            shippingItems.add(new ShippingItem(shippingItemE.getId(), shippingItemE.getDescription()));
        }

        for (ShippingItem shippingItemF : shippingItemsF) {
            shippingItems.add(new ShippingItem(shippingItemF.getId(), shippingItemF.getDescription()));
        }

        mCalendarView.setTimeSlotItem(shippingItems);

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(mContext);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(Contextor.getInstance().getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    @Override
    public void onClick(View v) {
        if (v == mCardView) {
            EventBus.getDefault().post(new BookTimeBus(v));
        }
    }

    @Override
    public void onPreviousClick(String[] days) {
        showProgressDialog();
        NextPreWeekday = days;
        String month = CommonMethod.convertMonth(NextPreWeekday[0]);
        //getData(month);
        getDataSlot(month);
    }

    @Override
    public void onNextClick(String[] days) {
        showProgressDialog();
        NextPreWeekday = days;
        String month = CommonMethod.convertMonth(NextPreWeekday[0]);
        //getData(month);
        getDataSlot(month);
    }

}
