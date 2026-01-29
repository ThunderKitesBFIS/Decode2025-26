package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/**
 * IMU TEST
 * 
 * Just checks that the IMU exists and displays heading.
 * Rotate the robot by hand and watch the numbers change.
 */
@TeleOp(name = "IMU Test", group = "Test")
public class IMUTest extends LinearOpMode {

    private IMU imu = null;

    @Override
    public void runOpMode() {
        
        // Try to initialize IMU
        try {
            imu = hardwareMap.get(IMU.class, "imu");
            
            // Configure orientation (adjust if your hub is mounted differently)
            RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
            );
            imu.initialize(new IMU.Parameters(orientationOnRobot));
            
            telemetry.addData("IMU Status", "FOUND - Ready!");
        } catch (Exception e) {
            telemetry.addData("IMU Status", "NOT FOUND - Error!");
            telemetry.addData("Error", e.getMessage());
        }
        
        telemetry.update();
        
        waitForStart();
        
        // Reset heading to zero at start
        if (imu != null) {
            imu.resetYaw();
        }
        
        while (opModeIsActive()) {
            
            if (imu != null) {
                YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
                
                double yaw = orientation.getYaw(AngleUnit.DEGREES);
                double pitch = orientation.getPitch(AngleUnit.DEGREES);
                double roll = orientation.getRoll(AngleUnit.DEGREES);
                
                telemetry.addData("=== IMU WORKING ===", "");
                telemetry.addData("YAW (heading)", "%.1f°", yaw);
                telemetry.addData("Pitch", "%.1f°", pitch);
                telemetry.addData("Roll", "%.1f°", roll);
                telemetry.addData("", "");
                telemetry.addData("Instructions", "Rotate robot by hand");
                telemetry.addData("", "YAW should change as you turn");
            } else {
                telemetry.addData("ERROR", "IMU not initialized");
            }
            
            telemetry.update();
        }
    }
}