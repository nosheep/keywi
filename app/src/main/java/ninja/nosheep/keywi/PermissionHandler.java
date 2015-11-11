package ninja.nosheep.keywi;

/**
 * Text om metoden
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

    public static void setOkToReadContacts(boolean okToReadContacts) {
        PermissionHandler.okToReadContacts = okToReadContacts;
    }

    public static boolean isOkToReadSMS() {
        return okToReadSMS;
    }

    public static void setOkToReadSMS(boolean okToReadSMS) {
        PermissionHandler.okToReadSMS = okToReadSMS;
    }
}
