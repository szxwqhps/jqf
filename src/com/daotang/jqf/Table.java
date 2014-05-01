package com.daotang.jqf;

/**
 * Created by xiawanqiang on 14-4-30.
 */

public class Table extends QActive {
    private int[] forks;
    private boolean[] isHungry;

    public Table() {
        super(initial);

        forks = new int[Dpp.PHILO_NUMBER];
        isHungry = new boolean[Dpp.PHILO_NUMBER];
        for (int i = 0; i < Dpp.PHILO_NUMBER; ++i) {
            forks[i] = Dpp.FORK_FREE;
            isHungry[i] = false;
        }
    }

    private static final QState initial = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            QActive ao = (QActive)me;
            QF.instance().subscribe(ao, Dpp.DPP_DONE);
            QF.instance().subscribe(ao, Dpp.DPP_TERMINATE);
            return ao.transfer(serving);
        }

    };

    private static final QState terminate = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            return QState.RET_HANDLED;
        }

        @Override
        public int entry(Object me) {
            QF.instance().unsubscribeAll((QActive)me);
            QF.instance().stopActive((QActive)me);
            return QState.RET_HANDLED;
        }
    };

    private static final QState serving = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            int n = 0;
            int	m = 0;
            Table table = (Table)me;

            switch (e.signal) {
                case Dpp.DPP_HUNGRY:
                    n = ((Integer)e.data).intValue();

                    Dpp.busyDelay(5);
                    Dpp.displayStat(n, "hungry");
                    m = left(n);
                    if ((table.forks[m] == Dpp.FORK_FREE) &&
                            (table.forks[n] == Dpp.FORK_FREE)) {
                        table.forks[m] = Dpp.FORK_USED;
                        table.forks[n] = Dpp.FORK_USED;
                        QF.instance().publish(new QEvent(Dpp.DPP_EAT, n, table));
                        Dpp.displayStat(n, "eating");
                    } else {
                        table.isHungry[n] = true;
                    }
                    return QState.RET_HANDLED;
                case Dpp.DPP_DONE:
                    Dpp.busyDelay(5);
                    n = ((Integer)e.data).intValue();
                    m = left(n);

                    table.forks[n] = Dpp.FORK_FREE;
                    table.forks[m] = Dpp.FORK_FREE;

                    // check right side
                    m = right(n);
                    if (table.isHungry[m] && (table.forks[m] == Dpp.FORK_FREE)) {
                        table.forks[m] = Dpp.FORK_USED;
                        table.forks[n] = Dpp.FORK_USED;
                        table.isHungry[m] = false;
                        QF.instance().publish(new QEvent(Dpp.DPP_EAT, m, table));
                        Dpp.displayStat(m, "eating");
                    }

                    // check left side
                    m = left(n);
                    n = left(m);
                    if (table.isHungry[m] && table.forks[n] == Dpp.FORK_FREE) {
                        table.forks[m] = Dpp.FORK_USED;
                        table.forks[n] = Dpp.FORK_USED;
                        table.isHungry[m] = false;
                        QF.instance().publish(new QEvent(Dpp.DPP_EAT, m, table));
                        Dpp.displayStat(m, "eating");
                    }
                    return QState.RET_HANDLED;
                case Dpp.DPP_TERMINATE:
                    return table.transfer(terminate);
            }
            return QState.RET_UNHANDLED;
        }

    };

    private static int left(int n) {
        return (n + 1) % Dpp.PHILO_NUMBER;
    }

    private static int right(int n) {
        return (n + Dpp.PHILO_NUMBER - 1) % Dpp.PHILO_NUMBER;
    }

}