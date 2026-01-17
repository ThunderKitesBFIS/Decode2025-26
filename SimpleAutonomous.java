package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Simple Static Autonomous for DECODE
 *
 * 4 Starting Positions:
 * 1 = RED Audience Side
 * 2 = RED Goal Side
 * 3 = BLUE Audience Side
 * 4 = BLUE Goal Side
 *
 * Strategy: Drive through closest spike mark (picking up 3 artifacts),
 *           then drive to shooting position and shoot all artifacts at goal
 */
@Autonomous(name = "DECODE Simple Auto", group = "Autonomous")
public class SimpleAutonomous extends LinearOpMode {

    // ========== HARDWARE ==========

    // Drivetrain motors
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;

    // Intake servos (continuous rotation)
    private CRServo intake1 = null;
    private CRServo intake2 = null;

    // Catapult servo
    private Servo upservo = null;

    // Shooter wheel motors
    private DcMotor shooterUp = null;
    private DcMotor shooterDown = null;

    private ElapsedTime runtime = new ElapsedTime();

    // ========== CONFIGURATION ==========

    // SELECT STARTING POSITION (1-4)
    private static final int STARTING_POSITION = 1;  // CHANGE THIS!
    private static final bool TAKE_MIDDLE_SPIKE = false; // CHANGE THIS

    // Motor configuration
    //TODO TUNE THESE
    private static final double COUNTS_PER_MOTOR_REV = 537.7;  // REV HD Hex
    private static final double WHEEL_DIAMETER_INCHES = 4.0;
    private static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
    private static final double ROBOT_WIDTH_INCHES = 15.0;  // Distance between left and right wheels

    // Movement speeds
    //TODO TUNE THESE
    private static final double DRIVE_SPEED = 0.6;
    private static final double ROTATE_SPEED = 0.5;

    // Intake configuration
    //TODO TUNE THESE
    private static final double INTAKE_POWER = -1.0;  // Power for intake servos (negative = intake)
    private static final double INTAKE_FEED_POWER = -1.0;  // Power to feed artifacts to catapult

    // Catapult configuration
    //TODO TUNE THESE
    private static final double CATAPULT_REST = 0.3;   // Resting/loaded position
    private static final double CATAPULT_FIRE = 0.1;   // Firing position
    private static final double SERVO_STEP = 0.01;     // Step size for smooth servo movement

    // Shooter wheel configuration
    //TODO TUNE THESE
    private static final double SHOOTER_POWER = 1.0;       // Power for shooter wheels
    private static final long SHOOTER_SPINUP_MS = 500;     // Time to spin up wheels before firing

    // Timing configuration (milliseconds)
    //TODO TUNE THESE
    private static final long CATAPULT_FIRE_TIME_MS = 500;    // Time to hold fire position
    private static final long CATAPULT_REST_TIME_MS = 400;    // Time to hold rest position
    private static final long RELOAD_TIME_MS = 1000;          // Time to run intake for reload
    private static final int NUM_ARTIFACTS = 3;               // Number of artifacts to shoot

    // Distance from goal for shooting (inches)
    private static final double DISTANCE_FROM_HOOP = 78.7;  // ~2 meters

    // ========== FIELD COORDINATES ==========

    private static final double FIELD_SIDE = 144;

    // Starting positions (robot center)
    private static final double[] RED_AUDIENCE_START = {60, 12};
    private static final double[] RED_GOAL_START = {14.7, 129.3};
    private static final double[] BLUE_AUDIENCE_START = {84, 12};
    private static final double[] BLUE_GOAL_START = {129.3, 129.3};

    // Spike mark positions (entry and exit points for each)
    private static final double[][] BLUE_SPIKE_NEAR = {{115 - 9, 36}, {125 + 9, 36}};
    private static final double[][] BLUE_SPIKE_MIDDLE = {{115 - 9, 60}, {125 + 9, 60}};
    private static final double[][] BLUE_SPIKE_FAR = {{115 - 9, 84}, {125 + 9, 84}};

    private static final double[][] RED_SPIKE_NEAR = {{29 + 9, 36}, {19 - 9, 36}};
    private static final double[][] RED_SPIKE_MIDDLE = {{29 + 9, 60}, {19 - 9, 60}};
    private static final double[][] RED_SPIKE_FAR = {{29 + 9, 84}, {19 - 9, 84}};

    // Goal positions
    private static final double[] RED_GOAL = {132, 132};
    private static final double[] BLUE_GOAL = {12, 132};

