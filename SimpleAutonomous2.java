package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@Autonomous(name = "Simple Autonomous", group = "Autonomous")
public class SimpleAutonomous extends LinearOpMode {

    // ============================================================
    // CONFIGURATION VARIABLES - ADJUST THESE FOR YOUR ROBOT
    // ============================================================
    
    // Drive speeds (0.0 to 1.0)
    private static final double DRIVE_SPEED = 0.4;          // Speed when driving forward/backward
    private static final double TURN_SPEED = 0.3;           // Speed when rotating
    
    // Encoder calibration
    // IMPORTANT: Measure this empirically! Push robot 24 inches, divide counts by 24
    private static final double COUNTS_PER_INCH = 35.7;     // Adjust based on your calibration
    
    // Rotation settings
    private static final double HEADING_THRESHOLD = 2.0;    // Degrees of acceptable error
    private static final double TURN_P_GAIN = 0.02;         // Proportional gain for turning
    
    // Timeout safety (seconds)
    private static final double DRIVE_TIMEOUT = 10.0;       // Max time for any drive movement
    private static final double TURN_TIMEOUT = 5.0;         // Max time for any rotation
    
    // ============================================================
    // HARDWARE DECLARATIONS
    // ============================================================
    
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private IMU imu = null;
    
    private ElapsedTime runtime = new ElapsedTime();

    // ============================================================
    // MAIN OPMODE
    // ============================================================
    
    @Override
    public void runOpMode() {
        
        // Initialize hardware
        initializeHardware();
        
        telemetry.addData("Status", "Initialized - Ready to Start");
        telemetry.addData("Heading", "%.1f degrees", getHeading());
        telemetry.update();
        
        // Wait for driver to press START
        waitForStart();
        
        // Reset IMU heading at start
        imu.resetYaw();
        
        // ========================================
        // YOUR AUTONOMOUS SEQUENCE GOES HERE
        // ========================================
        
        // Example sequence - modify for your needs:
        
        driveForward(24);       // Drive forward 24 inches
        
        rotateTo(90);           // Turn to face 90 degrees (left)
        
        driveForward(12);       // Drive forward 12 inches
        
        rotate(-45);            // Rotate 45 degrees clockwise (relative)
        
        driveBackward(10);      // Drive backward 10 inches
        
        rotateTo(0);            // Return to original heading
        
        // ========================================
        // END OF AUTONOMOUS SEQUENCE
        // ========================================
        
        telemetry.addData("Status", "Autonomous Complete");
        telemetry.update();
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================
    
    private void initializeHardware() {
        // Initialize motors
        frontLeft = hardwareMap.get(DcMotor.class, "front_left");
        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        backLeft = hardwareMap.get(DcMotor.class, "back_left");
        backRight = hardwareMap.get(DcMotor.class, "back_right");
        
        // Set motor directions (adjust if robot drives backward)
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        
        // Set motors to brake when power is zero
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        // Reset encoders
        resetEncoders();
        
        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = 
            RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = 
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot = 
            new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }
    
    private void resetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // ============================================================
    // MOVEMENT METHODS
    // ============================================================
    
    /**
     * Drive forward a specified distance in inches.
     * @param inches Distance to drive (positive value)
     */
    public void driveForward(double inches) {
        driveDistance(inches, DRIVE_SPEED);
    }
    
    /**
     * Drive backward a specified distance in inches.
     * @param inches Distance to drive (positive value)
     */
    public void driveBackward(double inches) {
        driveDistance(-inches, DRIVE_SPEED);
    }
    
    /**
     * Internal method to drive a distance (positive = forward, negative = backward)
     */
    private void driveDistance(double inches, double speed) {
        if (!opModeIsActive()) return;
        
        // Calculate target encoder counts
        int targetCounts = (int)(inches * COUNTS_PER_INCH);
        
        // Get current positions and calculate targets
        int frontLeftTarget = frontLeft.getCurrentPosition() + targetCounts;
        int frontRightTarget = frontRight.getCurrentPosition() + targetCounts;
        int backLeftTarget = backLeft.getCurrentPosition() + targetCounts;
        int backRightTarget = backRight.getCurrentPosition() + targetCounts;
        
        // Set target positions
        frontLeft.setTargetPosition(frontLeftTarget);
        frontRight.setTargetPosition(frontRightTarget);
        backLeft.setTargetPosition(backLeftTarget);
        backRight.setTargetPosition(backRightTarget);
        
        // Switch to RUN_TO_POSITION mode
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        // Start moving
        double power = Math.abs(speed);
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
        
        // Wait until movement is complete or timeout
        runtime.reset();
        while (opModeIsActive() && 
               runtime.seconds() < DRIVE_TIMEOUT &&
               (frontLeft.isBusy() || frontRight.isBusy() || 
                backLeft.isBusy() || backRight.isBusy())) {
            
            telemetry.addData("Driving", "%.1f inches", inches);
            telemetry.addData("Target", "%d counts", targetCounts);
            telemetry.addData("Front L/R", "%d / %d", 
                frontLeft.getCurrentPosition(), frontRight.getCurrentPosition());
            telemetry.addData("Back L/R", "%d / %d", 
                backLeft.getCurrentPosition(), backRight.getCurrentPosition());
            telemetry.update();
        }
        
        // Stop all motors
        stopAllMotors();
        
        // Switch back to RUN_USING_ENCODER mode
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        // Small pause to let robot settle
        sleep(100);
    }
    
    /**
     * Rotate to an absolute heading (0 = starting orientation).
     * Uses IMU for accurate rotation.
     * @param targetHeading Target heading in degrees (positive = counter-clockwise)
     */
    public void rotateTo(double targetHeading) {
        if (!opModeIsActive()) return;
        
        runtime.reset();
        
        while (opModeIsActive() && runtime.seconds() < TURN_TIMEOUT) {
            double currentHeading = getHeading();
            double error = normalizeAngle(targetHeading - currentHeading);
            
            // Check if we're close enough
            if (Math.abs(error) <= HEADING_THRESHOLD) {
                break;
            }
            
            // Calculate turn power using proportional control
            double turnPower = error * TURN_P_GAIN;
            turnPower = Range.clip(turnPower, -TURN_SPEED, TURN_SPEED);
            
            // Ensure minimum power to overcome friction
            if (Math.abs(turnPower) < 0.1 && Math.abs(error) > HEADING_THRESHOLD) {
                turnPower = 0.1 * Math.signum(error);
            }
            
            // Apply rotation (positive = counter-clockwise)
            frontLeft.setPower(-turnPower);
            backLeft.setPower(-turnPower);
            frontRight.setPower(turnPower);
            backRight.setPower(turnPower);
            
            telemetry.addData("Rotating to", "%.1f degrees", targetHeading);
            telemetry.addData("Current Heading", "%.1f degrees", currentHeading);
            telemetry.addData("Error", "%.1f degrees", error);
            telemetry.update();
        }
        
        stopAllMotors();
        sleep(100);
    }
    
    /**
     * Rotate by a relative amount from current heading.
     * @param deltaDegrees Degrees to rotate (positive = counter-clockwise, negative = clockwise)
     */
    public void rotate(double deltaDegrees) {
        double currentHeading = getHeading();
        double targetHeading = normalizeAngle(currentHeading + deltaDegrees);
        rotateTo(targetHeading);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    /**
     * Stop all drive motors.
     */
    private void stopAllMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    
    /**
     * Get current heading from IMU.
     * @return Heading in degrees (-180 to 180)
     */
    private double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }
    
    /**
     * Normalize an angle to be within -180 to 180 degrees.
     */
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle <= -180) angle += 360;
        return angle;
    }
}