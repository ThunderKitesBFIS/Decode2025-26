package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;

@Autonomous(name="FunctionTest")
public class BasicActionsTest extends LinearOpMode {
    private DcMotor motorLeft, motorRight;
    private Actions actions;
    @Override
    public void runOpMode() {
        motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
        motorRight = hardwareMap.get(DcMotor.class, "motorRight");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        actions = new Actions(this, motorLeft, motorRight);
        waitForStart();

        actions.driveforward(1,500);
        actions.turnleft(1, 500);
        actions.driveforward(1,500);
        actions.turnleft(1, 500);
        actions.driveforward(1,500);
        actions.turnleft(1, 500);
        actions.driveforward(1,500);
    }

}