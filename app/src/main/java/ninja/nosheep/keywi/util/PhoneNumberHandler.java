package ninja.nosheep.keywi.util;

/**
 * Handles information about phone numbers.
 *
 * @author David Söderberg
 * @since 2015-11-23
 */
public class PhoneNumberHandler {
    public static String getTrimmedNumber(String number){
        return number.trim().replace(" ", "").replace("-", "");
    }
}
