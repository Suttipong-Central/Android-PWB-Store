package cenergy.central.com.pwb_store.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.ShippingCalendarAdapter;
import cenergy.central.com.pwb_store.adapter.base.GridCellAdapter;
import cenergy.central.com.pwb_store.model.ShippingItem;
import cenergy.central.com.pwb_store.model.WeekSets;
import cenergy.central.com.pwb_store.model.response.ShippingSlot;
import cenergy.central.com.pwb_store.utils.CommonMethod;

/**
 * Created by napabhat on 8/18/2017 AD.
 */

public class CalendarViewCustom extends LinearLayout {

    private static final String TAG = CalendarViewCustom.class.getSimpleName();

    @BindView(R.id.previous_month)
    ImageView previousButton;

    @BindView(R.id.next_month)
    ImageView nextButton;

    @BindView(R.id.display_current_date)
    PowerBuyTextView currentDate;

    @BindView(R.id.calendar_grid)
    GridView calendarGridView;

    @BindView(R.id.sun)
    PowerBuyTextView sunday;

    @BindView(R.id.mon)
    PowerBuyTextView monday;

    @BindView(R.id.tue)
    PowerBuyTextView tuesday;

    @BindView(R.id.wed)
    PowerBuyTextView wednesday;

    @BindView(R.id.thu)
    PowerBuyTextView thursday;

    @BindView(R.id.fri)
    PowerBuyTextView friday;

    @BindView(R.id.sat)
    PowerBuyTextView saturday;

    @BindView(R.id.text10am)
    PowerBuyTextView text10am;

    @BindView(R.id.text12pm)
    PowerBuyTextView text12pm;

    @BindView(R.id.text14pm)
    PowerBuyTextView text14pm;

    @BindView(R.id.text16pm)
    PowerBuyTextView text16pm;

    @BindView(R.id.text18pm)
    PowerBuyTextView text18pm;

    @BindView(R.id.text20pm)
    PowerBuyTextView text20pm;

    private static final int MAX_CALENDAR_COLUMN = 42;
    private Context context;
//    private int month, year;
//    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
//    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//    private GridAdapter mAdapter;
//    private String mStartMonth;
//    private String mEndMonth;
//    private GregorianCalendar month;
//    private CalendarGridViewAdapter mCalendarGridViewAdapter;
//    private boolean flagMaxMin = false;
//    public static String currentDateSelected;
//    private Calendar mCalendar;
//    private DateFormat mDateFormat;
//    private GregorianCalendar mPMonth;
//    private int mMonthLength;
//    private GregorianCalendar mPMonthMaxSet;
//    private ArrayList<CalendarDecoratorDao> mEventList = new ArrayList<>();

    private Calendar _calendar;
    private GridCellAdapter adapter;
    //private HDLCalendarAdapter mHDLCalendarAdapter;
    private ShippingCalendarAdapter mShippingCalendarAdapter;
    @SuppressLint("NewApi")
    private int month, year;
    @SuppressWarnings("unused")
    @SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
    private final android.text.format.DateFormat dateFormatter = new android.text.format.DateFormat();
    private static final String dateTemplate = "MMMM yyyy";

    public String[] weekDays;
    public String[] nextPreWeekday;
    public String dateFormate;
    public String firstDayOfWeek;
    public String lastDayOfWeek;

    public int weekDaysCount = 0;
    public ArrayList<WeekSets> weekDatas;
    //private List<TimeSlotItem> mTimeSlotItems;
    private OnItemClickListener mListener;
    private ShippingSlot firstDay;
    private ShippingSlot lastDay;

    public CalendarViewCustom(Context context) {
        super(context);
        initInflate();
        initInstance();
    }

