package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;


@TeleOp(name="Voltage Compensation")
public class VoltageCompensation extends LinearOpMode {
    
    private DcMotor motor1, motor2;
    private VoltageSensor voltageSensor;
    
    // Configuration
    private static final double UPDATE_INTERVAL_MS = 20000; // 20 seconds for power update
    private static final double VOLTAGE_CHANGE_THRESHOLD = 0.15; // Threshold filter
    private static final double JUMP_THRESHOLD = 2.0; // Flag if voltage jumps > 2V
    
    // State variables
    private double currentVoltage = 13.0;
    private double previousUpdateVoltage = 13.0; // Voltage at last 20-second update
    private double currentPower = 0.6;
    private double previousPower = 0.6;
    private double filteredVoltage = 13.0;
    
    // Continuous monitoring for jumps
    private double continuousVoltage = 13.0;
    private double lastContinuousVoltage = 13.0;
    private boolean jump = false;
    
    // Timing
    private double lastUpdateTime = 0;
    private int updateCount = 0;
    
    @Override
    public void runOpMode() {
        motor1 = hardwareMap.get(DcMotor.class, "motor1");
        motor2 = hardwareMap.get(DcMotor.class, "motor2");
        voltageSensor = hardwareMap.voltageSensor.iterator().next();
        
        waitForStart();
        
        while (opModeIsActive()) {
            double currentTime = getRuntime() * 1000; // milliseconds
            
            // CONTINUOUS JUMP DETECTION (every loop)
            lastContinuousVoltage = continuousVoltage;
            continuousVoltage = voltageSensor.getVoltage();
            
            // Check for jump (change > 2V)
            double instantChange = Math.abs(continuousVoltage - lastContinuousVoltage);
            jump = (instantChange > JUMP_THRESHOLD);
            
            // POWER UPDATE (every 20 seconds)
            if (currentTime - lastUpdateTime >= UPDATE_INTERVAL_MS) {
                updateVoltageAndPower();
                lastUpdateTime = currentTime;
                updateCount++;
                
                // Display telemetry
                showTelemetry();
            }
            
            // Apply motor power
            motor1.setPower(currentPower);
            motor2.setPower(currentPower);
            
            sleep(20); // Fast loop for jump detection
        }
    }
    
    private void updateVoltageAndPower() {
        // Store previous values
        previousUpdateVoltage = currentVoltage;
        previousPower = currentPower;
        
        // Read voltage
        double rawVoltage = voltageSensor.getVoltage();
        
        // Apply threshold filter
        if (Math.abs(rawVoltage - filteredVoltage) > VOLTAGE_CHANGE_THRESHOLD) {
            filteredVoltage = rawVoltage;
        }
        
        currentVoltage = filteredVoltage;
        
        // Calculate new power
        currentPower = calculatePowerFromVoltage(currentVoltage);
    }
    
    private double calculatePowerFromVoltage(double voltage) {
        // Your thresholds: 14V→0.5, 13V→0.6, 12V→0.7, 11V→0.85, 10V→1.0
        
        if (voltage >= 14.0) {
            return 0.5;
        } else if (voltage >= 13.0) {
            return 0.5 + (14.0 - voltage) * 0.1;
        } else if (voltage >= 12.0) {
            return 0.6 + (13.0 - voltage) * 0.1;
        } else if (voltage >= 11.0) {
            return 0.7 + (12.0 - voltage) * 0.15;
        } else if (voltage >= 10.0) {
            return 0.85 + (11.0 - voltage) * 0.15;
        } else {
            return 1.0;
        }
    }
    
    private void showTelemetry() {
        telemetry.clear();
        
        // Header
        telemetry.addData("Update", "#%d @ %.1fs", updateCount, getRuntime());
        telemetry.addLine("─────────────────────────");
        
        // 20-second update values
        telemetry.addData("Prev Voltage", "%.2f V", previousUpdateVoltage);
        telemetry.addData("Curr Voltage", "%.2f V", currentVoltage);
        telemetry.addData("ΔVoltage", "%+.2f V", currentVoltage - previousUpdateVoltage);
        telemetry.addLine();
        
        telemetry.addData("Prev Power", "%.3f", previousPower);
        telemetry.addData("Curr Power", "%.3f", currentPower);
        telemetry.addData("ΔPower", "%+.3f", currentPower - previousPower);
        telemetry.addLine();
        
        // Continuous monitoring status
        telemetry.addData("Live Voltage", "%.2f V", continuousVoltage);
        telemetry.addData("Jump Detected", jump ? "YES ⚠️" : "No");
        
        if (jump) {
            telemetry.addLine("─────────────────────────");
            telemetry.addLine("⚠️ VOLTAGE JUMP > 2V ⚠️");
            telemetry.addData("Jump Size", "%.2f V", 
                Math.abs(continuousVoltage - lastContinuousVoltage));
        }
        
        if (currentVoltage < 11.0) {
            telemetry.addLine("⚠️ LOW BATTERY ⚠️");
        }
        
        telemetry.update();
    }
}