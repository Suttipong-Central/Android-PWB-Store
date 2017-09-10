package cenergy.central.com.pwb_store.manager.service;

import cenergy.central.com.pwb_store.model.response.TokenResponse;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public interface TokenService {

    @POST("/HDLAPI/API/Token/CreateToken")
    Call<TokenResponse> createToken(
            @Header("AppSecret") String appSecret,
            @Query("device_id") String deviceId,
            @Query("user_id") String userId,
            @Query("store_code") int storeCode
    );

}