    public CalendarViewCustom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstance();
    }

    public CalendarViewCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CalendarViewCustom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstance();
    }

    private void initInflate() {
        //Inflate Layout
        inflate(getContext(), R.layout.calendar_layout, this);
//        this.mRealm = RealmController.getInstance().getRealm();
//        _calendar = Calendar.getInstance(Locale.getDefault());
//        month = _calendar.get(Calendar.MONTH) + 1;
//        year = _calendar.get(Calendar.YEAR);
//        Log.d(TAG, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
//                + year);

        nextPreWeekday = getWeekDay();
        firstDayOfWeek = CommonMethod.convertWeekDays(nextPreWeekday[0]);
        lastDayOfWeek = CommonMethod.convertWeekDays(nextPreWeekday[6]);
//        try
//        {
//            new LoadViewsInToWeekView().execute("");
//        } catch (Exception e)
//        {
//            Log.getStackTraceString(e);
//        }

//        Calendar calendar = Calendar.getInstance();
//        mStartMonth = "1, " + String.valueOf(calendar.get(Calendar.YEAR));
//        mEndMonth = "12, " + String.valueOf(calendar.get(Calendar.YEAR));
//
//        Singleton.getInstance().setMonth((GregorianCalendar) GregorianCalendar.getInstance());
//        Singleton.getInstance().setCurrentDate(
//                CalendarUtils.getCalendarDBFormat().format(Calendar.getInstance().getTime()));
//        Singleton.getInstance().setTodayDate(
//                CalendarUtils.getCalendarDBFormat().format(Calendar.getInstance().getTime()));
//        Singleton.getInstance().setStartMonth(mStartMonth);
//        Singleton.getInstance().setEndMonth(mEndMonth);
    //    getData();
    }

    private void initInstance() {
        ButterKnife.bind(this);

        //setUpCalendarAdapter();
        //setDate();

        // Initialised
//        adapter = new GridCellAdapter(getContext(),
//                R.id.calendar_date_id, month, year);
//        adapter.notifyDataSetChanged();
//        calendarGridView.setAdapter(adapter);

        currentDate.setText(firstDayOfWeek + "-" + lastDayOfWeek + " "
                + CommonMethod.convertWeekDaysMouth(nextPreWeekday[6]));

        sunday.setText("SUN\n" + CommonMethod.convertWeekDays(nextPreWeekday[0]));
        monday.setText("MON\n" + CommonMethod.convertWeekDays(nextPreWeekday[1]));
        tuesday.setText("TUE\n" + CommonMethod.convertWeekDays(nextPreWeekday[2]));
        wednesday.setText("WED\n" + CommonMethod.convertWeekDays(nextPreWeekday[3]));
        thursday.setText("THU\n" + CommonMethod.convertWeekDays(nextPreWeekday[4]));
        friday.setText("FRI\n" + CommonMethod.convertWeekDays(nextPreWeekday[5]));
        saturday.setText("SAT\n" + CommonMethod.convertWeekDays(nextPreWeekday[6]));

        // time
        text10am.setText(getContext().getResources().getString(R.string.time_10am));
        text12pm.setText(getContext().getResources().getString(R.string.time_12pm));
        text14pm.setText(getContext().getResources().getString(R.string.time_14pm));
        text16pm.setText(getContext().getResources().getString(R.string.time_16pm));
        text18pm.setText(getContext().getResources().getString(R.string.time_18pm));
        text20pm.setText(getContext().getResources().getString(R.string.time_20pm));

//        setPreviousButtonClickEvent();
//        setNextButtonClickEvent();
        setGridCellClickEvents();
    }

