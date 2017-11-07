package cenergy.central.com.pwb_store.manager.service;

import java.util.List;

import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by napabhat on 8/25/2017 AD.
 */

public interface CategoryService {
    //@GET("/api/Categories")
    @GET("/rest/V2/categories")
    Call<List<Category>> getCategories();

    @GET("api/Categories")
    Call<List<ProductFilterHeader>> getProductFilter(
            @Query("parentId") int parentId);
}
