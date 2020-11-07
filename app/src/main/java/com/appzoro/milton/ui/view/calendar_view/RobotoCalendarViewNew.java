package com.appzoro.milton.ui.view.calendar_view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.appzoro.milton.R;
import com.appzoro.milton.utility.Utils;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class RobotoCalendarViewNew extends LinearLayout {

    private static final String DAY_OF_THE_WEEK_TEXT = "dayOfTheWeekText";
    private static final String DAY_OF_THE_WEEK_LAYOUT = "dayOfTheWeekLayout";
    private static final String DAY_OF_THE_MONTH_LAYOUT = "dayOfTheMonthLayout";
    private static final String DAY_OF_THE_MONTH_TEXT = "dayOfTheMonthText";
    private static final String DAY_OF_THE_MONTH_BACKGROUND = "dayOfTheMonthBackground";
    private static final String DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 = "dayOfTheMonthCircleImage1";
    private static final String DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 = "dayOfTheMonthCircleImage2";

    private TextView dateTitle;
    private ImageView leftButton;
    private ImageView rightButton;
    private View rootView;
    private ViewGroup robotoCalendarMonthLayout;
    private RobotoCalendarListener robotoCalendarListener;
    @NonNull
    private Calendar currentCalendar = Calendar.getInstance(Locale.US);
    @Nullable
    private Calendar lastSelectedDayCalendar;
    private final OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            // Extract day selected
            ViewGroup dayOfTheMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfTheMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_THE_MONTH_LAYOUT.length(), tagId.length());
            TextView dayOfTheMonthText = view.findViewWithTag(DAY_OF_THE_MONTH_TEXT + tagId);

            // Extract the day from the text
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfTheMonthText.getText().toString()));

            boolean isPreviousDate = new Utils().getWeekendAndPreviousDate(calendar.getTime());
            if (!isPreviousDate) {
                markDayAsSelectedDay(calendar.getTime());
                // Fire event
                if (robotoCalendarListener != null) {
                    robotoCalendarListener.onDayClick(calendar.getTime(), isPreviousDate);
                }
            }

        }
    };

    public RobotoCalendarViewNew(Context context) {
        super(context);
        init(null);
    }

    public RobotoCalendarViewNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RobotoCalendarViewNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RobotoCalendarViewNew(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private static int getDayIndexByDate(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        return currentDay + monthOffset;
    }

    private static int getMonthOffset(Calendar currentCalendar) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {

            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private static int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();

//        Log.e("weekIndex ", weekIndex + " firstDayWeekPosition " + firstDayWeekPosition +"");

//        return weekIndex;

        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {
            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }

    private static boolean areInTheSameDay(@NonNull Calendar calendarOne, @NonNull Calendar calendarTwo) {
        return calendarOne.get(Calendar.YEAR) == calendarTwo.get(Calendar.YEAR) && calendarOne.get(Calendar.DAY_OF_YEAR) == calendarTwo.get(Calendar.DAY_OF_YEAR);
    }

    private void init(@Nullable AttributeSet set) {

        if (isInEditMode()) {
            return;
        }

        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflate.inflate(R.layout.roboto_calendar_view_layout, this, true);
        findViewsById(rootView);
        setUpEventListeners();

        currentCalendar = Calendar.getInstance(Locale.US);
        setDate(currentCalendar.getTime());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }

    public void setDate(@NonNull Date date) {
        currentCalendar.setTime(date);
        updateView();
    }

    @NonNull
    public Date getDate() {
        return currentCalendar.getTime();
    }

    @Nullable
    public Date getSelectedDay() {
        return lastSelectedDayCalendar.getTime();
    }

    public void clearColorLastSelectedDay(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);

        // Store current values as last values
        lastSelectedDayCalendar = calendar;

        TextView dayOfTheMonth = getDayOfMonthText(calendar);
        dayOfTheMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));


    }

    public void markDayAsSelectedDay(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);

        // Clear previous current day mark
        clearSelectedDay();

        // Store current values as last values
        lastSelectedDayCalendar = calendar;

        // Mark current day as selected
        ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(calendar);
        dayOfTheMonthBackground.setBackgroundResource(R.drawable.circle);

        TextView dayOfTheMonth = getDayOfMonthText(calendar);
        dayOfTheMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));

        ImageView circleImage1 = getCircleImage1(calendar);
        ImageView circleImage2 = getCircleImage2(calendar);
        if (circleImage1.getVisibility() == VISIBLE) {
            DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        }

        if (circleImage2.getVisibility() == VISIBLE) {
            DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        }
    }

    public void clearSelectedDay() {
        if (lastSelectedDayCalendar != null) {
            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(lastSelectedDayCalendar);

            // If it's today, keep the current day style
            Calendar nowCalendar = Calendar.getInstance(Locale.US);
            if (nowCalendar.get(Calendar.YEAR) == lastSelectedDayCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.DAY_OF_YEAR) == lastSelectedDayCalendar.get(Calendar.DAY_OF_YEAR)) {
                dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
            } else {
                dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
            }

            TextView dayOfTheMonth = getDayOfMonthText(lastSelectedDayCalendar);

            if (new Utils().getWeekendAndPreviousDate(lastSelectedDayCalendar.getTime())) {
                dayOfTheMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.color_calendar_weekend));
            } else {
                dayOfTheMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.roboto_calendar_normal_day_font));
            }
            ImageView circleImage1 = getCircleImage1(lastSelectedDayCalendar);
            ImageView circleImage2 = getCircleImage2(lastSelectedDayCalendar);
            if (circleImage1.getVisibility() == VISIBLE) {
                DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_1));
            }

            if (circleImage2.getVisibility() == VISIBLE) {
                DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_2));
            }
        }
    }

    public void updateDateColor(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);
        TextView tvDate = getDayOfMonthText(calendar);
        tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
    }

    public void clearDateColor(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);
        TextView tvDate = getDayOfMonthText(calendar);
        tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
    }

    public void markCircleImage1(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);
        ImageView circleImage1 = getCircleImage1(calendar);
        circleImage1.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_1));
        }
    }

    public void markCurrentDate() {
        Calendar calendar = Calendar.getInstance(Locale.US);
        boolean isWeekendDate = new Utils().getWeekendAndPreviousDate(calendar.getTime());
        if (!isWeekendDate) {
            markDayAsSelectedDay(calendar.getTime());
        }else {
            lastSelectedDayCalendar = calendar;
            clearColorLastSelectedDay(calendar.getTime());
        }
    }

    public void markCircleImage2(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);
        ImageView circleImage2 = getCircleImage2(calendar);
        circleImage2.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_2));
        }
    }


    public void setRobotoCalendarListener(RobotoCalendarListener robotoCalendarListener) {
        this.robotoCalendarListener = robotoCalendarListener;
    }

    private void findViewsById(View view) {

        robotoCalendarMonthLayout = view.findViewById(R.id.robotoCalendarDateTitleContainer);
        leftButton = view.findViewById(R.id.leftButton);
        rightButton = view.findViewById(R.id.rightButton);
        dateTitle = view.findViewById(R.id.monthText);

        leftButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorIcons));

        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < 42; i++) {

            int weekIndex = (i % 7) + 1;

//            Log.e("weekIndex ", " " + weekIndex + " ");

            ViewGroup dayOfTheWeekLayout = view.findViewWithTag(DAY_OF_THE_WEEK_LAYOUT + weekIndex);

            // Create day of the month
            View dayOfTheMonthLayout = inflate.inflate(R.layout.roboto_calendar_day_of_the_month_layout, null);
            View dayOfTheMonthText = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_TEXT);
            View dayOfTheMonthBackground = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND);
            View dayOfTheMonthCircleImage1 = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1);
            View dayOfTheMonthCircleImage2 = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2);

            // Set tags to identify them
            int viewIndex = i + 1;
            dayOfTheMonthLayout.setTag(DAY_OF_THE_MONTH_LAYOUT + viewIndex);
            dayOfTheMonthText.setTag(DAY_OF_THE_MONTH_TEXT + viewIndex);
            dayOfTheMonthBackground.setTag(DAY_OF_THE_MONTH_BACKGROUND + viewIndex);
            dayOfTheMonthCircleImage1.setTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 + viewIndex);
            dayOfTheMonthCircleImage2.setTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 + viewIndex);

            dayOfTheWeekLayout.addView(dayOfTheMonthLayout);
        }
    }

    private void setUpEventListeners() {

        leftButton.setOnClickListener(view -> {
            if (robotoCalendarListener != null) {

                Calendar calendar = Calendar.getInstance(Locale.US);
                if (currentCalendar.getTimeInMillis() > calendar.getTimeInMillis()) {
                    //Log.e("currentCalendar ", "true");
                    // Decrease month
                    currentCalendar.add(Calendar.MONTH, -1);
                    lastSelectedDayCalendar = null;
                    updateView();

                    Log.e("calendar ", calendar.getTime().toString());
                    Log.e("currentCalendar ", currentCalendar.getTime().toString());

                    boolean isCurrentMonth = new Utils().getCompareYearMonth(calendar.getTime(), currentCalendar.getTime());
                    if (isCurrentMonth) {
                        leftButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_calendar_weekend));
                    } else {
                        leftButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    }
                    Log.e("isCurrentMonth ", " " + isCurrentMonth + " ");
                    robotoCalendarListener.onLeftButtonClick(currentCalendar.getTime());

                } else {
//                    Log.e("currentCalendar ", "false");
                    leftButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorIcons));
                }

            }
        });

        rightButton.setOnClickListener(view -> {
            if (robotoCalendarListener != null) {
                // Increase month
                currentCalendar.add(Calendar.MONTH, 1);
                lastSelectedDayCalendar = null;
                updateView();
                robotoCalendarListener.onRightButtonClick(currentCalendar.getTime());

                Calendar calendar = Calendar.getInstance(Locale.US);

                if (new Utils().getCompareYearMonth(calendar.getTime(), currentCalendar.getTime())) {
                    leftButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_calendar_weekend));
                } else {
                    leftButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }

            }
        });
    }

    public void changeButtonColor() {
        leftButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorIcons));
    }

    private void setUpMonthLayout() {
        String dateText = new DateFormatSymbols(Locale.US).getMonths()[currentCalendar.get(Calendar.MONTH)];
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());
        Calendar calendar = Calendar.getInstance(Locale.US);
        if (currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            dateTitle.setText(dateText);
        } else {
            dateTitle.setText(String.format("%s %s", dateText, currentCalendar.get(Calendar.YEAR)));
        }
    }

    private void setUpWeekDaysLayout() {
        TextView dayOfWeek;
        String dayOfTheWeekString;
        String[] weekDaysArray = new DateFormatSymbols(Locale.US).getWeekdays();

        int length = weekDaysArray.length;
        for (int i = 1; i < length; i++) {
            dayOfTheWeekString = weekDaysArray[i];


            dayOfWeek = rootView.findViewWithTag(DAY_OF_THE_WEEK_TEXT + getWeekIndex(i, currentCalendar));
//            dayOfWeek = rootView.findViewWithTag(DAY_OF_THE_WEEK_TEXT + getWeekIndex(i, currentCalendar));

//            Log.e("dayOfWeek ", "" +dayOfWeek + " ");

            dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase();
            dayOfWeek.setText(dayOfTheWeekString);
        }
    }

    private void setUpDaysOfMonthLayout() {

        TextView dayOfTheMonthText;
        View circleImage1;
        View circleImage2;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthBackground;

        for (int i = 1; i < 43; i++) {
//        for (int i = 1; i < 43; i++) {

            dayOfTheMonthContainer = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            dayOfTheMonthBackground = rootView.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND + i);
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            circleImage1 = rootView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 + i);
            circleImage2 = rootView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 + i);

