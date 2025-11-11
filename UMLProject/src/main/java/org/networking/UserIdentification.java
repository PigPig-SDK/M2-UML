package org.networking;

import java.util.Random;
import org.umlproject.Main;

public class UserIdentification {
    public final String userName;
    public final boolean isTerminalUser;
    private static final String[] anonNames = {"Jeff", "Diddy", "Bob"};
    public UserIdentification(String userName, boolean isTerminalUser)
    {
        this.isTerminalUser = isTerminalUser;
        this.userName = userName;
    }
    public static UserIdentification generateAnonymousUserInfo()
    {
        Random random = new Random();
        return new UserIdentification(anonNames[random.nextInt(anonNames.length)], Main.isInTerminalMode());
    }
    public static boolean isValid(UserIdentification uid)
    {
        return !(uid.userName == null || uid.userName.trim().isEmpty());
    }
}