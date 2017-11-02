package com.rx.example.timercheck;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by INNO_14 on 2017-02-01.
 */

public class TimerCheck {

    private TimerTask mTask;
    private Timer mTimer;
    private Handler mHandler;

    public static final int UNLIMITE_REPEATE = -1;
    public final int maxRepeat;
    public final int delay_Timer;
    public final int period_Timer;
    //maxRepeat * period_Timer = all sec;

    private long checkCnt = 0;

    private boolean isStarting = false;

    private OnCheckInTimeListener onCheckInTimeListener;

    public interface OnCheckInTimeListener {
        void onCheckInTask(long checkCnt);

        void callFinish();

        void onError(Exception e);
    }

    /**
     * only 1000ms == 1sec
     *
     * @param maxRepeat    count
     * @param delay_Timer  ms
     * @param period_Timer ms
     */
    public TimerCheck(int maxRepeat, int delay_Timer, int period_Timer, OnCheckInTimeListener onCheckInTimeListener) {
        this.maxRepeat = maxRepeat;
        this.delay_Timer = delay_Timer;
        this.period_Timer = period_Timer;
        this.onCheckInTimeListener = onCheckInTimeListener;
        Log.i("CheckTimer", "" + maxRepeat + " " + delay_Timer + " " + period_Timer);
        onCheck();
        startTimerSchedule();
    }


    public long getCheckCnt() {
        return checkCnt;
    }

    public void setCheckCnt(int checkCnt) {
        this.checkCnt = checkCnt;
    }

    private void startTimerSchedule() {

        isStarting = true;

        if (period_Timer == 0)
            mTimer.schedule(mTask, delay_Timer);
        else
            mTimer.schedule(mTask, delay_Timer, period_Timer);
    }

    public void setTimerCancel() {
        if (mTimer != null)
            mTimer.cancel();

        if (mTask != null)
            mTask.cancel();

        mTimer = null;
        mTask = null;
        mHandler = null;

        isStarting = false;
    }

    public boolean getMaxcCeckCnt() {
        if (maxRepeat == UNLIMITE_REPEATE)
            return false;
        if (checkCnt >= maxRepeat)
            return true;
        return false;
    }

    public boolean isStarting() {
        return isStarting;
    }

    private void onCheck() {

        setTimerCancel();

        mHandler = new Handler(Looper.getMainLooper());
        mTimer = new Timer();

        mTask = new TimerTask() {
            @Override
            public void run() {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onCheckInTimeListener.onCheckInTask(checkCnt);

                            if (maxRepeat != UNLIMITE_REPEATE) {
                                checkCnt++;
                                if (checkCnt > maxRepeat) {
                                    onCheckInTimeListener.callFinish();
                                    setTimerCancel();
                                }
                            }
                        } catch (Exception e) {
                            onCheckInTimeListener.onError(e);
                        }
                    }
                });
            }
        };
    }
}