    // Current position tracking
    private double currentX = 0;
    private double currentY = 0;
    private double currentHeading = 0;  // degrees: 0=up(+Y), 90=right(+X), 180=down(-Y), 270=left(-X)

    @Override
    public void runOpMode() {

        // Initialize hardware
        initHardware();

        // Set catapult to rest position
        upservo.setPosition(CATAPULT_REST);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Starting Position", STARTING_POSITION);
        telemetry.update();

        waitForStart();
        runtime.reset();

        if (opModeIsActive()) {

            // Execute autonomous based on starting position
            switch (STARTING_POSITION) {
                case 1:
                    redAudienceSide();
                    break;
                case 2:
                    redGoalSide();
                    break;
                case 3:
                    blueAudienceSide();
                    break;
                case 4:
                    blueGoalSide();
                    break;
                default:
                    telemetry.addData("ERROR", "Invalid starting position!");
                    telemetry.update();
            }

            // Display completion
            telemetry.addData("Status", "Autonomous Complete");
            telemetry.addData("Runtime", "%.1f sec", runtime.seconds());
            telemetry.update();

            // Keep running until stop
            while (opModeIsActive()) {
                idle();
            }
        }
    }

    // ========== AUTONOMOUS STRATEGIES ==========

    private void redAudienceSide() {
        telemetry.addData("Running", "RED Audience Side");
        telemetry.update();

        // Set starting position
        currentX = RED_AUDIENCE_START[0];
        currentY = RED_AUDIENCE_START[1];
        currentHeading = 0;  // Facing up (+Y)

        // Calculate shooting position (diagonal from goal)
        double shootX = RED_GOAL[0] - (DISTANCE_FROM_HOOP * 1.414) / 2;
        double shootY = RED_GOAL[1] - (DISTANCE_FROM_HOOP * 1.414) / 2;

        // Drive to spike mark entry point
        driveTo(RED_SPIKE_NEAR[0][0], RED_SPIKE_NEAR[0][1]);

        // Drive through spike mark with intake running
        driveWithIntake(RED_SPIKE_NEAR[1][0], RED_SPIKE_NEAR[1][1]);

        // Drive backward to entry point for safety (faster than rotating)
        double spikeDistance = Math.sqrt(
                Math.pow(RED_SPIKE_NEAR[1][0] - RED_SPIKE_NEAR[0][0], 2) +
                        Math.pow(RED_SPIKE_NEAR[1][1] - RED_SPIKE_NEAR[0][1], 2)
        );
        driveBackward(spikeDistance);
        currentX = RED_SPIKE_NEAR[0][0];
        currentY = RED_SPIKE_NEAR[0][1];

        // Drive to shooting position and face the goal
        driveTo(shootX, shootY);
        rotateTo(45);

        // Shoot all artifacts
        shootAll();

        if(TAKE_MIDDLE_SPIKE){
            // Drive to spike mark entry point
            driveTo(RED_SPIKE_MIDDLE[0][0], RED_SPIKE_MIDDLE[0][1]);

            // Drive through spike mark with intake running
            driveWithIntake(RED_SPIKE_MIDDLE[1][0], RED_SPIKE_MIDDLE[1][1]);

            // Drive backward to entry point for safety (faster than rotating)
            double spikeDistance = Math.sqrt(
                    Math.pow(RED_SPIKE_MIDDLE[1][0] - RED_SPIKE_MIDDLE[0][0], 2) +
                            Math.pow(RED_SPIKE_MIDDLE[1][1] - RED_SPIKE_MIDDLE[0][1], 2)
            );
            driveBackward(spikeDistance);
            currentX = RED_SPIKE_MIDDLE[0][0];
            currentY = RED_SPIKE_MIDDLE[0][1];

            // Drive to shooting position and face the goal
            driveTo(shootX, shootY);
            rotateTo(45);

            // Shoot all artifacts
            shootAll();
        }
    }

