package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.DrawerItem;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class DrawItemBus {

    private DrawerItem mDrawerItem;
    //private Category mCategory;

    public DrawItemBus(DrawerItem drawerItem){
        this.mDrawerItem = drawerItem;
    }

    public DrawerItem getDrawerItem() {
        return mDrawerItem;
    }

    public void setDrawerItem(DrawerItem drawerItem) {
        mDrawerItem = drawerItem;
    }

}
