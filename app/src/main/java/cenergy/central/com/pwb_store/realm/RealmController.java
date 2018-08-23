package cenergy.central.com.pwb_store.realm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.Date;
import java.util.List;

import cenergy.central.com.pwb_store.model.AddCompare;
import cenergy.central.com.pwb_store.model.CachedEndpoint;
import cenergy.central.com.pwb_store.model.CartItem;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CompareProduct;
import cenergy.central.com.pwb_store.model.Product;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by napabhat on 9/13/2017 AD.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public RealmController () {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController with(Context context) {
        if (instance == null) {
            instance = new RealmController();
        }
        return instance;
    }

    public static RealmController getInstance() {

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
        return realm.where(AddCompare.class).findAllSorted("productSku", Sort.DESCENDING);
    }

    public RealmResults<AddCompare> deletedCompare(final String productSku){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<AddCompare> result = realm.where(AddCompare.class).equalTo("productSku",productSku).findAllSorted("productSku", Sort.DESCENDING);
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
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                CompareProduct compareProduct =  CompareProduct.asCompareProduct(product);
                realm.copyToRealmOrUpdate(compareProduct);
            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccessfully();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                if (listener != null) {
                    listener.onFailure(error);
                }
            }
        });
    }

    public List<CompareProduct> deleteCompareProduct(final String sku) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<CompareProduct> realmCompareProducts = realm.where(CompareProduct.class).equalTo(CompareProduct.FIELD_SKU, sku).findAll();
                realmCompareProducts.deleteAllFromRealm();
            }
        });

        return getCompareProducts();
    }

    public List<CompareProduct> getCompareProducts() {
        Realm realm = getRealm();
        RealmResults<CompareProduct> realmCompareProducts =  realm.where(CompareProduct.class).sort(CompareProduct.FIELD_SKU, Sort.DESCENDING).findAll();
        return realmCompareProducts == null ? null : realm.copyFromRealm(realmCompareProducts);
    }

    public CompareProduct getCompareProduct(String sku) {
        return realm.where(CompareProduct.class).equalTo(CompareProduct.FIELD_SKU, sku).findFirst();
    }
    // endregion

    // region cart item
    public void saveCartItem(final CartItem cartItem, final DatabaseListener listener) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(cartItem);
            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                if (listener != null) {
                    Log.d("Database", "stored cart item");
                    listener.onSuccessfully();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                if (listener != null) {
                    listener.onFailure(error);
                }
            }
        });
    }

    public List<CartItem> deleteCartItem(final String itemId) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<CartItem> realmCompareProducts = realm.where(CartItem.class).equalTo(
                        CartItem.FIELD_ID, itemId).findAll();
                realmCompareProducts.deleteAllFromRealm();
            }
        });

        return getCartItems();
    }

    public List<CartItem> getCartItems() {
        Realm realm = getRealm();
        RealmResults<CartItem> realmCartItems = realm.where(CartItem.class).sort(CompareProduct.FIELD_SKU, Sort.DESCENDING).findAll();
        return realmCartItems == null ? null : realm.copyFromRealm(realmCartItems);
    }


    public CartItem getCartItem(String itemId) {
        return realm.where(CartItem.class).equalTo(CartItem.FIELD_ID, itemId).findFirst();
    }
    // endregion

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

    public boolean hasFreshlyCachedEndpoint(String endpoint) {
        return hasFreshlyCachedEndpoint(endpoint, 1);
    }

    private boolean hasFreshlyCachedEndpoint(String endpoint, int hours) {
        Realm realm = getRealm();
        CachedEndpoint cachedEndpoint = realm.where(CachedEndpoint.class).equalTo("endpoint", endpoint).findFirst();
        return cachedEndpoint != null && cachedEndpoint.getLastUpdated().after(new Date(System.currentTimeMillis() - (hours * 60 * 60 * 1000)));
    }
    // end region
}