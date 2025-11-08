package org.networking;

import java.util.Random;

public class UserIdentification {
    public final String userName;
    private static final String[] anonNames = {"Jeff", "Diddy", "Bob"};
    public UserIdentification(String userName)
    {
        this.userName = userName;
    }
    public static UserIdentification generateAnonymousUserInfo()
    {
        Random random = new Random();
        return new UserIdentification(anonNames[random.nextInt(anonNames.length)]);
    }
    public static boolean isValid(UserIdentification uid)
    {
        return !(uid.userName == null || uid.userName.trim().isEmpty());
    }
}