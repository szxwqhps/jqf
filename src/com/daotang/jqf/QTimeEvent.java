/**
 * Copyright (c) 2014, Wanqiang Xia. All rights reserved.
 *
 * This program is open source software: you can redistribute it and/or
 * modify it under the terms of the BSD 2-Clause license.
 *
 * This program is a java implementation of QP framework. You can visit QP
 * website (http://www.state-machine.com) for more information
 */

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