//            dayOfTheMonthText.setVisibility(View.INVISIBLE);
            circleImage1.setVisibility(View.GONE);
            circleImage2.setVisibility(View.GONE);

            // Apply styles
            dayOfTheMonthText.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthText.setTypeface(null, Typeface.NORMAL);
            dayOfTheMonthText.setTextColor(ContextCompat.getColor(getContext(), R.color.roboto_calendar_normal_day_font));
            dayOfTheMonthContainer.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthContainer.setOnClickListener(null);
            dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void setUpDaysInCalendar() {

        Calendar auxCalendar = Calendar.getInstance(Locale.US);
//        Calendar auxCalendar = Calendar.getInstance(Locale.US);
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);
        TextView dayOfTheMonthText;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthLayout;

        // Calculate dayOfTheMonthIndex
        int dayOfTheMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);
        int dayOfIndex = getWeekIndex(firstDayOfMonth, auxCalendar);

        int lastDay = getLastMonthDate(auxCalendar.getTime());
        if (dayOfTheMonthIndex > 0) {
            for (int i = 1; i < dayOfTheMonthIndex; i++) {
                TextView dayOfText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
                int date = lastDay - (dayOfTheMonthIndex - i) + 1;
                dayOfText.setText(String.valueOf(date));
                dayOfText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_calendar_weekend));
            }
        }

