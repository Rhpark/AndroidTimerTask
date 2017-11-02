package com.rx.example.timercheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TimerCheck timerCheckUnLimite;
    private long timerALong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv1 = (TextView) findViewById(R.id.tv1);
        final TextView tv2 = (TextView) findViewById(R.id.tv2);
        final TextView tv3 = (TextView) findViewById(R.id.tv3);

        startNomalTimerChecker(tv1,10,1000,1000);
        startNomalTimerChecker(tv2,100,1000,100);

        tv1.setText("Start");
        tv2.setText("Start");
        tv3.setText("Start");

        timerCheckUnLimite = new TimerCheck(TimerCheck.UNLIMITE_REPEATE, 1000, 500, new TimerCheck.OnCheckInTimeListener() {
            @Override
            public void onCheckInTask(long checkCnt) {
                tv3.setText("UNLIMITE_REPEATE\n" + "Repeat : " + timerCheckUnLimite.maxRepeat + " periodTime : "
                        + timerCheckUnLimite.period_Timer + " System.CurrentTimerMills " + System.currentTimeMillis());
            }

            @Override
            public void callFinish() {
                timerCheckUnLimite = null;
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                timerCheckUnLimite = null;
            }
        });
    }

    private void startNomalTimerChecker(final TextView tv, final int maxRepeat, final int delay_Timer, final int period_Timer)
    {
        new TimerCheck(maxRepeat, delay_Timer, period_Timer, new TimerCheck.OnCheckInTimeListener() {
            @Override
            public void onCheckInTask(long checkCnt) {
                tv.setText("Repeat : " + maxRepeat + " periodTime : " +period_Timer + "ms, Count : " + checkCnt);
            }

            @Override
            public void callFinish() {
                tv.setText(tv.getText().toString() + " 완료");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