    private void redGoalSide() {
        telemetry.addData("Running", "RED Goal Side");
        telemetry.update();

        // Set starting position
        currentX = RED_GOAL_START[0];
        currentY = RED_GOAL_START[1];
        currentHeading = 180;  // Facing down (-Y)

        // Calculate shooting position (diagonal from goal)
        double shootX = RED_GOAL[0] - (DISTANCE_FROM_HOOP * 1.414) / 2;
        double shootY = RED_GOAL[1] - (DISTANCE_FROM_HOOP * 1.414) / 2;

        // Drive to spike mark entry point (far spike is closest from goal side)
        driveTo(RED_SPIKE_FAR[0][0], currentY);           		// First: correct X
        driveTo(RED_SPIKE_FAR[0][0], RED_SPIKE_FAR[0][1]);    	// Then: correct Y

        // Drive through spike mark with intake running
        driveWithIntake(RED_SPIKE_FAR[1][0], RED_SPIKE_FAR[1][1]);

        // Drive backward to entry point for safety (faster than rotating)
        double spikeDistance = Math.sqrt(
                Math.pow(RED_SPIKE_FAR[1][0] - RED_SPIKE_FAR[0][0], 2) +
                        Math.pow(RED_SPIKE_FAR[1][1] - RED_SPIKE_FAR[0][1], 2)
        );
        driveBackward(spikeDistance);
        currentX = RED_SPIKE_FAR[0][0];
        currentY = RED_SPIKE_FAR[0][1];

        // Drive to shooting position and face the goal
        driveTo(shootX, shootY);
        rotateTo(45);

        // Shoot all artifacts
        shootAll();

        if(TAKE_MIDDLE_SPIKE){
            // Drive to spike mark entry point
            driveTo(RED_SPIKE_MIDDLE[0][0], RED_SPIKE_MIDDLE[0][1]);

            // Drive through spike mark with intake running
            driveWithIntake(RED_SPIKE_MIDDLE[1][0], RED_SPIKE_MIDDLE[1][1]);

            // Drive backward to entry point for safety (faster than rotating)
            double spikeDistance = Math.sqrt(
                    Math.pow(RED_SPIKE_MIDDLE[1][0] - RED_SPIKE_MIDDLE[0][0], 2) +
                            Math.pow(RED_SPIKE_MIDDLE[1][1] - RED_SPIKE_MIDDLE[0][1], 2)
            );
            driveBackward(spikeDistance);
            currentX = RED_SPIKE_MIDDLE[0][0];
            currentY = RED_SPIKE_MIDDLE[0][1];

            // Drive to shooting position and face the goal
            driveTo(shootX, shootY);
            rotateTo(45);

            // Shoot all artifacts
            shootAll();
        }
    }

    private void blueAudienceSide() {
        telemetry.addData("Running", "BLUE Audience Side");
        telemetry.update();

        // Set starting position
        currentX = BLUE_AUDIENCE_START[0];
        currentY = BLUE_AUDIENCE_START[1];
        currentHeading = 0;  // Facing up (+Y)

        // Calculate shooting position (diagonal from goal)
        double shootX = BLUE_GOAL[0] + (DISTANCE_FROM_HOOP * 1.414) / 2;
        double shootY = BLUE_GOAL[1] - (DISTANCE_FROM_HOOP * 1.414) / 2;

        // Drive to spike mark entry point
        driveTo(BLUE_SPIKE_NEAR[0][0], BLUE_SPIKE_NEAR[0][1]);

        // Drive through spike mark with intake running
        driveWithIntake(BLUE_SPIKE_NEAR[1][0], BLUE_SPIKE_NEAR[1][1]);

        // Drive backward to entry point for safety (faster than rotating)
        double spikeDistance = Math.sqrt(
                Math.pow(BLUE_SPIKE_NEAR[1][0] - BLUE_SPIKE_NEAR[0][0], 2) +
                        Math.pow(BLUE_SPIKE_NEAR[1][1] - BLUE_SPIKE_NEAR[0][1], 2)
        );
        driveBackward(spikeDistance);
        currentX = BLUE_SPIKE_NEAR[0][0];
        currentY = BLUE_SPIKE_NEAR[0][1];

        // Drive to shooting position and face the goal
        driveTo(shootX, shootY);
        rotateTo(315);

        // Shoot all artifacts
        shootAll();

        if(TAKE_MIDDLE_SPIKE){
            // Drive to spike mark entry point
            driveTo(BLUE_SPIKE_MIDDLE[0][0], BLUE_SPIKE_MIDDLE[0][1]);

            // Drive through spike mark with intake running
            driveWithIntake(BLUE_SPIKE_MIDDLE[1][0], BLUE_SPIKE_MIDDLE[1][1]);

            // Drive backward to entry point for safety (faster than rotating)
            double spikeDistance = Math.sqrt(
                    Math.pow(BLUE_SPIKE_MIDDLE[1][0] - BLUE_SPIKE_MIDDLE[0][0], 2) +
                            Math.pow(BLUE_SPIKE_MIDDLE[1][1] - BLUE_SPIKE_MIDDLE[0][1], 2)
            );
            driveBackward(spikeDistance);
            currentX = BLUE_SPIKE_MIDDLE[0][0];
            currentY = BLUE_SPIKE_MIDDLE[0][1];

            // Drive to shooting position and face the goal
            driveTo(shootX, shootY);
            rotateTo(315);

            // Shoot all artifacts
            shootAll();
        }
    }

