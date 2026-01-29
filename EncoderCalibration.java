package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * ENCODER CALIBRATION TOOL
 *
 * How to use:
 * 1. Place your robot at a starting point on the field
 * 2. Mark the starting position (use tape or a field line)
 * 3. Run this OpMode
 * 4. Manually push the robot EXACTLY 24 inches forward
 * 5. Read the "Counts per Inch" value from telemetry
 * 6. Use that value in your SimpleAutonomous COUNTS_PER_INCH constant
 *
 * For best results:
 * - Push slowly and steadily
 * - Keep the robot going straight
 * - Do this 3 times and average the results
 */
@TeleOp(name = "Encoder Calibration", group = "Calibration")
public class EncoderCalibration extends LinearOpMode {

    // Distance you will push the robot (in inches)
    private static final double CALIBRATION_DISTANCE = 24.0;

    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    @Override
    public void runOpMode() {

        // Initialize motors
        frontLeft = hardwareMap.get(DcMotor.class, "front_left");
        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        backLeft = hardwareMap.get(DcMotor.class, "back_left");
        backRight = hardwareMap.get(DcMotor.class, "back_right");

        // Set motors to FLOAT - wheels spin freely with no resistance
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Reset all encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set to run without encoder (so we can push freely)
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Ensure zero power (motors completely off)
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        telemetry.addData("Status", "Ready for calibration");
        telemetry.addData("Instructions", "Push robot %.0f inches after START", CALIBRATION_DISTANCE);
        telemetry.update();

        waitForStart();

        telemetry.clear();
        telemetry.addData("Status", "PUSH THE ROBOT %.0f INCHES NOW", CALIBRATION_DISTANCE);
        telemetry.update();

        while (opModeIsActive()) {
            // Read encoder values
            int flCounts = Math.abs(frontLeft.getCurrentPosition());
            int frCounts = Math.abs(frontRight.getCurrentPosition());
            int blCounts = Math.abs(backLeft.getCurrentPosition());
            int brCounts = Math.abs(backRight.getCurrentPosition());

            // Calculate average
            double avgCounts = (flCounts + frCounts + blCounts + brCounts) / 4.0;

            // Calculate counts per inch
            double countsPerInch = avgCounts / CALIBRATION_DISTANCE;

            // Display results
            telemetry.addData("=== ENCODER COUNTS ===", "");
            telemetry.addData("Front Left", flCounts);
            telemetry.addData("Front Right", frCounts);
            telemetry.addData("Back Left", blCounts);
            telemetry.addData("Back Right", brCounts);
            telemetry.addData("", "");
            telemetry.addData("=== CALIBRATION RESULT ===", "");
            telemetry.addData("Average Counts", "%.1f", avgCounts);
            telemetry.addData("Distance", "%.0f inches", CALIBRATION_DISTANCE);
            telemetry.addData("", "");
            telemetry.addData(">>> COUNTS PER INCH <<<", "%.2f", countsPerInch);
            telemetry.addData("", "");
            telemetry.addData("Instructions", "Use this value in SimpleAutonomous");
            telemetry.update();
        }
    }
}