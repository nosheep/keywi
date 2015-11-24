package ninja.nosheep.keywi;

/**
 * Text om metoden
 *
 * @author David Söderberg
 * @since 2015-11-23
 */
public class NumberHandler {
    public static String getTrimmedNumber(String number){
        return number.trim().replace(" ", "").replace("-", "");
    }
}