    private void blueGoalSide() {
        telemetry.addData("Running", "BLUE Goal Side");
        telemetry.update();

        // Set starting position
        currentX = BLUE_GOAL_START[0];
        currentY = BLUE_GOAL_START[1];
        currentHeading = 180;  // Facing down (-Y)

        // Calculate shooting position (diagonal from goal)
        double shootX = BLUE_GOAL[0] + (DISTANCE_FROM_HOOP * 1.414) / 2;
        double shootY = BLUE_GOAL[1] - (DISTANCE_FROM_HOOP * 1.414) / 2;

        // Drive to spike mark entry point (far spike is closest from goal side)
        driveTo(BLUE_SPIKE_FAR[0][0], currentY);           		// First: correct X
        driveTo(BLUE_SPIKE_FAR[0][0], BLUE_SPIKE_FAR[0][1]);    // Then: correct Y

        // Drive through spike mark with intake running
        driveWithIntake(BLUE_SPIKE_FAR[1][0], BLUE_SPIKE_FAR[1][1]);

        // Drive backward to entry point for safety (faster than rotating)
        double spikeDistance = Math.sqrt(
                Math.pow(BLUE_SPIKE_FAR[1][0] - BLUE_SPIKE_FAR[0][0], 2) +
                        Math.pow(BLUE_SPIKE_FAR[1][1] - BLUE_SPIKE_FAR[0][1], 2)
        );
        driveBackward(spikeDistance);
        currentX = BLUE_SPIKE_FAR[0][0];
        currentY = BLUE_SPIKE_FAR[0][1];

        // Drive to shooting position and face the goal
        driveTo(shootX, shootY);
        rotateTo(315);

        // Shoot all artifacts
        shootAll();

        if(TAKE_MIDDLE_SPIKE){
            // Drive to spike mark entry point
            driveTo(BLUE_SPIKE_MIDDLE[0][0], BLUE_SPIKE_MIDDLE[0][1]);

            // Drive through spike mark with intake running
            driveWithIntake(BLUE_SPIKE_MIDDLE[1][0], BLUE_SPIKE_MIDDLE[1][1]);

            // Drive backward to entry point for safety (faster than rotating)
            double spikeDistance = Math.sqrt(
                    Math.pow(BLUE_SPIKE_MIDDLE[1][0] - BLUE_SPIKE_MIDDLE[0][0], 2) +
                            Math.pow(BLUE_SPIKE_MIDDLE[1][1] - BLUE_SPIKE_MIDDLE[0][1], 2)
            );
            driveBackward(spikeDistance);
            currentX = BLUE_SPIKE_MIDDLE[0][0];
            currentY = BLUE_SPIKE_MIDDLE[0][1];

            // Drive to shooting position and face the goal
            driveTo(shootX, shootY);
            rotateTo(315);

            // Shoot all artifacts
            shootAll();
        }
    }

    // ========== MOVEMENT FUNCTIONS ==========

    /**
     * Drive forward by specified distance in inches.
     * Positive = forward (in direction robot is facing)
     */
    private void driveForward(double inches) {
        if (inches <= 0) return;

        int target = (int) (inches * COUNTS_PER_INCH);

        resetEncoders();

        // All wheels forward
        leftFront.setTargetPosition(target);
        rightFront.setTargetPosition(target);
        leftBack.setTargetPosition(target);
        rightBack.setTargetPosition(target);

        runToPosition(DRIVE_SPEED);

        while (opModeIsActive() && motorsAreBusy()) {
            telemetry.addData("Driving Forward", "%.1f inches", inches);
            telemetry.addData("Position", leftFront.getCurrentPosition());
            telemetry.update();
        }

        stopMotors();
        setRunUsingEncoder();
    }

