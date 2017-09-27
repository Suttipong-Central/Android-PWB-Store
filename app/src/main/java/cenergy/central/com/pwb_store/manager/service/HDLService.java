package cenergy.central.com.pwb_store.manager.service;

import cenergy.central.com.pwb_store.model.request.HDLRequest;
import cenergy.central.com.pwb_store.model.response.HDLResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public interface HDLService {

    @POST("/HDLAPIDEV/HomeDelivery/CheckTimeSlot")
    Call<HDLResponse> checkTimeSlot(
            @Header("Authorization") String appSecret,
            @Header("Content-Type") String type,
            @Body HDLRequest hdlRequest
    );
}
