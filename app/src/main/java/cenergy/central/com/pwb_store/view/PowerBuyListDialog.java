package cenergy.central.com.pwb_store.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.ListDialogAbstractItemAdapter;

/**
 * Created by napabhat on 3/13/2017 AD.
 */

public class PowerBuyListDialog extends Dialog implements View.OnClickListener{
    private static final String TAG = PowerBuyListDialog.class.getSimpleName();
    //View Members
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_ok)
    PowerBuyTextView ok;

    @BindView(R.id.txt_cancel)
    PowerBuyTextView cancel;

    //Data Members
    private View mView;
    private ListDialogAbstractItemAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private OnItemClickListener mListener;

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
        ButterKnife.bind(this, mView);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        setContentView(mView);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;

        mAdapter.setListener(mListener);
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
