package cenergy.central.com.pwb_store.manager.service;

import java.util.List;

import cenergy.central.com.pwb_store.model.StoreList;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by napabhat on 9/5/2017 AD.
 */

public interface StoreService {
        @GET("/api/Stores")
        Call<List<StoreList>> getStore();
}
