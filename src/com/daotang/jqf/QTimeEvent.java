package com.daotang.jqf;

import java.util.Date;

/**
 * Created by xiawanqiang on 14-4-30.
 */
public class QTimeEvent {
    public Date lastTick = new Date();
    public long interval = 0;
    public boolean repeat = false;
    public QActive activeObject = null;
    public String signal = "";

    public QTimeEvent(QActive ao, String signal) {
        this.activeObject = ao;
        this.signal = signal;
        this.lastTick = new Date();
        this.interval = 0;
        this.repeat = false;
    }
}
