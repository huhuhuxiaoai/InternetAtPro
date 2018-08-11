package internetat.tud.internetatpro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wear.ambient.AmbientMode;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ChooseTargetActivity extends Activity implements AmbientMode.AmbientCallbackProvider {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_target);

        // Enables Always-on
        AmbientMode.attachAmbientSupport(this);

        // get the source tank
        Intent intent = getIntent();
        final int source = intent.getIntExtra("source", 1);

        // get references for the ui elements
        FrameLayout button1 = (FrameLayout) findViewById(R.id.button_tgt_1);
        FrameLayout button2 = (FrameLayout) findViewById(R.id.button_tgt_2);
        TextView button1Text = (TextView) findViewById(R.id.text_tgt_1_button);
        TextView button2Text = (TextView) findViewById(R.id.text_tgt_2_button);
        LinearLayout buttonCancel = (LinearLayout) findViewById(R.id.button_tgt_cancel);

        // set target choices
        final int target1, target2;
        if (source == 1) {
            button1Text.setText(R.string.two);
            button2Text.setText(R.string.three);
            target1 = 2;
            target2 = 3;
        }
        else if (source == 2) {
            button1Text.setText(R.string.one);
            button2Text.setText(R.string.three);
            target1 = 1;
            target2 = 3;
        }
        else {
            button1Text.setText(R.string.one);
            button2Text.setText(R.string.two);
            target1 = 1;
            target2 = 2;
        }

        // init buttons
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSoapWriteRequest(source, target1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSoapWriteRequest(source, target2);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    public void run() {
                        finish();
                    }
                };
                handler.postDelayed(runnable, 400);
            }
        });
    }

    private void sendSoapWriteRequest(int source, int target) {
        // send a request to the server
        SoapWriteTask soapWriteTask = new SoapWriteTask(true, source, target);
        soapWriteTask.execute();
        // for a smooth transition, delay the finish a bit
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                finish();
            }
        };
        handler.postDelayed(runnable, 400);
    }

    @Override
    public AmbientMode.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientMode.AmbientCallback {
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);
            finish();
        }
    }
}
