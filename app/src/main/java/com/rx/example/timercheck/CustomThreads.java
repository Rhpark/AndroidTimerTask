package com.rx.example.timercheck;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by INNO_14 on 2017-11-15.
 */

public class CustomThreads {

    private long sleepTime;
    private Thread thread;
    private AppCompatActivity activity;

    private OnThreadAndUIThreadSleepListener onCheckInThreadListener;
    private OnThreadAndSleepListener onThreadAndSleepListener;
    private OnThreadAndUIThreadListener onThreadAndUIThreadListener;

    public interface OnThreadAndUIThreadSleepListener {
        void onCheckInSleepBefore();

        void onCheckInThread();

        void callFinishUiThread();

        void onError(Exception e);
    }

    public interface OnThreadAndUIThreadListener {
        void onCheckInThread();

        void callFinishUiThread();

        void onError(Exception e);
    }

    public interface OnThreadAndSleepListener {
        void onCheckInSleepBefore();

        void onCheckInThread();

        void onError(Exception e);
    }

    /**
     *  using uiThread &  Thread & sleep
     * @param activity
     * @param sleepTime over than 0
     * @param onCheckInThreadListener
     */
    public CustomThreads(AppCompatActivity activity, long sleepTime, OnThreadAndUIThreadSleepListener onCheckInThreadListener)
    {
        this.activity = activity;
        this.sleepTime = sleepTime;
        this.onCheckInThreadListener = onCheckInThreadListener;

        if (sleepTime < 0)
            throw new NumberFormatException("sleepTime value is over than -1, sleepTime = " + sleepTime);

        else
            doThreadSleepAndUiThread();
    }

    /**
     *  using uiThread &  Thread
     * @param activity
     * @param onThreadAndUIThreadListener
     */
    public CustomThreads(AppCompatActivity activity, OnThreadAndUIThreadListener onThreadAndUIThreadListener)
    {
        this.activity = activity;
        this.onThreadAndUIThreadListener = onThreadAndUIThreadListener;

        if (sleepTime < 0)
            throw new NumberFormatException("sleepTime value is over than -1, sleepTime = " + sleepTime);

        else
            doThreadAndUiThread();
    }

    /**
     * using Thread & sleepTime
     * @param sleepTime over than 0
     * @param onThreadAndSleepListener
     */
    public CustomThreads(long sleepTime, OnThreadAndSleepListener onThreadAndSleepListener)
    {
        this.sleepTime = sleepTime;
        this.onThreadAndSleepListener = onThreadAndSleepListener;

        if (sleepTime < 0)
            throw new NumberFormatException("sleepTime value is over than -1, sleepTime = " + sleepTime);

        else
            doThreadSleep();
    }

    private void doThreadSleepAndUiThread()
    {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (sleepTime > 0)
                    {
                        onCheckInThreadListener.onCheckInSleepBefore();
                        Thread.sleep(sleepTime);
                    }
                    onCheckInThreadListener.onCheckInThread();

                    if (activity != null)
                    {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onCheckInThreadListener.callFinishUiThread();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    onCheckInThreadListener.onError(e);
                }
            }
        });
        thread.start();
    }

    private void doThreadAndUiThread()
    {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    onThreadAndUIThreadListener.onCheckInThread();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onThreadAndUIThreadListener.callFinishUiThread();
                        }
                    });
                }
                catch (Exception e)
                {
                    onThreadAndUIThreadListener.onError(e);
                }
            }
        });
        thread.start();
    }

    private void doThreadSleep()
    {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    if (sleepTime > 0)
                    {
                        onThreadAndSleepListener.onCheckInSleepBefore();
                        Thread.sleep(sleepTime);
                    }
                    onThreadAndSleepListener.onCheckInThread();
                }
                catch (Exception e)
                {
                    onThreadAndSleepListener.onError(e);
                }
            }
        });
        thread.start();
    }

    public void onDestory()
    {
        thread.interrupt();
        thread = null;
        System.gc();
    }
}
