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
    private Node me;
    private LogicManager app;
    private IMessageHandler receiver;

    public NetworkManager(Context context, LogicManager app){
        this.context = context ;
        this.app = app ;
        this.me = app.getSelfNode();
    }

    public void setReceiver(IMessageHandler receiver) { this.receiver = receiver; }

    @Override
    public void receive(Packet msg) {
        if (msg.getType() == Packet.BROADCAST){
            this.broadcast(msg);
        }else{
            receiver.receive(msg);
        }
    }

    @Override
    public void send(Packet msg) {
        Node node = msg.getNext() ;
        Neighbour next = app.getNeighbor(node);
        IDevice device = next.getDevice() ;
        ISocket socket = next.getSocket();
        //TODO send msg over socket to device
    }

    @Override
    public void broadcast(Packet msg) {
        ArrayList<Neighbour> neighbors = app.getNeighbors() ;
        for (Neighbour neighbor : neighbors) {
            msg.setHop(me, neighbor.getNode());
            send(msg);
        }
    }
}
