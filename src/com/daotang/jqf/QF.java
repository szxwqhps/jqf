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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xiawanqiang on 14-4-30.
 */
public class QF {
    private static QF mInstance = null;

    private Map<String, List<QActive>> mSubscribes;
    private List<QActive> mActiveObjects;
    private List<QTimeEvent> mTimeEvents;
    private int mTickRate;
    private final int QF_TICK_RATE = 10;

    private final Lock mQFLock = new ReentrantLock();
    private final Lock mSubscribeLock = new ReentrantLock();
    private final Lock mTimeLock = new ReentrantLock();

    private ThreadPoolExecutor mExecutor = null;
    private BlockingQueue<Runnable> mWorkQueue = null;
    private Timer mTimer = null;
    private boolean mStop = false;

    public static synchronized QF instance() {
        if (mInstance == null) {
            mInstance = new QF();
        }
        return mInstance;
    }

    protected QF() {
        mSubscribes = new HashMap<String, List<QActive>>();
        mActiveObjects = new LinkedList<QActive>();
        mTimeEvents = new  LinkedList<QTimeEvent>();
        mWorkQueue = new LinkedBlockingQueue<Runnable>();
        mTickRate = QF_TICK_RATE;
        mStop = false;
    }

    public void init(int tickRate) {
        mQFLock.lock();

        mSubscribes.clear();
        mActiveObjects.clear();
        mTimeEvents.clear();
        mTickRate = tickRate;
        mWorkQueue.clear();
        mStop = false;

        mQFLock.unlock();
    }

    public void run() {
        mQFLock.lock();

        mStop = false;
        try {
            if (mTimer == null) {
                mTimer = new Timer();
            }

            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    tick();
                }

            }, mTickRate, mTickRate);

            mExecutor = new ThreadPoolExecutor(4, 8, 5, TimeUnit.MILLISECONDS, mWorkQueue);

        } finally {
            mQFLock.unlock();
        }
    }

    public void stop() {
        mQFLock.lock();

        try {
            mStop = true;
            mWorkQueue.clear();

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            if (mExecutor != null) {
                mExecutor.shutdownNow();
            }
        } finally {
            mQFLock.unlock();
        }
    }

    public void execute(QActive ao) {
        mQFLock.lock();

        if (!mStop) {
            mExecutor.execute(ao);
        }

        mQFLock.unlock();
    }

    public void startActive(QActive ao, int priority) {
        mQFLock.lock();

        if (!mActiveObjects.contains(ao)) {
            ao.priority = priority;
            mActiveObjects.add(ao);
            ao.init(QEvent.emptyEvent);
        }

        mQFLock.unlock();
    }

    public void stopActive(QActive ao) {
        unsubscribeAll(ao);

        mQFLock.lock();

        if (mActiveObjects.contains(ao)) {

            mActiveObjects.remove(ao);

            while (mWorkQueue.contains(ao)) {
                mWorkQueue.remove(ao);
            }
        }

        mQFLock.unlock();
    }


    public void arm(QTimeEvent te, int interval, boolean repeat) {
        mTimeLock.lock();

        if (!mTimeEvents.contains(te)) {
            te.interval = interval;
            te.repeat = repeat;
            te.lastTick = new Date();
            mTimeEvents.add(te);
        }

        mTimeLock.unlock();
    }

    public void disarm(QTimeEvent te) {
        mQFLock.lock();

        if (mTimeEvents.contains(te)) {
            mTimeEvents.remove(te);
        }

        mQFLock.unlock();
    }

    public void subscribe(QActive ao, String signal) {
        mSubscribeLock.lock();

        List<QActive> subs = null;

        if (mSubscribes.containsKey(signal)) {
            subs = mSubscribes.get(signal);
        } else {
            subs = new LinkedList<QActive>();
            mSubscribes.put(signal, subs);
        }

        if (!subs.contains(ao)) {
            subs.add(ao);
        }

        mSubscribeLock.unlock();
    }

    public void unsubscribe(QActive ao, String signal) {
        mSubscribeLock.lock();

        if (mSubscribes.containsKey(signal)) {
            List<QActive> subs = mSubscribes.get(signal);
            if (subs.contains(ao)) {
                subs.remove(ao);
            }
        }

        mSubscribeLock.unlock();
    }

    public void unsubscribeAll(QActive ao) {
        mSubscribeLock.lock();

        Iterator<List<QActive>> iter = mSubscribes.values().iterator();
        while (iter.hasNext()) {
            List<QActive> subs = iter.next();
            if (subs.contains(ao)) {
                subs.remove(ao);
            }
        }

        mSubscribeLock.unlock();
    }

    public void publish(QEvent e) {
        mSubscribeLock.lock();

        String signal = e.signal;
        if (mSubscribes.containsKey(signal)) {
            List<QActive> subs = mSubscribes.get(signal);
            Iterator<QActive> iter = subs.iterator();
            while (iter.hasNext()) {
                QActive ao = iter.next();
                ao.postFifo(e);
            }
        }

        mSubscribeLock.unlock();
    }

    private void tick() {
        mTimeLock.lock();

        Date timeNow = new Date();

        Iterator<QTimeEvent> iter = mTimeEvents.iterator();
        while (iter.hasNext()) {
            QTimeEvent te = iter.next();
            if ((timeNow.getTime() - te.lastTick.getTime()) > te.interval) {
                te.activeObject.postFifo(new QEvent(te.signal, null, te.activeObject));
                if (te.repeat) {
                    te.lastTick = timeNow;
                } else {
                    iter.remove();
                }
            }
        }

        mTimeLock.unlock();
    }

}
