package ninja.nosheep.keywi;

import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;

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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(ms));
        Log.d(TagHandler.MAIN_TAG, "Day: " + calendar.get(Calendar.DAY_OF_MONTH));
        return DateFormat.getInstance().format(Long.parseLong(ms));
    }

    private static String getTimeInRelationToToday(Calendar calendar) {
        return null;
    }
}
