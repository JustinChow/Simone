package in.chowjust.simone;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.chowjust.simone.Motors.Motors;
import in.chowjust.simone.Motors.MotorsAudio;
import in.chowjust.simone.Motors.MotorsUSB;

public class InitializeMotorsActivity extends AppCompatActivity {
    int initializationStep = 1;
    TextView tv;
    Button btnOK;

    int sampleRate;

    Motors motors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize_motors);

        // Get ideal sample rate from Android
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        String sampleRateStr = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        sampleRate = Integer.parseInt(sampleRateStr);

        motors = new MotorsAudio(sampleRate);
        //motors = new MotorsUSB(sampleRate, MainActivity.serialPort);

        tv = (TextView)findViewById(R.id.tv1);
        btnOK = (Button)findViewById(R.id.btnOK);

        tv.setText("Step 1: Unplug the battery. Click OK to continue.");

        // Set button listener for Create Hotspot button
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                initializationStep++;

                switch (initializationStep) {
                    case 2:
                        tv.setText(String.format("Step %d: Set phone volume to max. Click OK to continue.", initializationStep));
                        break;
                    case 3:
                        tv.setText(String.format("Step %d: Setting throttle to max... please wait.", initializationStep));
                        btnOK.setEnabled(false);
                        motors.setMotorDuty(Motors.MOTOR_1, 100);
                        motors.setMotorDuty(Motors.MOTOR_2, 100);
                        motors.setMotorDuty(Motors.MOTOR_3, 100);
                        motors.setMotorDuty(Motors.MOTOR_4, 100);
                        motors.resume_motors();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(String.format("Step %d: Plug in the battery. Please wait for chirp and then click OK to continue.", initializationStep));
                                btnOK.setEnabled(true);
                            }
                        }, 3000);
                        break;
                    case 4:
                        tv.setText(String.format("Step %d: Setting throttle to min. Please wait for chirp and click OK to continue.", initializationStep));
                        motors.setMotorDuty(Motors.MOTOR_1, 0);
                        motors.setMotorDuty(Motors.MOTOR_2, 0);
                        motors.setMotorDuty(Motors.MOTOR_3, 0);
                        motors.setMotorDuty(Motors.MOTOR_4, 0);
                        break;
                    case 5:
                        tv.setText(String.format("Step %d: Unplug the battery. Click OK to continue.", initializationStep));
                        break;
                    case 6:
                        motors.pause_motors();
                        tv.setText(String.format("Step %d: Motors have been initialized.", initializationStep));
                }
            }
        });

//        Handler handler=new Handler();
//
//        class MyRunnable implements Runnable {
//            private Handler handler;
//            private int i;
//            private TextView textView;
//            public MyRunnable(Handler handler) {
//            }
//            @Override
//            public void run() {
//                this.handler.postDelayed(this, 10);
//                switch (initializationStep) {
//                    case 2:
//                        motors.setMotorDuty(0, 100);
//                        motors.setMotorDuty(1, 100);
//                        motors.setMotorDuty(2, 100);
//                        motors.setMotorDuty(3, 100);
//
//                        tv.setText("Step 2: . \n\nClick OK to continue.");
//                }
//            }
//        }
//        handler.post(new MyRunnable(handler));

    }

}
