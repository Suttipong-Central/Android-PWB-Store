package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cenergy.central.com.pwb_store.CategoryUtils;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.CategoryAdapter;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.utils.Analytics;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.utils.Screen;

public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";
    private ProgressDialog mProgressDialog;

    private CategoryAdapter mAdapter;
    private Analytics analytics;

    public CategoryFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.trackScreen(Screen.CATEGORY_LV1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init() {
        if (getContext() != null) {
            analytics = new Analytics(getContext());
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        //Data Members
        mAdapter = new CategoryAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(mAdapter.getSpanSize());
        loadCategories();
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    public void updateView(List<Category> categories) {
        if (isAdded()) {
            mAdapter.setCategory(categories);
        }
    }

    private void loadCategories() {
        if (getContext() != null) {
            showProgressDialog();
            PreferenceManager pref = new PreferenceManager(getContext());
            String displaySpecialIds = pref.getSpecialCategoryIds();
            ArrayList<String> specialIds = new ArrayList<>();

            if (!displaySpecialIds.trim().equals("")) {
                String[] ids = displaySpecialIds.split(",");
                specialIds.addAll(Arrays.asList(ids));
            }

            HttpManagerMagento.Companion.getInstance(getContext()).retrieveCategory(
                    CategoryUtils.SUPER_PARENT_ID, false, specialIds,
                    new ApiResponseCallback<List<Category>>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final List<Category> categories) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (isAdded() && categories != null) {
                                        dismissProgressDialog();
                                        mAdapter.setCategory(categories);
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(@NotNull APIError error) {
                            if (getActivity() != null){
                                getActivity().runOnUiThread(() -> {
                                    Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                                    showAlertDialog(error.getErrorUserMessage() == null?
                                            getString(R.string.some_thing_wrong) : error.getErrorUserMessage());
                                    dismissProgressDialog();
                                });
                            }
                        }
                    });
        }
    }

    private void showAlertDialog(String message) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());
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
