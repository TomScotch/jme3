package mygame;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

public class ServerCommunication {

    @Serializable
    public static class PingMessage extends AbstractMessage {
    }

    @Serializable
    public static class PongMessage extends AbstractMessage {
    }
}
