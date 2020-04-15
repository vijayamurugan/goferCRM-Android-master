package com.gofercrm.user.gofercrm.websocket;



import android.util.Log;


import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public final class WebSocketClient extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    volatile Boolean isConnected = false;


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d("OPEN:","OPEN");
        SharedObserver.setSocketState(true);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
         Log.d("FROM_OLD_RECEIVINGX:",text);
         SharedObserver.setSocketObserver(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.d("CLOSING:","CLOSING");
        SharedObserver.setSocketState(false);
        isConnected=false;
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.d("FAILURE:","FAILURE");
        t.printStackTrace();
        SharedObserver.setSocketState(false);
        isConnected=false;
//        while (!isConnected) {
////            JSONObject intro_data = new JSONObject();
////            try {
////                intro_data.put("type", "intro");
////                intro_data.put("sender_id", LOGIN_USER_ID);
////                intro_data.put("connection_id", SharedPreferenceData.getInstance(getApplicationContext()).getClientConnectionID());
////                WebSocket ws = WSSingleton.getInstance();
////                ws.send(intro_data.toString());
////
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//        }

        if (isConnected) {
            Log.d("ON FAILURE",response.toString());
        }
    }

}
