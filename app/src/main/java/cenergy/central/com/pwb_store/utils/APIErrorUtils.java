package cenergy.central.com.pwb_store.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

import cenergy.central.com.pwb_store.manager.HttpManager;
import cenergy.central.com.pwb_store.model.APIError;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by napabhat on 4/7/2017 AD.
 */

public class APIErrorUtils {
    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                HttpManager.getInstance().getRetrofit()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (Exception e) {
            return new APIError();
        }

        return error;
    }

    @Nullable
    public static APIError parseError(@NotNull okhttp3.Response response) {
        Converter<ResponseBody, APIError> converter =
                HttpManager.getInstance().getRetrofit()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            error = converter.convert(response.body());
        } catch (Exception e) {
            return new APIError();
        }

        return error;
    }
}
