package ninja.nosheep.keywi.util;

/**
 * Handles all permission
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class PermissionHandler {

    private static boolean okToReadContacts = false;
    private static boolean okToReadSMS = false;

    public static boolean isOkToReadContacts() {
        return okToReadContacts;
    }

    public static void setOkToReadContacts() {
        PermissionHandler.okToReadContacts = true;
    }

    public static boolean isOkToReadSMS() {
        return okToReadSMS;
    }

    public static void setOkToReadSMS() {
        PermissionHandler.okToReadSMS = true;
    }

}
