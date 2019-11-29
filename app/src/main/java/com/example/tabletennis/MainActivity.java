//Written by Ricko 2019

package com.example.tabletennis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements BtDevicesDialog.SelectedDeviceListener {

    private static final float xSpinMin = -25f;
    private static final float xSpinMax = -xSpinMin;
    private static final float ySpinMin = 145f;
    private static final float ySpinMax = -ySpinMin;
    private static final float tableMinX = 0f;
    private static final float tableMaxX = 2.74f;
    private static final float tableMinY = 1.525f;
    private static final float tableMaxY = 0f;
    private static final float minV0 = 10f;
    private static final float maxV0 = 25f;

    float ballViewMappedX, ballViewMappedY, deviceViewMappedX, deviceViewMappedY, spinPointerMappedX, spinPointerMappedY, v0mapped;

    int axisRotDeg;

    TextView debugTxt1, debugTxt2, debugTxt3, debugTxt4, debugTxt5;
    String macAddressLoc = "";
    BtSend bt = new BtSend();
    int btSelectPosition = 0;
    int indexOfShot = 0;
    ShotParameters[] shotParameters = new ShotParameters[10];

    ImageView tableView, ballView, deviceView, spinCanvasView, axisView, spinPointerView;
    SeekBar speedSeekBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugTxt1 = findViewById(R.id.debugText1);
        debugTxt2 = findViewById(R.id.debugText2);
        debugTxt3 = findViewById(R.id.debugText3);
        debugTxt4 = findViewById(R.id.debugText4);
        debugTxt5 = findViewById(R.id.debugText5);


        for (int i = 0; i<shotParameters.length; i++){
            shotParameters[i] = new ShotParameters(0,0,0,0,0,0,0, false);
        }

        tableView = findViewById(R.id.tableImageView);
        ballView = findViewById(R.id.ballImageVIew);
        deviceView = findViewById(R.id.deviceImageView);
        spinCanvasView = findViewById(R.id.spinImageView);
        axisView = findViewById(R.id.axisImageView);
        spinPointerView = findViewById(R.id.spinPointerImageView);
        speedSeekBar = findViewById(R.id.speedSeekBar);

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setAll(false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Handler().postDelayed(this::setDefault,100);

        tableView.setOnTouchListener((v, event) -> {

            int tableViewXStart = (int) tableView.getX();
            int tableViewXEnd = (int) tableView.getX() + tableView.getWidth();
            int tableViewYStart = (int) tableView.getY();
            int tableViewYEnd = (int) tableView.getY() + tableView.getHeight();

            int deviceViewX = (int) deviceView.getX() + deviceView.getWidth()/2;
            int deviceViewY = (int) deviceView.getY() + deviceView.getHeight()/2;
            int ballViewX = (int) ballView.getX() + ballView.getWidth()/2;
            int ballViewY = (int) ballView.getY() + ballView.getHeight()/2;

            if(event.getX()>ballViewX-50 && event.getX()<ballViewX+50 &&
                    event.getY()>ballViewY-50 && event.getY()<ballViewY+50){
                ballView.setX(event.getX()-ballView.getWidth()/2f);
                ballView.setY(event.getY()-ballView.getHeight()/2f);

                if (event.getX()<tableViewXStart+ballView.getWidth()/2f){
                    ballView.setX(tableViewXStart);
                }
                if (event.getX()>tableViewXEnd-ballView.getWidth()/2f){
                    ballView.setX(tableViewXEnd-ballView.getWidth());
                }
                if (event.getY()<tableViewYStart+ballView.getHeight()/2f){
                    ballView.setY(tableViewYStart);
                }
                if (event.getY()>tableViewYEnd-ballView.getHeight()/2f){
                    ballView.setY(tableViewYEnd-ballView.getHeight());
                }
            }

            if(event.getX()>deviceViewX-50 && event.getX()<deviceViewX+50 &&
                    event.getY()>deviceViewY-50 && event.getY()<deviceViewY+50){
                deviceView.setX(event.getX()-deviceView.getWidth()/2f);
                deviceView.setY(event.getY()-deviceView.getHeight()/2f);

                if (event.getX()<tableViewXStart+deviceView.getWidth()/2f){
                    deviceView.setX(tableViewXStart);
                }
                if (event.getX()>tableViewXEnd-deviceView.getWidth()/2f){
                    deviceView.setX(tableViewXEnd-deviceView.getWidth());
                }
                if (event.getY()<tableViewYStart+deviceView.getHeight()/2f){
                    deviceView.setY(tableViewYStart);
                }
                if (event.getY()>tableViewYEnd-deviceView.getHeight()/2f){
                    deviceView.setY(tableViewYEnd-deviceView.getHeight());
                }
            }


            if(event.getAction()== MotionEvent.ACTION_UP){
                if(deviceViewX==ballViewX && deviceViewY==ballViewY){
                    ballView.setX(ballView.getX()+200);
                }
            }

            setAll(false);
            debugTxt1.setText(String.valueOf(deviceViewMappedX));
            debugTxt2.setText(String.valueOf(deviceViewMappedY));
            return true;
        });

        spinCanvasView.setOnTouchListener((v, event)->{

            float a = event.getX() + spinCanvasView.getX();
            float b = event.getY() + spinCanvasView.getY();
            int r = spinCanvasView.getWidth()/2 - spinPointerView.getWidth()/2;
            float x00 = spinCanvasView.getX() + r;
            float y00 = spinCanvasView.getY() + r;

            if (event.getX() > spinCanvasView.getWidth() - spinPointerView.getWidth()) {
                a = spinCanvasView.getX() + spinCanvasView.getWidth() - spinPointerView.getWidth();
            }
            if (event.getX() < 0) {
                a = spinCanvasView.getX();
            }

            double y1_lim = Math.sqrt(Math.pow(r, 2)-Math.pow(a-x00, 2))+y00;
            double y2_lim = -Math.sqrt(Math.pow(r, 2)-Math.pow(a-x00, 2))+y00;

            if (y1_lim < b)
                b = (float) y1_lim;
            if (y2_lim > b)
                b = (float) y2_lim;

            spinPointerView.setX(a);
            spinPointerView.setY(b);

            float x_lok = map(0, spinCanvasView.getWidth() - spinPointerView.getWidth(),
                    -spinCanvasView.getWidth()/2f + spinPointerView.getWidth()/2f,
                    spinCanvasView.getWidth()/2f - spinPointerView.getWidth()/2f, a);
            float y_lok =  map(0, spinCanvasView.getHeight() - spinPointerView.getHeight(),
                    -spinCanvasView.getHeight()/2f + spinPointerView.getHeight()/2f,
                    spinCanvasView.getHeight()/2f - spinPointerView.getHeight()/2f, b);
            axisRotDeg = (int) (Math.atan2(y_lok, x_lok)*180/Math.PI);

            setAll(false);
            debugTxt3.setText(String.valueOf(spinPointerMappedX));
            debugTxt4.setText(String.valueOf(spinPointerMappedY));
            return true;
        });



        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        LinearLayout linLayToAnimate = findViewById(R.id.linLayToAnimate);

        mainLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {
                if(indexOfShot >0){
                    slideRight(linLayToAnimate);
                    indexOfShot--;
                    new Handler().postDelayed(()-> setAll(true), 100);
                    debugTxt5.setText(String.valueOf(indexOfShot));
                }
            }
            public void onSwipeLeft() {
                if(indexOfShot <9) {
                    slideLeft(linLayToAnimate);
                    indexOfShot++;
                    new Handler().postDelayed(()-> setAll(true), 100);
                    debugTxt5.setText(String.valueOf(indexOfShot));
                }
                //Toast.makeText(getBaseContext(), "left", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.btDevicesBtn:
            {
                openBtDeviceDialog();
                break;
            }
            case R.id.settBtn:
            {
                openSettingsDialog();
                break;
            }
            case R.id.helpBtn:
            {
                openHelpDialog();
                break;
            }
            default:
                break;
        }

        return true;
    }

    public void slideRight(View view){
        //view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                -view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        view.setAnimation(animate);



    }
    public void FadeOutAnimation(View view){
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(500);

        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(500);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        view.setAnimation(animation);
    }

    public void slideLeft(View view){
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private void openBtDeviceDialog(){
        BtDevicesDialog btDevicesDialog = new BtDevicesDialog(btSelectPosition);
        btDevicesDialog.show(getSupportFragmentManager(), "btDeviceDialog");
    }

    private void openSettingsDialog(){
        debugTxt1.setText("Settings");
        openBtSocket();
    }

    private void openHelpDialog(){
        bt.write("hello!!");
        debugTxt1.setText(String.valueOf(shotParameters[1].isInitiated()));
    }


    @Override
    public void onConfirmedBtDeviceSelected(int pos, String deviceName, String macAddress) {
        btSelectPosition = pos;
        macAddressLoc = macAddress;
        debugTxt1.setText(String.valueOf(pos));
    }

     void openBtSocket(){
        bt.init(macAddressLoc);
        bt.run(this, (rcString, handel) -> handel.runOnUiThread(() -> {}));
        shotParameters[0].setSpinX(spinPointerView.getX());
        shotParameters[0].setSpinY(spinPointerView.getY());
        float v1 = shotParameters[0].v1Speed();
        float v2 = shotParameters[0].v2Speed();
        float v3 = shotParameters[0].v3Speed();
        bt.write(String.format("{\"vUp\":%s,\"vRight\":%s,\"vLeft\":%s}\n", String.valueOf(v1), String.valueOf(v2), String.valueOf(v3)));
    }

    void setDefault(){
        ballView.setX(tableView.getX()+tableView.getWidth()-ballView.getWidth()*2f);
        ballView.setY(tableView.getY()+tableView.getHeight()/2f-ballView.getHeight()/2f);
        deviceView.setX(tableView.getX()+deviceView.getWidth());
        deviceView.setY(tableView.getY()+tableView.getHeight()/2f-deviceView.getHeight()/2f);
        spinPointerView.setX(spinCanvasView.getX()+spinCanvasView.getWidth()/2f-spinPointerView.getWidth()/2f);
        spinPointerView.setY(spinCanvasView.getY()+spinCanvasView.getHeight()/2f-spinPointerView.getHeight()/2f);
        speedSeekBar.setProgress(0);
        axisView.setRotation(0);
        spinPointerView.setRotation(0);


        shotParameters[indexOfShot] = new ShotParameters(2.41f, 0.7625f, 0.33f, 0.7625f, 0f, 0f, 10f, true);

    }

    void setAll(boolean swiping){
        if(swiping && !shotParameters[indexOfShot].isInitiated()){
            setDefault();
        }else if(swiping){
            ballView.setX(map(tableMinX, tableMaxX, tableView.getX(), tableView.getX() + tableView.getWidth(), shotParameters[indexOfShot].getBallX())-ballView.getWidth()/2f);
            ballView.setY(map(tableMinY, tableMaxY, tableView.getY(), tableView.getY() + tableView.getHeight(), shotParameters[indexOfShot].getBallY())-ballView.getHeight()/2f);
            deviceView.setX(map(tableMinX, tableMaxX, tableView.getX(), tableView.getX() + tableView.getWidth(),shotParameters[indexOfShot].getDeviceX())-deviceView.getWidth()/2f);
            deviceView.setY(map(tableMinY, tableMaxY, tableView.getY(), tableView.getY() + tableView.getHeight(),shotParameters[indexOfShot].getDeviceY())-deviceView.getHeight()/2f);
            spinPointerView.setX(map(xSpinMin, xSpinMax, spinCanvasView.getX(), spinCanvasView.getX() + spinCanvasView.getWidth() - spinPointerView.getWidth(),shotParameters[indexOfShot].getSpinX()));
            spinPointerView.setY(map(ySpinMin, ySpinMax, spinCanvasView.getY(), spinCanvasView.getY() + spinCanvasView.getHeight() - spinPointerView.getHeight(), shotParameters[indexOfShot].getSpinY()));
            speedSeekBar.setProgress((int) map(minV0, maxV0, 0f, speedSeekBar.getMax(), shotParameters[indexOfShot].getV0()));

            ballViewMappedX = map(tableView.getX(), tableView.getX() + tableView.getWidth(), tableMinX, tableMaxX, ballView.getX() + ballView.getWidth() / 2f);
            ballViewMappedY = map(tableView.getY(), tableView.getY() + tableView.getHeight(), tableMinY, tableMaxY, ballView.getY() + ballView.getHeight() / 2f);
            deviceViewMappedX = map(tableView.getX(), tableView.getX() + tableView.getWidth(), tableMinX, tableMaxX, deviceView.getX() + deviceView.getWidth() / 2f);
            deviceViewMappedY = map(tableView.getY(), tableView.getY() + tableView.getHeight(), tableMinY, tableMaxY, deviceView.getY() + deviceView.getHeight() / 2f);
            spinPointerMappedX = map(spinCanvasView.getX(), spinCanvasView.getX() + spinCanvasView.getWidth() - spinPointerView.getWidth(), xSpinMin, xSpinMax, spinPointerView.getX());
            spinPointerMappedY = map(spinCanvasView.getY(), spinCanvasView.getY() + spinCanvasView.getHeight() - spinPointerView.getHeight(), ySpinMin, ySpinMax, spinPointerView.getY());
            v0mapped = map(0f, speedSeekBar.getMax(), minV0, maxV0, speedSeekBar.getProgress());

            setAxisRot();
        } else {
            ballViewMappedX = map(tableView.getX(), tableView.getX() + tableView.getWidth(), tableMinX, tableMaxX, ballView.getX() + ballView.getWidth() / 2f);
            ballViewMappedY = map(tableView.getY(), tableView.getY() + tableView.getHeight(), tableMinY, tableMaxY, ballView.getY() + ballView.getHeight() / 2f);
            deviceViewMappedX = map(tableView.getX(), tableView.getX() + tableView.getWidth(), tableMinX, tableMaxX, deviceView.getX() + deviceView.getWidth() / 2f);
            deviceViewMappedY = map(tableView.getY(), tableView.getY() + tableView.getHeight(), tableMinY, tableMaxY, deviceView.getY() + deviceView.getHeight() / 2f);
            spinPointerMappedX = map(spinCanvasView.getX(), spinCanvasView.getX() + spinCanvasView.getWidth() - spinPointerView.getWidth(), xSpinMin, xSpinMax, spinPointerView.getX());
            spinPointerMappedY = map(spinCanvasView.getY(), spinCanvasView.getY() + spinCanvasView.getHeight() - spinPointerView.getHeight(), ySpinMin, ySpinMax, spinPointerView.getY());
            v0mapped = map(0f, speedSeekBar.getMax(), minV0, maxV0, speedSeekBar.getProgress());

            setAxisRot();

            shotParameters[indexOfShot].setBallX(ballViewMappedX);
            shotParameters[indexOfShot].setBallY(ballViewMappedY);
            shotParameters[indexOfShot].setDeviceX(deviceViewMappedX);
            shotParameters[indexOfShot].setDeviceY(deviceViewMappedY);
            shotParameters[indexOfShot].setSpinX(spinPointerMappedX);
            shotParameters[indexOfShot].setSpinY(spinPointerMappedY);
            shotParameters[indexOfShot].setV0(v0mapped);
        }
    }

    void  setAxisRot(){
        float x_lok = map(0, spinCanvasView.getWidth() - spinPointerView.getWidth(),
                -spinCanvasView.getWidth()/2f + spinPointerView.getWidth()/2f,
                spinCanvasView.getWidth()/2f - spinPointerView.getWidth()/2f, spinPointerView.getX()+spinPointerView.getWidth()/2f);
        float y_lok =  map(0, spinCanvasView.getHeight() - spinPointerView.getHeight(),
                -spinCanvasView.getHeight()/2f + spinPointerView.getHeight()/2f,
                spinCanvasView.getHeight()/2f - spinPointerView.getHeight()/2f, spinPointerView.getY()+spinPointerView.getHeight()/2f);
        axisRotDeg = (int) (Math.atan2(y_lok, x_lok)*180/Math.PI);
        axisView.setRotation(axisRotDeg);
        spinPointerView.setRotation(axisRotDeg);
    }

    public float map(float xMinA, float xMaxA, float xMinB, float xMaxB, float xA){
        float xb;
        xb=(xMaxB-xMinB)*(xA-xMinA)/(xMaxA-xMinA)+xMinB;
        return xb;
    }
}
