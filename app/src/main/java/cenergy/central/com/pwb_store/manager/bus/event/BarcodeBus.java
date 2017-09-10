package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by napabhat on 7/20/2017 AD.
 */

public class BarcodeBus {
    private boolean isBarcode;

    public BarcodeBus(boolean isBarcode){
        this.isBarcode = isBarcode;
    }

    public boolean isBarcode() {
        return isBarcode;
    }

    public void setBarcode(boolean barcode) {
        isBarcode = barcode;
    }
}
