package com.ds18842.meetmenow.locationtest.network;


import android.content.Context;
import android.util.Log;

import com.ds18842.meetmenow.locationtest.common.IMessageHandler;
import com.ds18842.meetmenow.locationtest.common.Packet;
import com.ds18842.meetmenow.locationtest.common.Node;
import com.ds18842.meetmenow.locationtest.logic.LogicManager;
import com.ds18842.meetmenow.locationtest.network.infrastructure.Neighbour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NetworkManager implements IMessageHandler {
    private final Context context;
    private PeerManager peerManager;
    private LogicManager app;
    private Node me;
    private IMessageHandler receiver;
    private Set<String> seenId;

    public static final String TAG = "NetworkManager";

    public NetworkManager(Context context, LogicManager app, PeerManager peerManager){
        this.context = context ;
        this.peerManager = peerManager ;
        this.app = app ;
        this.me = app.getSelfNode();
        this.seenId = new HashSet<String>();
    }

    public void setReceiver(IMessageHandler receiver) { this.receiver = receiver; }

    @Override
    public void receive(Packet msg) {
        String id = msg.getSrc().getName() + "-" + msg.getId() ;
        if (hasSeen(id))
            return ;

        if (msg.getType() == Packet.FLOOD){
            app.updateBroadcast(msg.getSrc());
            this.broadcast(msg);
        }else{
            receiver.receive(msg);
        }
    }

    private boolean hasSeen(String id) {
        if (seenId.contains(id)){
            return true;
        }
        seenId.add(id);
        return false;
    }

    @Override
    public void send(Packet msg) {
        Log.d(TAG, "Enter send");

        Node node = msg.getNext() ;
        final Neighbour next = peerManager.getNeighbor(node);

        peerManager.setPacketNow(msg);
        peerManager.setState(PeerManager.SENDING);

        Thread t = new Thread() {
            public void run() {
                Log.d(TAG, "send: Before commWithPeer");
                peerManager.commWithPeer(next.getNode());
                Log.d(TAG, "send: After commWithPeer");
            }
        };
        t.start();
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

    public HashMap<String, Node> getNodes() {
        return app.getNodes();
    }

    public Node getSelfNode() {
        return app.getSelfNode();
    }
}
