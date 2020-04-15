package com.gofercrm.user.gofercrm.websocket;

import android.content.Context;
import android.util.Log;


import com.gofercrm.user.gofercrm.util.SharedPreferenceData;
import com.gofercrm.user.gofercrm.websocket.listener.WsStatusListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;


public class WSSingleton {

    private static WebSocket mInstance;
    private static WsManager wsManager;
    private static Context mcontext;
    private static WsStatusListener wsStatusListener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            String loggedInUserID = SharedPreferenceData.getInstance(mcontext).getLoggedInUserID();
            String value = SharedPreferenceData.getInstance(mcontext).getClientConnectionID();
            JSONObject intro_data = new JSONObject();
            try {
                intro_data.put("type", "intro");
                intro_data.put("sender_id", loggedInUserID);
                intro_data.put("connection_id", value);
                WsManager ws = WSSingleton.getInstanceWM(mcontext);
                if(ws.isWsConnected())
                {
                    ws.sendMessage(intro_data.toString());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessage(String text) {
            SharedObserver.setSocketObserver(text);
            Log.d("RECEIVINGX:",text);
        }

        @Override
        public void onMessage(ByteString bytes) {
        }

        @Override
        public void onReconnect() {
        }

        @Override
        public void onClosing(int code, String reason) {
        }

        @Override
        public void onClosed(int code, String reason) {
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            Log.d("FAILURE","Socket Failed");
        }
    };



    public static synchronized WsManager getInstanceWM(Context context) {
        mcontext=context;
        if (wsManager == null) {
            wsManager = new WsManager.Builder(context)
                    .client(
                            new OkHttpClient().newBuilder()
                                    .pingInterval(15, TimeUnit.SECONDS)
                                    .retryOnConnectionFailure(true)
                                    .build())
                    .needReconnect(true)
                    .wsUrl("wss://goferapi.ambivo.com/chat")
                    .build();
            wsManager.setWsStatusListener(wsStatusListener);
        }

        return wsManager;
    }

}
