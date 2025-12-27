/*
    This code was designed for a hypothetical map in which the robot must understand a main color sensor before going to the 
    second one (corresponding to initial telemetry values) and choosing where to go from there based on telemetry values of
    the new color pad.
    
    (yes I know the robot goes in a square before returning to its initial place idek why I even added that but it's
    here now)


*/


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Autonomous(name="CungDotestingv2");
public class CungDotestingv2 extends LinearOpMode {
    private DcMotor motorLeft;
    private DcMotor motorRight;
    private ColorSensor color1;
    private DistanceSensor distance1;
   @Override
    public void runOpMode() {
    motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
    motorRight = hardwareMap.get(DcMotor.class, "motorRight");
    color1 = hardwareMap.get(ColorSensor.class, "color1");
    distance1 = hardwareMap.get(DistanceSensor.class, "distance1");

    motorLeft.setDirection(DcMotor.Direction.REVERSE);
    waitForStart();
    motorLeft.setPower(1);
    motorRight.setPower(1);
    sleep(300);
    motorLeft.setPower(-1);
    motorRight.setPower(1);
    sleep(500);
    motorLeft.setPower(1);
    motorRight.setPower(1);
    sleep(300);
    motorLeft.setPower(-1);
    motorRight.setPower(1);
    sleep(500);
    motorLeft.setPower(1);
    motorRight.setPower(1);
    sleep(300);
    motorLeft.setPower(-1);
    motorRight.setPower(1);
    sleep(500);
    motorLeft.setPower(1);
    motorRight.setPower(1);
    sleep(300);
    while (opModeIsActive()) {
        telemetry.addData("Red Value", color1.red());
        telemetry.addData("Blue Value", color1.blue());
        telemetry.update();

        if (color1.red() == 255) {
            motorLeft.setPower(-1);
            motorRight.setPower(1);
            sleep(1000);
            motorLeft.setPower(1);
            motorRight.setPower(1);
            sleep(800);
            telemetry.addData("Red Value", color1.red());
            telemetry.addData("Blue Value", color1.blue());
            telemetry.update();
            if (color1.red() > color1.blue()) {
                motorLeft.setPower(-1);
                motorRight.setPower(1);
                sleep(500);
                motorLeft.setPower(1);
                motorRight.setPower(1);
                sleep(300);
                break;
            }
            if (color1.blue() > color1.red()) {
                motorLeft.setPower(1);
                motorRight.setPower(-1);
                sleep(500);
                motorLeft.setPower(1);
                motorRight.setPower(1);
                sleep(300);
                break;
            }
            if (color1.blue() == color1.red()) {
                motorLeft.setPower(-1);
                motorRight.setPower(-1);
                sleep(2000);
                break;
            }

        }
        if (color1.blue() == 255) {
            motorLeft.setPower(1);
            motorRight.setPower(1);
            sleep(800);
            motorLeft.setPower(0);
            motorRight.setPower(0);
            telemetry.addData("Red Value", color1.red());
            telemetry.addData("Blue Value", color1.blue());
            telemetry.update();
            if (color1.red() > color1.blue()) {
                motorLeft.setPower(-1);
                motorRight.setPower(1);
                sleep(500);
                motorLeft.setPower(1);
                motorRight.setPower(1);
                sleep(300);
                break;
            }
            if (color1.blue() > color1.red()) {
                motorLeft.setPower(1);
                motorRight.setPower(-1);
                sleep(500);
                motorLeft.setPower(1);
                motorRight.setPower(1);
                sleep(300);
                break;
            }
        }
    
    }

    }

}



