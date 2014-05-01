package com.daotang.jqf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xiawanqiang on 14-4-30.
 */
public class TestMachine extends QActive {

    private boolean foo = false;

    public TestMachine() {
        super(initial);
    }

    private static QState initial = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            TestMachine tm = (TestMachine)me;
            System.out.println("In initial.");
            tm.foo = false;
            System.out.println("Transfer to s2");
            return tm.transfer(s2);
        }

    };

    private static QState terminate = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            return QState.RET_HANDLED;
        }

        @Override
        public int entry(Object me) {
            System.out.println("In terminate.");
            QF.instance().stopActive((QActive)me);	// remove from QF
            return QState.RET_HANDLED;
        }

    };

    private static QState s = new QState(QState.TOP) {

        @Override
        public int handler(Object me, QEvent e) {
            TestMachine tm = (TestMachine)me;
            switch (e.signal) {
                case "tm:e":
                    System.out.println("s - e");
                    System.out.println("Transfer to s11");
                    return tm.transfer(s11);
                case "tm:terminate":

                    System.out.println("s - terminate");
                    System.out.println("Transfer to terminate");
                    return tm.transfer(terminate);
            }

            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            System.out.println("Enter s.");
            return QState.RET_HANDLED;
        }

        @Override
        public int exit(Object me) {
            System.out.println("Exit s.");
            return QState.RET_HANDLED;
        }

        @Override
        public int init(Object me) {
            TestMachine tm = (TestMachine)me;
            System.out.println("Init s.");
            if (tm.foo) {
                tm.foo = false;
                System.out.println("Transfer to s11.");
                return tm.transfer(s11);
            }
            return QState.RET_HANDLED;
        }
    };

    private static QState s1 = new QState(s) {

        @Override
        public int handler(Object me, QEvent e) {
            TestMachine tm = (TestMachine)me;
            switch (e.signal) {
                case "tm:b":
                    System.out.println("s1 - b");
                    System.out.println("Transfer to s11");
                    return tm.transfer(s11);
                case "tm:d":
                    if (!tm.foo) {
                        System.out.println("s1 - d");
                        tm.foo = true;
                        System.out.println("Transfer to s");
                        return tm.transfer(s);
                    }
                    return QState.RET_UNHANDLED;
                case "tm:c":
                    System.out.println("s1 - c");
                    System.out.println("Transfer to s2");
                    return tm.transfer(s2);
                case "tm:a":
                    System.out.println("s1 - a");
                    System.out.println("Self transfer to s1");
                    return tm.transfer(s1);
                case "tm:f":
                    System.out.println("s1 - f");
                    System.out.println("Transfer to s211");
                    return tm.transfer(s211);
            }

            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            System.out.println("Enter s1.");
            return QState.RET_HANDLED;
        }

        @Override
        public int exit(Object me) {
            System.out.println("Exit s1.");
            return QState.RET_HANDLED;
        }

        @Override
        public int init(Object me) {
            TestMachine tm = (TestMachine)me;
            System.out.println("Init s1.");
            System.out.println("Transfer to s11.");
            return tm.transfer(s11);
        }

    };

    private static QState s11 = new QState(s1) {

        @Override
        public int handler(Object me, QEvent e) {
            TestMachine tm = (TestMachine)me;
            switch (e.signal) {
                case "tm:d":
                    if (tm.foo) {
                        System.out.println("s11 - d");
                        System.out.println("Transfer to s1");
                        tm.foo = false;
                        return tm.transfer(s1);
                    }
                    return QState.RET_UNHANDLED;
                case "tm:h":
                    System.out.println("s11 - h");
                    System.out.println("Transfer to s");
                    return tm.transfer(s);
                case "tm:g":
                    System.out.println("s11 - g");
                    System.out.println("Transfer to s211");
                    return tm.transfer(s211);
            }

            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            System.out.println("Enter s11.");
            return QState.RET_HANDLED;
        }

        @Override
        public int exit(Object me) {
            System.out.println("Exit s11.");
            return QState.RET_HANDLED;
        }

    };

    private static QState s2 = new QState(s) {

        @Override
        public int handler(Object me, QEvent e) {
            TestMachine tm = (TestMachine)me;
            switch (e.signal) {
                case "tm:c":
                    System.out.println("s2 - c");
                    System.out.println("Transfer to s1");
                    return tm.transfer(s1);
                case "tm:f":
                    System.out.println("s2 - f");
                    System.out.println("Transfer to s11");
                    return tm.transfer(s11);
            }

            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            System.out.println("Enter s2.");
            return QState.RET_HANDLED;
        }

        @Override
        public int exit(Object me) {
            System.out.println("Exit s2.");
            return QState.RET_HANDLED;
        }

        @Override
        public int init(Object me) {
            TestMachine tm = (TestMachine)me;
            System.out.println("Init s2.");
            if (!tm.foo) {
                tm.foo = true;
                System.out.println("Transfer to s211");
                return tm.transfer(s211);
            }
            return QState.RET_HANDLED;
        }

    };

    private static QState s21 = new QState(s2) {

        @Override
        public int handler(Object me, QEvent e) {
            TestMachine tm = (TestMachine)me;
            switch (e.signal) {
                case "tm:a":
                    System.out.println("s21 - a");
                    System.out.println("Self transfer to s21");
                    return tm.transfer(s21);
                case "tm:g":
                    System.out.println("s21 - g");
                    System.out.println("Transfer to s11");
                    return tm.transfer(s11);
                case "tm:b":
                    System.out.println("s21 - b");
                    System.out.println("Transfer to s211");
                    return tm.transfer(s211);
            }

            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            System.out.println("Enter s21.");
            return QState.RET_HANDLED;
        }

        @Override
        public int exit(Object me) {
            System.out.println("Exit s21.");
            return QState.RET_HANDLED;
        }

        @Override
        public int init(Object me) {
            TestMachine tm = (TestMachine)me;
            System.out.println("Init s21.");
            System.out.println("Transfer to s211.");
            return tm.transfer(s211);
        }

    };

    private static QState s211 = new QState(s21) {

        @Override
        public int handler(Object me, QEvent e) {
            TestMachine tm = (TestMachine)me;
            switch (e.signal) {
                case "tm:d":
                    System.out.println("s211 - d");
                    System.out.println("Transfer to s21");
                    return tm.transfer(s21);
                case "tm:h":
                    System.out.println("s211 - h");
                    System.out.println("Transfer to s");
                    return tm.transfer(s);
            }

            return QState.RET_UNHANDLED;
        }

        @Override
        public int entry(Object me) {
            System.out.println("Enter s211.");
            return QState.RET_HANDLED;
        }

        @Override
        public int exit(Object me) {
            System.out.println("Exit s211.");
            return QState.RET_HANDLED;
        }

    };

	public static void main(String[] args) {
		TestMachine tm = new TestMachine();

		QF.instance().init(50);
		QF.instance().startActive(tm, 1);
		QF.instance().run();

		boolean stop = false;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		while (!stop) {
			String command = "";
			try {
				command = br.readLine().toLowerCase();
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
			 switch (command) {
				case "a":
					System.out.println("Send event A");
					tm.postFifo(new QEvent("tm:a"));
					break;
				case "b":
					System.out.println("Send event B");
					tm.postFifo(new QEvent("tm:b"));
					break;
				case "c":
					System.out.println("Send event C");
					tm.postFifo(new QEvent("tm:c"));
					break;
				case "d":
					System.out.println("Send event D");
					tm.postFifo(new QEvent("tm:d"));
					break;
				case "e":
					System.out.println("Send event E");
					tm.postFifo(new QEvent("tm:e"));
					break;
				case "f":
					System.out.println("Send event F");
					tm.postFifo(new QEvent("tm:f"));
					break;
				case "g":
					System.out.println("Send event G");
					tm.postFifo(new QEvent("tm:g"));
					break;
				case "h":
					System.out.println("Send event H");
					tm.postFifo(new QEvent("tm:h"));
					break;
				case "exit":
					System.out.println("exit the app.");
					tm.postLifo(new QEvent("tm:terminate"));
					QF.instance().stop();
					stop = true;
					break;
				default:
					break;
				}
		}

	}

}
