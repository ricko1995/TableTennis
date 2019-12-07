package com.example.tabletennis;

class ShotParameters {

    private float ballX, ballY, deviceX, deviceY, spinX, spinY, v0, d;
    private int delay;
    private boolean init;
    private final static float g = 9.81f;
    private final static float h0 = 0.25f;
    private final static float spinIntensityCoefficient = 3f; // 1<=spinIntensityCoefficient<=n  -> if 1 then its max spin
    private final static float[] motor1Coordinate = new float[]{0f,1f};
    private final static float[] motor2Coordinate = new float[]{(float) (Math.cos(Math.toRadians(30))), (float) (-Math.sin(Math.toRadians(30)))};
    private final static float[] motor3Coordinate = new float[]{(float) (-Math.cos(Math.toRadians(30))), (float) (-Math.sin(Math.toRadians(30)))};

    ShotParameters(float ballX, float ballY, float deviceX, float deviceY, float spinX, float spinY, float v0, int delay, boolean init) {
        this.ballX = ballX;
        this.ballY = ballY;
        this.deviceX = deviceX;
        this.deviceY = deviceY;
        this.spinX = spinX;
        this.spinY = spinY;
        this.v0 = v0;
        this.delay = delay;
        this.init = init;
        d = (float) Math.sqrt((this.deviceX-this.ballX)*
                (this.deviceX-this.ballX)+(this.deviceY -this.ballY)*
                (this.deviceY -this.ballY));
    }


    int v1Speed(){
        return  (int) ((this.v0 - this.v0/ spinIntensityCoefficient *Math.sqrt(Math.pow(spinX-motor1Coordinate[0],2)+Math.pow(spinY-motor1Coordinate[1],2))));
    }

    int v2Speed(){
        return (int) ((this.v0 - this.v0/ spinIntensityCoefficient *Math.sqrt(Math.pow(spinX-motor2Coordinate[0],2)+Math.pow(spinY-motor2Coordinate[1],2))));
    }

    int v3Speed(){
        return  (int) ((this.v0 - this.v0/ spinIntensityCoefficient *Math.sqrt(Math.pow(spinX-motor3Coordinate[0],2)+Math.pow(spinY-motor3Coordinate[1],2))));
    }

    float phiAngle(){
        return  (float) Math.atan((this.ballX-this.deviceX)/(this.ballY-this.deviceY));
    }

    float alphaAngle(){
        float first = d*d+h0*h0;
        float second = (g*d*d/(2f*v0))*(g*d*d/(2f*v0));
        if (first>second) {
            return 2f * (float) (Math.atan((d - Math.sqrt(first - second))) /
                    (h0 + (g * d * d / (2f * v0))));
        }else return 0;
    }

    boolean isInitiated(){
        return init;
    }


    //getter
    float getBallX() {
        return ballX;
    }

    float getBallY() {
        return ballY;
    }

    float getDeviceX() {
        return deviceX;
    }

    float getDeviceY() {
        return deviceY;
    }

    float getSpinX() {
        return spinX;
    }

    float getSpinY() {
        return spinY;
    }

    float getV0() {
        return v0;
    }

    int getDelay() {
        return delay;
    }

    //Setter

    void setBallX(float ballX) {
        this.ballX = ballX;
    }

    void setBallY(float ballY) {
        this.ballY = ballY;
    }

    void setDeviceX(float deviceX) {
        this.deviceX = deviceX;
    }

    void setDeviceY(float deviceY) {
        this.deviceY = deviceY;
    }

    void setSpinX(float spinX) {
        this.spinX = spinX;
    }

    void setSpinY(float spinY) {
        this.spinY = spinY;
    }

    void setV0(float v0) {
        this.v0 = v0;
    }

    void setDelay(int delay) {
        this.delay = delay;
    }

    private int map(float xMinA, float xMaxA, float xMinB, float xMaxB, float xA){
        int xb;
        xb=(int)((xMaxB-xMinB)*(xA-xMinA)/(xMaxA-xMinA)+xMinB);
        return xb;
    }

    EspShotData generateEspShotData(){
        return new EspShotData(v1Speed(), v2Speed(), v3Speed(), alphaAngle(), phiAngle(), this.delay);
    }
}
