package ninja.nosheep.keywi.util;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ninja.nosheep.keywi.R;

/**
 * Handles all information about time and date.
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class TimeHandler {
    private static Calendar nowCalendar;
    private static int todayDate;
    private Context context;

    public TimeHandler(Context context) {
        this.context = context;
    }

    public String getTimeFromLong(long ms) {

        createNowCalendar();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);

        return getTimeInRelationToToday(calendar);
    }

    private static Calendar getCalendarFromTime(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        return calendar;
    }

    private synchronized static void createNowCalendar() {
        if (nowCalendar == null) {
            Log.d(TagHandler.MAIN_TAG, "TimeHandler: Calendar is deprecated||null, updating!");
            nowCalendar = new GregorianCalendar();
            Date trialTime = new Date();
            nowCalendar.setTime(trialTime);

            String tempMonth = "";
            if (nowCalendar.get(Calendar.MONTH) < 10) {
                tempMonth += "0";
            }
            String tempDay = "";
            if (nowCalendar.get(Calendar.DAY_OF_MONTH) < 10) {
                tempDay += "0";
            }
            String todayDateString = nowCalendar.get(Calendar.YEAR) + "" +
                    tempMonth + (nowCalendar.get(Calendar.MONTH) + 1) + "" +
                    tempDay + nowCalendar.get(Calendar.DAY_OF_MONTH);
            todayDate = Integer.parseInt(todayDateString);
        }
    }

    private static boolean isInLastYear(Calendar calendar) {
        if (nowCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) return true;
        else if (nowCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) - 1)
            if (nowCalendar.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH))
                if (nowCalendar.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH))
                    return true;

        return false;
    }

    private static boolean isInLastWeek(Calendar calendar) {
        return nowCalendar.getTimeInMillis() - calendar.getTimeInMillis() < 7 * 24 * 60 * 60 * 1000;
    }

    private static boolean isToday(int year, int month, int day) {
        String tempMonth = "";
        String tempDay = "";
        if (month < 10) {
            tempMonth += "0";
        }
        if (day < 10) {
            tempDay += "0";
        }
        String date = year + "" + tempMonth + month + "" + tempDay + day;
        int dateInt = Integer.parseInt(date);
        return dateInt == todayDate;
    }

    private static boolean isYesterday(Calendar calendar) {
        Calendar yesterDay = new GregorianCalendar();
        yesterDay.setTimeInMillis(nowCalendar.getTimeInMillis() - 24 * 60 * 60 * 1000);
        return calendar.get(Calendar.YEAR) == yesterDay.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == yesterDay.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == yesterDay.get(Calendar.DAY_OF_MONTH);
    }

    private String getTimeInRelationToToday(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH) + 1,
                day = calendar.get(Calendar.DAY_OF_MONTH);
        if (isToday(year, month, day)) {
            String tempHour = "";
            if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
                tempHour += "0";
            }
            String tempMinute = "";
            if (calendar.get(Calendar.MINUTE) < 10) {
                tempMinute += "0";
            }
            return tempHour + calendar.get(Calendar.HOUR_OF_DAY) + ":" + tempMinute + calendar.get(Calendar.MINUTE);
        }
        else if (isYesterday(calendar)) {
            return context.getString(R.string.yesterday);
        }
        else if (isInLastWeek(calendar)) {
            return getNameOfDay(calendar.get(Calendar.DAY_OF_WEEK));
        }
        else if (isInLastYear(calendar)) {
            return calendar.get(Calendar.DAY_OF_MONTH) + " " + getNameOfMonth(calendar.get(Calendar.MONTH) + 1) + ".";
        }
        else {
            String tempMonth = "";
            String tempDay = "";
            if (calendar.get(Calendar.MONTH) < 10) {
                tempMonth += "0";
            }
            if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                tempDay += "0";
            }
            return calendar.get(Calendar.YEAR) + "-" +
                    tempMonth + calendar.get(Calendar.MONTH) + "-" +
                    tempDay + calendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    private String getNameOfDay(int day) {
        switch (day) {
            case 1:
                return context.getString(R.string.sunday);
            case 2:
                return context.getString(R.string.monday);
            case 3:
                return context.getString(R.string.tuesday);
            case 4:
                return context.getString(R.string.wednesday);
            case 5:
                return context.getString(R.string.thursday);
            case 6:
                return context.getString(R.string.friday);
            case 7:
                return context.getString(R.string.saturday);
            default:
                throw new IndexOutOfBoundsException("Can't get name of day " + day);
        }
    }
    private String getNameOfMonth(int month) {
        switch (month) {
            case 1:
                return context.getString(R.string.january_short);
            case 2:
                return context.getString(R.string.february_short);
            case 3:
                return context.getString(R.string.march_short);
            case 4:
                return context.getString(R.string.april_short);
            case 5:
                return context.getString(R.string.may_short);
            case 6:
                return context.getString(R.string.june_short);
            case 7:
                return context.getString(R.string.july_short);
            case 8:
                return context.getString(R.string.august_short);
            case 9:
                return context.getString(R.string.september_short);
            case 10:
                return context.getString(R.string.october_short);
            case 11:
                return context.getString(R.string.november_short);
            case 12:
                return context.getString(R.string.december_short);
            default:
                throw new IndexOutOfBoundsException("Can't get name of month " + month);
        }
    }

}
