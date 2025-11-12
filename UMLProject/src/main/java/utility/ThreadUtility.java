package utility;

import javafx.application.Platform;

/**
 * This is used for generic functions that a multi-threadded code might need (Specifically with javafx)
 */
public class ThreadUtility {
    public static void runOnMainThread(Runnable action) {
        
        try {
            if (Platform.isFxApplicationThread()) {
                action.run();
            }
            else if (Platform.isImplicitExit()) 
            {
                Platform.runLater(action);
            } 
            else 
            {
                action.run();
            }
        } 
        catch (IllegalStateException e) 
        {
            action.run();
        }
    }
}
