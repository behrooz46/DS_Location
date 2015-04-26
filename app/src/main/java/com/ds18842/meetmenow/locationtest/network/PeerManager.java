package com.ds18842.meetmenow.locationtest.network;

import com.ds18842.meetmenow.locationtest.common.IMessageHandler;
import com.ds18842.meetmenow.locationtest.common.Node;
import com.ds18842.meetmenow.locationtest.common.Packet;
import com.ds18842.meetmenow.locationtest.network.infrastructure.Neighbour;
import com.ds18842.meetmenow.locationtest.views.MainActivity;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;


import android.util.Log;
import android.widget.Toast;

public class PeerManager implements PeerListListener, ConnectionInfoListener, ChannelListener {
    //TODO Discovery
    //TODO establish connection : exchange location
    //TODO after exchanging location, add that to list of neighbors and update logic about the new node
    //TODO keep list of neighbours updated

    private final Context context;

    private List<WifiP2pDevice> peers;

    private ArrayList<Neighbour> neighbours;

    private WifiP2pDevice device;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiP2pInfo info;
    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private NetworkManager receiver;

    private Packet packetNow;

    private final IntentFilter intentFilter = new IntentFilter();

    //private WifiP2pManager.ConnectionInfoListener connListener;

    //private AddrCapsule destAddr;

    private Node me;

    public static String TAG = "PeerManager";
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CLIENT_INITIAL_SLEEP_TIME = 1000;
    private static final int DISCOVERY_MAX_TIME = 5000;
    private static final int DISCOVERY_RETRY_INTERVAL = 5000;



    private int SERVER_PORT = 8988;

    public static final int NEW = 0, EXCHANGE = 1, NORMAL = 2, SENDING = 3;
    private int state = NEW;
    private boolean hasInitDiscovery = false;
    private boolean retryChannel = false;

    private MainActivity mainActivity;


    //private boolean success = false;

    //public static boolean peerSuccess = false;

    public final Semaphore connSem = new Semaphore(1);
    //public final ReentrantLock connLock = new ReentrantLock();

    public PeerManager(Context context) {
        this.context = context;

        peers = new ArrayList<>();

        neighbours = new ArrayList<>();
        //ipToNodes = new HashMap<>();
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);

        broadcastReceiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        context.registerReceiver(broadcastReceiver, intentFilter);

        //connListener = new WifiP2pManager.

