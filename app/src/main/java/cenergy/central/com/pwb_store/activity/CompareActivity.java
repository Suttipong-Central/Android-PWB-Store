package cenergy.central.com.pwb_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.fragment.CompareFragment;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDetailBus;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Subscribe
    public void onEvent(CompareDetailBus compareDetailBus){
//        Intent intent = new Intent(this, ProductDetailActivity.class);
//        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, compareDetailBus.getProductCompareList().getProductId());
//        ActivityCompat.startActivity(this, intent,
//                ActivityOptionsCompat
//                        .makeScaleUpAnimation(compareDetailBus.getView(), 0, 0, compareDetailBus.getView().getWidth(), compareDetailBus.getView().getHeight())
//                        .toBundle());

        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_SKU, compareDetailBus.getCompareProduct().getSku());
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(compareDetailBus.getView(), 0, 0, compareDetailBus.getView().getWidth(), compareDetailBus.getView().getHeight())
                        .toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        initView();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container, CompareFragment.newInstance())
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }
}
