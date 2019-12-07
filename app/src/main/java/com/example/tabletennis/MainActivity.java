//Written by Ricko 2019

package com.example.tabletennis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements BtDevicesDialog.SelectedDeviceListener{

    private static final float xSpinMin = -1f;
    private static final float xSpinMax = -xSpinMin;
    private static final float ySpinMin = 1f;
    private static final float ySpinMax = -ySpinMin;
    private static final float tableMinX = 0f;
    private static final float tableMaxX = 2.74f;
    private static final float tableMinY = 1.525f;
    private static final float tableMaxY = 0f;
    static float minV0 = 20f;
    static float maxV0 = 180f;
    private static final int maxNumberOfShots = 10;
    private static final int maxNumberOfExercises = 10;

    float ballViewMappedX, ballViewMappedY, deviceViewMappedX, deviceViewMappedY, spinPointerMappedX, spinPointerMappedY, v0mapped;

    int axisRotDeg;
    String json;

    TextView debugTxt1, debugTxt2, debugTxt3, debugTxt4, debugTxt5;
    TextView currentExerciseTxt, currentShotTxt;
    TextView delayTxt;
    Button sendBtn, stopBtn;
    String macAddressLoc = "";
    BtSend bt = new BtSend();
    int btSelectPosition = 0;
    int indexOfShot = 0;
    int indexOfExercise = 0;
    ShotParameters[] shotParameters = new ShotParameters[maxNumberOfShots];

    ImageView tableView, ballView, deviceView, spinCanvasView, axisView, spinPointerView;
    SeekBar speedSeekBar;

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
//    nickNameMain = mPrefs.getString("nickName", "");

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getSharedPreferences("label", 0);
        mEditor = mPrefs.edit();

        debugTxt1 = findViewById(R.id.debugText1);
        debugTxt2 = findViewById(R.id.debugText2);
        debugTxt3 = findViewById(R.id.debugText3);
        debugTxt4 = findViewById(R.id.debugText4);
        debugTxt5 = findViewById(R.id.debugText5);


        for (int i = 0; i<shotParameters.length; i++){
            shotParameters[i] = new ShotParameters(0,0,0,0,0,0,0, 0, false);
        }

        tableView = findViewById(R.id.tableImageView);
        ballView = findViewById(R.id.ballImageVIew);
        deviceView = findViewById(R.id.deviceImageView);
        spinCanvasView = findViewById(R.id.spinImageView);
        axisView = findViewById(R.id.axisImageView);
        spinPointerView = findViewById(R.id.spinPointerImageView);
        speedSeekBar = findViewById(R.id.speedSeekBar);
        sendBtn = findViewById(R.id.sendBtn);
        stopBtn = findViewById(R.id.stopBtn);
        delayTxt = findViewById(R.id.delayTxt);
        currentExerciseTxt = findViewById(R.id.currentExerciseTxt);
        currentShotTxt = findViewById(R.id.currentShotTxt);

        delayTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    //shotDelay = Integer.parseInt(delayTxt.getText().toString());
                    setAll(false);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        sendBtn.setOnClickListener(v-> {
            setJsonObjects();
            bt.write(json+"\n");
        });

        stopBtn.setOnClickListener(v -> {
            debugTxt4.setText("[{\"alpha\":0,\"delay\":0,\"phi\":0,\"v1\":0,\"v2\":0,\"v3\":0}]");
            bt.write("[{\"alpha\":0,\"delay\":0,\"phi\":0,\"v1\":0,\"v2\":0,\"v3\":0}]");
        });

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    setAll(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Handler().postDelayed(()->{
            setDefault();
            if(macAddressLoc.equals("")){
                openBtDeviceDialog();
            }
            Intent myIntent = new Intent(this, SettingsActivity.class);
            myIntent.putExtra("firstStart", true);
            startActivity(myIntent);
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

            setAll(false);
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
            return true;
        });



        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        LinearLayout linLayToAnimate = findViewById(R.id.linLayToAnimate);
        LinearLayout linLaySecondary = findViewById(R.id.linLayToFade);

        mainLayout.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {
                if(indexOfShot >0){
                    MyAnimations.slideRight(linLayToAnimate);
                    MyAnimations.slideRightSecondary(linLaySecondary);
                    indexOfShot--;
                    new Handler().postDelayed(()-> setAll(true), 100);
                    currentShotTxt.setText(String.format("S%s", String.valueOf(indexOfShot)));
                }
            }
            public void onSwipeLeft() {
                if(indexOfShot < maxNumberOfShots-1) {
                    MyAnimations.slideLeft(linLayToAnimate);
                    MyAnimations.slideLeftSecondary(linLaySecondary);
                    indexOfShot++;
                    new Handler().postDelayed(()-> setAll(true), 100);
                    currentShotTxt.setText(String.format("S%s", String.valueOf(indexOfShot)));
                }
                //Toast.makeText(getBaseContext(), "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom(){
                if(indexOfExercise>0){
                    MyAnimations.slideUp(linLayToAnimate);
                    MyAnimations.slideUpSecondary(linLaySecondary);
                    indexOfExercise--;
                    //new Handler().postDelayed(()-> setAll(true), 100);
                    currentExerciseTxt.setText(String.format("E%s", String.valueOf(indexOfExercise)));
                }
            }
            public void onSwipeTop(){
                if(indexOfExercise<maxNumberOfExercises-1){
                    MyAnimations.slideDown(linLayToAnimate);
                    MyAnimations.slideDownSecondary(linLaySecondary);
                    indexOfExercise++;
                    //new Handler().postDelayed(()-> setAll(true), 100);
                    currentExerciseTxt.setText(String.format("E%s", String.valueOf(indexOfExercise)));
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        importSettingsValue();
        macAddressLoc = mPrefs.getString("macAddress", "");
        if(!macAddressLoc.equals("")) openBtSocket();
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

    private void openBtDeviceDialog(){
        BtDevicesDialog btDevicesDialog = new BtDevicesDialog(btSelectPosition);
        btDevicesDialog.show(getSupportFragmentManager(), "btDeviceDialog");
    }

    private void openSettingsDialog(){
        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);

    }

    private void openHelpDialog(){
        debugTxt1.setText(String.valueOf(shotParameters[1].isInitiated()));
    }


    @Override
    public void onConfirmedBtDeviceSelected(int pos, String deviceName, String macAddress) {
        btSelectPosition = pos;
        macAddressLoc = macAddress;
        mEditor.putString("macAddress", macAddressLoc).apply();
        openBtSocket();
        debugTxt1.setText(String.valueOf(pos));
    }

     void openBtSocket(){
        bt.init(macAddressLoc);
        //bt.run(this, (rcString, handel) -> handel.runOnUiThread(() -> {}));
    }

    void setDefault(){
        currentExerciseTxt.setX(tableView.getWidth()/4-currentExerciseTxt.getWidth()/2);
        currentExerciseTxt.setY(tableView.getHeight()/2-currentExerciseTxt.getHeight()/2);

        currentShotTxt.setX(tableView.getWidth()*3/4-currentShotTxt.getWidth()/2);
        currentShotTxt.setY(tableView.getHeight()/2-currentShotTxt.getHeight()/2);

        shotParameters[indexOfShot] = new ShotParameters(
                map(0f, 1f, 1.4f, 2.6f, new Random().nextFloat()),
                map(0f, 1f, 0.2f, 1.5f, new Random().nextFloat()), 0.33f, 0.7625f,
                map(0f, 1f, -0.707f, 0.707f, new Random().nextFloat()),
                map(0f, 1f, 0.707f, -0.707f, new Random().nextFloat()),
                map(0f, 1f, minV0, maxV0, new Random().nextFloat()),
                new Random().nextInt(10)+1, true);

        setAll(true);
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
            delayTxt.setText(String.valueOf(shotParameters[indexOfShot].getDelay()));

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
            shotParameters[indexOfShot].setDelay(Integer.parseInt(delayTxt.getText().toString()));

            debugTxt1.setText(String.valueOf(shotParameters[0].v1Speed()));
            debugTxt2.setText(String.valueOf(shotParameters[0].v2Speed()));
            debugTxt3.setText(String.valueOf(shotParameters[0].v3Speed()));
        }
    }

    void setJsonObjects(){
        Gson gson = new Gson();
        ArrayList<EspShotData> espShotData = new ArrayList<>();
        for(int i=0;i<shotParameters.length;i++){
            if(shotParameters[i].isInitiated()){
                espShotData.add(shotParameters[i].generateEspShotData());
            }
            else break;
        }
        json = gson.toJson(espShotData);
        debugTxt4.setText(json);
//        Type t = new TypeToken<ArrayList<EspShotData>>(){}.getType();
//        List<EspShotData> sd = gson.fromJson(json, t);
//        debugTxt3.setText(String.valueOf(sd.get(0).v1));
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

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit..", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    void importSettingsValue(){
        minV0 = Float.valueOf(SettingsActivity.minSpeedPref.getText());
        maxV0 = Float.valueOf(SettingsActivity.maxSpeedPref.getText());
        ShotParameters.spinIntensityCoefficient = Float.valueOf(SettingsActivity.spinIntensityPref.getText());
        setAll(false);
    }

}
