package com.ds18842.meetmenow.locationtest.logic;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.ds18842.meetmenow.locationtest.common.* ;
import com.ds18842.meetmenow.locationtest.views.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LogicManager implements IMessageHandler, ILocationHandler {
    private static final double LOCATION_CHANGE_THERESHOLD = 10;

    private final Context context;
    private final HashMap<String, Node> nodes;
    private IMessageHandler sender;
    private Node me;

    public static String TAG = "LogicManager";

    private MainActivity mainActivity;
    private Location location, dstLocation;

    public LogicManager(Context context) {
        this.context = context ;
        me = new Node(null, null, null);
        nodes = new HashMap<String, Node>();

        dstLocation = new Location("dummyprovider");
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    @Override
    public void receive(Packet msg) {
        if (msg.getType() == Packet.NETWORK ){
            Map<String, Node> top = (HashMap<String, Node>)(msg.getPayload());
            for(String key : top.keySet()){
                synchronized (nodes){
                    nodes.put(key, top.get(key));
                }
            }
        }else if (msg.getType() == Packet.LOCATION ){
            final String name = msg.getSrc().getName() ;
            final GeoLocation pos = msg.getSrc().getGeoLocation();
            final String instruction = (String) msg.getPayload() ;

            if (mainActivity != null) {
                dstLocation.setLatitude(pos.getLat());
                dstLocation.setLongitude(pos.getLng());
                Log.d(TAG, "pos : (" + pos.getLat() + ", " + pos.getLng() + ")");

                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        //Do your UI operations like dialog opening or Toast here
                        mainActivity.showRequest(name, instruction, pos);
                    }
                });
            }
        }else if (msg.getType() == Packet.RESPONSE ){
            String name = msg.getSrc().getName() ;
            GeoLocation pos = msg.getSrc().getGeoLocation();
            Boolean response = (Boolean) msg.getPayload() ;

            if (mainActivity != null)
                mainActivity.showResponse(name, response, pos);
        }
    }


    //Called by GeoLocation
    @Override
    public void updateLocation(Location location) {
        this.location = location ;
        GeoLocation geoLocaiton = new GeoLocation(location);
        if ( geoLocaiton.getDistance(me.getGeoLocation()) < LOCATION_CHANGE_THERESHOLD )
            return ;

        //Update my location
        me.setGeoLocation(geoLocaiton);
        //Boradcast this update
        Node src = me ;
        Node dst = me ;
        broadcast(new Packet(src, dst, Packet.FLOOD, "")) ;
    }

    //Called by UI
    public boolean sendMessageTo(String name, String content){
        Node src = me, dst = getNodeFromName(name);
        if (dst == null){
            return false ;
        }else{
            send(new Packet(src, dst, Packet.LOCATION, content)) ;
            return true ;
        }
    }

    //Called by UI
    public void sendResponseTo(String name, Boolean response){
        Node src = me, dst = getNodeFromName(name);
        if (dst != null){
            send(new Packet(src, dst, Packet.RESPONSE, new Boolean(response))) ;
        }
    }

    //Called by UI
    public void joinTheNetwork(String name) {
        if (me.getName() == null)
            me.setName(name);

        // TODO Add itself to nodes, may be changed later
        nodes.put(me.getName(), me);
    }






    @Override
    public void send(Packet msg) {
        Log.d(TAG, "Before send");
        sender.send(msg);
    }

    @Override
    public void broadcast(Packet msg) {
        sender.broadcast(msg);
    }

    public void setSender(IMessageHandler sender) {
        this.sender = sender;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Node getSelfNode() {
        return me;
    }

    public Set<String> getNodeNames(){
        return nodes.keySet() ;
    }

    public boolean isLoogedIn(){
        return getSelfNode().getName() != null ;
    }


    private Node getNodeFromName(String name){
        Node dst = null ;
        for(String key : getNodeNames()){
            if (key.equals(name)) {
                dst = nodes.get(key) ;
                break;
            }
        }
        return dst ;
    }

    public Location getMyLocation() {
        return location;
    }

    public Location getDstLocation() {
        return dstLocation;
    }

    public void updateBroadcast(Node src) {
        synchronized (nodes){
            nodes.put(src.getName(), src);
        }
    }
}
