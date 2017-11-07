package cenergy.central.com.pwb_store.manager.service;

import java.util.List;

import cenergy.central.com.pwb_store.model.AvaliableStoreItem;
import cenergy.central.com.pwb_store.model.StoreList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by napabhat on 9/5/2017 AD.
 */

public interface StoreService {
        //@GET("/api/Stores")
        @GET("/rest/V2/stores")
        Call<List<StoreList>> getStore();

        @GET("/rest/V2/availablestock/{sku}")
        Call<List<AvaliableStoreItem>> getAvailableStock(
                @Path("sku") String sku
        );
}
