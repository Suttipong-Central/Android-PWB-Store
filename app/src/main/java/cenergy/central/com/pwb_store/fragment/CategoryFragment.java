package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CategoryAdapter;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.CategoryDao;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class CategoryFragment extends Fragment {
    private static final String TAG = CategoryFragment.class.getSimpleName();

    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private ProgressDialog mProgressDialog;

    //Data Members
    private CategoryAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private CategoryDao mCategoryDao;

    public CategoryFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static CategoryFragment newInstance(CategoryDao categoryDao) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CATEGORY, categoryDao);
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        if (getArguments() != null) {
            mCategoryDao = getArguments().getParcelable(ARG_CATEGORY);
        }

        //mockData();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        mAdapter = new CategoryAdapter(getContext());
        mLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(mAdapter.getSpanSize());

        if (mCategoryDao == null) {
//            HttpManager.getInstance().getCategoryService().getCategories().enqueue(CALLBACK_CATEGORY);
            retrieveCategories(); // force retrieve category
        } else {
            try {
                mAdapter.setCategory(mCategoryDao);
            } catch (Exception e) {
                retrieveCategories();
            }
        }
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void retrieveCategories() {
        if (getContext() != null) {
            showProgressDialog();
            HttpManagerMagento.Companion.getInstance(getContext()).retrieveCategories(true, 2, 4, new ApiResponseCallback<Category>() {
                @Override
                public void success(@Nullable Category category) {
                    if (category != null) {
                        if (isAdded()) {
                            mAdapter.setCategory(category);
                        }
                        dismissProgressDialog();
                    }
                }

                @Override
                public void failure(@NotNull APIError error) {
                    dismissProgressDialog();
                    showAlertDialog(error.getErrorMessage(), false);
                }
            });
        }
    }

//    private void mockData(){
//        List<Category> mCategoryList = new ArrayList<>();
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_tv)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_app)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_cooling)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_small)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_kitchen)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_health)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_life)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_wearable)));
//        mCategoryList.add(new Category("http://www.mx7.com/i/1dd/5bZkIN.png", getResources().getString(R.string.home_accessories)));
//        mCategoryDao = new CategoryDao(mCategoryList);
//    }

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
        outState.putParcelable(ARG_CATEGORY, mCategoryDao);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        mCategoryDao = savedInstanceState.getParcelable(ARG_CATEGORY);
    }

    private void showAlertDialog(String message, final boolean shouldCloseActivity) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (shouldCloseActivity)
                                getActivity().finish();
                        }
                    });

            builder.show();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(getContext());
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (getActivity() != null && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
