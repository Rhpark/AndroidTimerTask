package com.rx.example.timercheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TimerCheck timerCheckUnLimited, timerCheckOnlyOnce, timerCheckNormal1,timerCheckNormal2;

    private Thread threadOnlyOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv1 = (TextView) findViewById(R.id.tv1);
        final TextView tv2 = (TextView) findViewById(R.id.tv2);
        final TextView tv3 = (TextView) findViewById(R.id.tv3);
        final TextView tv4 = (TextView) findViewById(R.id.tv4);
        final TextView tv5 = (TextView) findViewById(R.id.tv5);

        timerCheckNormal1 = startNormalTimerChecker(tv1,10,1000,1000);
        timerCheckNormal2 = startNormalTimerChecker(tv2,100,1000,100);

        tv1.setText("Timer normal1");
        tv2.setText("Timer normal2");
        tv3.setText("Timer unlimited");
        tv4.setText("Timer Only once");
        tv5.setText("Thread Type Only one");

        startThreadOnlyOnce(tv5);

        startTimerCheckOnlyOnce(tv4);

        //UnLimitedRepeatTimer
        timerCheckUnLimited = new TimerCheck(TimerCheck.UNLIMITED_REPEAT, 1000, 500, new TimerCheck.OnCheckInTimeListener() {
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

    private void startThreadOnlyOnce(final TextView tv5)
    {
        //OnlyOnceRepeatThread
        threadOnlyOnce = new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    Thread.sleep(5000);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            tv5.setTextSize(20);
                            tv5.setText("Thread runOnUiThread");
                            Toast.makeText(MainActivity.this,"test",Toast.LENGTH_SHORT).show();
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
    }


    private void startTimerCheckOnlyOnce(final TextView tv4)
    {
        //OnlyOnceRepeatTimer
        timerCheckOnlyOnce = new TimerCheck(5000, new TimerCheck.OnCheckInTimeListener() {
            @Override
            public void onCheckInTask(long checkCnt) {
                tv4.setText("Call only once, count " + checkCnt);
            }

            @Override
            public void callFinish() {
                tv4.setText(tv4.getText().toString() + " finish");
                Toast.makeText(MainActivity.this,tv4.getText().toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {      }
        });
    }

    private TimerCheck startNormalTimerChecker(final TextView tv, final int maxRepeat, final int delay_Timer, final int period_Timer)
    {
        return new TimerCheck(maxRepeat, delay_Timer, period_Timer, new TimerCheck.OnCheckInTimeListener() {
            @Override
            public void onCheckInTask(long checkCnt) {
                tv.setText("Repeat : " + maxRepeat + " periodTime : " +period_Timer + "ms, Count : " + checkCnt);
            }

            @Override
            public void callFinish() {
                tv.setText(tv.getText().toString() + " Finish");
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
    }

    private void destroyTimerCheck(TimerCheck t){
        if ( t!= null)
        {
            if ( t.isStarting())
                t.setTimerCancel();
            t = null;
        }
    }
}
