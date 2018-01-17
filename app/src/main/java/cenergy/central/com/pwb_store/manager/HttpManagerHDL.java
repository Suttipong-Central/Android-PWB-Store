package cenergy.central.com.pwb_store.manager;

import android.content.Context;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.manager.service.HDLService;
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
    //Specific Client
    private static final String CLIENT_SERVICENAME = "execute-api";
    private static final String CLIENT_REGION = "ap-southeast-1";
    private static final String CLIENT_X_API_KEY = "V9MwQAF6eX3soBR38nuNiajzXWvkI00pax22X14W";//"4KtWVSnAdm3kFuHygoWAo1dssKPMHGmd5pU1b00E";
    private static final String CLIENT_AUTH = "Basic cG93ZXJidXk6dWF0QHB3YiE=";

    private static final String BASE_URL_DEV = "http://10.5.0.31";
    private static final String BASE_URL = "https://sit-api.central.tech";

    private static HttpManagerHDL instance;
    private Context mContext;
    private Retrofit retrofit;
    private TokenService mTokenService;
    private HDLService mHDLService;
    private String url;

    private HttpManagerHDL() {
        mContext = Contextor.getInstance().getContext();
        Session session = auth();
        AWSCredentialsProvider awsCredentialsProvider = new MyAWSCredentialsProvider(session);
        AwsInterceptor awsInterceptor = new AwsInterceptor(awsCredentialsProvider, CLIENT_SERVICENAME, CLIENT_REGION, CLIENT_X_API_KEY);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                //.addHeader(HEADER_AWS, CLIENT_AWS)
                                .build();

                        return chain.proceed(request);
                    }
                })
                .addInterceptor(awsInterceptor)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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

    private static class MyAWSCredentialsProvider implements AWSCredentialsProvider {
        private final Session session;

        MyAWSCredentialsProvider(Session session) {
            this.session = session;
        }

        @Override
        public AWSCredentials getCredentials() {
            return new BasicAWSCredentials(session.accessKey, session.secretKey);
            //return new BasicSessionCredentials(session.access_key_id, session.secret_access_key, session.session_token);
        }

        @Override
        public void refresh() {

        }
    }

    private Session auth() {
        Session auth = new Session();
        auth.accessKey = "AKIAILUQVZ5TERXPIMUA";
        auth.secretKey = "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";

        return auth;
    }

    private class Session {
        String accessKey;
        String secretKey;
    }

}
