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
import cenergy.central.com.pwb_store.utils.DialogUtils;

public class CategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private static final String TAG = "CategoryFragment";
    private ProgressDialog mProgressDialog;

    private CategoryAdapter mAdapter;
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
        init();

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init() {
        if (getArguments() != null) {
            mCategoryDao = getArguments().getParcelable(ARG_CATEGORY);
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        //Data Members
        mAdapter = new CategoryAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(mAdapter.getSpanSize());

        if (mCategoryDao == null) {
            retrieveCategories(); // force retrieve category
        } else {
            try {
                mAdapter.setCategory(mCategoryDao.getCategoryList());
            } catch (Exception e) {
                retrieveCategories();
            }
        }
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void retrieveCategories() {
//        if (getContext() != null) {
//            showProgressDialog();
//            HttpManagerMagento.Companion.getInstance(getContext()).retrieveCategories(true, 2, 4, new ApiResponseCallback<Category>() {
//                @Override
//                public void success(@Nullable Category category) {
//                    if (category != null) {
//                        if (isAdded()) {
//                            mAdapter.setCategory(category);
//                        }
//                        dismissProgressDialog();
//                    }
//                }
//
//                @Override
//                public void failure(@NotNull APIError error) {
//                    dismissProgressDialog();
//                    if(error.getErrorCode() == null){
//                        showAlertDialog(getContext().getString(R.string.not_connected_network));
//                    } else {
//                        switch (error.getErrorCode()){
//                            case "401": showAlertDialog(getContext().getString(R.string.user_not_found));
//                                break;
//                            case "408":
//                            case "404":
//                            case "500": showAlertDialog(getContext().getString(R.string.server_not_found));
//                                break;
//                            default: showAlertDialog(getContext().getString(R.string.some_thing_wrong));
//                                break;
//                        }
//                    }
//                }
//            });
//        }
    }

    public void foreRefresh() {
        // do anything
        loadCategories();
    }

    private void loadCategories() {
        HttpManagerMagento.Companion.getInstance(getContext()).retrieveCategory("2",
                new ApiResponseCallback<List<Category>>() {
            @Override
            public void success(@org.jetbrains.annotations.Nullable final List<Category> categories) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                mAdapter.setCategory(categories);
                            }
                        }
                    });
                }
            }

            @Override
            public void failure(@NotNull APIError error) {
                Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                dismissProgressDialog();
            }
        });
    }

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

    private void showAlertDialog(String message) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
