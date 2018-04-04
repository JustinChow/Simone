package in.chowjust.simone.Motors;

/**
 * Created by Justin on 2018-02-10.
 */

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.View;

// Motors API for Jeremy
public class MotorsAudio extends Motors {

    private Audio audio = null;

    private double duty0 = 0;
    private double duty1 = 0;
    private double duty2 = 0;
    private double duty3 = 0;

    public MotorsAudio(int sampleRate) {
        super(sampleRate);
        audio = new Audio();

        setMotorDuty(MOTOR_1, 0);
        setMotorDuty(MOTOR_2, 0);
        setMotorDuty(MOTOR_3, 0);
        setMotorDuty(MOTOR_4, 0);
    }

    // Expects duty to be a double and passed as a percent
    public void setMotorDuty(int motor, double duty) {
        // Map a 0-100% duty cycle range to our full scale range
        double mapped_duty = getMappedDuty(duty);

        switch (motor) {
            case MOTOR_1:
                duty0 = mapped_duty;
                break;
            case MOTOR_2:
                duty1 = mapped_duty;
                break;
            case MOTOR_3:
                duty2 = mapped_duty;
                break;
            case MOTOR_4:
                duty3 = mapped_duty;
                break;
            default:
                throw new IndexOutOfBoundsException("Motor number must be between 0-3");
        }


    }

    public void pause_motors() {
        super.pause_motors();
        audio.stop();
    }

    public void resume_motors() {
        super.resume_motors();
        audio.start();
    }

    private class Audio implements Runnable {
        private AudioTrack at = null;
        private Thread thread = null;

        private int buff_size = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        private int freq = 50;

        // Amplitude percentages for first and second motor on each channel
        private int amplitude1 = (int)(8 / 100.0 * 32767);
        private int amplitude2 = (int)(2 / 100.0 * 32767);

        public void run() {
            processAudio();
        }

        private void start() {
            if (thread == null) {
                thread = new Thread(this, "Audio");
                thread.start();
                thread.setPriority(Thread.MAX_PRIORITY);
            }
        }

        private void stop()
        {
            Thread t = thread;
            thread = null;

            // Wait for the thread to exit
            while (t != null && t.isAlive())
                Thread.yield();
        }

        void processAudio() {
            at = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    buff_size,
                    AudioTrack.MODE_STREAM);

            double K = 2.0 * Math.PI / sampleRate;
            double phaseL = 0;
            double phaseR = 0;

            at.play();

            while (thread != null) {
                short samples[] = new short[buff_size];
                for (int i = 0; i < buff_size; i++) {
                    // Left sample
                    if (i % 2 == 0) {
                        // Motor 1
                        if (phaseL < Math.PI) {
                            double cut_off_phase = duty0 / 100.0 * 2.0 * Math.PI;
                            samples[i] = (phaseL > cut_off_phase) ? (short) 0 : (short) amplitude1;
                        }
                        // Motor 2
                        else {
                            // For motor 2, the cut off phase is offset by PI since the second
                            // half of each period is reserved for motor 2, while the first half of
                            // each period is reserved for motor 1. Note that this limits the duty
                            // cycle to 50%.
                            double cut_off_phase = (duty1 / 100.0 * 2.0 * Math.PI) + Math.PI;
                            samples[i] = (phaseL > cut_off_phase) ? (short) 0 : (short) amplitude2;
                        }

                        phaseL += (phaseL < 2.0 * Math.PI) ? freq * K : (freq * K) - (2.0 * Math.PI);
                    }
                    // Right sample
                    else {
                        // Motor 3
                        if (phaseR < Math.PI) {
                            double cut_off_phase = duty2 / 100.0 * 2.0 * Math.PI;
                            samples[i] = (phaseR > cut_off_phase) ? (short) 0 : (short) amplitude1;
                        }
                        // Motor 4
                        else {
                            // For motor 4, the cut off phase is offset by PI since the second
                            // half of each period is reserved for motor 4, while the first half of
                            // each period is reserved for motor 3. Note that this limits the duty
                            // cycle to 50%.
                            double cut_off_phase = (duty3 / 100.0 * 2.0 * Math.PI) + Math.PI;
                            samples[i] = (phaseR > cut_off_phase) ? (short) 0 : (short) amplitude2;
                        }

                        phaseR += (phaseR < 2.0 * Math.PI) ? freq * K : (freq * K) - (2.0 * Math.PI);
                    }
                }

                at.write(samples, 0, buff_size);
            }

            at.stop();
            at.release();
        }
    }
}
