package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.CompareFragment;
import cenergy.central.com.pwb_store.fragment.SpecFragment;
import cenergy.central.com.pwb_store.realm.RealmController;
import cenergy.central.com.pwb_store.view.PowerBuyCompareView;
import io.realm.Realm;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class SpecActivity extends AppCompatActivity implements PowerBuyCompareView.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.button_compare)
    PowerBuyCompareView mBuyCompareView;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spec);

        initView();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, SpecFragment.newInstance())
                .commit();
    }

    private void initView() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get realm instance
        this.mRealm = RealmController.with(this).getRealm();
        long count = RealmController.with(this).getCount();

        mBuyCompareView.setListener(this);
        mBuyCompareView.updateCartCount((int) count);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onShoppingBagClick(View view) {
        Intent intent = new Intent(this, CompareActivity.class);
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                        .toBundle());
    }
}
