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
