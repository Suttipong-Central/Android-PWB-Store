package cenergy.central.com.pwb_store.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;

public class BarcodeScanActivity extends AppCompatActivity {

    ImageButton mImageButtonBack;
    ImageButton mFlashLightButton;
    CompoundBarcodeView mCompoundBarcodeView;
    private CaptureManager captureManager;
    private static boolean cameraOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        mCompoundBarcodeView = findViewById(R.id.zxing_barcode_scanner);
        mImageButtonBack = findViewById(R.id.image_button_back);
        mFlashLightButton = findViewById(R.id.image_button_flashlight);

        //mCompoundBarcodeView.setStatusText(getString(R.string.barcode_status));
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setExposureEnabled(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setMeteringEnabled(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setScanInverted(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setBarcodeSceneModeEnabled(true);
        mCompoundBarcodeView.getBarcodeView().getCameraSettings().setContinuousFocusEnabled(true);

        mImageButtonBack.setOnClickListener(v -> onBackPressed());

        if (!hasFlash()){
            mFlashLightButton.setVisibility(View.GONE);
        }

        captureManager = new CaptureManager(this, mCompoundBarcodeView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
        mFlashLightButton.setOnClickListener(v -> {
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public boolean hasFlash(){
        return Contextor.getInstance().getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
