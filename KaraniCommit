package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous(name = "Auto2", group = "Autonomous")
public class Auto2 extends LinearOpMode {

    // ============================================================
    // CONFIGURATION
    // ============================================================

    private static final double DRIVE_SPEED = 0.4;
    private static final double TURN_SPEED = 0.3;
    private static final double COUNTS_PER_INCH = 35.7;  // Calibrate this!
    private static final double TURN_GAIN = 0.02;

    private static final int SELECTED_PATH = 0;  // 0, 1, 2, or 3

    // ============================================================
    // HARDWARE
    // ============================================================

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    // ============================================================
    // MAIN
    // ============================================================

    @Override
    public void runOpMode() {
        initHardware();

        telemetry.addData("Status", "Ready - Path " + SELECTED_PATH);
        telemetry.update();

        waitForStart();
        imu.resetYaw();

        switch (SELECTED_PATH) {
            case 0: path0(); break;
            case 1: path1(); break;
            case 2: path2(); break;
            case 3: path3(); break;
        }

        telemetry.addData("Status", "Done");
        telemetry.update();
    }

    // ============================================================
    // PATHS
    // ============================================================

    private void path0() {
        driveForward(24);
        rotateDelta(90);
        driveForward(12);
    }

    private void path1() {
        driveForward(10);
    }

    private void path2() {
        driveForward(10);
    }

    private void path3() {
        driveForward(10);
    }

    // ============================================================
    // MOVEMENT
    // ============================================================

    public void driveForward(double inches) {
        drive(inches);
    }

    public void driveBackward(double inches) {
        drive(-inches);
    }

    private void drive(double inches) {
        if (!opModeIsActive()) return;

        int target = (int)(inches * COUNTS_PER_INCH);

        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + target);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + target);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + target);
        backRight.setTargetPosition(backRight.getCurrentPosition() + target);

        setAllMode(DcMotor.RunMode.RUN_TO_POSITION);
        setAllPower(DRIVE_SPEED);

        while (opModeIsActive() && frontLeft.isBusy()) {
            telemetry.addData("Driving", "%.1f in", inches);
            telemetry.update();
        }

        setAllPower(0);
        setAllMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sleep(100);
    }

    /**
     * Rotate by degrees. Positive = right, Negative = left.
     */
    public void rotateDelta(double degrees) {
        if (!opModeIsActive()) return;

        double target = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) - degrees;

        while (opModeIsActive()) {
            double current = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double error = normalizeAngle(target - current);

            if (Math.abs(error) < 2.0) break;

            double power = Range.clip(error * TURN_GAIN, -TURN_SPEED, TURN_SPEED);
            if (Math.abs(power) < 0.1) power = 0.1 * Math.signum(error);

            frontLeft.setPower(-power);
            backLeft.setPower(-power);
            frontRight.setPower(power);
            backRight.setPower(power);

            telemetry.addData("Rotating", "%.1f°", degrees);
            telemetry.update();
        }

        setAllPower(0);
        sleep(100);
    }

    // ============================================================
    // INIT & HELPERS
    // ============================================================

    private void initHardware() {
        frontLeft = hardwareMap.get(DcMotor.class, "leftFront");
        frontRight = hardwareMap.get(DcMotor.class, "rightFront");
        backLeft = hardwareMap.get(DcMotor.class, "leftBack");
        backRight = hardwareMap.get(DcMotor.class, "rightBack");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        setAllMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setAllMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        )));
    }

    private void setAllPower(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    private void setAllMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle <= -180) angle += 360;
        return angle;
    }
}
