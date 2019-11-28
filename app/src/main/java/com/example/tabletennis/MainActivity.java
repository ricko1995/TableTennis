//Written by Ricko 2019
//Contributed by Hextech 2019

package com.example.tabletennis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BtDevicesDialog.SelectedDeviceListener {

    TextView debugTxt;
    String macAddressLoc = "";
    BtSend bt = new BtSend();
    int position = 0;
    int exerciseCount = 1;
    ShotParameters shotParameters;

    ImageView tableView, ballView, deviceView, spinCanvasView, axisView, spinPointerView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableView = findViewById(R.id.tableImageView);
        ballView = findViewById(R.id.ballImageVIew);
        deviceView = findViewById(R.id.deviceImageView);
        spinCanvasView = findViewById(R.id.spinImageView);
        axisView = findViewById(R.id.axisImageView);
        spinPointerView = findViewById(R.id.spinPointerImageView);

        new Handler().postDelayed(()->{
            ballView.setX(tableView.getX()+tableView.getWidth()-ballView.getWidth()*2f);
            ballView.setY(tableView.getY()+tableView.getHeight()/2f-ballView.getHeight()/2f);
            deviceView.setX(tableView.getX()+deviceView.getWidth());
            deviceView.setY(tableView.getY()+tableView.getHeight()/2f-deviceView.getHeight()/2f);
            spinPointerView.setX(spinCanvasView.getX()+spinCanvasView.getWidth()/2f-spinPointerView.getWidth()/2f);
            spinPointerView.setY(spinCanvasView.getY()+spinCanvasView.getHeight()/2f-spinPointerView.getHeight()/2f);
            debugTxt.setText(String.valueOf(exerciseCount));
            shotParameters = new ShotParameters(ballView.getX(), ballView.getY(), deviceView.getX(), deviceView.getY(), spinPointerView.getX(), spinPointerView.getY(), 15f);
        },100);

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

            float x_lok = (float) map(0, spinCanvasView.getWidth() - spinPointerView.getWidth(),
                    -spinCanvasView.getWidth()/2f + spinPointerView.getWidth()/2f,
                    spinCanvasView.getWidth()/2f - spinPointerView.getWidth()/2f, a);
            float y_lok = (float) map(0, spinCanvasView.getHeight() - spinPointerView.getHeight(),
                    -spinCanvasView.getHeight()/2f + spinPointerView.getHeight()/2f,
                    spinCanvasView.getHeight()/2f - spinPointerView.getHeight()/2f, b);
            int axisRotDeg = (int) (Math.atan2(y_lok, x_lok)*180/Math.PI);
            axisView.setRotation(axisRotDeg);
            spinPointerView.setRotation(axisRotDeg);
            debugTxt.setText(String.valueOf(x_lok));

            return true;
        });


        debugTxt = findViewById(R.id.debugText);
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        LinearLayout linLayToAnimate = findViewById(R.id.linLayToAnimate);
        LinearLayout linLayToFade = findViewById(R.id.linLayToFade);

        mainLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {
                if(exerciseCount>1){
                    slideRight(linLayToAnimate);
                    exerciseCount--;
                    debugTxt.setText(String.valueOf(exerciseCount));

                    //FadeOutAnimation(linLayToFade);
                    //Toast.makeText(getBaseContext(), "right", Toast.LENGTH_SHORT).show();
                }
            }
            public void onSwipeLeft() {
                slideLeft(linLayToAnimate);
                exerciseCount++;
                debugTxt.setText(String.valueOf(exerciseCount));

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
        view.setVisibility(View.VISIBLE);
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
    // slide the view from its current position to below itself
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
        BtDevicesDialog btDevicesDialog = new BtDevicesDialog(position);
        btDevicesDialog.show(getSupportFragmentManager(), "btDeviceDialog");
    }

    private void openSettingsDialog(){
        debugTxt.setText("Settings");
        openBtSocket();
    }

    private void openHelpDialog(){
        bt.write("hello!!");
        debugTxt.setText("Help!!!!");
    }


    @Override
    public void onConfirmedBtDeviceSelected(int pos, String deviceName, String macAddress) {
        position = pos;
        macAddressLoc = macAddress;
        debugTxt.setText(String.valueOf(pos));
    }

     void openBtSocket(){
        bt.init(macAddressLoc);
        bt.run(this, (rcString, handel) -> handel.runOnUiThread(() -> {}));
        shotParameters.setSpinX(spinPointerView.getX());
        shotParameters.setSpinY(spinPointerView.getY());
        float v1 = shotParameters.v1Speed();
        float v2 = shotParameters.v2Speed();
        float v3 = shotParameters.v3Speed();
        bt.write(String.format("{\"vUp\":%s,\"vRight\":%s,\"vLeft\":%s}\n", String.valueOf(v1), String.valueOf(v2), String.valueOf(v3)));
    }

    public double map(double xamin, double xamax, double xbmin, double xbmax, double xa){
        double xb;
        xb=(xbmax-xbmin)*(xa-xamin)/(xamax-xamin)+xbmin;
        return xb;
    }
}
