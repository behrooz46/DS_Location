//// Copyright 2011 Google Inc. All Rights Reserved.
//
//package com.ds18842.meetmenow.locationtest.messaging;
//
//import android.app.IntentService;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
///**
// * A service that process each message transfer request i.e Intent by opening a
// * socket connection with the WiFi Direct Group Owner and writing the file
// */
//public class MessageTransferService extends IntentService {
//
//    public static DeviceDetailFragment fragment;
//
//    private static final int SOCKET_TIMEOUT = 5000;
//
//    public static final String ACTION_SEND_MESSAGE = "com.cmu18842.team3.testwifidirect.SEND_MESSAGE";
//
//    //public static final String ACTION_SEND_FILE = "com.cmu18842.team3.testwifidirect.SEND_FILE";
//    //public static final String EXTRAS_FILE_PATH = "file_url";
//
//    public static final String EXTRAS_FRAGMENT = "fragment";
//
//    public static final String EXTRAS_MESSAGE_CONTENT = "message_content";
//    public static final String EXTRAS_DESTINATION_ADDRESS = "host";
//    public static final String EXTRAS_DESTINATION_PORT = "port";
//
//    public MessageTransferService(String name) {
//        super(name);
//    }
//
//    public MessageTransferService() {
//        super("MessageFileTransferService");
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
//     */
//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//        Context context = getApplicationContext();
//
//        //Toast.makeText(context, "Into transfer",
//        //        Toast.LENGTH_SHORT).show();
//
//
//        if (intent.getAction().equals(ACTION_SEND_MESSAGE)) {
//            //String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
//            Message message = (Message) intent.getExtras().getSerializable(EXTRAS_MESSAGE_CONTENT);
//
//            String host = intent.getExtras().getString(EXTRAS_DESTINATION_ADDRESS);
//
//            Socket socket = new Socket();
//            int port = intent.getExtras().getInt(EXTRAS_DESTINATION_PORT);
//
//            //Toast.makeText(context, "Before try",
//            //        Toast.LENGTH_SHORT).show();
//
//            try {
//                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
//                socket.bind(null);
//                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
//
//                Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
//                OutputStream out = socket.getOutputStream();
//                ObjectOutputStream stream = new ObjectOutputStream(out);
//                stream.writeObject(message);
//
//                out.close();
//                stream.close();
//
//                //Toast.makeText(context, "Message trans: " + message.getMessageContent(),
//                //        Toast.LENGTH_SHORT).show();
//
//                /*
//                ContentResolver cr = context.getContentResolver();
//                InputStream is = null;
//                try {
//                    is = cr.openInputStream(Uri.parse(fileUri));
//
//                } catch (FileNotFoundException e) {
//                    Log.d(WiFiDirectActivity.TAG, e.toString());
//                }
//                DeviceDetailFragment.copyFile(is, stream);*/
//
//                Log.d(WiFiDirectActivity.TAG, "Client: Data written");
//
//
//            } catch (IOException e) {
//                Log.e(WiFiDirectActivity.TAG, e.getMessage());
//            } finally {
//                if (socket != null) {
//                    if (socket.isConnected()) {
//                        try {
//                            socket.close();
//                        } catch (IOException e) {
//                            // Give up
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//        }
//    }
//}
