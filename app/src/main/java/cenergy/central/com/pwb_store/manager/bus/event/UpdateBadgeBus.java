package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by napabhat on 9/19/2017 AD.
 */

public class UpdateBadgeBus {
    private boolean isUpdate;

    public UpdateBadgeBus(boolean isUpdate){
        this.isUpdate = isUpdate;
    }

    public boolean isUpdate() {
        return isUpdate;
    }
}
