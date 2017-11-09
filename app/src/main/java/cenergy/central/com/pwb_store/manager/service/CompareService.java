package cenergy.central.com.pwb_store.manager.service;

import java.util.List;

import cenergy.central.com.pwb_store.model.CompareDao;
import cenergy.central.com.pwb_store.model.CompareList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by napabhat on 11/8/2017 AD.
 */

public interface CompareService {
    @GET("/rest/V1/products")
    Call<CompareList> getCompareProductList(
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String sku,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") String skuId,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String in,
            @Query("searchCriteria[filterGroups][1][filters][0][field]") String inStore,
            @Query("searchCriteria[filterGroups][1][filters][0][value]") String storeId,
            @Query("searchCriteria[filterGroups][1][filters][0][conditionType]") String finSet,
            @Query("searchCriteria[sortOrders][0][field]") String skuSort);

    @GET("/rest/V2/compare")
    Call<List<CompareDao>> getCompareItem(
            @Query("sku") String sku);
}
