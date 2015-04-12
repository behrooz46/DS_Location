package com.ds18842.meetmenow.locationtest.logic;

import android.content.Context;

import com.ds18842.meetmenow.locationtest.common.* ;
import com.ds18842.meetmenow.locationtest.network.infrastructure.* ;

import java.util.ArrayList;

public class LogicManager implements IMessageHandler, ILocationHandler {
    private static final double LOCATION_CHANGE_THERESHOLD = 10;

    private final Context context;
    private IMessageHandler sender;
    private Node me;

    public LogicManager(Context context) {
        this.context = context ;
        me = new Node(null, null);
    }

    public Node getSelfNode() {
        return me;
    }

    public ArrayList <Node> getNodes(){
        //TODO return list of all nodes
        return null ;
    }

    @Override
    public void receive(Packet msg) {
        //TODO pass that to UI
    }

    @Override
    public void send(Packet msg) {
        sender.send(msg);
    }

    @Override
    public void broadcast(Packet msg) {
        //TODO call routing.broadcast
        sender.broadcast(msg);
    }

    public void setSender(IMessageHandler sender) {
        this.sender = sender;
    }


    //Called by GeoLocation
    @Override
    public void updateLocation(GeoLocation location) {
        //TODO decide if it should tell UI
        if ( location.getDistance(me.getGeoLocation()) < LOCATION_CHANGE_THERESHOLD )
            return ;

        //Update my location
        me.setGeoLocation(location);
        //Boradcast this update
        Node src = me ;
        Node dst = me ;
        broadcast(new Packet(src, dst, Packet.BROADCAST, "")) ;
    }

    //Called by UI
    public void sendMessageTo(Node node, String content){
        Node src = me ;
        Node dst = node ;
        send(new Packet(src, dst, Packet.LOCATION, content)) ;
    }

    //Called by UI
    public void joinTheNetwork(String name) {
        me.setName(name);
    }
}
