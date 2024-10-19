package me.pixel.perk;

import eu.byncing.net.api.INetListener;
import eu.byncing.net.api.NetClient;
import eu.byncing.net.api.channel.INetChannel;
import eu.byncing.net.api.protocol.packet.EmptyPacket;

public class networking {
    public static void connect() {
        NetClient client = new NetClient();
        client.addListener(new INetListener() {

            @Override
            public void handleConnected(INetChannel iNetChannel) {

            }

            @Override
            public void handleDisconnected(INetChannel iNetChannel) {

            }

            @Override
            public void handlePacket(INetChannel iNetChannel, EmptyPacket emptyPacket) {

            }
        });
        client.connect("localhost", 52798);
    }
}
