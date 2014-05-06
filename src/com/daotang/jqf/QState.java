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
public abstract class QState {
    private QState mParent = null;

    public static final int RET_HANDLED = 0;
    public static final int RET_IGNORED = 1;
    public static final int RET_TRANSFER = 2;
    public static final int RET_SUPER = 3;
    public static final int RET_UNHANDLED = 4;

    public static final QState TOP = new QState(null) {
        @Override
        public int handler(Object me, QEvent e) {
            return RET_IGNORED;
        }
    };

    public QState(QState parent) {
        if (parent == null) {
            parent = QState.TOP;
        } else {
            mParent = parent;
        }
    }

    public abstract int handler(Object me, QEvent e);

    public int entry(Object me) {
        return RET_HANDLED;
    }

    public int exit(Object me) {
        return RET_HANDLED;
    }

    public int init(Object me) {
        return RET_HANDLED;
    }

    public boolean isTop() {
        return mParent == null;
    }

    public QState getParent() {
        return mParent;
    }
}
