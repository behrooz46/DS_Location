package com.ds18842.meetmenow.locationtest.network;


import android.content.Context;

import com.ds18842.meetmenow.locationtest.common.IMessageHandler;
import com.ds18842.meetmenow.locationtest.common.Packet;
import com.ds18842.meetmenow.locationtest.common.Node;
import com.ds18842.meetmenow.locationtest.logic.LogicManager;
import com.ds18842.meetmenow.locationtest.network.infrastructure.IDevice;
import com.ds18842.meetmenow.locationtest.network.infrastructure.ISocket;
import com.ds18842.meetmenow.locationtest.network.infrastructure.Neighbour;

import java.util.ArrayList;

public class NetworkManager implements IMessageHandler {
    private final Context context;
    private PeerManager peerManager;
    private LogicManager app;
    private Node me;
    private IMessageHandler receiver;

    public NetworkManager(Context context, LogicManager app, PeerManager peerManager){
        this.context = context ;
        this.peerManager = peerManager ;
        this.app = app ;
        this.me = app.getSelfNode();
    }

    public void setReceiver(IMessageHandler receiver) { this.receiver = receiver; }

    @Override
    public void receive(Packet msg) {
        if (msg.getType() == Packet.BROADCAST){
            //TODO check if it's a new broadcast to re-do
            //TODO broadcast if #id of packet is new
            //TODO update logic about this broadcast
            this.broadcast(msg);
        }else{
            receiver.receive(msg);
        }
    }

    @Override
    public void send(Packet msg) {
        //TODO reduce TTL by 1

        Node node = msg.getNext() ;
        Neighbour next = peerManager.getNeighbor(node);
        IDevice device = next.getDevice() ;
        ISocket socket = next.getSocket();
        //TODO send msg over socket to device
    }

    @Override
    public void broadcast(Packet msg) {
        ArrayList<Neighbour> neighbors = peerManager.getNeighbors() ;
        for (Neighbour neighbor : neighbors) {
            if (msg.getPrev().getName().equals(neighbor.getNode().getName())){
                //Don't broadcast it back to the node you get the message from.
                continue;
            }
            msg.setHop(me, neighbor.getNode());
            send(msg);
        }
    }
}
