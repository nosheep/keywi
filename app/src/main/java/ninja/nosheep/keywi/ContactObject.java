package ninja.nosheep.keywi;

/**
 * Class that contains all information about contacts.
 *
 * @author David Söderberg
 * @since 2015-11-11
 */
public class ContactObject {
    private String phoneNumber;
    private String displayName;
    private int id;

    public ContactObject(String displayName, int id, String phoneNumber) {
        this.displayName = displayName;
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
