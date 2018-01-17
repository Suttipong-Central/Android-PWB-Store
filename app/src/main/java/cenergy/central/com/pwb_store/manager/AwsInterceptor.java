package cenergy.central.com.pwb_store.manager;

import android.support.annotation.NonNull;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.http.HttpMethodName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by mrdevxus on 1/12/18.
 */

public class AwsInterceptor implements Interceptor {

    @NonNull
    private final AWSCredentialsProvider credentialsProvider;
    @NonNull
    private final String serviceName;
    @NonNull
    private final String xApiKey;
    @NonNull
    private final AWS4Signer signer;

    public AwsInterceptor(@NonNull AWSCredentialsProvider credentialsProvider, @NonNull String serviceName, @NonNull String region, @NonNull String xApiKey) {
        this.credentialsProvider = credentialsProvider;
        this.serviceName = serviceName;
        this.xApiKey = xApiKey;
        signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request signedRequest = sign(chain.request());

        signedRequest = signedRequest.newBuilder()
                .addHeader("x-api-key", xApiKey)
                .build();

        return chain.proceed(signedRequest);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private Request sign(@NonNull Request request) throws IOException {
        Request.Builder builder = request.newBuilder();
        DefaultRequest awsDummyRequest = new DefaultRequest(serviceName);

        HttpUrl url = setEndpoint(builder, awsDummyRequest, request.url());

        setQueryParams(awsDummyRequest, url);

        setHttpMethod(awsDummyRequest, request.method());

        setBody(awsDummyRequest, request.body());

        signer.sign(awsDummyRequest, credentialsProvider.getCredentials());

        applyAwsHeaders(builder, awsDummyRequest.getHeaders());

        return builder.build();
    }

    @NonNull
    private HttpUrl setEndpoint(@NonNull Request.Builder builder, @NonNull DefaultRequest awsRequest, @NonNull HttpUrl url) {
        HttpUrl canonicalUrl = ensureTrailingSlash(builder, url);
        awsRequest.setEndpoint(canonicalUrl.uri());

        return canonicalUrl;
    }

    private void setQueryParams(@NonNull DefaultRequest awsRequest, @NonNull HttpUrl url) {
        for (String paramName : url.queryParameterNames()) {
            awsRequest.addParameter(paramName, url.queryParameter(paramName));
        }
    }

    private void setHttpMethod(@NonNull DefaultRequest awsRequest, @NonNull String method) {
        HttpMethodName methodName = HttpMethodName.valueOf(method);
        awsRequest.setHttpMethod(methodName);
    }

    private void setBody(@NonNull DefaultRequest awsRequest, @NonNull RequestBody body) throws IOException {
        if (body == null) {
            return;
        }

        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        awsRequest.setContent(new ByteArrayInputStream(buffer.readByteArray()));
        awsRequest.addHeader("Content-Length", String.valueOf(body.contentLength()));
        buffer.close();
    }

    private void applyAwsHeaders(@NonNull Request.Builder builder, @NonNull Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }
    }

    @NonNull
    private HttpUrl ensureTrailingSlash(@NonNull Request.Builder builder, @NonNull HttpUrl url) {
        String lastPathSegment = url.pathSegments().get(url.pathSize() - 1);
        if (!lastPathSegment.isEmpty()) {
            url = url.newBuilder().addPathSegment("").build();
            builder.url(url);
        }

        return url;
    }

}
