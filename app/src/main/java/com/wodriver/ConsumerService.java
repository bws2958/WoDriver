/*
 * Copyright (c) 2015 Samsung Electronics Co., Ltd. All rights reserved. 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that 
 * the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer. 
 *     * Redistributions in binary form must reproduce the above copyright notice, 
 *       this list of conditions and the following disclaimer in the documentation and/or 
 *       other materials provided with the distribution. 
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its contributors may be used to endorse or 
 *       promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.wodriver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.*;
import com.wodriver.AWS.userpool.SignUpActivity;
import com.wodriver.util.DBManager;

public class ConsumerService extends SAAgent {
    private static final String TAG = "WoDriver";
    private static final Class<ServiceConnection> SASOCKET_CLASS = ServiceConnection.class;
    private final IBinder mBinder = new LocalBinder();
    private ServiceConnection mConnectionHandler = null;
    Handler mHandler = new Handler();
    DBManager dbManager;

    double hr;
    double x, y, z;

    public ConsumerService() {
        super(TAG, SASOCKET_CLASS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SA mAccessory = new SA();
        try {
            Log.d("start_consumer", "start_activity");
            mAccessory.initialize(this);
        } catch (SsdkUnsupportedException e) {
            // try to handle SsdkUnsupportedException
            if (processUnsupportedException(e) == true) {
                Log.d("consum_service", "first exception");
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.d("consum_service", "second exception");
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onFindPeerAgentsResponse(SAPeerAgent[] peerAgents, int result) {
        if ((result == SAAgent.PEER_AGENT_FOUND) && (peerAgents != null)) {
            for(SAPeerAgent peerAgent:peerAgents)
                requestServiceConnection(peerAgent);
        } else if (result == SAAgent.FINDPEER_DEVICE_NOT_CONNECTED) {
            Toast.makeText(getApplicationContext(), "FINDPEER_DEVICE_NOT_CONNECTED", Toast.LENGTH_LONG).show();
        //    updateTextView("Disconnected");
        } else if (result == SAAgent.FINDPEER_SERVICE_NOT_FOUND) {
            Toast.makeText(getApplicationContext(), "FINDPEER_SERVICE_NOT_FOUND", Toast.LENGTH_LONG).show();
         //   updateTextView("Disconnected");
        } else {
            Toast.makeText(getApplicationContext(), "noPeer", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        if (peerAgent != null) {
            acceptServiceConnectionRequest(peerAgent);
        }
        Log.d("peer", "request");
    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
        if (result == SAAgent.CONNECTION_SUCCESS) {
            this.mConnectionHandler = (ServiceConnection) socket;
            //updateTextView("Connected");
        } else if (result == SAAgent.CONNECTION_ALREADY_EXIST) {
           // updateTextView("Connected");
            Toast.makeText(getBaseContext(), "CONNECTION_ALREADY_EXIST", Toast.LENGTH_LONG).show();
        } else if (result == SAAgent.CONNECTION_DUPLICATE_REQUEST) {
            Toast.makeText(getBaseContext(), "CONNECTION_DUPLICATE_REQUEST", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "ConnectionFail", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onError(SAPeerAgent peerAgent, String errorMessage, int errorCode) {
        super.onError(peerAgent, errorMessage, errorCode);
    }

    @Override
    protected void onPeerAgentsUpdated(SAPeerAgent[] peerAgents, int result) {
        final SAPeerAgent[] peers = peerAgents;
        final int status = result;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (peers != null) {
                    if (status == SAAgent.PEER_AGENT_AVAILABLE) {
                        Toast.makeText(getApplicationContext(), "PEER_AGENT_AVAILABLE", Toast.LENGTH_LONG).show();
                        Log.d("peer", "Available");
                    } else {
                        Toast.makeText(getApplicationContext(), "PEER_AGENT_UNAVAILABLE", Toast.LENGTH_LONG).show();
                        Log.d("peer", "UNAvailable");
                    }
                }
            }
        });
    }

    public class ServiceConnection extends SASocket {
        public ServiceConnection() {
            super(ServiceConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            final String message = new String(data);

            if(!message.equals("vibration")){

            String[] detail = new String[10];
                Log.d("Message", message);
            detail = message.split(",");
            hr = Double.parseDouble(detail[0]);
            Log.d("hr", String.valueOf(hr));
            x = Double.parseDouble(detail[1]);
            Log.d("x", String.valueOf(x));
            y = Double.parseDouble(detail[2]);
            Log.d("y", String.valueOf(y));
            z = Double.parseDouble(detail[3]);
            Log.d("z", String.valueOf(z));
            }else{
                Log.d("bug", "catch bug");
            }

            new updatetable().execute();


            try{
                Thread.sleep(3000);
            }catch (Exception e){

            }

        }
        @Override
        protected void onServiceConnectionLost(int reason) {
            closeConnection();
            Log.d("Close", "connection");
        }
    }

    private class updatetable extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... params){
            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getcredentials(getApplicationContext());

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat test = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = test.format(date);

            HealthInfo healthInfo = new HealthInfo();
            healthInfo.setHr(hr);
            healthInfo.setTime(time);
            healthInfo.setX(x);
            healthInfo.setY(y);
            healthInfo.setZ(z);

//            try{
//                dbManager = new DBManager(getApplicationContext(), "WoDriver.db", null, 1);
//                dbManager.insert("insert into HR_DATA values(null, '" + hr + "', '" + x + "', '" + y + "', '" + z + "');");
////                dbManager.delete("DELETE FROM HR_DATA");
//            }catch (Exception e){
//
//            }

            if(credentialsProvider != null && managerClass != null){
                DynamoDBMapper dynamoDBMapper = managerClass.intiDynamoClient(credentialsProvider);
                dynamoDBMapper.save(healthInfo);
            }else{
                return 2;
            }
            return 1;
        }

        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            if(integer == 1){
                Toast.makeText(getApplicationContext(), "update success", Toast.LENGTH_LONG).show();
            }else if(integer == 2){
                Toast.makeText(getApplicationContext(), "fail updating", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class LocalBinder extends Binder {
        public ConsumerService getService() {
            return ConsumerService.this;
        }
    }

    public void findPeers() {
        findPeerAgents();
    }

    public boolean sendData(final String data) {
        boolean retvalue = false;
        if (mConnectionHandler != null) {
            try {
                mConnectionHandler.send(getServiceChannelId(0), data.getBytes());
                retvalue = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            //addMessage("Sent: ", data);
        }
        return retvalue;
    }
    public void ReceiveData(byte[] Rdata){
       // final String Rmessage = new String(Rdata);
        mConnectionHandler.onReceive(getServiceChannelId(0), Rdata);
        //addMessage("Receive: ", Rmessage);
    }
    public boolean closeConnection() {
        if (mConnectionHandler != null) {
            mConnectionHandler.close();
            mConnectionHandler = null;
            Log.d("Close", "connectionHandler");
            return true;
        } else {
            return false;
        }
    }

    private boolean processUnsupportedException(SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Log.e(TAG, "You need to install Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Log.e(TAG, "You need to update Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Log.e(TAG, "We recommend that you update your Samsung Accessory SDK before using this application.");
            return false;
        }
        return true;
    }
/*
    private void updateTextView(final String str) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ConsumerActivity.updateTextView(str);
            }
        });
    }

    private void addMessage(final String prefix, final String data) {
        final String strToUI = prefix.concat(data);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ConsumerActivity.addMessage(strToUI);
            }
        });
    }*/
}
