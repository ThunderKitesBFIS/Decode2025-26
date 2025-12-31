package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class BasicActions {
    private final LinearOpMode opMode;
    private final DcMotor left;
    private final DcMotor right;

    public Actions(LinearOpMode opMode, DcMotor left, DcMotor right) {
        this.opMode = opMode;
        this.left = left;
        this.right = right;
    }

    public void driveforward(double power, long ms) {
        motorLeft.setPower(power);
        motorRight.setPower(power);
        sleep(ms);
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }

    public void turnleft(double power, long ms) {
        motorLeft.setPower(-power);
        motorRight.setPower(power);
        sleep(ms);
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }
    public void turnright(double power, long ms) {
        motorLeft.setPower(power);
        motorRight.setPower(-power);
        sleep(ms);
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }
    public void stop(long ms) {
        motorLeft.setPower(0);
        motorRight.setPower(0);
        sleep(ms);
    }
}
