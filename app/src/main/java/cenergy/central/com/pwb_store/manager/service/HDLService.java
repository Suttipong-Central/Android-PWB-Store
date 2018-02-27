package cenergy.central.com.pwb_store.manager.service;

import cenergy.central.com.pwb_store.model.AvaliableStoreDao;
import cenergy.central.com.pwb_store.model.ShippingDao;
import cenergy.central.com.pwb_store.model.request.HDLRequest;
import cenergy.central.com.pwb_store.model.request.ShippingRequest;
import cenergy.central.com.pwb_store.model.response.HDLResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public interface HDLService {

    @POST("/HDLAPI/HomeDelivery/CheckTimeSlot")
    Call<HDLResponse> checkTimeSlot(
            @Header("Authorization") String appSecret,
            @Header("Content-Type") String type,
            @Body HDLRequest hdlRequest
    );

    @POST("v1/logistics/shipment/list-shipping-slot")
    Call<ShippingDao> checkShippingTime(
            @Body ShippingRequest shippingRequest
            );

    @GET("v1/products/list-stock")
    Call<AvaliableStoreDao> getStore(
            @Query("skus") String sku
    );
}
