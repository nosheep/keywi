package ninja.nosheep.keywi;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Handles all information about time and date.
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class TimeHandler {
    public TimeHandler() {

    }

    public static String getTimeFromString(String ms) {
        Calendar nowCalendar = new GregorianCalendar();
        Date trialTime = new Date();
        nowCalendar.setTime(trialTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(ms));
        calendar.compareTo(nowCalendar);

        /**
         *  TODO: Configure "today", "yesterday", etc instead of date.
         */
        return DateFormat.getInstance().format(Long.parseLong(ms));
    }

    private static String getTimeInRelationToToday(Calendar calendar) {
        return null;
    }
}
