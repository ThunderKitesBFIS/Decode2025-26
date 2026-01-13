package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
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
 * Strategy: Drive through 3 closest spike marks, then shoot at goal
 */
@Autonomous(name="DECODE Simple Auto", group="Autonomous")
public class SimpleAutonomous extends LinearOpMode {

    // Hardware
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;
    private DcMotor shooterLeft = null;
    private DcMotor shooterRight = null;

    private ElapsedTime runtime = new ElapsedTime();

    // ========== CONFIGURATION ==========

    // SELECT STARTING POSITION (1-4)
    private static final int STARTING_POSITION = 1;  // CHANGE THIS!

    // Shooting configuration (tune these values)
    private static final double SHOOTER_POWER = 0.0;  // 0.0 to 1.0
    private static final long SHOOTER_TIME_MS = 0;    // milliseconds
    private static final double DISTANCE_FROM_HOOP = 48.0;  // inches (4 feet)

    // Motor configuration
    private static final double COUNTS_PER_MOTOR_REV = 537.7;  // REV HD Hex
    private static final double WHEEL_DIAMETER_INCHES = 4.0;
    private static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV) / (WHEEL_DIAMETER_INCHES * Math.PI);

    private static final double DRIVE_SPEED = 0.6;

    // ========== FIELD COORDINATES ==========

    // Starting positions (robot center)
    private static final double[] RED_AUDIENCE_START = {108, 15};
    private static final double[] RED_GOAL_START = {108, 120};
    private static final double[] BLUE_AUDIENCE_START = {36, 15};
    private static final double[] BLUE_GOAL_START = {36, 120};

    // Spike mark positions (3 per alliance)
    private static final double[] RED_SPIKE_NEAR = {114, 48};
    private static final double[] RED_SPIKE_MIDDLE = {114, 72};
    private static final double[] RED_SPIKE_FAR = {114, 96};

    private static final double[] BLUE_SPIKE_NEAR = {30, 48};
    private static final double[] BLUE_SPIKE_MIDDLE = {30, 72};
    private static final double[] BLUE_SPIKE_FAR = {30, 96};

    // Goal positions
    private static final double[] RED_GOAL = {108, 144};
    private static final double[] BLUE_GOAL = {36, 144};

    // Current position tracking
    private double currentX = 0;
    private double currentY = 0;
    private double currentHeading = 90;  // degrees, 0=right, 90=up, 180=left, 270=down

    @Override
    public void runOpMode() {

        // Initialize hardware
        initHardware();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Starting Position", STARTING_POSITION);
        telemetry.addData("Distance from Hoop", "%.1f inches", DISTANCE_FROM_HOOP);
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
        currentHeading = 90;  // Facing up

        // Pick up 3 balls (drive through spike marks)
        driveTo(RED_SPIKE_NEAR[0], RED_SPIKE_NEAR[1]);    // Near spike
        driveTo(RED_SPIKE_MIDDLE[0], RED_SPIKE_MIDDLE[1]); // Middle spike
        driveTo(RED_SPIKE_FAR[0], RED_SPIKE_FAR[1]);       // Far spike

        // Move to shooting position
        double shootX = RED_GOAL[0];
        double shootY = RED_GOAL[1] - DISTANCE_FROM_HOOP;
        driveTo(shootX, shootY);

        // Shoot
        shoot();
    }

    private void redGoalSide() {
        telemetry.addData("Running", "RED Goal Side");
        telemetry.update();

        currentX = RED_GOAL_START[0];
        currentY = RED_GOAL_START[1];
        currentHeading = 90;

        // Pick up 3 balls (closest: far, middle, near)
        driveTo(RED_SPIKE_FAR[0], RED_SPIKE_FAR[1]);
        driveTo(RED_SPIKE_MIDDLE[0], RED_SPIKE_MIDDLE[1]);
        driveTo(RED_SPIKE_NEAR[0], RED_SPIKE_NEAR[1]);

        // Move to shooting position
        double shootX = RED_GOAL[0];
        double shootY = RED_GOAL[1] - DISTANCE_FROM_HOOP;
        driveTo(shootX, shootY);

        shoot();
    }

    private void blueAudienceSide() {
        telemetry.addData("Running", "BLUE Audience Side");
        telemetry.update();

        currentX = BLUE_AUDIENCE_START[0];
        currentY = BLUE_AUDIENCE_START[1];
        currentHeading = 90;

        // Pick up 3 balls
        driveTo(BLUE_SPIKE_NEAR[0], BLUE_SPIKE_NEAR[1]);
        driveTo(BLUE_SPIKE_MIDDLE[0], BLUE_SPIKE_MIDDLE[1]);
        driveTo(BLUE_SPIKE_FAR[0], BLUE_SPIKE_FAR[1]);

        // Move to shooting position
        double shootX = BLUE_GOAL[0];
        double shootY = BLUE_GOAL[1] - DISTANCE_FROM_HOOP;
        driveTo(shootX, shootY);

        shoot();
    }

    private void blueGoalSide() {
        telemetry.addData("Running", "BLUE Goal Side");
        telemetry.update();

        currentX = BLUE_GOAL_START[0];
        currentY = BLUE_GOAL_START[1];
        currentHeading = 90;

        // Pick up 3 balls (closest: far, middle, near)
        driveTo(BLUE_SPIKE_FAR[0], BLUE_SPIKE_FAR[1]);
        driveTo(BLUE_SPIKE_MIDDLE[0], BLUE_SPIKE_MIDDLE[1]);
        driveTo(BLUE_SPIKE_NEAR[0], BLUE_SPIKE_NEAR[1]);

        // Move to shooting position
        double shootX = BLUE_GOAL[0];
        double shootY = BLUE_GOAL[1] - DISTANCE_FROM_HOOP;
        driveTo(shootX, shootY);

        shoot();
    }

    // ========== MOVEMENT FUNCTIONS ==========

    /**
     * Drive to target position (x, y) in field coordinates
     */
    private void driveTo(double targetX, double targetY) {
        // Calculate distances
        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        telemetry.addData("Driving to", "(%.1f, %.1f)", targetX, targetY);
        telemetry.addData("Delta", "X=%.1f Y=%.1f", deltaX, deltaY);
        telemetry.update();

        // Drive X direction (strafe)
        if (Math.abs(deltaX) > 1) {
            strafeInches(deltaX);
            currentX = targetX;
        }

        // Drive Y direction (forward/backward)
        if (Math.abs(deltaY) > 1) {
            driveInches(deltaY);
            currentY = targetY;
        }

        sleep(200);  // Brief pause between movements
    }

    /**
     * Drive forward (+) or backward (-) in inches
     */
    private void driveInches(double inches) {
        int target = (int)(inches * COUNTS_PER_INCH);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setTargetPosition(target);
        rightFront.setTargetPosition(target);
        leftBack.setTargetPosition(target);
        rightBack.setTargetPosition(target);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFront.setPower(DRIVE_SPEED);
        rightFront.setPower(DRIVE_SPEED);
        leftBack.setPower(DRIVE_SPEED);
        rightBack.setPower(DRIVE_SPEED);

        while (opModeIsActive() && leftFront.isBusy() && rightFront.isBusy()) {
            telemetry.addData("Driving", "%.1f inches", inches);
            telemetry.addData("Position", leftFront.getCurrentPosition());
            telemetry.update();
        }

        stopMotors();

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Strafe right (+) or left (-) in inches (mecanum/omni)
     */
    private void strafeInches(double inches) {
        int target = (int)(inches * COUNTS_PER_INCH);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Mecanum strafe: LF+, RF-, LB-, RB+
        leftFront.setTargetPosition(target);
        rightFront.setTargetPosition(-target);
        leftBack.setTargetPosition(-target);
        rightBack.setTargetPosition(target);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFront.setPower(DRIVE_SPEED);
        rightFront.setPower(DRIVE_SPEED);
        leftBack.setPower(DRIVE_SPEED);
        rightBack.setPower(DRIVE_SPEED);

        while (opModeIsActive() && leftFront.isBusy() && rightFront.isBusy()) {
            telemetry.addData("Strafing", "%.1f inches", inches);
            telemetry.update();
        }

        stopMotors();

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Run shooting motors
     */
    private void shoot() {
        telemetry.addData("Action", "SHOOTING");
        telemetry.update();

        if (shooterLeft != null && shooterRight != null) {
            shooterLeft.setPower(SHOOTER_POWER);
            shooterRight.setPower(SHOOTER_POWER);

            sleep(SHOOTER_TIME_MS);

            shooterLeft.setPower(0);
            shooterRight.setPower(0);
        }

        telemetry.addData("Status", "Shot complete");
        telemetry.update();
    }

    // ========== HARDWARE INITIALIZATION ==========

    private void initHardware() {
        // Drivetrain motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // Set directions (adjust if robot drives wrong way)
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
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Shooter motors (optional)
        try {
            shooterLeft = hardwareMap.get(DcMotor.class, "shooterLeft");
            shooterRight = hardwareMap.get(DcMotor.class, "shooterRight");
            shooterLeft.setDirection(DcMotor.Direction.FORWARD);
            shooterRight.setDirection(DcMotor.Direction.FORWARD);
        } catch (Exception e) {
            telemetry.addData("Warning", "Shooter motors not configured");
        }
    }

    private void stopMotors() {
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);
    }
}

