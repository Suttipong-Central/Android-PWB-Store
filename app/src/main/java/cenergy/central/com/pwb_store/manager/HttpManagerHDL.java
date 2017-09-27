package cenergy.central.com.pwb_store.manager;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.manager.service.CategoryService;
import cenergy.central.com.pwb_store.manager.service.HDLService;
import cenergy.central.com.pwb_store.manager.service.ProductService;
import cenergy.central.com.pwb_store.manager.service.StoreService;
import cenergy.central.com.pwb_store.manager.service.TokenService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class HttpManagerHDL {
    //Specific Header
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_CONTENT = "Content-Type";
    private static final String HEADER_TYPE = "datatype";
    //Specific Client
    private static final String CLIENT_ANDROID = "Android";
    private static final String CLIENT_LANGUAGE = "th";
    private static final String CLIENT_APP_ID = "pwb-store";
    private static final String CLIENT_AUTH = "Basic cG93ZXJidXk6dWF0QHB3YiE=";
    private static final String CLIENT_CONTENT = "application/json";
    private static final String CLIENT_TYPE = "json";

    private static final String BASE_URL_DEV = "http://10.5.0.31";
    private static final String BASE_URL_UAT = "http://uat-api.powerbuy.co.th";

    private static HttpManagerHDL instance;
    private Context mContext;
    private Retrofit retrofit;
    private TokenService mTokenService;
    private HDLService mHDLService;
    private String url;

    private HttpManagerHDL() {
        mContext = Contextor.getInstance().getContext();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                //.addHeader(HEADER_AUTHORIZATION, CLIENT_AUTH)
                                //.addHeader(HEADER_CONTENT, CLIENT_CONTENT)
                                .addHeader(HEADER_TYPE, CLIENT_TYPE)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_DEV)
                .addConverterFactory(GsonConverterFactory.create())
                .client(defaultHttpClient)
                .build();
        mTokenService = retrofit.create(TokenService.class);
        mHDLService = retrofit.create(HDLService.class);
    }

    public static HttpManagerHDL getInstance() {
        if (instance == null)
            instance = new HttpManagerHDL();
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public TokenService getTokenService(){
        return mTokenService;
    }

    public HDLService getHDLService() {
        return mHDLService;
    }
}
