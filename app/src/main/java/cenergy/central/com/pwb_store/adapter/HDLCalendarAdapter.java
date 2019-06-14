package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.TimeSlotItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class HDLCalendarAdapter extends BaseAdapter {
    private Context mContext;
    private List<TimeSlotItem> mTimeSlotItems;

    public HDLCalendarAdapter(Context context, List<TimeSlotItem> timeSlotItems){
        this.mContext = context;
        this.mTimeSlotItems = timeSlotItems;
    }
    @Override
    public int getCount() {
        return mTimeSlotItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TimeSlotItem timeSlotItem = mTimeSlotItems.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.list_item_calendar_text, null);

            final PowerBuyTextView nameTextView = convertView.findViewById(R.id.txt_calendar);

            final ViewHolder viewHolder = new ViewHolder(nameTextView);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        if (timeSlotItem.getSlotLabel().equalsIgnoreCase("FULL")){
            viewHolder.nameTextView.setText(timeSlotItem.getSlotLabel());
        }else {
            //viewHolder.nameTextView.setText(timeSlotItem.getSlotLabel());
            viewHolder.nameTextView.setText("FREE");
            viewHolder.nameTextView.setTextColor(convertView.getResources().getColor(R.color.inStockColor));
        }


        return convertView;
    }

    private class ViewHolder {
        private final PowerBuyTextView nameTextView;

        public ViewHolder(PowerBuyTextView nameTextView) {
            this.nameTextView = nameTextView;
        }
    }
}