//    private void setPreviousButtonClickEvent(){
//        previousButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weekDays = getWeekDayPrev();
//                showDate(weekDays);
//            }
//        });
//    }
//    private void setNextButtonClickEvent(){
//        nextButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weekDays = getWeekDayNext();
//                showDate(weekDays);
//            }
//        });
//    }
    private void setGridCellClickEvents(){
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "Clicked " + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    public String[] getMonthDay() {

        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int delta = now.get(GregorianCalendar.DAY_OF_MONTH);
        String[] days = new String[delta];
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < delta; i++) {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }

    public String[] getWeekDay() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] days = new String[7];
        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 1;
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    @SuppressLint("SimpleDateFormat")
    public String[] getWeekDayNext() {

        weekDaysCount++;
        Calendar now1 = Calendar.getInstance();
        Calendar now = (Calendar) now1.clone();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] days = new String[7];
        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 1;
        now.add(Calendar.WEEK_OF_YEAR, weekDaysCount);
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;

    }

    @SuppressLint("SimpleDateFormat")
    public String[] getWeekDayPrev() {

        weekDaysCount--;
        Calendar now1 = Calendar.getInstance();
        Calendar now = (Calendar) now1.clone();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] days = new String[7];
        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 1;
        now.add(Calendar.WEEK_OF_YEAR, weekDaysCount);
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;

    }

    public void showDate(String[] weekDays){
        nextPreWeekday = weekDays;
        firstDayOfWeek = CommonMethod.convertWeekDays(nextPreWeekday[0]);
        lastDayOfWeek = CommonMethod.convertWeekDays(nextPreWeekday[6]);

        currentDate.setText(firstDayOfWeek + "-" + lastDayOfWeek + " "
                + CommonMethod.convertWeekDaysMouth(nextPreWeekday[6]));

        sunday.setText("SUN\n" + CommonMethod.convertWeekDays(nextPreWeekday[0]));
        monday.setText("MON\n" + CommonMethod.convertWeekDays(nextPreWeekday[1]));
        tuesday.setText("TUE\n" + CommonMethod.convertWeekDays(nextPreWeekday[2]));
        wednesday.setText("WED\n" + CommonMethod.convertWeekDays(nextPreWeekday[3]));
        thursday.setText("THU\n" + CommonMethod.convertWeekDays(nextPreWeekday[4]));
        friday.setText("FRI\n" + CommonMethod.convertWeekDays(nextPreWeekday[5]));
        saturday.setText("SAT\n" + CommonMethod.convertWeekDays(nextPreWeekday[6]));
        checkWeekDays();
    }

    public void setTimeSlotItem(List<ShippingItem> shippingItems) {
        if(mShippingCalendarAdapter == null){
            mShippingCalendarAdapter = new ShippingCalendarAdapter(getContext(), shippingItems);
        } else {
            mShippingCalendarAdapter.setShippingItems(shippingItems);
        }
        calendarGridView.setAdapter(mShippingCalendarAdapter);
    }

    private void checkWeekDays() {
        Boolean equalFirstDay = false;
        Boolean equalLastDay = false;
        for (String day : nextPreWeekday) {
            if (firstDay != null && day.equals(firstDay.getShippingDate() == null ? "" : firstDay.getShippingDate())) {
                equalFirstDay = true;
            }
            if (lastDay != null && day.equals(lastDay.getShippingDate() == null ? "" : lastDay.getShippingDate())) {
                equalLastDay = true;
            }
        }
        if(equalFirstDay){
            previousButton.setVisibility(GONE);
        } else {
            previousButton.setVisibility(VISIBLE);
        }
        if(equalLastDay){
            nextButton.setVisibility(GONE);
        } else {
            nextButton.setVisibility(VISIBLE);
        }
    }
//    private void setUpCalendarAdapter(){
//        List<Date> dayValueInCells = new ArrayList<Date>();
//        //mQuery = new DatabaseQuery(context);
////        List<EventObjects> mEvents = getAllFutureEvents();
////        List<EventObjects> mEvents = new ArrayList<>();
////        mEvents.add(new EventObjects("test", convertStringToDate("27082017")));
////        mEvents.add(new EventObjects("test", convertStringToDate("28082017")));
//        Calendar mCal = (Calendar)cal.clone();
//        mCal.set(Calendar.DAY_OF_MONTH, 1);
//        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
//        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
//        while(dayValueInCells.size() < MAX_CALENDAR_COLUMN){
//            dayValueInCells.add(mCal.getTime());
//            mCal.add(Calendar.DAY_OF_MONTH, 1);
//        }
//        Log.d(TAG, "Number of date " + dayValueInCells.size());
//        String sDate = formatter.format(cal.getTime());
//        currentDate.setText(sDate);
//        mAdapter = new GridAdapter(context, dayValueInCells, cal);
//        calendarGridView.setAdapter(mAdapter);
//    }

