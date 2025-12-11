package org.umlproject.UI;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.networking.NetworkManager;
import org.networking.NetworkManagerListener;
import org.networking.UserIdentification;
import org.umlproject.RelationshipType;
import org.umlproject.UMLDocument;
import org.umlproject.UndoRedoManager;

/**
 * This class handles the 'connect/disconnect' GUI for the server.
 * 
 * THERE SHOULD BE NO NETWORK LOGIC WITHIN THIS CLASS! THIS CLASS IS ONLY FOR 'Generic Gui related network stuff'.
 */
public class GuiNetwork  implements NetworkManagerListener
{
    private static GuiNetwork instance = new GuiNetwork();
    
    
    public static void initialize()
    {
        NetworkManager.listeners.add(instance);
    }
    
    @Override
    public void onNetworkConnect() {
        //LOCK OPENFILE/NEWFILE
        MenuItem openFile =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Open…");
        MenuItem newFile =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"New");
        MenuItem disconnect =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Disconnect");
        MenuItem undo =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Undo");
        MenuItem redo =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Redo");
        MenuItem host =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Host");
        MenuItem connect =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Connect");
        
        connect.setDisable(true);
        host.setDisable(true);
        openFile.setDisable(true);
        newFile.setDisable(true);
        undo.setDisable(true);
        redo.setDisable(true);
        disconnect.setDisable(false);
        UMLDocument.usesMemento = false;

    }

    @Override
    public void onNetworkDisconnect() {
        MenuItem openFile =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Open…");
        MenuItem newFile =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"New");
        MenuItem disconnect =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Disconnect");
        MenuItem undo =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Undo");
        MenuItem redo =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Redo");
        MenuItem host =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Host");
        MenuItem connect =  GuiKeyBinds.findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),"Connect");
        
        connect.setDisable(false);
        host.setDisable(false);
        openFile.setDisable(false);
        newFile.setDisable(false);
        disconnect.setDisable(true);
        undo.setDisable(false);
        redo.setDisable(false);
        UMLDocument.usesMemento = true;
    }
    
    public static void promptHostScreen()
    {
        if(NetworkManager.isConnected())
            return;

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usenameField = new TextField(UserIdentification.generateUserInfo().userName);
        TextField portField = new TextField(Integer.toString(NetworkManager.DEFAULT_PORT));

        grid.add(new Label("Username"), 0, 0);
        grid.add(usenameField, 1, 0);
        grid.add(new Label("PORT"), 0, 1);
        grid.add(portField, 1, 1);

        Alert alert = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Host Server", null, null, grid);
        Alert failureAlert = FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Try again", "Invalid Port", null, null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) //User Acceptance
        {
            UserIdentification.username = usenameField.getText();
            try{
                int port = Integer.parseInt(portField.getText());
                if(port < 1) {
                    failureAlert.show();
                    return;
                }
                NetworkManager.startHost(port, true);
            }
            catch(NumberFormatException ex)
            {
                failureAlert.show();
            }
        }
        
    }
    
    public static void promptConnectScreen()
    {
        if(NetworkManager.isConnected())
            return;

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usenameField = new TextField(UserIdentification.generateUserInfo().userName);
        TextField portField = new TextField(Integer.toString(NetworkManager.DEFAULT_PORT));
        TextField ipField = new TextField("localhost");
        
        grid.add(new Label("Username"), 0, 0);
        grid.add(usenameField, 1, 0);
        grid.add(new Label("IP"), 0, 1);
        grid.add(ipField, 1, 1);
        grid.add(new Label("Port"), 0, 2);
        grid.add(portField, 1, 2);

        Alert alert = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Host Server", null, null, grid);
        Alert failureAlert = FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Try again", "Invalid Port", null, null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) //User Acceptance
        {
            UserIdentification.username = usenameField.getText();
            
            String ipAddress = ipField.getText();
            int portTry;
            try
            {
                portTry = Integer.parseInt(portField.getText());
                if(portTry < 1) {
                    failureAlert.show();
                    return;
                }
            }
            catch(NumberFormatException ex)
            {
                failureAlert.show();
                return;
            }
            InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, portTry);
            NetworkManager.connect(socketAddress);
        }
    }

    @Override
    public void onClientDisconnect(UUID clientId) {
        // Do nothing...
    }
}
