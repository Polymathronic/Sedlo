package com.aprilbrother.blueduino;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Uros.Milosevic on 6/2/2016.
 */
public class VisualizeActivity extends Activity {

    private CanvasView customCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_visualize);
        super.onCreate(savedInstanceState);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
    }

    public void clearCanvas(View v) {
        customCanvas.clearCanvas();
    }
}
