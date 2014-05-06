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
public class Philo extends QActive {

    private QTimeEvent mTimeEvent;
    private int mId;

    public Philo(int id) {
        super(initial);
        mTimeEvent = new QTimeEvent(this, Dpp.DPP_TIMEOUT);
        mId = id;
    }

    private static final QState initial = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            QActive ao = (QActive)me;
            QF.instance().subscribe(ao, Dpp.DPP_EAT);
            return ao.transfer(thinking);
        }

    };

    private static final QState thinking = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            Philo philo = (Philo)me;
            switch (e.signal) {
                case Dpp.DPP_TIMEOUT:
                    Dpp.busyDelay(5);
                    return philo.transfer(hungry);
                case Dpp.DPP_DONE:
                    return QState.RET_HANDLED;
                case Dpp.DPP_EAT:
                    return QState.RET_HANDLED;
            }
            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            Philo philo = (Philo)me;
            Dpp.displayStat(philo.mId, "thinking");
            QF.instance().arm(philo.mTimeEvent, Dpp.THINK_TIME, false);
            return QState.RET_HANDLED;
        }

    };

    private static final QState hungry = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            Philo philo = (Philo)me;
            switch (e.signal) {
                case Dpp.DPP_DONE:
                    return QState.RET_HANDLED;
                case Dpp.DPP_EAT:
                    int id = ((Integer)e.data).intValue();
                    if (philo.mId == id) {
                        Dpp.busyDelay(5);
                        return philo.transfer(eating);
                    }
                    return QState.RET_HANDLED;
            }
            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            Philo philo = (Philo)me;
            Dpp.table.postFifo(new QEvent(Dpp.DPP_HUNGRY, philo.mId, philo));
            return QState.RET_HANDLED;
        }
    };

    private static final QState eating = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            Philo philo = (Philo)me;
            switch (e.signal) {
                case Dpp.DPP_TIMEOUT:
                    Dpp.busyDelay(5);
                    return philo.transfer(thinking);
                case Dpp.DPP_DONE:
                    return QState.RET_HANDLED;
                case Dpp.DPP_EAT:
                    return QState.RET_HANDLED;
            }
            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            Philo philo = (Philo)me;
            QF.instance().arm(philo.mTimeEvent, Dpp.EAT_TIME, false);
            return QState.RET_HANDLED;
        }

        @Override
        public int exit(Object me) {
            Philo philo = (Philo)me;
            QF.instance().publish(new QEvent(Dpp.DPP_DONE, philo.mId, philo));
            return QState.RET_HANDLED;
        }
    };
}