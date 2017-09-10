package cenergy.central.com.pwb_store.view;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by napabhat on 7/11/2017 AD.
 */

public class PowerBuyTextWatcherAdapter implements TextWatcher{


    private final PowerBuyEditText view;
    private final PowerBuyTextWatcherListener listener;

    public PowerBuyTextWatcherAdapter(PowerBuyEditText editText, PowerBuyTextWatcherListener listener) {
        this.view = editText;
        this.listener = listener;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        listener.onTextChanged(view, s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // pass
    }

    @Override
    public void afterTextChanged(Editable s) {
        // pass
    }

    public interface PowerBuyTextWatcherListener {

        void onTextChanged(PowerBuyEditText view, String text);

    }
}
