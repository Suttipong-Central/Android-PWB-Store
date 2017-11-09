package cenergy.central.com.pwb_store.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import cenergy.central.com.pwb_store.model.AddCompare;
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

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
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
}