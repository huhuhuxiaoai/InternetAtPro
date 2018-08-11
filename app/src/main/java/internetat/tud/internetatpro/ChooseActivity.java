package internetat.tud.internetatpro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wear.ambient.AmbientMode;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ChooseActivity extends WearableActivity  {
    Boolean flag_von_1=false;
    Boolean flag_von_2=false;
    Boolean flag_von_3=false;
    int von_number;
    int nach_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        setAmbientEnabled();

        //get UI element
        final FrameLayout button_von_1=(FrameLayout) findViewById(R.id.button_von_1);
        final FrameLayout button_von_2=(FrameLayout) findViewById(R.id.button_von_2);
        final FrameLayout button_von_3=(FrameLayout) findViewById(R.id.button_von_3);
        final FrameLayout button_nach_1=(FrameLayout) findViewById(R.id.button_nach_1);
        final FrameLayout button_nach_2=(FrameLayout) findViewById(R.id.button_nach_2);
        final FrameLayout button_nach_3=(FrameLayout) findViewById(R.id.button_nach_3);

        ImageView button_start=(ImageView)findViewById(R.id.image__pumping_start_button);
        ImageView button_cancel=(ImageView) findViewById(R.id.image_pumping_cancel_button);

        //set eventListeners for each button
        button_von_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseVon(1);
                button_von_1.setBackgroundColor(0xFFFFFFF);
                flag_von_1=true;
            }
        });
        button_von_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseVon(2);
                button_von_2.setBackgroundColor(0xFFFFFFF);
                flag_von_2=true;
            }
        });
        button_von_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseVon(3);
                button_von_3.setBackgroundColor(0xFFFFFFF);
                flag_von_3=true;
            }
        });
        button_nach_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseNach(1);
                if(flag_von_2||flag_von_3){
                    button_nach_1.setBackgroundColor(0xFFFFFFF);
                    button_nach_1.setClickable(false);
                    flag_von_2=false;
                    flag_von_3=false;
                }
            }
        });
        button_nach_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseNach(2);
                if(flag_von_1||flag_von_3){
                    button_nach_2.setBackgroundColor(0xFFFFFFF);
                    button_nach_2.setClickable(false);
                    flag_von_1=false;
                    flag_von_3=false;
                }
            }
        });
        button_nach_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseNach(3);
                if(flag_von_2||flag_von_1){
                    button_nach_3.setBackgroundColor(0xFFFFFFF);
                    button_nach_3.setClickable(false);
                    flag_von_1=false;
                    flag_von_2=false;
                }
            }
        });
        //noch arbeiten
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler=new Handler();
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                };
                handler.postDelayed(runnable, 400);
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWriteSoapRequest(von_number,nach_number);
            }
        });

    }

    private void chooseVon(int resource){
        von_number=resource;
    }
    private void chooseNach(int target){
        nach_number=target;
    }
    // After get the source_number and the target_number, we'll send the data to server
    private void sendWriteSoapRequest(int source,int target){
        //send a request to the server
        SoapWriteTask soapWriteTask=new SoapWriteTask(true,source,target);
        soapWriteTask.execute();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                finish();
            }
        };
        handler.postDelayed(runnable, 400);
    }

}
