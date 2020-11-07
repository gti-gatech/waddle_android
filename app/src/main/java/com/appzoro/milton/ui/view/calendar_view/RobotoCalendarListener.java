package com.appzoro.milton.ui.view.calendar_view;

import java.util.Date;

public interface RobotoCalendarListener {

    void onDayClick(Date date, Boolean isPreviousDate);

    void onRightButtonClick(Date date);

    void onLeftButtonClick(Date date);

    void onVisibleMonthYear(Date date);

}
