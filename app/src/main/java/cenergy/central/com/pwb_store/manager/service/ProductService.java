package cenergy.central.com.pwb_store.manager.service;

import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDao;
import cenergy.central.com.pwb_store.model.ProductDetail;
import cenergy.central.com.pwb_store.model.ProductDetailDao;
import cenergy.central.com.pwb_store.model.response.BrandResponse;
import cenergy.central.com.pwb_store.model.response.ProductByBarcodeResponse;
import cenergy.central.com.pwb_store.model.response.ProductResponse;
import cenergy.central.com.pwb_store.model.response.ProductSearchResponse;
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

//    @GET("/rest/V1/products")
//    Call<ProductDao> getProductList(
//            @Query("searchCriteria[filterGroups][0][filters][0][field]") String category,
//            @Query("searchCriteria[filterGroups][0][filters][0][value]") String categoryId,
//            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String in,
//            @Query("searchCriteria[filterGroups][1][filters][0][field]") String inStore,
//            @Query("searchCriteria[filterGroups][1][filters][0][value]") String storeId,
//            @Query("searchCriteria[filterGroups][1][filters][0][conditionType]") String type,
//            @Query("searchCriteria[pageSize]") int pageSize,
//            @Query("searchCriteria[currentPage]") int currentPage,
//            @Query("searchCriteria[sortOrders][0][field]") String name,
//            @Query("searchCriteria[sortOrders][0][direction]") String typeSearch,
//            @Query("fields") String fields);

//    @GET("/api/Products/{id}")
//    Call<ProductDetail> getProductDetail(
//            @Path("id") String productId,
//            @Query("storeid") String storeId
//    );

    @GET("/rest/V1/products/{sku}")
    Call<ProductDetail> getProductDetailMagento(
            @Path("sku") String sku,
            @Query("branch_id") String storeId,
            @Query("fields") String fields
    );

    @GET("/api/Products")
    Call<ProductDetail> getSearchBarcode(
            @Query("barcode") String barcode,
            @Query("storeid") String storeId);

    @GET("/rest/V1/products")
    Call<ProductDetailDao> getSearchBarcodeMagento(
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String inStore,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") String storeId,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String type,
            @Query("searchCriteria[filterGroups][1][filters][0][field]") String barCodeName,
            @Query("searchCriteria[filterGroups][1][filters][0][value]") String barCode,
            @Query("searchCriteria[filterGroups][1][filters][0][conditionType]") String eq,
            @Query("searchCriteria[sortOrders][0][field]") String name,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage);

    @GET("/rest/V2/search")
    Call<ProductDao> getProductSearch(
            @Query("searchCriteria[requestName]") String quick,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String searchTerm,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") String keyWord,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage,
            @Query("fields") String fields,
            @Query("branch_id") String branchId);

    // region new api

    @GET("/{lang}/rest/V1/headless/search-suggestion")
    Call<ProductSearchResponse> getProductSearch(
            @Path("lang") String language,
            @Query("q") String keyword,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage,
            @Query("searchCriteria[sortOrders][0][field]") String orderName,
            @Query("searchCriteria[sortOrders][0][direction]") String dir);

    @GET("/{lang}/rest/V1/headless/search-suggestion")
    Call<ProductSearchResponse> getProductSearch(
            @Path("lang") String language,
            @Query("q") String keyword,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage);

    @GET("/rest/V1/products")
    Call<ProductResponse> getProductList(
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String category,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") String categoryId,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String in,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage,
            @Query("searchCriteria[sortOrders][0][direction]") String typeSearch,
            @Query("fields") String fields);

    @GET("/rest/V1/headless/categories/{categoryId}/products")
    Call<ProductResponse> getProductList(
            @Path("categoryId") String categoryId,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String statusQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") int statusValue,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String conditionType,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage);

    @GET("/{lang}/rest/V1/headless/categories/{categoryId}/products")
    Call<ProductResponse> getProductList(
            @Path("lang") String lang,
            @Path("categoryId") String categoryId,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String statusQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") int statusValue,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String conditionType,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage,
            @Query("searchCriteria[sortOrders][0][field]") String orderName,
            @Query("searchCriteria[sortOrders][0][direction]") String dir);

    @GET("/rest/V1/headless/categories/{categoryId}/products")
    Call<ProductResponse> getProductsFilterByBrand(
            @Path("categoryId") String categoryId,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String statusQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") int statusValue,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String conditionType,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String brandQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") Long brandId,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String brandConditionType,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage);

    @GET("/{lang}/rest/V1/headless/categories/{categoryId}/products")
    Call<ProductResponse> getProductsFilterByBrand(
            @Path("lang") String lang,
            @Path("categoryId") String categoryId,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String statusQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") int statusValue,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String conditionType,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String brandQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") Long brandId,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String brandConditionType,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage,
            @Query("searchCriteria[sortOrders][0][field]") String orderName,
            @Query("searchCriteria[sortOrders][0][direction]") String dir);

    @GET("/{lang}/rest/V1/headless/categories/{categoryId}/products")
    Call<ProductResponse> getProductsFilterByBrand(
            @Path("lang") String lang,
            @Path("categoryId") String categoryId,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String statusQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") int statusValue,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String conditionType,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String brandQuery,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") Long brandId,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String brandConditionType,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage);

    @GET("/rest/V1/headless/categories/{categoryId}/brands")
    Call<BrandResponse> getBrands(@Path("categoryId") String categoryId);

    @GET("/rest/V1/products/{sku}")
    Call<Product> getProductDetail(
            @Path("sku") String sku);

    @GET("/{lang}/rest/V1/products")
    Call<ProductByBarcodeResponse> getProductFromBarcode(
            @Path("lang") String language,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") String barCodeName,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") String barCode,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") String eq,
            @Query("searchCriteria[sortOrders][0][field]") String name,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage);

    // TODO: Delete when get new api this case so hard for filter
    @GET("/rest/V1/products")
    Call<ProductResponse> getProductFromSearch(
            @Query("searchCriteria[filterGroups][0][filters][1][field]") String filterName,
            @Query("searchCriteria[filterGroups][0][filters][1][value]") String name,
            @Query("searchCriteria[filterGroups][0][filters][1][conditionType]") String ctName,
            @Query("searchCriteria[filterGroups][0][filters][2][field]") String filterSku,
            @Query("searchCriteria[filterGroups][0][filters][2][value]") String sku,
            @Query("searchCriteria[filterGroups][0][filters][2][conditionType]") String ctSku,
            @Query("searchCriteria[filterGroups][0][filters][3][field]") String filterBrand,
            @Query("searchCriteria[filterGroups][0][filters][3][value]") String brand,
            @Query("searchCriteria[filterGroups][0][filters][3][conditionType]") String ctBrand,
            @Query("searchCriteria[pageSize]") int pageSize,
            @Query("searchCriteria[currentPage]") int currentPage,
            @Query("fields") String fields
    );
    // end region
}
