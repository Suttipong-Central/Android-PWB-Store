package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;

/**
 * Created by napabhat on 9/12/2016 AD.
 */

public class CategoryBus {
    private View view;
    private Category category;

    public CategoryBus(Category category, View view) {
        this.category = category;
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public Category getCategory() {
        return category;
    }
}
