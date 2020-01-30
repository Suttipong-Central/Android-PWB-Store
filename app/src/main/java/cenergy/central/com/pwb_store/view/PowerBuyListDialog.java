package cenergy.central.com.pwb_store.view;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.ListDialogAbstractItemAdapter;

/**
 * Created by napabhat on 3/13/2017 AD.
 */

public class PowerBuyListDialog extends Dialog implements View.OnClickListener{
    private static final String TAG = PowerBuyListDialog.class.getSimpleName();
    //View Members
    private RecyclerView mRecyclerView;
    private PowerBuyTextView ok;
    private PowerBuyTextView cancel;

    //Data Members
    private View mView;
    private ListDialogAbstractItemAdapter mAdapter;

    public PowerBuyListDialog(@NonNull Context context, ListDialogAbstractItemAdapter adapter) {
        super(context);
        this.mAdapter = adapter;
        initInflate();
        initInstance();
    }

    public PowerBuyListDialog(@NonNull Context context, @StyleRes int theme, ListDialogAbstractItemAdapter adapter) {
        super(context, theme);
        this.mAdapter = adapter;
        initInflate();
        initInstance();
    }

    protected PowerBuyListDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener, ListDialogAbstractItemAdapter adapter) {
        super(context, cancelable, cancelListener);
        this.mAdapter = adapter;
        initInflate();
        initInstance();
    }

    private void initInflate() {
        //Inflate Layout
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.view_list_dialog, null);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    private void initInstance() {

        mRecyclerView = findViewById(R.id.recycler_view);
        ok = findViewById(R.id.txt_ok);
        cancel = findViewById(R.id.txt_cancel);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        setContentView(mView);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mAdapter.setListener(listener);
    }

    @Override
    public void onClick(View v) {
        if (v == ok){
            dismiss();
        }else if (v == cancel){
            dismiss();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Object object);
    }
}
