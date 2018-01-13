package com.cibc.hexclock;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class MainActivity extends AppCompatActivity {
    Calendar c;
    int hrsInt;
    int minInt;
    int secInt;
    String hrs;
    String min;
    String sec;
    int alarmHrsInt;
    int alarmMinInt;
    int alarmSecInt;
    Boolean alarmEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Begin updating the time
        updateTime();
    }

    /**
     * Updates the time each second
     */
    public void updateTime() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                c = Calendar.getInstance();
                hrsInt = c.get(Calendar.HOUR_OF_DAY);
                minInt = c.get(Calendar.MINUTE);
                secInt = c.get(Calendar.SECOND);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hrs = getDualDigits(hrsInt);
                        min = getDualDigits(minInt);
                        sec = getDualDigits(secInt);

                        hideStatusBar();

                        String clockText = hrs + ':' + min + ':' + sec;

                        checkAlarm(clockText);

                        ((TextView) findViewById(R.id.time)).setText(clockText);
                        ((RelativeLayout) ((TextView) findViewById(R.id.time)).getParent()).setBackgroundColor(Color.rgb(hrsInt, minInt, secInt));
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Pads zeros to make input a two-digit string
     *
     * @param int value
     * @return String
     */
    private String getDualDigits(Integer value) {
        if (value < 10) {
            return '0' + String.valueOf(value);
        }

        return String.valueOf(value);
    }

    /**
     * Check that the current time is equal to the alarm time
     *
     * @param String clockText
     */
    private void checkAlarm(String clockText) {
        if ((hrsInt == alarmHrsInt) && (minInt == alarmMinInt) && (secInt == alarmSecInt) && alarmEnabled) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Your Alarm")
                    .setMessage("It is " + clockText)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alarmEnabled = false;
                        }
                    }).create().show();
            findViewById(R.id.disableAlarm).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set an alarm
     *
     * @param View v
     */
    public void setAlarm(View v) {
        final ColorPicker cp = new ColorPicker(MainActivity.this, alarmHrsInt, alarmMinInt, alarmSecInt);

        // Show the colorPicker
        cp.show();

        // Add an onClick listener for the button
        Button okColor = (Button) cp.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmEnabled = true;
                checkAlarmInput(Integer.toHexString(cp.getRed()), Integer.toHexString(cp.getGreen()), Integer.toHexString(cp.getBlue()));

                cp.dismiss();
            }
        });
    }

    /**
     * Check alarm input to verify it can actually occur as a time
     *
     * @param String h
     * @param String m
     * @param String s
     */
    private void checkAlarmInput(String h, String m, String s) {
        try {
            alarmHrsInt = Integer.valueOf(h);
            alarmMinInt = Integer.valueOf(m);
            alarmSecInt = Integer.valueOf(s);
            findViewById(R.id.disableAlarm).setVisibility(View.VISIBLE);
        } catch (NumberFormatException e) {
            findViewById(R.id.disableAlarm).setVisibility(View.INVISIBLE);
            alarmEnabled = false;
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Your Alarm")
                    .setMessage("Your alarm value is invalid. The hex value of the alarm must be an integer.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alarmEnabled = false;
                        }
                    }).create().show();
        }
    }

    public void cancelAlarm(View v) {
        alarmEnabled = false;
        findViewById(R.id.disableAlarm).setVisibility(View.INVISIBLE);
    }

    public void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