    /**
     * Drive backward by specified distance in inches.
     * Positive value = backward (opposite of facing direction)
     */
    private void driveBackward(double inches) {
        if (inches <= 0) return;

        int target = (int) (inches * COUNTS_PER_INCH);

        resetEncoders();

        // All wheels backward (negative target)
        leftFront.setTargetPosition(-target);
        rightFront.setTargetPosition(-target);
        leftBack.setTargetPosition(-target);
        rightBack.setTargetPosition(-target);

        runToPosition(DRIVE_SPEED);

        while (opModeIsActive() && motorsAreBusy()) {
            telemetry.addData("Driving Backward", "%.1f inches", inches);
            telemetry.addData("Position", leftFront.getCurrentPosition());
            telemetry.update();
        }

        stopMotors();
        setRunUsingEncoder();
    }

    /**
     * Rotate to target heading.
     * Heading: 0=up(+Y), 90=right(+X), 180=down(-Y), 270=left(-X)
     */
    private void rotateTo(double targetHeading) {
        // Calculate shortest rotation angle
        double deltaAngle = targetHeading - currentHeading;

        // Normalize to -180 to +180 range
        while (deltaAngle > 180) deltaAngle -= 360;
        while (deltaAngle < -180) deltaAngle += 360;

        telemetry.addData("Rotating", "from %.0f to %.0f (delta: %.0f)",
                currentHeading, targetHeading, deltaAngle);
        telemetry.update();

        if (Math.abs(deltaAngle) < 2) {  // Already close enough
            return;
        }

        // Calculate arc length each wheel travels
        double arcLength = Math.toRadians(deltaAngle) * (ROBOT_WIDTH_INCHES / 2.0);
        int target = (int) (arcLength * COUNTS_PER_INCH);

        resetEncoders();

        // Rotation: left wheels forward, right wheels backward for clockwise (positive angle)
        leftFront.setTargetPosition(target);
        rightFront.setTargetPosition(-target);
        leftBack.setTargetPosition(target);
        rightBack.setTargetPosition(-target);

        runToPosition(ROTATE_SPEED);

        while (opModeIsActive() && motorsAreBusy()) {
            telemetry.addData("Rotating", "%.0f degrees", deltaAngle);
            telemetry.addData("Position", leftFront.getCurrentPosition());
            telemetry.update();
        }

        stopMotors();
        currentHeading = targetHeading;
        setRunUsingEncoder();

        sleep(100);  // Brief pause after rotation
    }

    /**
     * Drive to target position (x, y) in field coordinates.
     * Automatically rotates to face the target, then drives forward.
     */
    private void driveTo(double targetX, double targetY) {
        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        // Calculate distance to target
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Skip if already at target
        if (distance < 1) {
            return;
        }

        telemetry.addData("Driving to", "(%.1f, %.1f)", targetX, targetY);
        telemetry.addData("Current", "(%.1f, %.1f)", currentX, currentY);
        telemetry.addData("Distance", "%.1f inches", distance);
        telemetry.update();

        // Calculate angle to target (0 = +Y, 90 = +X)
        double targetHeading = Math.toDegrees(Math.atan2(deltaX, deltaY));

        // Normalize to 0-360 range
        if (targetHeading < 0) {
            targetHeading += 360;
        }

        // Rotate to face target
        rotateTo(targetHeading);

        // Drive forward to target
        driveForward(distance);

        // Update position
        currentX = targetX;
        currentY = targetY;

        sleep(100);
    }

    /**
     * Drive to target position while running intake servos.
     * Automatically rotates to face the target, then drives forward with intake on.
     * Used when driving through spike marks to collect artifacts.
     */
    private void driveWithIntake(double targetX, double targetY) {
        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        // Calculate distance to target
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Skip if already at target
        if (distance < 1) {
            return;
        }

        telemetry.addData("Driving with Intake to", "(%.1f, %.1f)", targetX, targetY);
        telemetry.addData("Distance", "%.1f inches", distance);
        telemetry.update();

        // Calculate angle to target (0 = +Y, 90 = +X)
        double targetHeading = Math.toDegrees(Math.atan2(deltaX, deltaY));

        // Normalize to 0-360 range
        if (targetHeading < 0) {
            targetHeading += 360;
        }

        // Rotate to face target
        rotateTo(targetHeading);

        // Start intake
        setIntakePower(INTAKE_POWER);

        // Drive forward to target
        driveForward(distance);

        // Stop intake
        setIntakePower(0);

        // Update position
        currentX = targetX;
        currentY = targetY;

        sleep(100);
    }

    // ========== SHOOTING FUNCTIONS ==========

