package cenergy.central.com.pwb_store.manager.service;

import cenergy.central.com.pwb_store.model.request.CreateTokenRequest;
import cenergy.central.com.pwb_store.model.response.CancelTokenResponse;
import cenergy.central.com.pwb_store.model.response.CheckTokenResponse;
import cenergy.central.com.pwb_store.model.response.TokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public interface TokenService {

//    @POST("/HDLAPIDEV/Token/CreateToken")
//    Call<TokenResponse> createToken(
//            @Header("AppSecret") String appSecret,
//            @Query("device_id") String deviceId,
//            @Query("user_id") String userId,
//            @Query("store_code") String storeCode
//    );

    @POST("/HDLAPI/Token/CreateToken")
    Call<TokenResponse> createToken(
            @Header("AppSecret") String appSecret,
            @Header("Content-Type") String type,
            @Body CreateTokenRequest createTokenRequest
            );


    @POST("/HDLAPI/Token/GetTokenInfo")
    Call<CheckTokenResponse> checkToken(
            @Header("Authorization") String appSecret
    );

    @POST("/HDLAPI/Token/DisposeToken")
    Call<CancelTokenResponse> cancelToken(
            @Header("Authorization") String appSecret
    );
}
