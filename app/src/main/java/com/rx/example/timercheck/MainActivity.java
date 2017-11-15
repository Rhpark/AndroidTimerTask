package com.rx.example.timercheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();

    private CustomTimerCheck timerCheckUnLimited, timerCheckOnlyOnce, timerCheckNormal1,timerCheckNormal2;

    private Thread threadOnlyOnce;

    private CustomThreads customUiThreadInThreadSleep, customUiThreadInThread,customThread;

    private final String Timer_Nomal_1                      = "Timer normal1";
    private final String Timer_Nomal_2                      = "Timer normal2";
    private final String Timer_Unlimited                    = "Timer Unlimited";
    private final String Timer_Only_once_call               = "Timer Only once call";
    private final String UiThread_Is_In_Thread_Sleep        = "UiThread is in Thread Sleep";
    private final String Custom_UiThread_Is_In_Thread_Sleep = "Custom UiThread is in Thread Sleep";
    private final String Custom_UiThread_Is_In_Thread       = "Custom UiThread is in Thread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv1 = (TextView) findViewById(R.id.tv1);
        final TextView tv2 = (TextView) findViewById(R.id.tv2);
        final TextView tv3 = (TextView) findViewById(R.id.tv3);
        final TextView tv4 = (TextView) findViewById(R.id.tv4);
        final TextView tv5 = (TextView) findViewById(R.id.tv5);
        final TextView tv6 = (TextView) findViewById(R.id.tv6);
        final TextView tv7 = (TextView) findViewById(R.id.tv7);

        tv1.setText(Timer_Nomal_1);
        tv2.setText(Timer_Nomal_2);
        tv3.setText(Timer_Unlimited);
        tv4.setText(Timer_Only_once_call);
        tv5.setText(UiThread_Is_In_Thread_Sleep);
        tv6.setText(Custom_UiThread_Is_In_Thread_Sleep);
        tv7.setText(Custom_UiThread_Is_In_Thread);

        timerCheckNormal1 = startNormalTimerChecker(tv1,10,1000,1000);
        timerCheckNormal2 = startNormalTimerChecker(tv2,100,1000,100);

        startThreadType(tv5, tv6, tv7);

        startTimerCheckOnlyOnce(tv4);

        //UnLimitedRepeatTimer
        timerCheckUnLimited = new CustomTimerCheck(CustomTimerCheck.UNLIMITED_REPEAT, 1000, 500, new CustomTimerCheck.OnCheckInTimeListener() {
            @Override
            public void onCheckInTask(long checkCnt) {
                tv3.setText("UNLIMITED_REPEAT\n" + "Repeat : " + timerCheckUnLimited.maxRepeat + " periodTime : "
                        + timerCheckUnLimited.period_Timer + " System.CurrentTimerMills " + System.currentTimeMillis());
            }

            @Override
            public void callFinish() {
                timerCheckUnLimited = null;
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                timerCheckUnLimited = null;
            }
        });
    }

    private void startThreadType(final TextView tv5, final TextView tv6, final TextView tv7)
    {
        //Normally code
        threadOnlyOnce = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG,"Normally Thread onCheckInSleepBefore");

                try{
                    Thread.sleep(5000);

                    Log.d(TAG,"Normally Thread onCheckIn");

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d(TAG,"Normally Thread UiThread");

                            tv6.setText(UiThread_Is_In_Thread_Sleep + " Call End");
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    threadOnlyOnce = null;
                }
            }
        });
        threadOnlyOnce.start();

        //customThread
        customUiThreadInThreadSleep = new CustomThreads(this, 2000, new CustomThreads.OnThreadAndUIThreadSleepListener() {
            @Override
            public void onCheckInSleepBefore() {
                Log.d(TAG,"CustomThread in uiThreadSleep onCheckInSleepBefore");
            }

            @Override
            public void onCheckInThread() {
                Log.d(TAG,"CustomThread in uiThreadSleep onCheckInThread");
            }

            @Override
            public void callFinishUiThread() {
                tv5.setText(Custom_UiThread_Is_In_Thread_Sleep + " Ui Changed -");
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG,"Thread in uiThreadSleep onError");
                Log.e(TAG,"3 " + e.getMessage());
            }
        });

        customUiThreadInThread = new CustomThreads(this, new CustomThreads.OnThreadAndUIThreadListener() {
            @Override
            public void onCheckInThread() {
                Log.d(TAG,"Thread in uiThread onCheckInThread");
            }

            @Override
            public void callFinishUiThread() {
                Log.d(TAG,"Thread in uiThread callFinishUiThread");
                tv7.setText(Custom_UiThread_Is_In_Thread + " Ui Changed -");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG,"Thread in uiThread onError " + e.getMessage());
            }
        });

        customThread = new CustomThreads(2000, new CustomThreads.OnThreadAndSleepListener() {
            @Override
            public void onCheckInSleepBefore() {
                Log.d(TAG, "CustomThread in onCheckInSleepBefore");
            }

            @Override
            public void onCheckInThread() {
                Log.d(TAG, "CustomThread in onCheckInThread");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "CustomThread in onError " + e.getMessage());
            }
        });
    }

    private void startTimerCheckOnlyOnce(final TextView tv4)
    {
        //OnlyOnceRepeatTimer
        timerCheckOnlyOnce = new CustomTimerCheck(5000, new CustomTimerCheck.OnCheckInTimeListener() {
            @Override
            public void onCheckInTask(long checkCnt) {
                tv4.setText("Call only once, count " + checkCnt + " Ui Changed -");
            }

            @Override
            public void callFinish() {
                tv4.setText(tv4.getText().toString() + " finish Ui Changed -");
                Toast.makeText(MainActivity.this,tv4.getText().toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {      }
        });
    }

    private CustomTimerCheck startNormalTimerChecker(final TextView tv, final int maxRepeat, final int delay_Timer, final int period_Timer)
    {
        return new CustomTimerCheck(maxRepeat, delay_Timer, period_Timer, new CustomTimerCheck.OnCheckInTimeListener() {
            @Override
            public void onCheckInTask(long checkCnt) {
                tv.setText("Repeat : " + maxRepeat + " periodTime : " +period_Timer + "ms, Count : " + checkCnt);
            }

            @Override
            public void callFinish() {
                tv.setText(tv.getText().toString() + " Finish Ui Changed - ");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( threadOnlyOnce != null) threadOnlyOnce.interrupt();

        threadOnlyOnce = null;
        destroyTimerCheck(timerCheckOnlyOnce);
        destroyTimerCheck(timerCheckNormal1);
        destroyTimerCheck(timerCheckNormal2);
        destroyTimerCheck(timerCheckUnLimited);

        customUiThreadInThreadSleep.onDestory();
        customUiThreadInThreadSleep = null;
        customUiThreadInThread.onDestory();
        customUiThreadInThread = null;
        customThread.onDestory();
        customThread = null;
    }

    private void destroyTimerCheck(CustomTimerCheck t){
        if ( t!= null)
        {
            if ( t.isStarting())
                t.setTimerCancel();
            t = null;
        }
    }
}
