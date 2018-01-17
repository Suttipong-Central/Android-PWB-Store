package cenergy.central.com.pwb_store.manager;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.manager.service.CategoryService;
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

public class HttpManager {
    //Specific Header
    public static final String HEADER_GUEST_ID = "GuestId";
    private static final String HEADER_CLIENT = "X-Client";
    private static final String HEADER_OS_VERSION = "OSVersion";
    private static final String HEADER_LANGUAGE = "X-Language";
    private static final String HEADER_APP_ID = "AppId";
    private static final String HEADER_UUID = "UUID";
    private static final String HEADER_USER_ID = "UserId"; // Mock Data
    private static final String HEADER_EMPLOYEE_ID = "EmployeeId";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    //Specific Client
    private static final String CLIENT_ANDROID = "Android";
    private static final String CLIENT_LANGUAGE = "th";
    private static final String CLIENT_APP_ID = "pwb-store";
    private static final String CLIENT_AUTH = "Basic cG93ZXJidXk6dWF0QHB3YiE=";
    private static final String CLIENT_MAGENTO = "Bearer q8fx1239sgs79ssfn7opto3ivr8mmcqr";

    private static final String BASE_URL_MAGENTO = "http://api.powerbuy.co.th";
    private static final String BASE_URL_UAT = "http://uat-api.powerbuy.co.th";
    private static HttpManager instance;
    private Context mContext;
    private Retrofit retrofit;
    private TokenService mTokenService;
    private CategoryService mCategoryService;
    private ProductService mProductService;
    private StoreService mStoreService;
    private String url;

    private HttpManager() {
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
                                .addHeader(HEADER_AUTHORIZATION, CLIENT_MAGENTO)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_MAGENTO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(defaultHttpClient)
                .build();
        mTokenService = retrofit.create(TokenService.class);
        mCategoryService = retrofit.create(CategoryService.class);
        mProductService = retrofit.create(ProductService.class);
        mStoreService = retrofit.create(StoreService.class);
    }

    public static HttpManager getInstance() {
        if (instance == null)
            instance = new HttpManager();
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public TokenService getTokenService(){
        return mTokenService;
    }

    public CategoryService getCategoryService(){
        return mCategoryService;
    }

    public ProductService getProductService(){
        return mProductService;
    }

    public StoreService getStoreService(){
        return  mStoreService;
    }

}
