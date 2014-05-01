package com.daotang.jqf;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xiawanqiang on 14-4-30.
 */
public class QActive extends QHsm implements Runnable {

    public int priority = 0;

    private LinkedList<QEvent> mEventQueue = new LinkedList<QEvent>();
    private LinkedList<QEvent> mDeferQueue = new LinkedList<QEvent>();

    private final Lock mQueueLock = new ReentrantLock();

    public QActive(QState initial) {
        super(initial);
        this.priority = 0;
    }

    public void postFifo(QEvent e) {
        mQueueLock.lock();

        mEventQueue.addLast(e);

        mQueueLock.unlock();

        QF.instance().execute(this);
    }

    public void postLifo(QEvent e) {
        mQueueLock.lock();

        mEventQueue.addFirst(e);

        mQueueLock.unlock();

        QF.instance().execute(this);
    }


    public void defer(QEvent e) {
        mQueueLock.lock();

        mDeferQueue.addLast(e);

        mQueueLock.unlock();
    }

    public boolean recall() {
        boolean result = mDeferQueue.size() > 0;

        mQueueLock.lock();

        while (mDeferQueue.size() > 0) {
            mEventQueue.push(mDeferQueue.poll());
        }

        mQueueLock.unlock();

        if (result) {
            QF.instance().execute(this);
        }
        return result;
    }

    private QEvent getEvent() {
        QEvent e = null;

        mQueueLock.lock();

        if (mEventQueue.size() > 0) {
            e = mEventQueue.removeFirst();
        }

        mQueueLock.unlock();

        return e;
    }

    @Override
    public void run() {
        QEvent e = null;

        synchronized(this) {
            while ((e = getEvent()) != null) {
                dispatch(e);
            }
        }
    }

}
