package com.gofercrm.user.gofercrm.websocket;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SharedObserver {


    static Subject<String>  socketObserver = PublishSubject.create();

    static Subject<Boolean>  socketState = PublishSubject.create();



    public static Observable<String> getSocketObserver() {
        return socketObserver;
    }

    public static void setSocketObserver(String text) {
        socketObserver.onNext(text);
    }

    public static Observable<Boolean> getSocketState() {
        return socketState;
    }

    public static void setSocketState(boolean value) {
        socketState.onNext(value);
    }
}
