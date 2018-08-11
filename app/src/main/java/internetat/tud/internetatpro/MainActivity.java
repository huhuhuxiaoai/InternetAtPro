package internetat.tud.internetatpro;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.Fragment;
import android.os.Handler;
import android.support.wear.ambient.AmbientMode;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity implements Available,
        AmbientMode.AmbientCallbackProvider {

    private static final int NUM_PAGES = 2;
    private static final float MAX_LEVEL = 280.0f;
    private static final int ACTIVE_READ_PERIOD = 1000;
    private static final int AMBIENT_READ_PERIOD = 10000;

    private static Available availableListener;

    private TankPageFragment tankPageFragment;
    private PumpPageFragment pumpPageFragment;

    private BoxInsetLayout boxLayout;
    private LinearLayout noConnectionOverlay;
    private LinearLayout startOverlay;
    private LinearLayout loadingOverlay;
    private ProgressBar loadingProgressBar;
    private ProgressBar startProgressBar;
    private TextView noConnectionText;

    private Handler handler;
    private Runnable runnable;

    private int readPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        AmbientMode.attachAmbientSupport(this);

        // set up the pages of the main activity
        GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        DotsPageIndicator pageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        pageIndicator.setPager(pager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);

        // get references for the ui elements
        boxLayout = (BoxInsetLayout) findViewById(R.id.box_main);
        noConnectionOverlay = (LinearLayout) findViewById(R.id.overlay_no_connection);
        startOverlay = (LinearLayout) findViewById(R.id.overlay_start);
        loadingOverlay = (LinearLayout) findViewById(R.id.overlay_loading);
        loadingProgressBar = (ProgressBar) findViewById(R.id.progressbar_loading);
        startProgressBar = (ProgressBar) findViewById(R.id.progressbar_start);
        noConnectionText = (TextView) findViewById(R.id.text_no_connection);

        // change the color of the progressbars
        final int ACTIVE_UI = getResources()
                .getColor(R.color.active_ui_element, getTheme());
        loadingProgressBar.getIndeterminateDrawable()
                .setColorFilter(ACTIVE_UI, PorterDuff.Mode.SRC_IN);
        startProgressBar.getIndeterminateDrawable()
                .setColorFilter(ACTIVE_UI, PorterDuff.Mode.SRC_IN);

        // get a static reference to the Available Interface
        availableListener = this;

        // set the readPeriod to active
        readPeriod = ACTIVE_READ_PERIOD;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSoapReadTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSoapReadTask();
    }

    private void startSoapReadTask() {
        handler = new Handler();

        // start a Runnable that executes the SoapReadTask periodically
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SoapReadTask soapReadTask = new SoapReadTask();
                    soapReadTask.execute();
                }
                catch (Exception e) {
                    Log.e("ASYNC_TASK", e.toString());
                }
                finally {
                    if (readPeriod == AMBIENT_READ_PERIOD) {
                        // make sure the ui is updated every Period in Ambient Mode
                        getAmbientCallback().onUpdateAmbient();
                    }
                    handler.postDelayed(this, readPeriod);
                }
            }
        };
        handler.post(runnable);
    }

    private void stopSoapReadTask() {
        handler.removeCallbacks(runnable);
    }

    @Override
    public void updateTankLevels(float level1, float level2, float level3) {
        // scale the tank levels with the height of the tanks in the activity
        float tankHeight = getResources().getDimension(R.dimen.tank_height);
        level1 = level1 / MAX_LEVEL * tankHeight;
        level2 = level2 / MAX_LEVEL * tankHeight;
        level3 = level3 / MAX_LEVEL * tankHeight;

        if (tankPageFragment.isAdded()) {
            tankPageFragment.setTankLevels(level1, level2, level3);
        }
    }

    @Override
    public void updateCapacitiveSensorStates(boolean ll1, boolean ll2, boolean ll3,
                                             boolean lh1, boolean lh2, boolean lh3) {
        if (tankPageFragment.isAdded()) {
            tankPageFragment.setCapacitiveSensorStates(ll1, ll2, ll3, lh1, lh2, lh3);
        }
    }

    @Override
    public void updatePumpingState(boolean pumping, int tankA, int tankB) {
        if (pumpPageFragment.isAdded()) {
            pumpPageFragment.setPumpingState(pumping, tankA, tankB);
        }
    }

    @Override
    public void setNoConnectionOverlayVisibility(int visibility) {
        noConnectionOverlay.setVisibility(visibility);
    }

    @Override
    public void hideStartOverlay() {
        startOverlay.setVisibility(View.GONE);
    }

    @Override
    public void setLoadingOverlayVisibility(int visibility) {
        loadingOverlay.setVisibility(visibility);
    }

    private void enterAmbient() {
        readPeriod = AMBIENT_READ_PERIOD;

        // change the color of the ui elements
        boxLayout.setBackgroundColor(Color.BLACK);
        noConnectionOverlay.setBackgroundColor(Color.BLACK);
        startOverlay.setBackgroundColor(Color.BLACK);
        loadingOverlay.setBackgroundColor(Color.BLACK);
        loadingProgressBar.getIndeterminateDrawable()
                .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        startProgressBar.getIndeterminateDrawable()
                .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        noConnectionText.setTextColor(Color.WHITE);
    }

    private void exitAmbient() {
        readPeriod = ACTIVE_READ_PERIOD;

        // change the color of the ui elements
        final int ACCENT = getResources()
                .getColor(R.color.accent, getTheme());
        final int ACTIVE_UI = getResources()
                .getColor(R.color.active_ui_element, getTheme());
        final int BACKGROUND = getResources()
                .getColor(R.color.dark_background, getTheme());
        boxLayout.setBackgroundColor(BACKGROUND);
        noConnectionOverlay.setBackgroundColor(BACKGROUND);
        startOverlay.setBackgroundColor(BACKGROUND);
        loadingOverlay.setBackgroundColor(BACKGROUND);
        loadingProgressBar.getIndeterminateDrawable()
                .setColorFilter(ACTIVE_UI, PorterDuff.Mode.SRC_IN);
        startProgressBar.getIndeterminateDrawable()
                .setColorFilter(ACTIVE_UI, PorterDuff.Mode.SRC_IN);
        noConnectionText.setTextColor(ACCENT);
    }

    @Override
    public AmbientMode.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    public static Available getAvailableListener() {
        return availableListener;
    }

    private class MyAmbientCallback extends AmbientMode.AmbientCallback {
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);

            enterAmbient();
            if (tankPageFragment.isAdded()) tankPageFragment.enterAmbient();
            if (pumpPageFragment.isAdded()) pumpPageFragment.enterAmbient();
        }

        @Override
        public void onExitAmbient() {
            super.onExitAmbient();

            exitAmbient();
            if (tankPageFragment.isAdded()) tankPageFragment.exitAmbient();
            if (pumpPageFragment.isAdded()) pumpPageFragment.exitAmbient();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentGridPagerAdapter {

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getFragment(int row, int col) {
            switch (col) {
                case 0:
                    tankPageFragment = new TankPageFragment();
                    return tankPageFragment;
                case 1:
                    pumpPageFragment = new PumpPageFragment();
                    return pumpPageFragment;
            }

            return null;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int row) {
            return NUM_PAGES;
        }
    }
}
