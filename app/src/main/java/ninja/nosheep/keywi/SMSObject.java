package ninja.nosheep.keywi;

/**
 * Class that contains all information about SMS.
 *
 * @author David Söderberg
 * @since 2015-11-05
 */
public class SMSObject extends MessageObject {

    public SMSObject(long id, String address, String messageBody, String folder, String time, boolean isReaded) {
        super(id, address, messageBody, folder, time, isReaded);
    }
}