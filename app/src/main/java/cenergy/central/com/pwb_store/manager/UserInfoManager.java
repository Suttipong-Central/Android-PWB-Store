package cenergy.central.com.pwb_store.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.securepreferences.SecurePreferences;

import java.util.UUID;

import cenergy.central.com.pwb_store.model.response.TokenResponse;

/**
 * Created by napabhat on 3/29/2017 AD.
 */

public class UserInfoManager {
    public static final int USER_TYPE_GUEST = 1;
    public static final int USER_TYPE_LOGIN = 2;

    private static final String TAG = "UserInfoManager";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EXPIRE_DATE = "expire_date";
    private static final String PREF_SECURE_VALUE = "sec_val.xml";
    private static final String KEY_EMPLOYEE_ID = "employee_id";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_GUEST_ID = "guest_id";
    private static final String KEY_USER_ID = "user_id";

    private static final String KEY_T1C_USERNAME = "t1c_username";
    private static final String KEY_T1C_PASSWORD = "t1c_password";
    private static final String KEY_T1C_CUSTOMER_ID = "t1c_customer_id";
    private static final String KEY_T1C_NO = "t1c_no";

    private static UserInfoManager instance;
    private Context mContext;
    private String mUserToken;
    private String mEmployeeId;
    private String mUUID;
    private String mGuestId;
    private String mUserId;
    private int mUserType;
    //private TheOneCardSecure mTheOneCardSecure;

    private UserInfoManager() {
        mContext = Contextor.getInstance().getContext();
        mUserType = loadProfile();

        setUserType(USER_TYPE_GUEST);
        loadUUID();

        if (mUserType == USER_TYPE_GUEST) {
            loadGuestId();
        } else {
            loadUserInfo();
        }
    }

    public static UserInfoManager getInstance() {
        if (instance == null)
            instance = new UserInfoManager();
        return instance;
    }

    public boolean isGuestUser() {
        return mUserType == USER_TYPE_GUEST;
    }

    public String getUserToken() {
        return mUserToken;
    }

    public void setUserToken(String userToken) {
        mUserToken = userToken;
    }

    public String getEmployeeId() {
        return mEmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        mEmployeeId = employeeId;
    }

//    public void setEmployeeId(Employee employee) {
//        saveProfile(employee);
//        setEmployeeId(employee.getEmployeeId());
//    }

    public void setUserType(int userType) {
        this.mUserType = userType;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

//    public void setUserLogin(LoginResponse loginResponse) {
//        saveToken(loginResponse);
//        setUserToken(loginResponse.getToken());
//        setUserType(USER_TYPE_GUEST);
//    }

    public void setCreateToken(TokenResponse tokenResponse){
        saveToken(tokenResponse);
        setUserToken(tokenResponse.getTokenData());
    }

    public void setUserIdLogin(String userId) {
        saveUserId(userId);
        setUserId(userId);
    }

    private void saveToken(TokenResponse tokenResponse){
        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, tokenResponse.getTokenData());
        editor.putString(KEY_EXPIRE_DATE, tokenResponse.getTokenExpireDate());
        editor.apply();
    }

//    private void saveToken(LoginResponse loginResponse) {
//        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(KEY_TOKEN, loginResponse.getToken());
//        editor.apply();
//    }
//
//    private void saveProfile(Employee employee) {
//        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(KEY_EMPLOYEE_ID, employee.getEmployeeId());
//        editor.apply();
//    }

//    public void saveTheOneCardProfile(TheOneCardSecure theOneCardSecure) {
//        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(KEY_T1C_USERNAME, theOneCardSecure.getUsername());
//        editor.putString(KEY_T1C_PASSWORD, theOneCardSecure.getPassword());
//        editor.putString(KEY_T1C_CUSTOMER_ID, theOneCardSecure.getCustomerId());
//        editor.putString(KEY_T1C_NO, theOneCardSecure.getCardNo());
//        editor.apply();
//
//        loadTheOneCardSecure();
//    }

//    public void clearTheOneCardProfile() {
//        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.remove(KEY_T1C_USERNAME);
//        editor.remove(KEY_T1C_PASSWORD);
//        editor.remove(KEY_T1C_CUSTOMER_ID);
//        editor.remove(KEY_T1C_NO);
//        editor.apply();
//
//        mTheOneCardSecure = new TheOneCardSecure("", "", "", "");
//    }
//
//    private void loadTheOneCardSecure() {
//        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
//        String username = preferences.getString(KEY_T1C_USERNAME, "");
//        String password = preferences.getString(KEY_T1C_PASSWORD, "");
//        String customerId = preferences.getString(KEY_T1C_CUSTOMER_ID, "");
//        String cardNo = preferences.getString(KEY_T1C_NO, "");
//
//        mTheOneCardSecure = new TheOneCardSecure(username, password, customerId, cardNo);
//    }
//
//    public TheOneCardSecure getTheOneCardSecure() {
//        return mTheOneCardSecure;
//    }
//
//    public boolean isTheOneCardConnected() {
//        return mTheOneCardSecure != null && mTheOneCardSecure.isTheOneCardSecureAvaiable();
//    }

    private void loadUUID() {
        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
        mUUID = preferences.getString(KEY_UUID, "");

        if (TextUtils.isEmpty(mUUID)) {
            mUUID = UUID.randomUUID().toString();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_UUID, mUUID);
            editor.apply();
        }
    }

    public String getUUID() {
        return mUUID;
    }

    private void loadGuestId() {
        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
        mGuestId = preferences.getString(KEY_GUEST_ID, "");

        if (TextUtils.isEmpty(mGuestId)) {
            mGuestId = UUID.randomUUID().toString();
            Log.d(TAG, "loadGuestId: GuestID generated = " + mGuestId);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_GUEST_ID, mGuestId);
            editor.apply();
        }
    }

    public String getGuestId() {
        return mGuestId;
    }

    private void saveUserId(String userId) {
        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    private void loadUserInfo() {
        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
        mUserId = preferences.getString(KEY_USER_ID, "");

        if (TextUtils.isEmpty(mUserId)) {
            mUserId = getUserId();
            Log.d(TAG, "loadUserId: UserID generated = " + mUserId);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_USER_ID, mUserId);
            editor.apply();
        }
    }

    private int loadProfile() {
        SharedPreferences preferences = new SecurePreferences(mContext, "", PREF_SECURE_VALUE);
        mUserToken = preferences.getString(KEY_TOKEN, "");
        mUserId = preferences.getString(KEY_USER_ID, "");

        return !TextUtils.isEmpty(mUserToken) ? USER_TYPE_LOGIN : USER_TYPE_GUEST;
    }


}
