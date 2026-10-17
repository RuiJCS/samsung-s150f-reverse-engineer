package org.cybergarage.http;

import java.util.Calendar;
import java.util.TimeZone;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class Date {
    private static final String[] MONTH_STRING = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final String[] WEEK_STRING = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private Calendar cal;

    public Date(Calendar cal) {
        this.cal = cal;
    }

    public Calendar getCalendar() {
        return this.cal;
    }

    public int getHour() {
        return getCalendar().get(11);
    }

    public int getMinute() {
        return getCalendar().get(12);
    }

    public int getSecond() {
        return getCalendar().get(13);
    }

    public static final Date getLocalInstance() {
        return new Date(Calendar.getInstance());
    }

    public static final Date getInstance() {
        return new Date(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
    }

    public static final String toDateString(int value) {
        return value < 10 ? "0" + Integer.toString(value) : Integer.toString(value);
    }

    public static final String toMonthString(int value) {
        int value2 = value + 0;
        return (value2 < 0 || value2 >= 12) ? "" : MONTH_STRING[value2];
    }

    public static final String toWeekString(int value) {
        int value2 = value - 1;
        return (value2 < 0 || value2 >= 7) ? "" : WEEK_STRING[value2];
    }

    public static final String toTimeString(int value) {
        String str = "";
        if (value < 10) {
            str = String.valueOf("") + "0";
        }
        return String.valueOf(str) + Integer.toString(value);
    }

    public String getDateString() {
        Calendar cal = getCalendar();
        return String.valueOf(toWeekString(cal.get(7))) + ", " + toTimeString(cal.get(5)) + " " + toMonthString(cal.get(2)) + " " + Integer.toString(cal.get(1)) + " " + toTimeString(cal.get(11)) + SOAP.DELIM + toTimeString(cal.get(12)) + SOAP.DELIM + toTimeString(cal.get(13)) + " GMT";
    }

    public String getTimeString() {
        Calendar cal = getCalendar();
        return String.valueOf(toDateString(cal.get(11))) + (cal.get(13) % 2 == 0 ? SOAP.DELIM : " ") + toDateString(cal.get(12));
    }
}
