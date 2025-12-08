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
    
    //Ai generated list.
    private static final String[] RANDOM_USERNAME_STRINGS = {
        "Hamburger", "Corn Dog", "Pickles", "Cheeseburger", "Milkshake", "Big Mac", "Water", "Fry Sauce",
        "Hot Dog", "French Fries", "Onion Rings", "Buffalo Wings", "BBQ Ribs", "Pulled Pork Sandwich",
        "Mac and Cheese", "Meatwad", "Grilled Cheese", "BLT Sandwich", "Philly Cheesesteak", "Sloppy Joe",
        "Potato Chips", "Cornbread", "Pancakes", "Waffles", "Apple Pie", "Pumpkin Pie",
        "Chocolate Chip Cookies", "Brownies", "Donuts", "Bagels", "Cinnamon Rolls", "Chicken Nuggets",
        "Tater Tots", "Fried Chicken", "Meatloaf", "Mashed Potatoes", "Coleslaw", "Bacon",
        "Eggs and Bacon", "Breakfast Burrito", "Cheese Fries", "Cheesesteak Eggrolls", "Chicken Sandwich",
        "Grits", "Funnel Cake", "Cheese Curds", "Corn on the Cob", "Hot Ham and Cheese", "Root Beer Float",
        "Nachos", "Chili Dog", "Chicken Wings", "Mozzarella Sticks", "Caesar Salad", "Club Sandwich",
        "Veggie Burger", "Fish Sandwich", "Turkey Club", "BBQ Chicken Pizza", "Pepperoni Pizza", "Sub Sandwich",
        "Ice Cream Sundae", "Soft Pretzel", "Apple Turnover", "Strawberry Shortcake", "PoopManBob", "Fried Green Tomatoes",
        "Cheeseburger Sliders", "Corn Fritters", "Chili", "Cheese Quesadilla", "Chicken Quesadilla",
        "Pulled Pork Nachos", "Jalapeno Poppers", "Stuffed Peppers", "Baked Beans", "Sweet Potato Fries",
        "Chicken Tenders", "Carl", "Caesar Wrap", "Club Wrap", "Potato Salad", "Macaroni Salad", "Cobb Salad"
    };
    
    public static String username = null;
    
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
    public static UserIdentification generateUserInfo()
    {
        Random random = new Random();

        return new UserIdentification(
                username == null? RANDOM_USERNAME_STRINGS[random.nextInt(RANDOM_USERNAME_STRINGS.length)] : username,
                Main.isInTerminalMode());
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