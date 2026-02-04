package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * ENCODER CALIBRATION TOOL
 *
 * How to use:
 * 1. Place robot at a marked starting line
 * 2. Press START - robot drives forward for a few seconds
 * 3. Robot stops automatically
 * 4. Measure actual distance traveled with tape measure
 * 5. Divide "Average Counts" by your measured distance
 * 6. That's your COUNTS_PER_INCH value!
 */
@TeleOp(name = "Encoder Calibration 2", group = "Calibration")
public class EncoderCalibration2 extends LinearOpMode {

    // How long to drive (seconds)
    private static final double DRIVE_TIME = 3.0;

    // How fast to drive (keep low for accuracy)
    private static final double DRIVE_POWER = 0.3;

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

        // Set motor directions
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Reset all encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Run with encoder
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Ready");
        telemetry.addData("Instructions", "Robot will drive for %.1f seconds", DRIVE_TIME);
        telemetry.addData("", "Mark starting position!");
        telemetry.update();

        waitForStart();

        // === PHASE 1: DRIVE ===
        double startTime = getRuntime();

        while (opModeIsActive() && (getRuntime() - startTime) < DRIVE_TIME) {
            // Drive forward
            frontLeft.setPower(DRIVE_POWER);
            frontRight.setPower(DRIVE_POWER);
            backLeft.setPower(DRIVE_POWER);
            backRight.setPower(DRIVE_POWER);

            // Show live data
            telemetry.addData("Status", "DRIVING... %.1f sec left",
                    DRIVE_TIME - (getRuntime() - startTime));
            telemetry.addData("FL", frontLeft.getCurrentPosition());
            telemetry.addData("FR", frontRight.getCurrentPosition());
            telemetry.addData("BL", backLeft.getCurrentPosition());
            telemetry.addData("BR", backRight.getCurrentPosition());
            telemetry.update();
        }

        // Stop motors
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        // === PHASE 2: DISPLAY RESULTS ===
        int flCounts = Math.abs(frontLeft.getCurrentPosition());
        int frCounts = Math.abs(frontRight.getCurrentPosition());
        int blCounts = Math.abs(backLeft.getCurrentPosition());
        int brCounts = Math.abs(backRight.getCurrentPosition());
        double avgCounts = (flCounts + frCounts + blCounts + brCounts) / 4.0;

        while (opModeIsActive()) {
            telemetry.addData("=== DONE ===", "");
            telemetry.addData("", "");
            telemetry.addData("Front Left", flCounts);
            telemetry.addData("Front Right", frCounts);
            telemetry.addData("Back Left", blCounts);
            telemetry.addData("Back Right", brCounts);
            telemetry.addData("", "");
            telemetry.addData(">>> AVERAGE COUNTS <<<", "%.0f", avgCounts);
            telemetry.addData("", "");
            telemetry.addData("=== NOW MEASURE ===", "");
            telemetry.addData("1.", "Measure distance with tape");
            telemetry.addData("2.", "COUNTS_PER_INCH = %.0f / distance", avgCounts);
            telemetry.addData("", "");
            telemetry.addData("Example:", "If robot moved 24 inches:");
            telemetry.addData("COUNTS_PER_INCH =", "%.0f / 24 = %.2f", avgCounts, avgCounts / 24.0);
            telemetry.update();
        }
    }
}