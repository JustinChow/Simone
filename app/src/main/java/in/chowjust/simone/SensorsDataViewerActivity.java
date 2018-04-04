package in.chowjust.simone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class SensorsDataViewerActivity extends AppCompatActivity {
    private SensorManager mSensorManager;

    //TextViews to display whether or not sensor is available
    TextView tvMagnetometer;
    TextView tvAccelerometer;
    TextView tvGyroscope;
    TextView tvGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_data_viewer);

        // Initialize sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        // Get list of all sensors
//        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        tvMagnetometer = (TextView) findViewById(R.id.tvMagnetometerAvailable);
        tvAccelerometer = (TextView) findViewById(R.id.tvAccelerometerAvailable);
        tvGyroscope = (TextView) findViewById(R.id.tvGyroscopeAvailable);
        tvGPS = (TextView) findViewById(R.id.tvGPSAvailable);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // Accelerometer available
            tvMagnetometer.setText("YES");
            tvMagnetometer.setTextColor(Color.GREEN);
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // Accelerometer available
            tvAccelerometer.setText("YES");
            tvAccelerometer.setTextColor(Color.GREEN);
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // Accelerometer available
            tvGyroscope.setText("YES");
            tvGyroscope.setTextColor(Color.GREEN);
        }

        PackageManager packageManager = getApplicationContext().getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if (hasGPS) {
            // Accelerometer available
            tvGPS.setText("YES");
            tvGPS.setTextColor(Color.GREEN);
        }
    }

}
