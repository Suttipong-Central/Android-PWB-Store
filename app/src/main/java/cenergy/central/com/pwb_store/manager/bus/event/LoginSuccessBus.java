package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by napabhat on 11/12/2017 AD.
 */

public class LoginSuccessBus {
    private boolean isSuccess;

    public LoginSuccessBus(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
