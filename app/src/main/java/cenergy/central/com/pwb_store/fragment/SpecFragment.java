package cenergy.central.com.pwb_store.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.SpecDetailAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.manager.bus.event.SpecDaoBus;
import cenergy.central.com.pwb_store.model.SpecDao;
import cenergy.central.com.pwb_store.model.SpecItem;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class SpecFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private SpecDao mSpecDao;
    private SpecDetailAdapter mAdapter;
    private GridLayoutManager mLayoutManager;


    public SpecFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static SpecFragment newInstance() {
        SpecFragment fragment = new SpecFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spec, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        List<SpecItem> specItems = new ArrayList<>();
        specItems.add(new SpecItem("Instant Film","Fujifilm Instant Color “Instax mini”"));
        specItems.add(new SpecItem("Picture size","62x46mm"));
        specItems.add(new SpecItem("Shutter","Shutter speed : 1/60 sec"));
        specItems.add(new SpecItem("Exposure Control","Manual Switching System (LED indecator in exposure meter)"));
        specItems.add(new SpecItem("Flash","Constant firing flash (automatic light adjustment)\n" +
                "Recycle time : 0.2 sec. to 6 sec. (when using new batteries), Effective flash\n" +
                "range : 0.6m - 2.7m"));
        specItems.add(new SpecItem("Display Screen","3.0 Inches."));

        mSpecDao = new SpecDao(specItems);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);
        mAdapter = new SpecDetailAdapter(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());
        mAdapter.setSpecDetail(mSpecDao);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDetach() {
//        EventBus.getDefault().unregister(this);
//        super.onDetach();
//    }

}
