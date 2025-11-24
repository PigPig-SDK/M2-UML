package org.umlproject.Commands;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import org.networking.ClientHandler;
import org.networking.NetworkManager;
import org.networking.NetworkPacket;
import org.networking.PacketType;
import org.networking.PayloadRequestDocument;

public class CommandNetwork extends BaseCommand {

    @Override
    public String actionName() {
        return "net";
    }

    @Override
    public void act(String[] args) {
        if(args.length <= 0)
        {
            System.out.println(description());
            return;
        }
        
        String subcommand = args[0];
        
        switch(subcommand)
        {
            case "refresh" ->
            {
                if(!NetworkManager.isConnected())
                {
                    System.out.println("Not connected to a server. Cannot refresh");    
                    return;
                }
                if(NetworkManager.isHosting())
                {
                    System.out.println("Cannot refresh document. You are hosting a server.");    
                    return;
                }
                System.out.println("Asking server for a document refresh...");
                NetworkManager.getClientInstance().sendNetworkPacket(PayloadRequestDocument.generatePacket());
            }
            case "connect" ->
            {
                if(args.length != 2)
                {
                    System.out.println("Improper command, EX: net connect IP:PORT");
                    return;
                }
                String ipAddress = args[1];
                String[] split = ipAddress.split(":");
                if(split.length != 2)
                {
                    System.out.println("Please provide an IP in the format IP:PORT  ... EX: 192.168.0.1:1000");
                    return;
                }
                int portTry = -1;
                try
                {
                    portTry = Integer.parseInt(split[1]);
                }
                catch(NumberFormatException ex)
                {
                    System.out.println("Port was not valid.");
                    return;
                }
                if(portTry < 1)
                {
                    System.out.println("Port was not valid.");
                    return;
                }
                
                InetSocketAddress socketAddress = new InetSocketAddress(split[0], portTry);
                NetworkManager.connect(socketAddress);
            }
            case "disconnect" ->
            {
                System.out.println("Disconnecting...");
                NetworkManager.shutdown();
            }
            case "list" ->
            {
                if(NetworkManager.getServerInstance() == null)
                {
                    System.out.println("You must be hosting a server in order to list information...");
                    return;
                }
                for(ClientHandler clienthandler : NetworkManager.getServerInstance().getClients())
                {
                    System.out.println(clienthandler.getUserID().userName + " : "
                            + ((double)clienthandler.getHeartbeatDelta() / 1000000000.0) + " : "
                            + clienthandler.getUserIP() + " : Is terminal user? " +
                            clienthandler.getUserID().isTerminalUser);
                }
            }
            case "host" ->
            {
                if(args.length != 2)
                {
                    System.out.println("Improper command. EX: net host <port>");
                    return;
                }
                int portTry = -1;
                try
                {
                    portTry = Integer.parseInt(args[1]);
                }
                catch(NumberFormatException ex)
                {
                    System.out.println("Port was not valid.");
                    return;
                }
                if(portTry < 1)
                {
                    System.out.println("Port was not valid.");
                    return;
                }
                NetworkManager.startHost(portTry, true);
            }
            case "say" ->
            {
                String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                if(NetworkManager.getClientInstance() == null)
                    return;

                NetworkManager.getClientInstance().sendNetworkPacket(new NetworkPacket(NetworkManager.getTick(), PacketType.MESSAGE, message));
            }
        }
        
    }

    @Override
    public String description() {
        return "host, connect, say, list";
    }
    
}
