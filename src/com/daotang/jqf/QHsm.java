package com.daotang.jqf;

import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xiawanqiang on 14-4-30.
 */
public class QHsm {
    private QState mState = QState.TOP;
    private QState mTarget = QState.TOP;

    private final Lock mLock = new ReentrantLock();
    private boolean mInited = false;

    public QHsm(QState initial) {
        mState = QState.TOP;
        mTarget = initial;
        mInited = false;
    }

    public int transfer(QState t) {
        mTarget = t;
        return QState.RET_TRANSFER;
    }

    public void init(QEvent e) {
        mLock.lock();

        if (mInited) {
            mLock.unlock();
            return;
        }

        QState s = mState;
        QState t = mTarget;
        Stack<QState> path = new Stack<QState>();

        t.handler(this, e);

        do {
            path.clear();
            path.push(mTarget);
            t = mTarget.getParent();
            while (t != s) {
                path.push(t);
                t = t.getParent();
            }

            while (path.size() > 0) {
                path.pop().entry(this);
            }

            s = mTarget;
        } while (s.init(this) == QState.RET_TRANSFER);

        mState = s;
        mTarget = s;
        mInited = true;

        mLock.unlock();
    }

    public void dispatch(QEvent e) {
        mLock.lock();

        QState s = mState;
        QState t = mTarget;
        QState p = QState.TOP;
        LinkedList<QState> entries = new LinkedList<QState>();
        LinkedList<QState> exits = new LinkedList<QState>();

        int r = t.handler(this, e);
        while (r == QState.RET_UNHANDLED) {
            t = t.getParent();
            r = t.handler(this, e);
        }

        if (r != QState.RET_TRANSFER) {
            mLock.unlock();
            return;
        }

        // current -> transfer source, exit
        while (s != t) {
            exits.addLast(s);
            s = s.getParent();
        }

        if (s == mTarget) {	// self transfer
            exits.addLast(s);
            entries.addLast(s);
        } else {
            // Lca p
            p = findLca(s, mTarget);

            // source -> p, exit
            while (s != p) {
                exits.addLast(s);
                s = s.getParent();
            }

            // target -> p, entries
            s = mTarget;
            while (s != p) {
                entries.addLast(s);
                s = s.getParent();
            }
        }

        // do exists
        while (exits.size() > 0) {
            exits.removeFirst().exit(this);
        }

        // do entries
        while (entries.size() > 0) {
            entries.removeLast().entry(this);
        }

        // drill in target, init
        t = mTarget;
        while (t.init(this) == QState.RET_TRANSFER) {
            entries.clear();
            entries.addLast(mTarget);
            s = mTarget.getParent();
            while (s != t) {
                entries.addLast(s);
                s = s.getParent();
            }

            while (entries.size() > 0) {
                entries.removeLast().entry(this);
            }

            t = mTarget;
        }

        mState = t;
        mTarget = t;

        mLock.unlock();
    }

    public boolean isIn(QState s) {
        QState p = mState;

        while (!p.isTop()) {
            if (p == s) {
                return true;
            }
            p = p.getParent();
        }

        return false;
    }

    protected QState findLca(QState p, QState q) {
        Stack<QState> ps = new Stack<QState>();
        Stack<QState> qs = new Stack<QState>();
        QState t = QState.TOP;

        ps.push(p);
        qs.push(q);

        while (!p.isTop()) {
            p = p.getParent();
            ps.push(p);
        }

        while(!q.isTop()) {
            q = q.getParent();
            qs.push(q);
        }

        while ((ps.size() > 0) && (qs.size() > 0)) {
            p = ps.pop();
            q = qs.pop();
            if (p != q) {
                break;
            }
            t = p;
        }

        return t;
    }
}
