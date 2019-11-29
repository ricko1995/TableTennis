package com.example.tabletennis;

class ShotParameters {

    private float ballX, ballY, deviceX, deviceY, spinX, spinY, v0, d;
    private boolean init;
    private final static float a = 0.2f;
    private final static float b = 0.0173f;
    private final static float c = 0.1f;
    private final static float g = 9.81f;
    private final static float h0 = 0.25f;

    ShotParameters(float ballX, float ballY, float deviceX, float deviceY, float spinX, float spinY, float v0, boolean init) {
        this.ballX = ballX;
        this.ballY = ballY;
        this.deviceX = deviceX;
        this.deviceY = deviceY;
        this.spinX = spinX;
        this.spinY = spinY;
        this.v0 = v0;
        this.init = init;
        d = (float) Math.sqrt((this.deviceX-this.ballX)*
                (this.deviceX-this.ballX)+(this.deviceY -this.ballY)*
                (this.deviceY -this.ballY));
    }

//    v1 = v0 + 0.02f * yRot;
//    v3 = v0 + 0.0173f * xRot - 0.01f * yRot;
//    v2 = v0 - 0.0173f * xRot - 0.01f * yRot;


    float v1Speed(){
        float v1;
        v1 = this.v0 + a*this.spinY;
        return v1;
    }

    float v2Speed(){
        float v2;
        v2 = this.v0 + b*this.spinX - c*this.spinY;
        return v2;
    }

    float v3Speed(){
        float v3;
        v3 = this.v0 - b*this.spinX - c*this.spinY;
        return v3;
    }

    float phiAngle(){
        float phi;
        phi = (float) Math.atan((this.ballX-this.deviceX)/(this.ballY-this.deviceY));
        return phi;
    }

    float alphaAngle(){
        float alpha;
        alpha =  2f* (float) (Math.atan((d-Math.sqrt(d*d+h0*h0-(g*d*d/(2f*v0))*(g*d*d/(2f*v0)))))/
                (h0+(g*d*d/(2f*v0))));
        return alpha;
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
}
