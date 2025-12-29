/*
    This code was designed for a hypothetical map in which the robot must understand a main color sensor before going to the 
    second one (corresponding to initial telemetry values) and choosing where to go from there based on telemetry values of
    the new color pad.
    
    (yes I know the robot goes in a square before returning to its initial place idek why I even added that but it's
    here now)

    Okay, so the new code is for the other part of the map, if the robot detects green on the color sensor instead of alpha
    (alpha is the stand in for yellow cause apparently FTC sensors don't got it), then it goes backward, then it turns to
    detect the closer side to it, and then moves towards the closer side, if they happen to be equal in distance, the robot
    just heads back to the initial color sensor. If the initial color sensor is alpha, it'll spin and be bored.


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
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
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

@Autonomous(name="CungDoattemptpart2")
public class CungDoattemptpart2 extends LinearOpMode {
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
        sleep(500);
        motorLeft.setPower(-1);
        motorRight.setPower(-1);
        sleep(1000);
        motorLeft.setPower(1);
        motorRight.setPower(1);
        sleep(500);
        motorLeft.setPower(0);
        motorRight.setPower(0);

        while (opModeIsActive()) {
            telemetry.addData("Green Value", color1.green());
            telemetry.addData("Alpha Value", color1.alpha());
            telemetry.update();
            
            if (color1.green() > color1.alpha()) {
                motorLeft.setPower(-1);
                motorRight.setPower(-1);
                sleep(1500);
                telemetry.update();
                motorLeft.setPower(-1);
                motorRight.setPower(1);
                sleep(500);
                double leftDistance = distance1.getDistance(DistanceUnit.CM);
                telemetry.addData("Distance from left", leftDistance);
                motorLeft.setPower(1);
                motorRight.setPower(-1);
                sleep(1000);
                double rightDistance = distance1.getDistance(DistanceUnit.CM);
                telemetry.addData("Distance from right", rightDistance);
                telemetry.update();
                if (rightDistance < leftDistance){
                    motorLeft.setPower(1);
                    motorRight.setPower(1);
                    sleep(1000);
                    motorLeft.setPower(0);
                    motorRight.setPower(0);

                } else if (leftDistance < rightDistance) {
                    motorLeft.setPower(1);
                    motorRight.setPower(-1);
                    sleep(1000);
                    motorLeft.setPower(1);
                    motorRight.setPower(1);
                    sleep(1000);
                    motorLeft.setPower(0);
                    motorRight.setPower(0);
                    break;
                } else {
                    motorLeft.setPower(-1);
                    motorRight.setPower(1);
                    sleep(500);
                    motorLeft.setPower(1);
                    motorRight.setPower(1);
                    sleep(1500);
                    motorLeft.setPower(0);
                    motorRight.setPower(0);
                    break;
                }
                telemetry.addLine("Yippee I'm done!");
                telemetry.update();
                break;
                    

    
            }
            if (color1.alpha() > color1.green()) {
                motorLeft.setPower(-1);
                motorRight.setPower(1);
                telemetry.addLine("There's not much to do now I guess");
                telemetry.update();
                break;
            }
        }

    }
}



