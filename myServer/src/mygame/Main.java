package mygame;

import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;

public class Main {

    private static class ServerPingResponder implements MessageListener<HostedConnection> {

        @Override
        public void messageReceived(HostedConnection source, com.jme3.network.Message message) {
            if (message instanceof ServerCommunication.PingMessage) {
                System.out.println("Server: Received ping message!");
                source.send(new ServerCommunication.PongMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Serializer.registerClass(ServerCommunication.PingMessage.class);
        Serializer.registerClass(ServerCommunication.PongMessage.class);

        Server server = Network.createServer(5110);
        server.start();
        server.addMessageListener(new ServerPingResponder(), ServerCommunication.PingMessage.class);

        Object obj = new Object();
        synchronized (obj) {
            obj.wait();
        }
    }
}
