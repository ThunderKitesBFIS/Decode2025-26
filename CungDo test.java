package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="CungDotest");
public class CungDotest extends LinearOpMode{
    private Dcmotor motorLeft;
    private Dcmotor motorRight;

    public void runOpMode() {
        motorLeft = hardwaremap.get(Dcmotor.class, "motorLeft");
        motorLeft = hardwaremap.get(Dcmotor.class, "motorRight");
        motorLeft.setDirection(Dcmotor.Direction.REVERSE);
        waitForStart;
        while (opModeIsActive()){
                double left = -gamepad1.left_stick_y;
                double right = -gamepad1.right_stick_y;

                motorLeft.setPower(left);
                motorRight.setPower(right);
            }
        }
}
