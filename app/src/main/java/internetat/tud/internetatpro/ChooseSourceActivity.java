package internetat.tud.internetatpro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wear.ambient.AmbientMode;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ChooseSourceActivity extends Activity implements AmbientMode.AmbientCallbackProvider {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_source);

        // Enables Always-on
        AmbientMode.attachAmbientSupport(this);

        // get references for the ui elements
        FrameLayout button1 = (FrameLayout) findViewById(R.id.button_src_1);
        FrameLayout button2 = (FrameLayout) findViewById(R.id.button_src_2);
        FrameLayout button3 = (FrameLayout) findViewById(R.id.button_src_3);
        LinearLayout buttonCancel = (LinearLayout) findViewById(R.id.button_src_cancel);

        // init buttons
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTarget(1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTarget(2);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTarget(3);
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

    private void chooseTarget(int source) {
        // start the ChooseTargerActivity
        Intent intent =
                new Intent(ChooseSourceActivity.this, ChooseTargetActivity.class);
        intent.putExtra("source", source);
        startActivity(intent);
        // for a smooth transition, delay the finish a bit
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                finish();
            }
        };
        handler.postDelayed(runnable, 600);
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
