package cenergy.central.com.pwb_store.realm;


import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

import cenergy.central.com.pwb_store.model.AddCompare;
import cenergy.central.com.pwb_store.model.Branch;
import cenergy.central.com.pwb_store.model.Brand;
import cenergy.central.com.pwb_store.model.CacheCartItem;
import cenergy.central.com.pwb_store.model.CachedEndpoint;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CompareProduct;
import cenergy.central.com.pwb_store.model.District;
import cenergy.central.com.pwb_store.model.Order;
import cenergy.central.com.pwb_store.model.Postcode;
import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.Province;
import cenergy.central.com.pwb_store.model.StorePickupList;
import cenergy.central.com.pwb_store.model.SubDistrict;
import cenergy.central.com.pwb_store.model.UserInformation;
import cenergy.central.com.pwb_store.model.UserToken;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by napabhat on 9/13/2017 AD.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;


    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController getInstance() {
        if (instance == null) {
            instance = new RealmController();
        }
        instance.realm.refresh();
        return instance;
    }

    public Realm getRealm() {
        return realm != null ? realm : Realm.getDefaultInstance();
    }

    public AddCompare getCompare(String id) {
        return realm.where(AddCompare.class).equalTo("productId", id).findFirst();
    }

    public long getCount() {
        long count = realm.where(AddCompare.class).count();
        return count;
    }

    //find all objects in the AppCompare.class
    public RealmResults<AddCompare> getCompares() {

        //return realm.where(AddCompare.class).findAll();
        return realm.where(AddCompare.class).sort("productSku", Sort.DESCENDING).findAll();
    }

    public RealmResults<AddCompare> deletedCompare(final String productSku) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<AddCompare> result = realm.where(AddCompare.class)
                        .equalTo("productSku", productSku)
                        .sort("productSku", Sort.DESCENDING)
                        .findAll();
                result.deleteAllFromRealm();
            }
        });

        return realm.where(AddCompare.class).findAll();
    }

    // region category
    public void saveCategory(final Category category) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                // delete category
                realm.delete(Category.class);
                // store category
                realm.insertOrUpdate(category);
            }
        });
    }

    public Category getCategory() {
        Realm realm = getRealm();
        return realm.where(Category.class).findFirst();
    }
    // endregion

    // region compare product
    public void saveCompareProduct(final Product product, final DatabaseListener listener) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(realm1 -> {
            CompareProduct compareProduct = CompareProduct.asCompareProduct(product);
            realm1.copyToRealmOrUpdate(compareProduct);
        }, () -> {
            if (listener != null) {
                listener.onSuccessfully();
            }
        }, error -> {
            if (listener != null) {
                listener.onFailure(error);
            }
        });
    }

    public List<CompareProduct> deleteCompareProduct(final String sku) {
        Realm realm = getRealm();
        realm.executeTransaction(realm1 -> {
            RealmResults<CompareProduct> realmCompareProducts = realm1.where(CompareProduct.class).equalTo(CompareProduct.FIELD_SKU, sku).findAll();
            if (realmCompareProducts != null) {
                realmCompareProducts.deleteAllFromRealm();
            }
        });

        return getCompareProducts();
    }

    public List<CompareProduct> getCompareProducts() {
        Realm realm = getRealm();
        RealmResults<CompareProduct> realmCompareProducts = realm.where(CompareProduct.class).sort(CompareProduct.FIELD_SKU, Sort.DESCENDING).findAll();
        return realmCompareProducts == null ? null : realm.copyFromRealm(realmCompareProducts);
    }

    public void deleteAllCompareProduct() {
        Realm realm = getRealm();
        realm.executeTransaction(realm1 -> realm1.where(CompareProduct.class).findAll().deleteAllFromRealm());
    }

    public CompareProduct getCompareProduct(String sku) {
        return realm.where(CompareProduct.class).equalTo(CompareProduct.FIELD_SKU, sku).findFirst();
    }
    // endregion

    // region cart item
    public void saveCartItem(final CacheCartItem cacheCartItem) {
        Realm realm = getRealm();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(cacheCartItem));
    }

    public void saveCartItem(final CacheCartItem cacheCartItem, final DatabaseListener listener) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(realm1 -> realm1.copyToRealmOrUpdate(cacheCartItem), () -> {
            if (listener != null) {
                Log.d("Database", "stored cart item");
                listener.onSuccessfully();
            }
        }, error -> {
            if (listener != null) {
                listener.onFailure(error);
            }
        });
    }

    public void saveCartItem(final CacheCartItem cacheCartItem, StorePickupList storePickupList,
                             final DatabaseListener listener) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(realm1 -> {
            realm1.copyToRealmOrUpdate(cacheCartItem);
            realm1.copyToRealmOrUpdate(storePickupList);
        }, () -> {
            if (listener != null) {
                Log.d("Database", "stored cart item");
                listener.onSuccessfully();
            }
        }, error -> {
            if (listener != null) {
                listener.onFailure(error);
            }
        });
    }

    public void editBranchInCartItem(final Branch branch, final DatabaseListener listener) {
        realm.executeTransactionAsync(realm1 -> {
            RealmResults<CacheCartItem> realmCartItems = realm1.where(CacheCartItem.class)
                    .sort(CacheCartItem.FIELD_ID, Sort.DESCENDING).findAll();
            for (CacheCartItem realmItem : realmCartItems) {
                CacheCartItem item = realm1.copyFromRealm(realmItem);
                item.setBranch(branch);
                realm1.copyToRealmOrUpdate(item);
            }
        }, () -> {
            if (listener != null) {
                Log.d("Database", "stored cart item");
                listener.onSuccessfully();
            }
        }, error -> {
            if (listener != null) {
                listener.onFailure(error);
            }
        });
    }

    public void deleteCartItem(final Long itemId) {
        Realm realm = getRealm();
        realm.executeTransaction(realm1 -> {
            RealmResults<CacheCartItem> items = realm1.where(CacheCartItem.class).equalTo(
                    CacheCartItem.FIELD_ID, itemId).findAll();

            // Delete Store Pick List
            if (items.get(0) != null) {
                String sku = items.get(0).getSku();
                RealmResults<StorePickupList> storePickupLists = realm1.where(
                        StorePickupList.class).equalTo(StorePickupList.FIELD_SKU, sku).findAll();
                storePickupLists.deleteAllFromRealm();
            }

            // Delete Cache Cart Item
            items.deleteAllFromRealm();
        });
    }

    public List<CacheCartItem> getCacheCartItems() {
        Realm realm = getRealm();
        RealmResults<CacheCartItem> realmCartItems = realm.where(CacheCartItem.class).sort(CacheCartItem.FIELD_ID, Sort.DESCENDING).findAll();
        return realmCartItems == null ? null : realm.copyFromRealm(realmCartItems);
    }

    public CacheCartItem getCacheCartItem(Long itemId) {
        Realm realm = getRealm();
        CacheCartItem realmCartItem = realm.where(CacheCartItem.class).equalTo(CacheCartItem.FIELD_ID, itemId).findFirst();
        return realmCartItem == null ? null : realm.copyFromRealm(realmCartItem);
    }

    public CacheCartItem getCacheCartItemBySKU(String sku) {
        Realm realm = getRealm();
        CacheCartItem realmCartItem = realm.where(CacheCartItem.class).equalTo(CacheCartItem.FIELD_SKU, sku).findFirst();
        return realmCartItem == null ? null : realm.copyFromRealm(realmCartItem);
    }

    public void deleteAllCacheCartItem() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Delete Store Pickup List
                realm.where(StorePickupList.class).findAll().deleteAllFromRealm();
                // Delete Cache Cart Item
                realm.where(CacheCartItem.class).findAll().deleteAllFromRealm();
            }
        });
    }

    public void deleteAllCacheCartItem(final DatabaseListener listener) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(it ->
        {
            // Delete Store Pickup List
            it.where(StorePickupList.class).findAll().deleteAllFromRealm();
            // Delete Cache Cart Item
            it.where(CacheCartItem.class).findAll().deleteAllFromRealm();
        }, () -> {
            if (listener != null) {
                listener.onSuccessfully();
            }
        }, error -> {
            if (listener != null) {
                listener.onFailure(error);
            }
        });
    }
    // endregion

    // region Store Pickup
    public List<StorePickupList> getStorePickupLists() {
        Realm realm = getRealm();
        RealmResults<StorePickupList> realmCartItems = realm.where(StorePickupList.class).findAll();
        return realmCartItems == null ? null : realm.copyFromRealm(realmCartItems);
    }
    // endregion

    // region Order
    public void saveOrder(final Order order, final DatabaseListener listener) {
        realm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(order), () -> {
            if (listener != null) {
                listener.onSuccessfully();
            }
        }, error -> {
            if (listener != null) {
                listener.onFailure(error);
            }
        });
    }

    public List<Order> deleteOrder(final String orderId) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<Order> realmOrderResponses = realm.where(Order.class).equalTo(
                        Order.FIELD_ORDER_ID, orderId).findAll();
                realmOrderResponses.deleteAllFromRealm();
            }
        });

        return getOrders();
    }

    public List<Order> getOrders() {
        Realm realm = getRealm();
        RealmResults<Order> realmOrderResponses = realm.where(Order.class).sort(Order.FIELD_ORDER_ID, Sort.DESCENDING).findAll();
        return realmOrderResponses == null ? null : realm.copyFromRealm(realmOrderResponses);
    }

    public Order getOrder(String orderId) {
        Order realmOrderResponse = realm.where(Order.class).equalTo(Order.FIELD_ORDER_ID, orderId).findFirst();
        return realmOrderResponse == null ? null : realm.copyFromRealm(realmOrderResponse);
    }

    public void deleteAllOrder() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Order.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // endregion

    // region brands
    public void saveBands(final Brand brand) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(brand);
            }
        });
    }

    public Brand getBrand(Long orderId) {
        Brand realmBrand = realm.where(Brand.class).equalTo(Brand.FIELD_BRAN_ID, orderId).findFirst();
        return realmBrand == null ? null : realm.copyFromRealm(realmBrand);
    }

    public void deleteBrans() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Brand.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // endregion

    // region user
    public void saveUserInformation(final UserInformation userInformation) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(userInformation);
            }
        });
    }

    public UserInformation getUserInformation() {
        UserInformation realmUser = realm.where(UserInformation.class).findFirst();
        return realmUser == null ? null : realm.copyFromRealm(realmUser);
    }

    public void deleteUserInformation() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(UserInformation.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // endregion

    // end region

    // region userToken
    public void saveUserToken(final UserToken userToken) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.insertOrUpdate(userToken);
            }
        });
    }

    public UserToken getUserToken() {
        UserToken realmUserToken = realm.where(UserToken.class).findFirst();
        return realmUserToken == null ? null : realm.copyFromRealm(realmUserToken);
    }

    public void deleteUserToken() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(UserToken.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // end region userToken

    // region caching
    public void updateCachedEndpoint(String endpoint) {
        final CachedEndpoint cachedEndpoint = new CachedEndpoint();
        cachedEndpoint.setEndpoint(endpoint);
        cachedEndpoint.setLastUpdated(new Date());

        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(cachedEndpoint);
            }
        });
    }

    public void clearCachedEndpoint(final String endpoint) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(CachedEndpoint.class).like("endpoint", endpoint).findAll().deleteAllFromRealm();
            }
        });
    }

    public void deleteAllCachedEndpoint() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(CachedEndpoint.class).findAll().deleteAllFromRealm();
            }
        });
    }

    public boolean hasFreshlyCachedEndpoint(String endpoint) {
        return hasFreshlyCachedEndpoint(endpoint, 1);
    }

    public boolean hasFreshlyCachedEndpoint(String endpoint, int hours) {
        Realm realm = getRealm();
        CachedEndpoint cachedEndpoint = realm.where(CachedEndpoint.class).equalTo("endpoint", endpoint).findFirst();
        return cachedEndpoint != null && cachedEndpoint.getLastUpdated().after(new Date(System.currentTimeMillis() - (hours * 60 * 60 * 1000)));
    }
    // end region

    // region province
    public void storeProvince(final Province province) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(province);
            }
        });
    }

    public Province getProvince(String provinceId) {
        Realm realm = getRealm();
        return realm.where(Province.class).equalTo(Province.FIELD_ID, provinceId).findFirst();
    }

    public Province getProvinceByFieldName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Realm realm = getRealm();
        return realm.where(Province.class).equalTo(Province.FIELD_NAME, name).findFirst();
    }

    public Province getProvinceByDefaultName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Realm realm = getRealm();
        return realm.where(Province.class).equalTo(Province.FIELD_DEFAULT_NAME, name).findFirst();
    }

    public Province getProvinceByName(String name) {
        return (getProvinceByFieldName(name) == null) ? getProvinceByDefaultName(name) : getProvinceByFieldName(name);
    }


    public List<Province> getProvinces() {
        Realm realm = getRealm();
        return realm.where(Province.class).findAll();
    }

    public void deleteProvinces() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Province.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // endregion

    // region district
    public void storeDistrict(final District district) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(district);
            }
        });
    }

    public District getDistrict(String districtId) {
        Realm realm = getRealm();
        return realm.where(District.class).equalTo(District.FIELD_ID, districtId).findFirst();
    }

    public District getDistrictByFieldName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Realm realm = getRealm();
        return realm.where(District.class).equalTo(District.FIELD_NAME, name).findFirst();
    }

    public District getDistrictByDefaultName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Realm realm = getRealm();
        return realm.where(District.class).equalTo(District.FIELD_DEFAULT_NAME, name).findFirst();
    }

    public District getDistrictByName(String name) {
        return (getDistrictByFieldName(name) == null) ? getDistrictByDefaultName(name) : getDistrictByFieldName(name);
    }

    public List<District> getDistricts() {
        Realm realm = getRealm();
        return realm.where(District.class).sort(District.FIELD_ID, Sort.ASCENDING).findAll();
    }

    public List<District> getDistrictsByProvinceId(String provinceId) {
        Realm realm = getRealm();
        return realm.where(District.class)
                .equalTo(District.FIELD_PROVINCE_ID, provinceId)
                .sort(District.FIELD_ID, Sort.ASCENDING).findAll();
    }

    public void deleteDistricts() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(District.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // endregion

    // region sub district
    public void storeSubDistrict(final SubDistrict subDistrict) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(subDistrict);
            }
        });
    }

    public SubDistrict getSubDistrict(String subDistrictId) {
        Realm realm = getRealm();
        return realm.where(SubDistrict.class).equalTo(SubDistrict.FIELD_ID, subDistrictId).findFirst();
    }

    public SubDistrict getSubDistrictByFieldName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Realm realm = getRealm();
        return realm.where(SubDistrict.class).equalTo(SubDistrict.FIELD_NAME, name).findFirst();
    }

    public SubDistrict getSubDistrictByDefaultName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Realm realm = getRealm();
        return realm.where(SubDistrict.class).equalTo(SubDistrict.FIELD_DEFAULT_NAME, name).findFirst();
    }

    public SubDistrict getSubDistrictByName(String name) {
        return (getSubDistrictByFieldName(name) == null) ? getSubDistrictByDefaultName(name) : getSubDistrictByFieldName(name);
    }

    public List<SubDistrict> getSubDistricts() {
        Realm realm = getRealm();
        return realm.where(SubDistrict.class).sort(SubDistrict.FIELD_ID, Sort.ASCENDING).findAll();
    }

    public List<SubDistrict> getSubDistrictsByDistrictId(String districtId) {
        Realm realm = getRealm();
        return realm.where(SubDistrict.class)
                .equalTo(SubDistrict.FIELD_DISTRICT_ID, districtId)
                .sort(SubDistrict.FIELD_ID, Sort.ASCENDING).findAll();
    }

    public void deleteSubDistricts() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(SubDistrict.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // endregion


    // region postcode
    public void storePostcode(final Postcode postcode) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(postcode);
            }
        });
    }

    public Postcode getPostcode(String postcodeId) {
        Realm realm = getRealm();
        return realm.where(Postcode.class).equalTo(Postcode.FIELD_ID, postcodeId).findFirst();
    }

    public Postcode getPostcodeByCode(String postcode) {
        if (postcode == null || postcode.trim().isEmpty()) {
            return null;
        }
        Realm realm = getRealm();
        return realm.where(Postcode.class).equalTo(Postcode.FIELD_POST_CODE, postcode).findFirst();
    }


    public List<Postcode> getPostcodes() {
        Realm realm = getRealm();
        return realm.where(Postcode.class).sort(Postcode.FIELD_ID, Sort.ASCENDING).findAll();
    }

    public List<Postcode> getPostcodeBySubDistrictId(String subDistrictId) {
        Realm realm = getRealm();
        return realm.where(Postcode.class)
                .equalTo(Postcode.FIELD_SUB_DISTRICT_ID, subDistrictId)
                .sort(Postcode.FIELD_ID, Sort.ASCENDING).findAll();
    }

    public void deletePostcodes() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Postcode.class).findAll().deleteAllFromRealm();
            }
        });
    }
    // endregion


    public void userLogout() {
        deleteAllCacheCartItem();
        deleteAllCompareProduct();
        deleteUserInformation();
        deleteAllOrder();
        deleteUserToken();
        deleteAllCachedEndpoint();
    }
}