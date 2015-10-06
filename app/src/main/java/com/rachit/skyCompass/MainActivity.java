package com.rachit.skyCompass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    public TextView tvHeading;

    public ImageView worldOverlay;

    public int apiVersion = Build.VERSION.SDK_INT;

    public ImageView compass;

    private RelativeLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // our compass image
        image = (ImageView) findViewById(R.id.imageViewCompass);

        // TextView that will tell the user what degree is the heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Button skinsButton = (Button) findViewById((R.id.skinsButton));
        Button feedbackButton = (Button) findViewById((R.id.feedbackButton));
        worldOverlay = (ImageView) findViewById(R.id.worldOverlay);
        worldOverlay.setAlpha(0.1f);
        myLayout = (RelativeLayout) findViewById(R.id.background);

        Typeface font = Typeface.createFromAsset(getAssets(), "HelveticaNeue.ttf");
        tvHeading.setTypeface(Typeface.createFromAsset(getAssets(),"Sansation-Regular.ttf"));

        skinsButton.setTypeface(font);
        feedbackButton.setTypeface(font);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    private boolean earth = false;
    private boolean candy = false;
    private boolean sky = true;

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        int degree = Math.round(event.values[0]);

        //Change degree heading, add direction indicator
        String toDisplay = Integer.toString(degree)+"°";

        if ( degree > 337.5 | degree <= 22.5 ){
            toDisplay += " N";
        }
        else if ( degree > 22.5 & degree <= 67.5 ){
            toDisplay += " NE";
        }
        else if ( degree > 67.5 & degree <= 112.5 ){
            toDisplay += " E";
        }
        else if ( degree > 112.5 & degree <= 157.5 ){
            toDisplay += " SE";
        }
        else if ( degree > 157.5 & degree <= 202.5 ){
            toDisplay += " S";
        }
        else if ( degree > 202.5 & degree <= 247.5 ){
            toDisplay += " SW";
        }
        else if ( degree > 247.5 & degree <= 292.5 ){
            toDisplay += " W";
        }
        else if ( degree > 292.5 & degree <= 337.5 ){
            toDisplay += " NW";
        }

            tvHeading.setText(toDisplay);

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(200);

            ra.setFillAfter(true);

            // Start the animation
            image.startAnimation(ra);
            currentDegree = -degree;

        int colorChange;

        if (degree > 180) {
            colorChange = ( degree - 360) * (-1);
        }
        else {
            colorChange = degree;
        }

        int myColor;

        // To change color as degrees change
        if (earth) {
            myColor = Color.rgb(64 + colorChange/2, 224 - colorChange, 208 - colorChange);
            myLayout.setBackgroundColor(myColor);
            if (apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(myColor);
                getWindow().setNavigationBarColor(myColor);
            }
        }
        else if (candy){
            myColor= Color.rgb(255 - colorChange, 95 - colorChange/2 , 75 + colorChange);
            myLayout.setBackgroundColor(myColor);
            if (apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(myColor);
                getWindow().setNavigationBarColor(myColor);
            }
        }
        else if (sky){
            myColor = Color.rgb(colorChange, 191 - colorChange , 255 - colorChange);
            myLayout.setBackgroundColor(myColor);
            if (apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(myColor);
                getWindow().setNavigationBarColor(myColor);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Opens mail apps to send feedback
    public void sendFeedback(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);intent.setType("text/email");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.skyCompass));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.myEmail)});
        Intent mailer = Intent.createChooser(intent, null);
        startActivity(mailer);
    }

    // Opens dialog box showing skin options
    public void showSkins (View view) {
        CharSequence colors[] = new CharSequence[]{"Earth", "Candy","Sky", "Dark"};

        compass = (ImageView) findViewById(R.id.imageViewCompass);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select skin");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if ( which == 3 ){
                    myLayout.setBackgroundColor(Color.parseColor("#2F2F2F"));
                    worldOverlay.setAlpha(0.3f);
                    if (apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(Color.parseColor("#2F2F2F"));
                        getWindow().setNavigationBarColor(Color.parseColor("#2F2F2F"));
                    }
                    candy=false;
                    earth=false;
                    sky=false;
                }
                else if ( which == 0 ){
                    candy=false;
                    earth=true;
                    sky=false;
                    worldOverlay.setAlpha(0.1f);
                }
                else if ( which == 1 ){
                    candy=true;
                    earth=false;
                    sky=false;
                    worldOverlay.setAlpha(0.1f);
                }
                else if ( which == 2 ){
                    sky=true;
                    earth=false;
                    candy=false;
                    worldOverlay.setAlpha(0.1f);
                }
            }
        });
        builder.show();
    }
}
