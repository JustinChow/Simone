package in.chowjust.simone.Motors;

/**
 * Created by Justin on 2018-02-10.
 */
public abstract class Motors {
    public static final int MOTOR_1 = 0;
    public static final int MOTOR_2 = 1;
    public static final int MOTOR_3 = 2;
    public static final int MOTOR_4 = 3;

    // Enabled flag
    boolean enabled;

    // Parameters for mapping motor duty cycles
    private int upper_bound = 10;
    private int lower_bound = 5;

    int resolution = 2; //Number of decimal places to keep for duty cycle, which is stored as a percent

    // Sample rate
    int sampleRate;

    Motors(int sampleRate) {
        this.sampleRate = sampleRate;
        enabled = false;
    }

    // Takes a percentage from 0-100% and maps it to our full scale output
    double getMappedDuty(double duty) {
        double mapped_duty = duty / 100.0 * (upper_bound - lower_bound) + lower_bound;
        return Math.floor(mapped_duty * Math.pow(10, resolution)) / Math.pow(10, resolution);
    }

    public abstract void setMotorDuty(int motor, double duty);

    public void pause_motors() {
        setMotorDuty(MOTOR_1, 0);
        setMotorDuty(MOTOR_2, 0);
        setMotorDuty(MOTOR_3, 0);
        setMotorDuty(MOTOR_4, 0);
        enabled = false;
    }

    public void resume_motors() {
        enabled = true;
    }

    public boolean is_Enabled() {
        return enabled;
    }
}