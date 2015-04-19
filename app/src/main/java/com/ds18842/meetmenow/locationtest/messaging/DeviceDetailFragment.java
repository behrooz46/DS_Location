/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ds18842.meetmenow.locationtest.messaging;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmu18842.team3.testwifidirect.DeviceListFragment.DeviceActionListener;
import com.cmu18842.team3.testwifidirect.location.GeoLocationProvider;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    //protected static final int CHOOSE_FILE_RESULT_CODE = 20;

    // TODO: Added location information
    public static final int INTENT_TYPE_LOCATION_CHANGE = 1;
    private GeoLocationProvider geoLocationProvider;
    Location location;

    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;

    public AddrCapsule destAddr = new AddrCapsule();

    ProgressDialog progressDialog = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        geoLocationProvider = new GeoLocationProvider(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceActionListener) getActivity()).connect(config);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                        destAddr.setAddr(null);
                    }
                });

        mContentView.findViewById(R.id.btn_send_message).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);*/

                        if (destAddr.getAddr() == null) {
                            Log.v(WiFiDirectActivity.TAG, "destAddr is null");
                            return;
                        }

                        // Allow user to send message
                        EditText editMessage = (EditText) mContentView.findViewById(R.id.edit_message);
                        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
                        String text = editMessage.getText().toString();
                        statusText.setText("Sending: " + text);
                        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + text);

                        Intent serviceIntent = new Intent(getActivity(), MessageTransferService.class);

                        Message message = new Message();
                        message.setMessageContent(text);

                        //statusText.setText("Message: " + message.getMessageContent());

                        serviceIntent.setAction(MessageTransferService.ACTION_SEND_MESSAGE);
                        serviceIntent.putExtra(MessageTransferService.EXTRAS_MESSAGE_CONTENT, message);

                        //serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_ADDRESS,
                        //        info.groupOwnerAddress.getHostAddress());
                        serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_ADDRESS,
                                destAddr.getAddr());

                        serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_PORT, 8988);
                        getActivity().startService(serviceIntent);

                        //TODO: Automatic Disconnect after message sent
                        ((DeviceActionListener) getActivity()).disconnect();
                        destAddr.setAddr(null);

                        //statusText.setText("Message 2: " + message.getMessageContent());

                        /*Toast.makeText(getActivity(), "Message should sent",
                                Toast.LENGTH_SHORT).show();*/
                    }
                });

        mContentView.findViewById(R.id.btn_send_location).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        location = geoLocationProvider.getLocation();

                        if (location != null) {
                            Log.v(WiFiDirectActivity.TAG, "Sending: " + location.toString());

                            Message message = new Message();
                            message.setLocation(location.getLatitude(),
                                    location.getLongitude(),
                                    location.getTime());
                            message.setIsLocation(true);

                            Intent serviceIntent = new Intent(getActivity(), MessageTransferService.class);

                            serviceIntent.setAction(MessageTransferService.ACTION_SEND_MESSAGE);
                            serviceIntent.putExtra(MessageTransferService.EXTRAS_MESSAGE_CONTENT, message);
                            serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_ADDRESS,
                                    destAddr.getAddr());
                            serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_PORT, 8988);
                            getActivity().startService(serviceIntent);

                            TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
                            statusText.setText("Sending location");
                            Log.v(WiFiDirectActivity.TAG, "Sending location to " + destAddr.getAddr());

                        }
                        else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setMessage("Location not available now")
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Dismiss
                                        }
                                    });
                            dialog.show();
                        }
                    }
                });

        return mContentView;
    }


    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;

        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.

        Toast.makeText(getActivity(), "Conn info avail",
                Toast.LENGTH_SHORT).show();

        // TODO: Modified to let every node setting up server
        if (info.groupFormed) {
            if (!info.isGroupOwner) {
                destAddr.setAddr(info.groupOwnerAddress.getHostAddress());

                Intent serviceIntent = new Intent(getActivity(), MessageTransferService.class);


                Message message = new Message();
                message.setIsInit(true);

                serviceIntent.setAction(MessageTransferService.ACTION_SEND_MESSAGE);
                serviceIntent.putExtra(MessageTransferService.EXTRAS_MESSAGE_CONTENT, message);

                //serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_ADDRESS,
                //        info.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_ADDRESS,
                        destAddr.getAddr());
                serviceIntent.putExtra(MessageTransferService.EXTRAS_DESTINATION_PORT, 8988);

                MessageTransferService.fragment = this;

                getActivity().startService(serviceIntent);

                Log.v(WiFiDirectActivity.TAG, "Sending initial message to " + destAddr);

                Toast.makeText(getActivity(), destAddr.getAddr(),
                        Toast.LENGTH_SHORT).show();
            }
            //else {
            new MessageServerAsyncTask(getActivity(),
                    mContentView.findViewById(R.id.status_text),
                    new AlertDialog.Builder(getActivity()),
                    destAddr)
                    .execute();
            //}

            // The other device acts as the client. In this case, we enable the
            // get file button.
            // NOTE: No longer valid

            mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.btn_send_message).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.btn_send_location).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.edit_message).setVisibility(View.VISIBLE);
            //((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
            //        .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_send_message).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public class MessageServerAsyncTask extends AsyncTask<Void, Void, Message> {

        private Context context;
        private TextView statusText;
        private AlertDialog.Builder dialog;
        //private String destAddr;

        private AddrCapsule destAddr;

        /**
         * @param context
         * @param statusText
         */
        public MessageServerAsyncTask(Context context, View statusText,
                                      AlertDialog.Builder dialog, AddrCapsule destAddr) {
            this.context = context;
            this.statusText = (TextView) statusText;
            this.dialog = dialog;
            this.destAddr = destAddr;

        }

        @Override
        protected Message doInBackground(Void... params) {
            try {

                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();

                destAddr.setAddr(client.getInetAddress().getHostAddress());

                Log.d(WiFiDirectActivity.TAG, "Server: connection done");

                InputStream in = client.getInputStream();
                ObjectInputStream inputStream = new ObjectInputStream(in);
                Message message = (Message) inputStream.readObject();

                in.close();
                inputStream.close();
                serverSocket.close();

                // record the address
                if (message != null) {

                    //destAddr = client.getInetAddress();
                    Log.v(WiFiDirectActivity.TAG, "Receive message");
                    Log.v(WiFiDirectActivity.TAG, destAddr.getAddr());

                }

                return message;

            } catch (Exception e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Message result) {
            if (result != null) {
                statusText.setText("Message received - " + result.getMessageContent());

                if (!result.getIsInit())  {
                    if (result.getMessageContent() != null)
                    {
                        dialog.setTitle("Message");
                        dialog.setMessage(result.getMessageContent());
                    }
                    if (result.getIsLocation()) {
                        Log.v(WiFiDirectActivity.TAG, "Receive location");
                        Message.LocationInfo pos = result.getLocation();
                        if (pos != null) {
                            dialog.setTitle("Location");
                            dialog.setMessage("Latitude: " + pos.getLatitude() +
                                    "\nLongitude: " + pos.getLongitude() +
                                    "\nTime: " + pos.getTime());
                        }
                        Log.v(WiFiDirectActivity.TAG, "Location is null");

                    }
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Dismiss
                        }
                    });
                    dialog.show();
                }

                Log.v(WiFiDirectActivity.TAG, "PostExecute");
                Log.v(WiFiDirectActivity.TAG, destAddr.getAddr());
                ((WiFiDirectActivity)context).onResume();

                Log.v(WiFiDirectActivity.TAG, destAddr.getAddr());

            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }


    class AddrCapsule {
        String addr;

        AddrCapsule() {
            super();
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }
    }
}