//    public List<EventObjects> getAllFutureEvents(){
//        Date dateToday = new Date();
//        List<EventObjects> events = new ArrayList<>();
////        String query = "select * from reminder";
////        Cursor cursor = this.getDbConnection().rawQuery(query, null);
////        if(cursor.moveToFirst()){
////            do{
////                int id = cursor.getInt(0);
////                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
////                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
//        //convert start date to date object
//
//        Date reminderDate = convertStringToDate(dateToday.toString());
//        if(reminderDate.after(dateToday) || reminderDate.equals(dateToday)){
//            events.add(new EventObjects(id, "ttt", reminderDate));
//        }
////            }while (cursor.moveToNext());
////        }
////        cursor.close();
//        return events;
//    }

//    private Date convertStringToDate(String dateInString){
//        DateFormat format = new SimpleDateFormat("d-MM-yyyy", Locale.ENGLISH);
//        Date date = null;
//        try {
//            date = format.parse(dateInString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }
//
//    private void setDate(){
//        month = Singleton.getInstance().getMonth();
//
//        mCalendarGridViewAdapter = new CalendarGridViewAdapter(getContext(), mEventList, month);
//
//        mCalendar = month;
//        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
//        mCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
//        mDateFormat = CalendarUtils.getCalendarDBFormat();
//
//
////        TextView title = (TextView) rootView.findViewById(R.id.title);
//        currentDate.setText(android.text.format.DateFormat.format(CalendarUtils.getCalendarMonthTitleFormat(), month));
//        calendarGridView.setAdapter(mCalendarGridViewAdapter);
//
//        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//
//                String selectedDate = mEventList.get(position).getDate();
//                ((CalendarGridViewAdapter) parent.getAdapter()).setSelected(v, selectedDate);
//                fetchEvents(selectedDate);
//
//            }
//        });
//    }
//
//    /**
//     * check if date has event or not,
//     * then
//     *
//     * @param date
//     */
//    public void fetchEvents(String date) {
//        boolean flag = false;
//        int pos = 0;
//        for (int i = 0; i < Singleton.getInstance().getEventManager().size(); i++) {
//            if (Singleton.getInstance().getEventManager().get(i).getDate().equalsIgnoreCase(date)) {
//                flag = true;
//                pos = i;
//            }
//        }
//        ArrayList<EventData> eventDataArrayList = new ArrayList();
//        if (flag) {
//            if (Singleton.getInstance().getEventManager().get(pos).getEventData() != null) {
//                eventDataArrayList = Singleton.getInstance().getEventManager().get(pos).getEventData();
//            }
//        }
//
////        if (mCustomCalendar != null)
////            mCustomCalendar.setDateSelectionData(eventDataArrayList);
//    }
//
//    /**
//     * refresh current month
//     */
//    public void refreshCalendar() {
//
//        refreshDays();
//        currentDate.setText(android.text.format.DateFormat.format(CalendarUtils.getCalendarMonthTitleFormat(), month));
//    }
//
//    /**
//     * refresh current month days
//     */
//    public void refreshDays() {
//
//        //clear List
//        mEventList.clear();
//        //create clone
//        mPMonth = (GregorianCalendar) mCalendar.clone();
//
//        CalendarGridViewAdapter.firstDay = mCalendar.get(GregorianCalendar.DAY_OF_WEEK);
//
//        int mMaxWeekNumber = mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
//
//        mMonthLength = mMaxWeekNumber * 7;
//        int mMaxP = getmMaxP();
//        int mCalMaxP = mMaxP - (CalendarGridViewAdapter.firstDay - 1);
//
//        mPMonthMaxSet = (GregorianCalendar) mPMonth.clone();
//
//        mPMonthMaxSet.set(GregorianCalendar.DAY_OF_MONTH, mCalMaxP + 1);
//
//        setData(getCalendarData());
//
//    }
//
//    /**
//     * @return
//     */
//    private CalendarResponse getCalendarData() {
//        CalendarResponse calendarResponse = new CalendarResponse();
//        calendarResponse.setStartmonth(Singleton.getInstance().getStartMonth());
//        calendarResponse.setEndmonth(Singleton.getInstance().getEndMonth());
//        calendarResponse.setMonthdata(Singleton.getInstance().getEventManager());
//        return calendarResponse;
//    }
//
//    /**
//     * @param calendarResponse
//     */
//    private void setData(CalendarResponse calendarResponse) {
//
////        mLlDayList.setVisibility(View.VISIBLE);
////        mRlHeader.setVisibility(View.VISIBLE);
////        mGridview.setVisibility(View.VISIBLE);
//
//        if (calendarResponse.getMonthdata() != null) {
//
//            ArrayList<Event> monthDataList = calendarResponse.getMonthdata();
//            int m = 0;
//
//            for (int n = 0; n < mMonthLength; n++) {
//                String mItemValue = mDateFormat.format(mPMonthMaxSet.getTime());
//                mPMonthMaxSet.add(GregorianCalendar.DATE, 1);
//
//                if (m < monthDataList.size()) {
//                    if (mItemValue.equalsIgnoreCase(monthDataList.get(m).getDate())) {
//                        CalendarDecoratorDao eventDao = new CalendarDecoratorDao(
//                                monthDataList.get(m).getDate(),
//                                Integer.parseInt(monthDataList.get(m).getCount()));
//                        mEventList.add(eventDao);
//                        m++;
//                    } else {
//                        CalendarDecoratorDao eventDao = new CalendarDecoratorDao(mItemValue, 0);
//                        mEventList.add(eventDao);
//                    }
//                } else {
//                    CalendarDecoratorDao eventDao = new CalendarDecoratorDao(mItemValue, 0);
//                    mEventList.add(eventDao);
//                }
//            }
//
//            mCalendarGridViewAdapter.notifyDataSetChanged();
//
//            if (!flagMaxMin) {
//                flagMaxMin = true;
//            }
//        }
//    }
//
//    private int getmMaxP() {
//        int maxP;
//        if (mCalendar.get(GregorianCalendar.MONTH) == mCalendar
//                .getActualMinimum(GregorianCalendar.MONTH)) {
//            mPMonth.set((mCalendar.get(GregorianCalendar.YEAR) - 1),
//                    mCalendar.getActualMaximum(GregorianCalendar.MONTH), 1);
//        } else {
//            mPMonth.set(GregorianCalendar.MONTH,
//                    mCalendar.get(GregorianCalendar.MONTH) - 1);
//        }
//        maxP = mPMonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
//
//        return maxP;
//    }

    public void setListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @OnClick(R.id.previous_month)
    public void onImageViewPreviousClick(ImageView imageView) {
        weekDays = getWeekDayPrev();
        showDate(weekDays);
        if (mListener != null) {
            mListener.onPreviousClick(weekDays);
        }
    }

    @OnClick(R.id.next_month)
    public void onImageViewNextClick(ImageView imageView) {
        weekDays = getWeekDayNext();
        showDate(weekDays);
        if (mListener != null) {
            mListener.onNextClick(weekDays);
        }
    }

    public void setFirstDayAndLastDay(@NotNull ShippingSlot firstDay, @NotNull ShippingSlot lastDay) {
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        checkWeekDays();
    }

    public interface OnItemClickListener {
        void onPreviousClick(String[] day);

        void onNextClick(String[] day);
    }
}
