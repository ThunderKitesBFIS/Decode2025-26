public class BasicActions {

    private final LinearOpMode opMode;
    private final DcMotor left;
    private final DcMotor right;

    public BasicActions(LinearOpMode opMode, DcMotor left, DcMotor right) {
        this.opMode = opMode;
        this.left = left;
        this.right = right;
    }

    public void driveForward(double power, long ms) {
        left.setPower(power);
        right.setPower(power);
        opMode.sleep(ms);
        left.setPower(0);
        right.setPower(0);
    }

    public void turnLeft(double power, long ms) {
        left.setPower(-power);
        right.setPower(power);
        opMode.sleep(ms);
        left.setPower(0);
        right.setPower(0);
    }

    public void turnRight(double power, long ms) {
        left.setPower(power);
        right.setPower(-power);
        opMode.sleep(ms);
        left.setPower(0);
        right.setPower(0);
    }

    public void stop(long ms) {
        left.setPower(0);
        right.setPower(0);
        opMode.sleep(ms);
    }
}
