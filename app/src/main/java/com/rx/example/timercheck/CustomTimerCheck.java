package com.rx.example.timercheck;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class CustomTimerCheck {

    public static final int UNLIMITED_REPEAT = -1;

    private TimerTask mTask;
    private Timer mTimer;
    private Handler mHandler;

    public final int maxRepeat;
    public final int delay_Timer;
    public final int period_Timer;

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
     * @param maxRepeat    count    >   0
     * @param delay_Timer  ms       >   -1
     * @param period_Timer ms       >   -1
     */
    public CustomTimerCheck(int maxRepeat, int delay_Timer, int period_Timer, OnCheckInTimeListener onCheckInTimeListener)
    {
        this.maxRepeat = maxRepeat;
        this.delay_Timer = delay_Timer;
        this.period_Timer = period_Timer;
        this.onCheckInTimeListener = onCheckInTimeListener;

        Log.i("CheckTimer", "" + maxRepeat + " " + delay_Timer + " " + period_Timer);
        parameterExceptionCheck();
        onCheck();
        startTimerSchedule();
    }

    private void parameterExceptionCheck()
    {
        if( maxRepeat < UNLIMITED_REPEAT)
            throw new NumberFormatException("maxRepeat value is over than -1, maxRepeat = " + maxRepeat);
        else if( delay_Timer < 0)
            throw new NumberFormatException("delay_Timer value is over than -1, delay_Timer = " + delay_Timer);
        else if( period_Timer < 0)
            throw new NumberFormatException("period_Timer value is over than -1, period_Timer = " + period_Timer);
    }

    /**
     * only once
     * only 1000ms == 1sec
     * @param delay_Timer  ms   >   -1
     */
    public CustomTimerCheck(int delay_Timer, OnCheckInTimeListener onCheckInTimeListener)
    {
        this.maxRepeat = 1;
        this.delay_Timer = delay_Timer;
        this.period_Timer = 0;
        this.onCheckInTimeListener = onCheckInTimeListener;
        Log.i("CheckTimer", "" + maxRepeat + " " + delay_Timer + " " + period_Timer);
        parameterExceptionCheck();
        onCheck();
        startTimerSchedule();
    }

    public long getCheckCnt() {
        return checkCnt;
    }

    public void setCheckCnt(int checkCnt) {
        this.checkCnt = checkCnt;
    }

    private void startTimerSchedule()
    {
        isStarting = true;

        if (period_Timer == 0)
            mTimer.schedule(mTask, delay_Timer);
        else
            mTimer.schedule(mTask, delay_Timer, period_Timer);
    }

    public void setTimerCancel()
    {
        if (mTimer != null)
            mTimer.cancel();

        if (mTask != null)
            mTask.cancel();

        mTimer = null;
        mTask = null;
        mHandler = null;

        isStarting = false;
        System.gc();
    }

    public boolean getMaxcCeckCnt()
    {
        if (maxRepeat == UNLIMITED_REPEAT)
            return false;
        if (checkCnt >= maxRepeat)
            return true;
        return false;
    }

    public boolean isStarting() {
        return isStarting;
    }

    private void onCheck()
    {
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
                            onCheckInTimeListener.onCheckInTask(checkCnt + 1);

                            if (maxRepeat != UNLIMITED_REPEAT) {
                                checkCnt++;
                                if (checkCnt >= maxRepeat) {
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
