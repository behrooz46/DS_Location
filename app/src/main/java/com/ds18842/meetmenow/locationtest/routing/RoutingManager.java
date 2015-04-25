package com.ds18842.meetmenow.locationtest.routing;

import android.content.Context;
import android.util.Log;

import com.ds18842.meetmenow.locationtest.network.PeerManager;
import com.ds18842.meetmenow.locationtest.network.infrastructure.* ;
import com.ds18842.meetmenow.locationtest.common.* ;
import com.ds18842.meetmenow.locationtest.logic.LogicManager;

import java.util.ArrayList;

public class RoutingManager implements IMessageHandler {
    private final Context context;
    private PeerManager peerManager;
    private LogicManager app;
    private IMessageHandler receiver;
    private IMessageHandler sender;

    public static final String TAG = "RoutingManager";


    private Node me ;

    public RoutingManager(Context context, LogicManager app, PeerManager peerManager){
        this.context = context ;
        this.peerManager = peerManager ;
        this.app = app ;
        this.me = app.getSelfNode();
    }

    public void setSender(IMessageHandler sender) { this.sender = sender; }
    public void setReceiver(IMessageHandler receiver) { this.receiver = receiver; }

    @Override
    public void receive(Packet msg){
        if (amIequalTo(msg.getDst())){
            receiver.receive(msg);
        }else{
            this.send(msg);
        }
    }

    @Override
    public void send(Packet msg){
        Node next = findNextNode(msg);
        msg.setHop(me, next);
        Log.d(TAG, "Before send");
        sender.send(msg);
    }

    @Override
    public void broadcast(Packet msg) {
        sender.broadcast(msg);
    }

    private boolean amIequalTo(Node dst){
        return me.getName().equals(dst.getName());
    }

    private Node findNextNode(Packet msg) {
        ArrayList<Neighbour> neighbors = peerManager.getNeighbors() ;
        Node best = me ;
        /*double min_dis = me.getGeoLocation().getDistance(msg.getDst().getGeoLocation()) ;

        for (Neighbour neighbor : neighbors) {
            double dis = neighbor.getNode().getGeoLocation().getDistance(msg.getDst().getGeoLocation()) ; ;

            if (dis < min_dis){
                min_dis = dis ;
                best = neighbor.getNode() ;
            }
        }*/
        best = neighbors.get(0).getNode();

        if (amIequalTo(best)) {
            //TODO there's no node to send it to.
            return null;
        }

        return best;
    }


}
