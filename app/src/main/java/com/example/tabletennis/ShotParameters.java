package com.example.tabletennis;

public class ShotParameters {

    float ballX, ballY, deviceX, deviceY, spinX, spinY, v0, d;
    final float a = 0.2f;
    final float b = 0.0173f;
    final float c = 0.1f;
    final float g = 9.81f;
    final float h0 = 0.25f;

    public ShotParameters(float ballX, float ballY, float deviceX, float deviceY, float spinX, float spinY, float v0) {
        this.ballX = ballX;
        this.ballY = ballY;
        this.deviceX = deviceX;
        this.deviceY = deviceY;
        this.spinX = spinX;
        this.spinY = spinY;
        this.v0 = v0;
        d =(float) Math.sqrt((this.deviceX-this.ballX)*
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

    public float phiAngle(){
        float phi;
        phi = (float) Math.atan((this.ballX-this.deviceX)/(this.ballY-this.deviceY));
        return phi;
    }

    public float alphaAngle(){
        float alpha;
        alpha =  2f* (float) (Math.atan((d-Math.sqrt(d*d+h0*h0-(g*d*d/(2f*v0))*(g*d*d/(2f*v0)))))/
                (h0+(g*d*d/(2f*v0))));
        return alpha;
    }


    //getter
    public float getBallX() {
        return ballX;
    }

    public float getBallY() {
        return ballY;
    }

    public float getDeviceX() {
        return deviceX;
    }

    public float getDeviceY() {
        return deviceY;
    }

    public float getSpinX() {
        return spinX;
    }

    public float getSpinY() {
        return spinY;
    }

    public float getV0() {
        return v0;
    }
    //Setter

    public void setBallX(float ballX) {
        this.ballX = ballX;
    }

    public void setBallY(float ballY) {
        this.ballY = ballY;
    }

    public void setDeviceX(float deviceX) {
        this.deviceX = deviceX;
    }

    public void setDeviceY(float deviceY) {
        this.deviceY = deviceY;
    }

    public void setSpinX(float spinX) {
        this.spinX = spinX;
    }

    public void setSpinY(float spinY) {
        this.spinY = spinY;
    }

    public void setV0(float v0) {
        this.v0 = v0;
    }
}
