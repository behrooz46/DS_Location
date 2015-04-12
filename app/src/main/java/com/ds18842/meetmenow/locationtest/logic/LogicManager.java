package com.ds18842.meetmenow.locationtest.logic;

import android.content.Context;

import com.ds18842.meetmenow.locationtest.common.* ;
import com.ds18842.meetmenow.locationtest.network.infrastructure.* ;

import java.util.ArrayList;

public class LogicManager implements IMessageHandler, ILocationHandler {
    private static final double LOCATION_CHANGE_THERESHOLD = 10;

    private final Context context;
    private IMessageHandler sender;

    public LogicManager(Context context) {
        this.context = context ;
    }

    public Node getSelfNode() {
        //TODO return self node
        return null;
    }

    //USED by UI
    public ArrayList <String> getNodeNames(){
        //TODO return list of all nodes' names
        return null ;
    }

    public ArrayList <Node> getNodes(){
        //TODO return list of all nodes
        return null ;
    }

    public Node getNode(String name) {
        //TODO return node with name
        return null;
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
        Node me = getSelfNode() ;
        if ( me.getGeoLocation().getDistance(location) < LOCATION_CHANGE_THERESHOLD )
            return ;

        me.setGeoLocation(location);
        Node src = getSelfNode() ;
        Node dst = getSelfNode() ;
        broadcast(new Packet(src, dst, Packet.BROADCAST, "")) ;
    }

    //Called by UI
    public void sendMessageTo(String name, String content){
        Node src = getSelfNode() ;
        Node dst = getNode(name) ;
        send(new Packet(src, dst, Packet.LOCATION, content)) ;
    }
}