//        int lastPosition = 0;

        for (int i = 1; i <= auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++, dayOfTheMonthIndex++) {
            dayOfTheMonthContainer = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + dayOfTheMonthIndex);
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + dayOfTheMonthIndex);

            if (dayOfTheMonthText == null) {
                break;
            }

            dayOfTheMonthContainer.setOnClickListener(onDayOfMonthClickListener);
            dayOfTheMonthText.setVisibility(View.VISIBLE);
            dayOfTheMonthText.setText(String.valueOf(i));

            String date = auxCalendar.get(Calendar.YEAR) + "-" + (auxCalendar.get(Calendar.MONTH) + 1) + "-" + i;
            if (new Utils().getWeekend(date)) {
                dayOfTheMonthText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_calendar_weekend));
            } else {
                dayOfTheMonthText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }

//            Log.e("date>>>>>>> ", date + " ");

        }

        int startPosition = auxCalendar.getActualMaximum(Calendar.DATE) + dayOfIndex;

        Log.e("lastPosition ", " " + auxCalendar.getActualMaximum(Calendar.DATE) + " ");
        Log.e("dayOfTheMonthIndex ", " " + dayOfIndex + " ");
        Log.e("startPosition ", " " + startPosition + " ");

        int lastPosition = 35;
        if (startPosition > 36) {
            lastPosition = 44;
        }

        Log.e("lastPosition ", " " + lastPosition + " ");

        int nextMonthDate = 0;
        for (int i = startPosition; i <= lastPosition; i++) {

            Log.e("position ", " " + i + " ");
            nextMonthDate++;
            TextView dayOfText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
//            int date = lastDate + 1;
            if (dayOfText != null) {
//                dayOfText.setVisibility(View.VISIBLE);
                dayOfText.setText(String.valueOf(nextMonthDate));
                dayOfText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_calendar_weekend));
            }
        }

        for (int i = 36; i < 43; i++) {
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            dayOfTheMonthLayout = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);

           if(lastPosition<37){
               dayOfTheMonthLayout.setVisibility(GONE);
           }else {
               if (dayOfTheMonthText.getVisibility() == INVISIBLE) {
                   dayOfTheMonthLayout.setVisibility(GONE);
               } else {
                   dayOfTheMonthLayout.setVisibility(VISIBLE);
               }
           }
        }

    }

    private int getLastMonthDate(Date date) {

        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, -1);
        Log.e("auxCalendar ", "" + calendar.getTime() + " ");

        int res = calendar.getActualMaximum(Calendar.DATE);
        Log.e("res last date", "" + res + " ");
        return res;
    }

    private void markDayAsCurrentDay() {
        // If it's the current month, mark current day
        Calendar nowCalendar = Calendar.getInstance(Locale.US);
        if (nowCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            Calendar currentCalendar = Calendar.getInstance(Locale.US);
            currentCalendar.setTime(nowCalendar.getTime());

            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(currentCalendar);
            dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
        }
    }

    private void updateView() {
        setUpMonthLayout();
        setUpWeekDaysLayout();
        setUpDaysOfMonthLayout();
        setUpDaysInCalendar();
        markDayAsCurrentDay();
    }

    private ViewGroup getDayOfMonthBackground(Calendar currentCalendar) {
        return (ViewGroup) getView(DAY_OF_THE_MONTH_BACKGROUND, currentCalendar);
    }

    private TextView getDayOfMonthText(Calendar currentCalendar) {
        return (TextView) getView(DAY_OF_THE_MONTH_TEXT, currentCalendar);
    }

    private ImageView getCircleImage1(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1, currentCalendar);
    }

    private ImageView getCircleImage2(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2, currentCalendar);
    }

    private View getView(String key, Calendar currentCalendar) {
        int index = getDayIndexByDate(currentCalendar);

        //fun getWeekName(date: Date): String {
        //            val sdf = SimpleDateFormat("EEEE", Locale.US)
        //            return sdf.format(date)
        //        }

        return rootView.findViewWithTag(key + index);
    }


}
