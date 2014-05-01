package com.daotang.jqf;

/**
 * Created by xiawanqiang on 14-4-30.
 */
public class QEvent {
    public String signal;
    public Object data;
    public QActive sender;

    public static final String EMPTY_SIG = "HSM:EMPTY";
    public static final QEvent emptyEvent = new QEvent(EMPTY_SIG);

    public QEvent(String signal, Object data, QActive sender) {
        this.signal = signal;
        this.data = data;
        this.sender = sender;
    }

    public QEvent(String signal, Object data) {
        this.signal = signal;
        this.data = data;
        this.sender = null;
    }

    public QEvent(String signal) {
        this.signal = signal;
        this.data = null;
        this.sender = null;
    }
}
