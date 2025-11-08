package org.umlproject.Commands;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import org.networking.NetworkManager;
import org.networking.NetworkPacket;
import org.networking.PacketType;

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
                
                InetSocketAddress socketAddress = new InetSocketAddress(split[0], portTry);
                NetworkManager.connect(socketAddress);
            }
            case "disconnect" ->
            {
                System.out.println("Disconnecting...");
                NetworkManager.shutdown();
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

                NetworkManager.startHost(portTry, true);
            }
            case "say" ->
            {
                try
                {
                    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    NetworkManager.getClientInstance().sendNetworkPacket(new NetworkPacket(0, PacketType.MESSAGE, message));
                }
                catch(IOException ex)
                {
                    System.out.println("Send message failure:  " + ex.getMessage());
                }
            }
        }
        
    }

    @Override
    public String description() {
        return "host, connect, ";
    }
    
}
