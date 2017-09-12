package cenergy.central.com.pwb_store.manager.service;

import java.util.List;

import cenergy.central.com.pwb_store.model.ProductDao;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.ProductList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by napabhat on 8/30/2017 AD.
 */

public interface ProductService {
//    @GET("/api/Products")
//    Call<List<ProductList>> getProductByPage(
//            @Query("DepartmentId") int departmentId,
//            @Query("Offset") int offSet,
//            @Query("Limit") int limit,
//            @Query("StoreId") String storeId);

    @GET("/api/Products")
    Call<ProductDao> getProduct(
            @Query("DepartmentId") int departmentId,
            @Query("Offset") int offSet,
            @Query("Limit") int limit,
            @Query("StoreId") String storeId,
            @Query("SortBy") String sortBy);

    @GET("/api/Products/{id}")
    Call<ProductDetail> getProductDetail(
            @Path("id") String productId,
            @Query("storeid") String storeId
    );

    @GET("/api/Products")
    Call<ProductDetail> getSearchBarcode(
            @Query("barcode") String barcode,
            @Query("storeid") String storeId);
}
