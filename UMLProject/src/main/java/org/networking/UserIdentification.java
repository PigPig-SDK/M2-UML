package org.networking;

import java.util.Random;
import org.umlproject.Main;


/**
 * The user connection is a class that stores key characteristics about a client.
 * Things I expect this class might store:
 *  - *User prefs that are important for network traffic / ownership
 * 
 * This class is only known by the server.
 * 
 * NOTE: THIS CLASS SHOULD BE IMMUTABLE! 
 *  --Might consider making this a record later... Who knows...
 */
public class UserIdentification {
    public final String userName;
    public final boolean isTerminalUser;
    private static final String[] anonNames = {"Jeff", "Diddy", "Bob"};
    
    public UserIdentification(String userName, boolean isTerminalUser)
    {
        this.isTerminalUser = isTerminalUser;
        this.userName = userName;
    }
    /**
     * Generates a random 'dummy' userid.
     * Should only be used for testing purposes and laziness.
     * 
     * @return A randomly created userID.
     */
    public static UserIdentification generateAnonymousUserInfo()
    {
        Random random = new Random();
        return new UserIdentification(anonNames[random.nextInt(anonNames.length)], Main.isInTerminalMode());
    }
    /**
     * Is the given UserIdentification valid?
     * 
     * @param uid the UserId to check validity for...
     * @return True if valid, False if invalid.
     */
    public static boolean isValid(UserIdentification uid)
    {
        return !(uid.userName == null || uid.userName.trim().isEmpty());
    }
}