        //state = NEW;
    }

    /*public ArrayList<Neighbour> getNeighbours() {
        //TODO return all neighbour
        return this.neighbours;
    }

    public Neighbour getNeighbour(Node next) {
        //TODO return neighbour from node

        for (Neighbour neighbour : neighbours) {
            if (neighbour.getNode().getName().equals(next.getName())) {
                return neighbour;
            }
        }

        return null ;
    }*/

    public ArrayList<Neighbour> getNeighbors() {
        //TODO return all neighbour
        return this.neighbours;
    }

    public Neighbour getNeighbor(Node next) {
        //TODO return neighbour from node

        for (Neighbour neighbour : neighbours) {
            if (neighbour.getNode().getName().equals(next.getName())) {
                return neighbour;
            }
        }

        return null ;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setReceiver(NetworkManager receiver) {
        this.receiver = receiver;
    }

    public Packet getPacketNow() {
        return packetNow;
    }

    public void setPacketNow(Packet packetNow) {
        this.packetNow = packetNow;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (state == EXCHANGE) return;

        if (!hasInitDiscovery) return;

        Log.d(TAG, "Enter onPeersAvailable");
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        if (peers.size() == 0) {
            Log.d(TAG, "No device found");

            mainActivity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Toast.makeText(mainActivity, "No device found",
                        Toast.LENGTH_SHORT).show();
                }
            });

            if (state == NEW) {
                state = NORMAL;
            }

            if (state == NORMAL && hasInitDiscovery) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            sleep(DISCOVERY_RETRY_INTERVAL);
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "NoDevFound discoverPeers");
                        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "NoDevFound discoverPeers success");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "NoDevFound discoverPeers failure");
                            }
                        });
                    }
                };
                t.start();
            }

            return;
        }

        Log.d(TAG, "Peers: " + peers.size());
        mainActivity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(mainActivity, "Peers: " + peers.size(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        if (state == NEW) {
            state = EXCHANGE;
            Thread thread = new Thread() {
                public void run() {
                    Log.d(TAG, "Before exchange");
                    exchangeWithPeers();
                }
            };
            thread.start();
        }

    }

    /**
     * Update UI for this device.
     *
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;

    }


    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        Toast.makeText(mainActivity, "Enter onConnectionInfo",
                Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Enter onConnectionInfo");
        Log.d(TAG, "isGroup:" + String.valueOf(info.groupFormed));

        this.info = info;

        if (info.groupFormed) {
            if (state == EXCHANGE) {
                if (info.isGroupOwner) {
                    newNodeOwnerOperation();
                }
                else {
                    newNodeClientOperation();
                }
            }
            else if (state == NORMAL) {
                if (info.isGroupOwner) {
                    normalNodeOwnerOperation();
                }
                else {
                    normalNodeClientOperation();
                }
            }
            else if (state == SENDING) {
                if (info.isGroupOwner) {
                    sendingNodeOwnerOperation();
                }
                else {
                    sendingNodeClientOperation();
                }
            }

        }

        Log.d(TAG, "Leave onConnInfo");
    }


    public void newNodeOwnerOperation() {
        Log.d(TAG, "New Node Owner");
        Thread server = new Thread() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    Socket clientSocket = serverSocket.accept();
                    Log.d(TAG, clientSocket.getInetAddress().getHostAddress());

                    InputStream in = clientSocket.getInputStream();
                    ObjectInputStream inputStream = new ObjectInputStream(in);
                    Packet initPacket = (Packet) inputStream.readObject();

                    Log.d(TAG, "Receive: " + initPacket.getPayload());

                    OutputStream out = clientSocket.getOutputStream();
                    ObjectOutputStream outputStream = new ObjectOutputStream(out);
                    Node src = new Node(device.deviceName, null, device.deviceAddress);
                    Packet outPacket = new Packet(src, null, Packet.EXCHANGE, receiver.getNodes());

                    outputStream.writeObject(outPacket);

                    Log.d(TAG, "Sending: " + outPacket.getPayload());

                    Packet rePacket = (Packet) inputStream.readObject();

                    neighbours.add(new Neighbour(rePacket.getSrc()));

                    // TODO Add nodes here, maybe changed later
                    HashMap<String, Node> nodes = receiver.getNodes();
                    HashMap<String, Node> top = (HashMap<String, Node>)(rePacket.getPayload());
                    for(String key : top.keySet()){
                        synchronized (nodes){
                            nodes.put(key, top.get(key));
                        }
                    }

                    Log.d(TAG, "Receive: " + rePacket.getPayload());

                    in.close();
                    inputStream.close();
                    serverSocket.close();
                    clientSocket.close();

                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                for (Neighbour node : neighbours) {
                    Log.d(TAG, node.getNode().getName() + " " + node.getNode().getAddress());
                }

                Log.d(TAG, "operation: before release: " + connSem.availablePermits());
                connSem.release();
                Log.d(TAG, "operation: after release: " + connSem.availablePermits());

                Log.d(TAG, "Server exit");
            }
        };
        server.start();
    }


    public void newNodeClientOperation() {
        Log.d(TAG, "New Node Client");
        try {
            Thread.sleep(CLIENT_INITIAL_SLEEP_TIME);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Thread client = new Thread() {
            public void run() {

                try {
                    Socket socket = new Socket();
                    String host = info.groupOwnerAddress.getHostAddress();
                    int port = SERVER_PORT;

                    Log.d(TAG, "Opening client socket - ");
                    socket.bind(null);
                    socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                    Log.d(TAG, "Client socket - " + socket.isConnected());
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream outStream = new ObjectOutputStream(out);

                    Node src = new Node(device.deviceName, null, device.deviceAddress);
                    Packet outPacket = new Packet(src, null, Packet.EXCHANGE, receiver.getNodes());

                    outStream.writeObject(outPacket);

                    Log.d(TAG, "Sending: " + outPacket.getPayload());

                    InputStream in = socket.getInputStream();
                    ObjectInputStream inStream = new ObjectInputStream(in);

                    Packet inPacket = (Packet) inStream.readObject();

                    Log.d(TAG, "Receive: " + inPacket.getPayload());

                    neighbours.add(new Neighbour(inPacket.getSrc()));

                    // TODO Add nodes here, maybe changed later
                    HashMap<String, Node> nodes = receiver.getNodes();
                    HashMap<String, Node> top = (HashMap<String, Node>)(inPacket.getPayload());
                    for(String key : top.keySet()){
                        synchronized (nodes){
                            nodes.put(key, top.get(key));
                        }
                    }

                    in.close();
                    inStream.close();
                    out.close();
                    outStream.close();

                    socket.close();

                    for (Neighbour node : neighbours) {
                        Log.d(TAG, node.getNode().getName() + " " + node.getNode().getAddress());
                    }

                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "operation: before release: " + connSem.availablePermits());
                connSem.release();
                Log.d(TAG, "operation: after release: " + connSem.availablePermits());

                Log.d(TAG, "Client exit");

            }
        };
        client.start();

    }


    public void normalNodeOwnerOperation() {
        Toast.makeText(mainActivity, "Normal Node Owner",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Normal Node Owner");
        Thread server = new Thread() {
            public void run() {
                try {

                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    Socket clientSocket = serverSocket.accept();
                    Log.d(TAG, clientSocket.getInetAddress().getHostAddress());

                    InputStream in = clientSocket.getInputStream();
                    ObjectInputStream inputStream = new ObjectInputStream(in);
                    Packet inPacket = (Packet) inputStream.readObject();

                    if (inPacket.getType() == Packet.EXCHANGE) {
                        neighbours.add(new Neighbour(inPacket.getSrc()));
                    }

                    Log.d(TAG, "Receive: " + inPacket.toString());

                    OutputStream out = clientSocket.getOutputStream();
                    ObjectOutputStream outputStream = new ObjectOutputStream(out);

                    Packet outPacket;
                    Node src = new Node(device.deviceName, null, device.deviceAddress);

                    if (inPacket.getType() == Packet.EXCHANGE) {
                        // TODO Add nodes here, maybe changed later
                        HashMap<String, Node> nodes = receiver.getNodes();
                        HashMap<String, Node> top = (HashMap<String, Node>)(inPacket.getPayload());
                        for(String key : top.keySet()){
                            synchronized (nodes){
                                nodes.put(key, top.get(key));
                            }
                        }

                        outPacket = new Packet(src, null, Packet.EXCHANGE, receiver.getNodes());
                    }
                    else {
                        receiver.receive(inPacket);
                        outPacket = new Packet(src, null, Packet.ACK, "Ack");
                    }

                    outputStream.writeObject(outPacket);

                    Log.d(TAG, "Sending: " + outPacket.toString());

                    in.close();
                    inputStream.close();
                    serverSocket.close();
                    clientSocket.close();

                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                for (Neighbour node : neighbours) {
                    Log.d(TAG, node.getNode().getName() + " " + node.getNode().getAddress());
                }

                /*Log.d(TAG, "operation: before release: " + connSem.availablePermits());
                connSem.release();
                Log.d(TAG, "operation: after release: " + connSem.availablePermits());*/

                Log.d(TAG, "Server exit");
            }
        };
        server.start();
    }


    public void normalNodeClientOperation() {
        Toast.makeText(mainActivity, "Normal Node Client",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Normal Node Client");
        try {
            Thread.sleep(CLIENT_INITIAL_SLEEP_TIME);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Thread client = new Thread() {
            public void run() {

                try {
                    Socket socket = new Socket();
                    String host = info.groupOwnerAddress.getHostAddress();
                    int port = SERVER_PORT;

                    Log.d(TAG, "Opening client socket - ");
                    socket.bind(null);
                    socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                    Log.d(TAG, "Client socket - " + socket.isConnected());
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream outStream = new ObjectOutputStream(out);

                    Packet initPacket = new Packet(new Node(device.deviceName, null, device.deviceAddress)
                            , null, Packet.EXCHANGE, "Initial");

                    outStream.writeObject(initPacket);

                    Log.d(TAG, "Sending: " + initPacket.getPayload());

                    InputStream in = socket.getInputStream();
                    ObjectInputStream inStream = new ObjectInputStream(in);

                    Packet inPacket = (Packet) inStream.readObject();

                    if (inPacket.getType() == Packet.EXCHANGE) {
                        neighbours.add(new Neighbour(inPacket.getSrc()));
                    }

                    Log.d(TAG, "Receive: " + inPacket.toString());

                    Node src = new Node(device.deviceName, null, device.deviceAddress);

                    Packet outPacket;
                    if (inPacket.getType() == Packet.EXCHANGE) {
                        // TODO Add nodes here, maybe changed later
                        HashMap<String, Node> nodes = receiver.getNodes();
                        HashMap<String, Node> top = (HashMap<String, Node>)(inPacket.getPayload());
                        for(String key : top.keySet()){
                            synchronized (nodes){
                                nodes.put(key, top.get(key));
                            }
                        }

                        outPacket = new Packet(src, null, Packet.EXCHANGE, receiver.getNodes());
                    }
                    else {
                        receiver.receive(inPacket);
                        outPacket = new Packet(src, null, Packet.ACK, "Ack");
                    }

                    outStream.writeObject(outPacket);

                    in.close();
                    inStream.close();
                    out.close();
                    outStream.close();

                    socket.close();

                    for (Neighbour neighbour : neighbours) {
                        Log.d(TAG, neighbour.getNode().getName() + " " + neighbour.getNode().getAddress());
                    }

                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                /*Log.d(TAG, "operation: before release: " + connSem.availablePermits());
                connSem.release();
                Log.d(TAG, "operation: after release: " + connSem.availablePermits());*/

                Log.d(TAG, "Client exit");

            }
        };
        client.start();
    }


    public void sendingNodeOwnerOperation() {
        Toast.makeText(mainActivity, "Sending Node Owner",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Sending Node Owner");
        Thread server = new Thread() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    Socket clientSocket = serverSocket.accept();
                    Log.d(TAG, clientSocket.getInetAddress().getHostAddress());

                    InputStream in = clientSocket.getInputStream();
                    ObjectInputStream inputStream = new ObjectInputStream(in);
                    Packet inPacket = (Packet) inputStream.readObject();

                    neighbours.add(new Neighbour(inPacket.getSrc()));

                    Log.d(TAG, "Receive: " + inPacket.toString());

                    OutputStream out = clientSocket.getOutputStream();
                    ObjectOutputStream outputStream = new ObjectOutputStream(out);
                    //Node src = new Node(device.deviceName, null, device.deviceAddress);
                    //Packet outPacket = new Packet(src, null, Packet.EXCHANGE, "Old Location");

                    outputStream.writeObject(packetNow);

                    Log.d(TAG, "Sending: " + packetNow.toString());

                    Packet ack = (Packet) inputStream.readObject();

                    Log.d(TAG, ack.toString());

                    in.close();
                    inputStream.close();
                    serverSocket.close();
                    clientSocket.close();

                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                state = NORMAL;

                Log.d(TAG, "operation: before release: " + connSem.availablePermits());
                connSem.release();
                Log.d(TAG, "operation: after release: " + connSem.availablePermits());

                Log.d(TAG, "Change state to NORMAL. Server exit");
            }
        };
        server.start();
    }


    public void sendingNodeClientOperation() {
        Toast.makeText(mainActivity, "Sending Node Client",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Sending Node Client");
        try {
            Thread.sleep(CLIENT_INITIAL_SLEEP_TIME);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Thread client = new Thread() {
            public void run() {

                try {
                    Socket socket = new Socket();
                    String host = info.groupOwnerAddress.getHostAddress();
                    int port = SERVER_PORT;

                    Log.d(TAG, "Opening client socket - ");
                    socket.bind(null);
                    socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                    Log.d(TAG, "Client socket - " + socket.isConnected());
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream outStream = new ObjectOutputStream(out);

                    outStream.writeObject(packetNow);

                    Log.d(TAG, "Sending: " + packetNow.toString());

                    InputStream in = socket.getInputStream();
                    ObjectInputStream inStream = new ObjectInputStream(in);

                    Packet ack = (Packet) inStream.readObject();

                    Log.d(TAG, "Receive: " + ack.toString());

                    in.close();
                    inStream.close();
                    out.close();
                    outStream.close();

                    socket.close();

                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                state = NORMAL;

                Log.d(TAG, "operation: before release: " + connSem.availablePermits());
                connSem.release();
                Log.d(TAG, "operation: after release: " + connSem.availablePermits());

                Log.d(TAG, "Change state to NORMAL. Client exit");

            }
        };
        client.start();
    }


    public void discoverPeers() {

        final PeerManager peerManager = this;
        hasInitDiscovery = true;

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Discovery initiated");

                Thread t = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(DISCOVERY_MAX_TIME);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "After initial sleep " + String.valueOf(state));
                        if (state == NEW) {
                            Log.d(TAG, "Request for empty peers");
                            manager.requestPeers(channel, peerManager);
                        }
                    }
                };
                t.start();

            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Discovery Failed : " + reasonCode);
            }
        });

        Log.d(TAG, "Before requestPeers");

    }


    public void exchangeWithPeers() {
        Log.d(TAG, "Enter exchangeWithPeers");

        for (WifiP2pDevice peerDevice : peers) {
            commWithPeer(new Node(peerDevice.deviceName, null, peerDevice.deviceAddress));
        }

        Log.d(TAG, "Loop ended. Setting state to NORMAL");
        state = NORMAL;

    }

    public void commWithPeer(Node node) {
        final WifiP2pConfig config = new WifiP2pConfig();

        for (WifiP2pDevice peer : peers) {
            if (peer.deviceName.equals(node.getName())) {
                config.deviceAddress = peer.deviceAddress;
            }
        }
        if (config.deviceAddress == null) {
            Log.d(TAG, "Cannot find device address from peers");
            return;
        }

        //config.deviceAddress = node.getAddress();
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 15;

        Log.d(TAG, "Connecting to: " + node.getName() + " " + node.getAddress());

        Thread thread = new Thread(){
            public void run(){
                connect(config);
                Log.d(TAG, "Thread finished");
            }
        };

        thread.start();

        try {
            Log.d(TAG, "commWithPeer before sleep");
            Thread.sleep(1000);
            Log.d(TAG, "commWithPeer after sleep");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //connLock.lock();
        try {
            Log.d(TAG, "commWithPeer: before acquire: " + connSem.availablePermits());
            connSem.acquire();
            Log.d(TAG, "commWithPeer: after acquire: " + connSem.availablePermits());


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Before disconnect");

        disconnect();
    }

    public WifiP2pManager getWifiP2pManager() {
        return this.manager;
    }

    public WifiP2pManager.Channel getWifiP2pChannel() {
        return this.channel;
    }


    public void connect(WifiP2pConfig config) {
        Log.d(TAG, "Enter connect");

        try {
            Log.d(TAG, "connect: before acquire: " + connSem.availablePermits());
            connSem.acquire();
            Log.d(TAG, "connect: after acquire: " + connSem.availablePermits());

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        //Log.d(TAG, "After acquire connSem in connect");

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Log.d(TAG, "connect onSuccess.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Connect failed. Retry.");
            }
        });

        Log.d(TAG, "Leaving connect");

    }

    public void disconnect() {
        Log.d(TAG, "Enter disconnect");
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

                try {
                    Thread.sleep(1000);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                synchronized (manager) {
                    manager.removeGroup(channel, this);
                }

            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "Disconnect succeeded");

                /*manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
                channel = manager.initialize(context, context.getMainLooper(), null);

                try {
                    Class<?> wifiManager = Class
                            .forName("android.net.wifi.p2p.WifiP2pManager");

                    Method method = wifiManager
                            .getMethod(
                                    "enableP2p",
                                    new Class[] { android.net.wifi.p2p.WifiP2pManager.Channel.class });

                    method.invoke(manager, channel);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }*/

                synchronized (this) {
                    manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "disconnect: discover success");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d(TAG, "disconnect: discover fail");
                        }
                    });
                }

                Log.d(TAG, "disconnect: before release: " + connSem.availablePermits());
                connSem.release();
                Log.d(TAG, "disconnect: after release: " + connSem.availablePermits());

            }
        });
        Log.d(TAG, "Leave disconnect");
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Log.d(TAG, "Channel lost. Trying again");
            //retryChannel = true;
            channel = manager.initialize(context, context.getMainLooper(), null);
        } else {
            Log.d(TAG, "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.");
        }

        //Log.d(TAG, "onChannelDisconnected: reset manager and channel");

        /*manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);

        try {
            Class<?> wifiManager = Class
                    .forName("android.net.wifi.p2p.WifiP2pManager");

            Method method = wifiManager
                    .getMethod(
                            "enableP2p",
                            new Class[] { android.net.wifi.p2p.WifiP2pManager.Channel.class });

            method.invoke(manager, channel);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }


}
