package internetat.tud.internetatpro;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PumpPageFragment extends Fragment{

    private TextView titleText;
    private LinearLayout pumpingButton;
    private ImageView pumpingButtonImage;
    private TextView pumpingButtonText;
    private TextView pumpingStateText;

    private boolean pumping;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pump_page,
                container, false);

        // get references for the ui elements
        titleText = (TextView) rootView.findViewById(R.id.text_pump_title);
        pumpingButtonImage = (ImageView) rootView.findViewById(R.id.image_toggle_pumping_start_button);
        pumpingButtonText = (TextView) rootView.findViewById(R.id.text_toggle_pumping_start_button);
        pumpingStateText = (TextView) rootView.findViewById(R.id.text_pumping_state);
        pumpingButton = (LinearLayout) rootView.findViewById(R.id.button_toggle_pumping);

        // initial value for pumping state
        pumping = false;

        // init the button
        pumpingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pumping) {
                    // if pumping is active -> stop it
                    SoapWriteTask soapWriteTask =
                            new SoapWriteTask(false, 0, 0);
                    soapWriteTask.execute();
                }
                else {
                    // if pumping is inactive -> start it
                    Intent intent = new Intent(getActivity(),ChooseActivity.class);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    public void setPumpingState(boolean pumping, int tankA, int tankB) {
        // save the pumping state
        this.pumping = pumping;

        // adjust the ui to the pumping state
        if (pumping) {
            pumpingButtonImage.setImageResource(R.mipmap.ic_action_stop);
            pumpingButtonText.setText(R.string.stop);
            String pumpingState = getString(R.string.pumping_active);
            pumpingState = String.format(pumpingState, tankA, tankB);
            pumpingStateText.setText(pumpingState);
        }
        else {
            pumpingButtonImage.setImageResource(R.mipmap.ic_action_start);
            pumpingButtonText.setText(R.string.start);
            pumpingStateText.setText(R.string.pumping_inactive);
        }
    }

    public void enterAmbient() {
        // change the color of the ui elements
        titleText.setTextColor(Color.WHITE);
        pumpingStateText.setTextColor(Color.WHITE);
        pumpingButton.setVisibility(View.INVISIBLE);
    }

    public void exitAmbient() {
        // change the color of the ui elements
        final int ACCENT = getResources()
                .getColor(R.color.accent, getActivity().getTheme());
        titleText.setTextColor(ACCENT);
        pumpingStateText.setTextColor(ACCENT);
        pumpingButton.setVisibility(View.VISIBLE);
    }
}
