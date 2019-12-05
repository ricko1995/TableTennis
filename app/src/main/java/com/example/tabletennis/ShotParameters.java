package com.example.tabletennis;

class ShotParameters {

    private float ballX, ballY, deviceX, deviceY, spinX, spinY, v0, d;
    private int delay;
    private boolean init;
    private final static float a = 0.02f;
    private final static float b = 0.0173f;
    private final static float c = 0.01f;
    private final static float g = 9.81f;
    private final static float h0 = 0.25f;
    private final static float vMin = 0f;
    private final static float vMax = 25f;
    private final static int minPWM = 0;
    private final static int maxPWM = 180;
    private final static float multiplicator = 5f;
    private final static float multiplicatorNext = 1.17f;
    private final static float[] motor1Coordinate = new float[]{0f,multiplicator};
    private final static float[] motor2Coordinate = new float[]{(float) (multiplicator*Math.cos(Math.toRadians(30))), (float) (-multiplicator*Math.sin(Math.toRadians(30)))};
    private final static float[] motor3Coordinate = new float[]{(float) (-multiplicator*Math.cos(Math.toRadians(30))), (float) (-multiplicator*Math.sin(Math.toRadians(30)))};

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

//    v1 = v0 + 0.02f * yRot;
//    v3 = v0 + 0.0173f * xRot - 0.01f * yRot;
//    v2 = v0 - 0.0173f * xRot - 0.01f * yRot;


    int v1Speed(){
        int v1;
//        v1 = this.v0 + a*this.spinY;
        v1 = (int) (multiplicatorNext*(this.v0 - Math.sqrt(Math.pow(spinX-motor1Coordinate[0],2)+Math.pow(spinY-motor1Coordinate[1],2))));
        return map(vMin,vMax, minPWM, maxPWM, v1);
    }

    int v2Speed(){
        int v2;
//        v2 = this.v0 + b*this.spinX - c*this.spinY;
        v2 = (int) (multiplicatorNext*(this.v0 - Math.sqrt(Math.pow(spinX-motor2Coordinate[0],2)+Math.pow(spinY-motor2Coordinate[1],2))));
        return map(vMin,vMax, minPWM, maxPWM, v2);
    }

    int v3Speed(){
        float v3;
//        v3 = this.v0 - b*this.spinX - c*this.spinY;
        v3 = (int) (multiplicatorNext*(this.v0 - Math.sqrt(Math.pow(spinX-motor3Coordinate[0],2)+Math.pow(spinY-motor3Coordinate[1],2))));
        return map(vMin,vMax, minPWM, maxPWM, v3);
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
