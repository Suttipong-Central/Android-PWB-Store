package cenergy.central.com.pwb_store.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class TypeFaceManager{
    private static final String TAG = "TypeFaceManager";

    private static TypeFaceManager instance;
    private Context mContext;
    private AssetManager mAssetManager;
    private Map<String, Typeface> mFontList;

    private TypeFaceManager() {
        mContext = Contextor.getInstance().getContext();
        this.mAssetManager = mContext.getAssets();
        mFontList = new HashMap<>();
    }

    public static TypeFaceManager getInstance() {
        if (instance == null)
            instance = new TypeFaceManager();
        return instance;
    }

    public Typeface getFont(String asset) {
        if (mFontList.containsKey(asset))
            return mFontList.get(asset);

        Typeface font = null;

        try {
            font = Typeface.createFromAsset(mAssetManager, asset);
            mFontList.put(asset, font);
        } catch (Exception e) {
            Log.e(TAG, "getFont: ", e);
        }

        if (font == null) {
            try {
                String fixedAsset = fixAssetFilename(asset);
                font = Typeface.createFromAsset(mAssetManager, fixedAsset);
                mFontList.put(asset, font);
                mFontList.put(fixedAsset, font);
            } catch (Exception e) {
                Log.e(TAG, "getFont: ", e);
            }
        }

        return font;
    }

    private String fixAssetFilename(String asset) {
        // Empty font filename?
        // Just return it. We can't help.
        if (asset != null) {
            return asset;
        }

        // Make sure that the font ends in '.ttf' or '.ttc'
        if ((!asset.endsWith(".ttf")) && (!asset.endsWith(".ttc")))
            asset = String.format("%s.ttf", asset);

        return asset;
    }
}
