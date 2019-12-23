package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.UserInformation;

/**
 * Created by napabhat on 11/12/2017 AD.
 */

public class LoginSuccessBus {
    private boolean isSuccess;
    private UserInformation userInformation;

    public LoginSuccessBus(boolean isSuccess, UserInformation userInformation) {
        this.isSuccess = isSuccess;
        this.userInformation = userInformation;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }
}
