package com.ds18842.meetmenow.locationtest.logic;

import android.content.Context;

import com.ds18842.meetmenow.locationtest.MeetMeNow;
import com.ds18842.meetmenow.locationtest.common.* ;
import com.ds18842.meetmenow.locationtest.network.infrastructure.* ;
import com.ds18842.meetmenow.locationtest.routing.RoutingManager;

import java.util.ArrayList;

public class LogicManager implements IMessageHandler {

    private final Context context;
    private IMessageHandler sender;

    public LogicManager(Context context) {
        this.context = context ;
    }

    public Node getSelfNode() {
        //TODO return self node
        return null;
    }

    public ArrayList<Neighbour> getNeighbors() {
        //TODO return all neighbour
        return null;
    }

    public Neighbour getNeighbor(Node next) {
        //TODO return neighbour from node
        return null ;
    }

    @Override
    public void receive(Packet msg) {
        //TODO pass that to UI
    }

    @Override
    public void send(Packet msg) {
        //TODO call routing.send
        sender.send(msg);
    }

    @Override
    public void broadcast(Packet msg) {
        //TODO location change
        //TODO call routing.broadcast
        sender.broadcast(msg);
    }

    public void setSender(IMessageHandler sender) {
        this.sender = sender;
    }
}
