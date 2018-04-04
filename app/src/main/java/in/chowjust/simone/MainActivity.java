package in.chowjust.simone;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btnViewSensors;
    Button btnInitializeMotors;
    Button btnCreateHotspot;
    Button btnArduinoConnect;
    Button btnArduinoDisconnect;
    Button btnRemoteControl;
    TextView tvMessage;

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbManager usbManager;
    public static UsbSerialDevice serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate the button in activity_main.xml
        btnViewSensors = (Button) findViewById(R.id.btnSensorStatusViewer);
        btnInitializeMotors = (Button) findViewById(R.id.btnInitializeMotors);
        btnCreateHotspot = (Button) findViewById(R.id.btnHotSpot);
        btnArduinoConnect = (Button) findViewById(R.id.btnArduinoConnect);
        btnArduinoDisconnect = (Button) findViewById(R.id.btnArduinoDisconnect);
        btnRemoteControl = (Button) findViewById(R.id.btnRemoteControl);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(broadcastReceiver, filter);

        // Set button listener for Sensors Status Viewer button
        btnViewSensors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        SensorsDataViewerActivity.class);
                startActivity(myIntent);
            }
        });

        // Set button listener for Initialize Motors button
        btnInitializeMotors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        InitializeMotorsActivity.class);
                startActivity(myIntent);
            }
        });

        // Set button listener for Create Hotspot button
        btnCreateHotspot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent);
            }
        });

        // Set button listener for Remote Control button
        btnRemoteControl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.github.jeremyroy.simone.phone_imu",
                        "com.github.jeremyroy.simone.phone_imu.MainActivity"));
                startActivity(intent);
            }
        });

    }

    public void onClickArduinoConnect(View view) {
        usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                            new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    device = null;
                    connection = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    public void onClickArduinoDisconnect(View view) {
        serialPort.close();
    }


//    public void openHotspotSettings(View view) {
//        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
//        intent.setComponent(cn);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity( intent);
//    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted =
                        intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    UsbDeviceConnection connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    Log.d("SERIAL", connection.toString());
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            //setUiEnabled(true); //Enable Buttons in UI
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback); //
                            tvAppend(tvMessage,"Serial Connection Opened!");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickArduinoConnect(btnArduinoConnect);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickArduinoDisconnect(btnArduinoDisconnect);
            }
        };
    };

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {}
    };

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        runOnUiThread(new Runnable() {
            @Override public void run() {
                ftv.setText(ftext);
            }});

        runOnUiThread(new Runnable() {
            @Override public void run() {
                ftv.setText(ftext);
            }});

    }
}