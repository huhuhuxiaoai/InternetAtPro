package internetat.tud.internetatpro;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TankPageFragment extends Fragment{

    private TextView titleText;
    private View tank1View, tank2View, tank3View;
    private View level1View, level2View, level3View;
    private TextView tank1Text, tank2Text, tank3Text;
    private ImageView warning1Image, warning2Image, warning3Image;

    private boolean ll1_old, ll2_old, ll3_old, lh1_old, lh2_old, lh3_old;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tank_page,
                container, false);

        // get references for the ui elements
        titleText = (TextView) rootView.findViewById(R.id.text_tank_title);
        tank1View = (View) rootView.findViewById(R.id.view_tank_1);
        tank2View = (View) rootView.findViewById(R.id.view_tank_2);
        tank3View = (View) rootView.findViewById(R.id.view_tank_3);
        level1View = (View) rootView.findViewById(R.id.view_level_1);
        level2View = (View) rootView.findViewById(R.id.view_level_2);
        level3View = (View) rootView.findViewById(R.id.view_level_3);
        tank1Text = (TextView) rootView.findViewById(R.id.text_tank_1);
        tank2Text = (TextView) rootView.findViewById(R.id.text_tank_2);
        tank3Text = (TextView) rootView.findViewById(R.id.text_tank_3);
        warning1Image = (ImageView) rootView.findViewById(R.id.image_warning_1);
        warning2Image = (ImageView) rootView.findViewById(R.id.image_warning_2);
        warning3Image = (ImageView) rootView.findViewById(R.id.image_warning_3);

        // initial values
        ll1_old = true;
        ll2_old = true;
        ll3_old = true;
        lh1_old = false;
        lh2_old = false;
        lh3_old = false;

        return rootView;
    }

    public void setTankLevels(float level1, float level2, float level3) {
        // adjust the ui to the tank levels
        level1View.getLayoutParams().height = (int) level1;
        level2View.getLayoutParams().height = (int) level2;
        level3View.getLayoutParams().height = (int) level3;

        level1View.requestLayout();
        level2View.requestLayout();
        level3View.requestLayout();
    }

    public void setCapacitiveSensorStates(boolean ll1, boolean ll2, boolean ll3,
                                          boolean lh1, boolean lh2, boolean lh3) {
        // adjust the ui to the capacitive sensor states
        if (lh1 || !ll1) {
            // for critical state show warning
            warning1Image.setVisibility(View.VISIBLE);
            // only vibrate once
            if (lh1 != lh1_old || ll1 != ll1_old) vibrate();
        }
        else warning1Image.setVisibility(View.INVISIBLE);
        if (lh2 || !ll2) {
            warning2Image.setVisibility(View.VISIBLE);
            if (lh2 != lh2_old || ll2 != ll2_old) vibrate();
        }
        else warning2Image.setVisibility(View.INVISIBLE);
        if (lh3 || !ll3) {
            warning3Image.setVisibility(View.VISIBLE);
            if (lh3 != lh3_old || ll3 != ll3_old) vibrate();
        }
        else warning3Image.setVisibility(View.INVISIBLE);

        // save the capacitive sensor states
        ll1_old = ll1;
        ll2_old = ll2;
        ll3_old = ll3;
        lh1_old = lh1;
        lh2_old = lh2;
        lh3_old = lh3;
    }

    public void vibrate() {
        Vibrator vibrator = (Vibrator) getActivity()
                .getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        // -1 - don't repeat
        final int indexInPatternToRepeat = -1;
        try {
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
        }
        catch (Exception e) {
            Log.e("VIBRATION", e.toString());
        }
    }

    public void enterAmbient() {
        // change the color of the ui elements
        titleText.setTextColor(Color.WHITE);
        tank1View.setBackgroundResource(R.drawable.rect_border);
        tank2View.setBackgroundResource(R.drawable.rect_border);
        tank3View.setBackgroundResource(R.drawable.rect_border);
        level1View.setBackgroundResource(R.drawable.rect_border);
        level2View.setBackgroundResource(R.drawable.rect_border);
        level3View.setBackgroundResource(R.drawable.rect_border);
        tank1Text.setTextColor(Color.WHITE);
        tank2Text.setTextColor(Color.WHITE);
        tank3Text.setTextColor(Color.WHITE);
    }

    public void exitAmbient() {
        // change the color of the ui elements
        final int ACCENT = getResources()
                .getColor(R.color.accent, getActivity().getTheme());
        final int DARK_UI = getResources()
                .getColor(R.color.darker_ui_element, getActivity().getTheme());
        final int ACTIVE_UI = getResources()
                .getColor(R.color.active_ui_element, getActivity().getTheme());
        titleText.setTextColor(ACCENT);
        tank1View.setBackgroundColor(DARK_UI);
        tank2View.setBackgroundColor(DARK_UI);
        tank3View.setBackgroundColor(DARK_UI);
        level1View.setBackgroundColor(ACTIVE_UI);
        level2View.setBackgroundColor(ACTIVE_UI);
        level3View.setBackgroundColor(ACTIVE_UI);
        tank1Text.setTextColor(ACCENT);
        tank2Text.setTextColor(ACCENT);
        tank3Text.setTextColor(ACCENT);
    }
}
