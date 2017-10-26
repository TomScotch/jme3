package mygame;

import com.jme3.network.*;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;

public class Main {

    @Serializable
    public static class PingMessage extends AbstractMessage {
    }

    @Serializable
    public static class PongMessage extends AbstractMessage {
    }

    private static class ServerPingResponder implements MessageListener<HostedConnection> {
        @Override
        public void messageReceived(HostedConnection source, com.jme3.network.Message message) {
            if (message instanceof PingMessage){
                System.out.println("Server: Received ping message!");
                source.send(new PongMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException{
        Serializer.registerClass(PingMessage.class);
        Serializer.registerClass(PongMessage.class);

        Server server = Network.createServer(5110);
        server.start();
        server.addMessageListener(new ServerPingResponder(), PingMessage.class);
                       
        Object obj = new Object();
        synchronized (obj){
            obj.wait();
        }
    }
}