/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;

/**
 *
 * @author tomscotch
 */
public class client extends SimpleApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        Serializer.registerClass(ServerMain.PingMessage.class);
        Serializer.registerClass(ServerMain.PongMessage.class);
        Client client = Network.connectToServer("localhost", 5110);
        client.start();
        client.addMessageListener(new ClientPingResponder(), ServerMain.PongMessage.class);
        client.send(new ServerMain.PingMessage());

        System.out.println("Client: Sending ping message..");
    }

    @Serializable
    public static class PingMessage extends AbstractMessage {
    }

    @Serializable
    public static class PongMessage extends AbstractMessage {
    }

    @Override
    public void simpleInitApp() {
        //
    }

    private static class ClientPingResponder implements MessageListener<Client> {

        @Override
        public void messageReceived(Client source, com.jme3.network.Message message) {
            if (message instanceof PongMessage) {
                System.out.println("Client: Received pong message!");
            }
        }
    }
}
