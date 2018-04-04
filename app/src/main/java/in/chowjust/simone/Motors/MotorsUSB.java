package in.chowjust.simone.Motors;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import com.felhr.usbserial.UsbSerialDevice;

import java.util.Locale;

/**
 * Created by Justin on 2018-02-10.
 */


// Motors API for Jeremy
public class MotorsUSB extends Motors {
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    private UsbSerialDevice serialPort;

    public MotorsUSB(int sampleRate, UsbSerialDevice serialPort) {
        super(sampleRate);
        this.serialPort = serialPort;

        setMotorDuty(MOTOR_1, 0);
        setMotorDuty(MOTOR_2, 0);
        setMotorDuty(MOTOR_3, 0);
        setMotorDuty(MOTOR_4, 0);
    }

    // Expects duty to be a double and passed as a percent
    public void setMotorDuty(int motor, double duty) {
        // Map a 0-100% duty cycle range to our full scale range
        double mapped_duty = getMappedDuty(duty);

        if (motor < MOTOR_1 || motor > MOTOR_4) {
            throw new IndexOutOfBoundsException("Motor number must be between 0-3");
        }

        serialPort.write(String.format(Locale.CANADA, "<%d,%f>", motor, mapped_duty/100).getBytes());
    }
}
