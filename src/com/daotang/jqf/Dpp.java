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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiawanqiang on 14-4-30.
 */
public class Dpp {

    public static final int PHILO_NUMBER = 5;
    public static final int THINK_TIME = 2000;
    public static final int EAT_TIME = 3000;

    public static final String DPP_DONE = "dpp:done";
    public static final String DPP_HUNGRY = "dpp:hungry";
    public static final String DPP_EAT = "dpp:eat";
    public static final String DPP_TIMEOUT = "dpp:timeout";
    public static final String DPP_TERMINATE = "dpp:terminate";

    public static final int FORK_FREE = 0;
    public static final int FORK_USED = 1;


    public static QActive table = null;
    public static List<QActive> philos = null;

    public static void busyDelay(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    public static void displayStat(int n, String stat) {
        System.out.println(String.format("Philosophy %d is %s in %s", n, stat, Thread.currentThread().getName()));
    }

    public static void main(String[] args) {
        philos = new ArrayList<QActive>();

        QF.instance().init(20);

        for (int i = 0; i < PHILO_NUMBER; ++i) {
            Philo philo = new Philo(i);
            philos.add(philo);
            QF.instance().startActive(philo, i + 1);
        }

        table = new Table();
        QF.instance().startActive(table, PHILO_NUMBER + 1);
        QF.instance().run();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        boolean stop = false;
        String command = "";
        while (!stop) {
            try {
                command = br.readLine().toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (command.equals("exit")) {
                stop = true;
            }
        }

        System.out.println("exit the app.");
        QF.instance().stop();
    }

}