    /**
     * Shoot all collected artifacts.
     * Fires catapult, then reloads and repeats.
     */
    private void shootAll() {
        telemetry.addData("Action", "SHOOTING ALL ARTIFACTS");
        telemetry.update();

        for (int i = 0; i < NUM_ARTIFACTS; i++) {
            telemetry.addData("Shooting", "Artifact %d of %d", i + 1, NUM_ARTIFACTS);
            telemetry.update();

            // Fire the catapult
            fireCatapult();

            // Reload (except after last shot)
            if (i < NUM_ARTIFACTS - 1) {
                reloadCatapult();
            }
        }

        telemetry.addData("Status", "All shots complete");
        telemetry.update();
    }

    /**
     * Fire the catapult servo.
     * Spins up shooter wheels, then moves catapult from rest to fire position.
     */
    private void fireCatapult() {
        telemetry.addData("Catapult", "SPINNING UP");
        telemetry.update();

        // Spin up shooter wheels
        setShooterPower(SHOOTER_POWER);
        sleep(SHOOTER_SPINUP_MS);

        telemetry.addData("Catapult", "FIRING");
        telemetry.update();

        // Move servo smoothly to fire position
        moveServoSmooth(upservo, CATAPULT_FIRE, SERVO_STEP);
        sleep(CATAPULT_FIRE_TIME_MS);

        // Stop shooter wheels
        setShooterPower(0);

        // Return to rest position
        moveServoSmooth(upservo, CATAPULT_REST, SERVO_STEP);
        sleep(CATAPULT_REST_TIME_MS);
    }

    /**
     * Reload the catapult by running intake servos.
     * Pushes next artifact onto the catapult.
     */
    private void reloadCatapult() {
        telemetry.addData("Catapult", "RELOADING");
        telemetry.update();

        // Run intake to feed next artifact
        setIntakePower(INTAKE_FEED_POWER);
        sleep(RELOAD_TIME_MS);
        setIntakePower(0);
    }

    /**
     * Move servo smoothly to target position.
     */
    private void moveServoSmooth(Servo servo, double target, double step) {
        double current = servo.getPosition();

        while (opModeIsActive() && Math.abs(current - target) > step) {
            if (current < target) {
                current = Math.min(current + step, target);
            } else {
                current = Math.max(current - step, target);
            }
            servo.setPosition(current);
            sleep(10);  // Small delay for smooth movement
        }

        servo.setPosition(target);
    }

    // ========== INTAKE CONTROL ==========

    /**
     * Set power for both intake servos.
     */
    private void setIntakePower(double power) {
        if (intake1 != null) intake1.setPower(power);
        if (intake2 != null) intake2.setPower(power);
    }

    /**
     * Set power for both shooter wheel motors.
     */
    private void setShooterPower(double power) {
        if (shooterUp != null) shooterUp.setPower(power);
        if (shooterDown != null) shooterDown.setPower(power);
    }

    // ========== MOTOR HELPER FUNCTIONS ==========

    private void resetEncoders() {
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void runToPosition(double speed) {
        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFront.setPower(speed);
        rightFront.setPower(speed);
        leftBack.setPower(speed);
        rightBack.setPower(speed);
    }

    private void setRunUsingEncoder() {
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private boolean motorsAreBusy() {
        return leftFront.isBusy() && rightFront.isBusy() &&
                leftBack.isBusy() && rightBack.isBusy();
    }

    private void stopMotors() {
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);
    }

    // ========== HARDWARE INITIALIZATION ==========

    private void initHardware() {
        // Drivetrain motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // Set directions (matching Test class pattern)
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        // Set zero power behavior
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize encoders
        resetEncoders();
        setRunUsingEncoder();

        // Intake servos (continuous rotation)
        try {
            intake1 = hardwareMap.get(CRServo.class, "intake1");
            intake2 = hardwareMap.get(CRServo.class, "intake2");
        } catch (Exception e) {
            telemetry.addData("Warning", "Intake servos not configured");
        }

        // Catapult servo
        try {
            upservo = hardwareMap.get(Servo.class, "upservo");
        } catch (Exception e) {
            telemetry.addData("Warning", "Catapult servo not configured");
        }

        // Shooter wheel motors
        try {
            shooterUp = hardwareMap.get(DcMotor.class, "shooterUp");
            shooterDown = hardwareMap.get(DcMotor.class, "shooterDown");
            shooterUp.setDirection(DcMotor.Direction.FORWARD);
            shooterDown.setDirection(DcMotor.Direction.FORWARD);
            shooterUp.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            shooterDown.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        } catch (Exception e) {
            telemetry.addData("Warning", "Shooter motors not configured");
        }
    }
}