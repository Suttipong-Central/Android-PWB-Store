package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.SpecDao;

/**
 * Created by napabhat on 8/4/2017 AD.
 */

public class SpecDaoBus {
    private View mView;
    private SpecDao mSpecDao;

    public SpecDaoBus(View view, SpecDao specDao){
        this.mSpecDao = specDao;
        this.mView = view;
    }

    public View getView() {
        return mView;
    }

    public SpecDao getSpecDao() {
        return mSpecDao;
    }
}
