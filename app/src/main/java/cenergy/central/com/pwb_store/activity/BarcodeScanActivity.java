package cenergy.central.com.pwb_store.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;

public class BarcodeScanActivity extends AppCompatActivity {

    @BindView(R.id.image_button_back)
    ImageButton mImageButtonBack;

    @BindView(R.id.image_button_flashlight)
    ImageButton mFlashLightButton;

    @BindView(R.id.zxing_barcode_scanner)
    CompoundBarcodeView mCompoundBarcodeView;

    private CaptureManager captureManager;

    private static boolean cameraOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        ButterKnife.bind(this);

//        mCompoundBarcodeView.setStatusText(getString(R.string.barcode_status));
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setExposureEnabled(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setMeteringEnabled(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setScanInverted(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setBarcodeSceneModeEnabled(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setContinuousFocusEnabled(true);

        mImageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (!hasFlash()){
            mFlashLightButton.setVisibility(View.GONE);
        }

        captureManager = new CaptureManager(this, mCompoundBarcodeView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
        mFlashLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cameraOn){
                    mFlashLightButton.setImageResource(R.drawable.ic_flash_off);
                    mCompoundBarcodeView.getBarcodeView().setTorch(true);
                    cameraOn = true;
                }
                else {
                    mFlashLightButton.setImageResource(R.drawable.ic_flash_on);
                    mCompoundBarcodeView.getBarcodeView().setTorch(false);
                    cameraOn = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        captureManager.onPause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        captureManager.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public boolean hasFlash(){
        return Contextor.getInstance().getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
