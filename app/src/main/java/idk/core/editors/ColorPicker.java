package idk.core.editors;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import engine.internal.math.Color;
import idk.core.engine.R;

public class ColorPicker implements SeekBar.OnSeekBarChangeListener {
    private View view;
    private SeekBar red_seek;
    private SeekBar green_seek;
    private SeekBar blue_seek;
    private SeekBar alpha_seek;

    private TextView red_text;
    private TextView green_text;
    private TextView blue_text;
    private TextView alpha_text;

    private LinearLayout color_preview;
    public ColorPicker(LinearLayout parent){
        view = ViewGroup.inflate(parent.getContext(), R.layout.color_editor,null);
        parent.addView(view);

        color_preview = view.findViewById(R.id.color_editor_preview);
        red_seek = view.findViewById(R.id.color_red_seek);
        green_seek = view.findViewById(R.id.color_green_seek);
        blue_seek = view.findViewById(R.id.color_blue_seek);
        alpha_seek = view.findViewById(R.id.color_alpha_seek);

        red_text = view.findViewById(R.id.color_red_text);
        green_text = view.findViewById(R.id.color_green_text);
        blue_text = view.findViewById(R.id.color_blue_text);
        alpha_text = view.findViewById(R.id.color_alpha_text);
    }
    private Color targetColor;

    public void setTarget(Color color){
        targetColor = color;
        int red = (int) color.r * 255;
        int blue = (int) color.b * 255;
        int green = (int) color.g * 255;
        int alpha = (int) color.a * 255;
        red_text.setText("RED :"+red);
        green_text.setText("GREEN :"+green);
        blue_text.setText("BLUE :"+blue);
        alpha_text.setText("ALPHA :"+alpha);

        red_seek.setProgress(red);
        green_seek.setProgress(green);
        blue_seek.setProgress(green);
        alpha_seek.setProgress(alpha);

        red_seek.setOnSeekBarChangeListener(this);
        green_seek.setOnSeekBarChangeListener(this);
        blue_seek.setOnSeekBarChangeListener(this);
        alpha_seek.setOnSeekBarChangeListener(this);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(android.graphics.Color.argb(alpha,red,green,blue));
        drawable.setStroke(4,android.graphics.Color.BLACK);
        drawable.setCornerRadius(30);
        color_preview.setBackground(drawable);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int red = red_seek.getProgress();
        int blue = blue_seek.getProgress();
        int green = green_seek.getProgress();
        int alpha = alpha_seek.getProgress();

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(android.graphics.Color.argb(alpha,red,green,blue));
        drawable.setStroke(4,android.graphics.Color.BLACK);
        drawable.setCornerRadius(30);
        color_preview.setBackground(drawable);

        red_text.setText("RED :"+red);
        green_text.setText("GREEN :"+green);
        blue_text.setText("BLUE :"+blue);
        alpha_text.setText("ALPHA :"+alpha);
        targetColor.r = red / 255f;
        targetColor.b = blue / 255f;
        targetColor.g = green / 255f;
        targetColor.a = alpha / 255f;

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
