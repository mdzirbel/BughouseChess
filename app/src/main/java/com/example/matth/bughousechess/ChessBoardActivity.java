package com.example.matth.bughousechess;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.ImageView;

public class ChessBoardActivity extends AppCompatActivity {

    // class member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        // add both a touch listener and a click listener
        ImageView myView = findViewById(R.id.boardImage);
        myView.setOnTouchListener(touchListener);
        myView.setOnClickListener(clickListener);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }
            // let the touch event pass on to whoever needs it
            return false;
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // retrieve the stored coordinates
            float x = lastTouchDownXY[0];
            float y = lastTouchDownXY[1];

            // use the coordinates for whatever
            Log.i("TAG", "onLongClick: x = " + x + ", y = " + y);
        }
    };
}


