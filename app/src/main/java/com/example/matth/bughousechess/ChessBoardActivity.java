package com.example.matth.bughousechess;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableRow;


public class ChessBoardActivity extends AppCompatActivity {

    // class member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    // class member to save board variable
    public static ChessBoard board = new ChessBoard(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        // Instantiate ImageView object, set touch/click listener, get coords of top left
        ImageView imageView = findViewById(R.id.boardImage);
        imageView.setOnTouchListener(touchListener);
        imageView.setOnClickListener(clickListener);
        int[] topLeftImageView = new int[2];
        imageView.getLocationOnScreen(topLeftImageView);

        // Instantiate top TableRow object, get coords of top left
        TableRow topRow = findViewById(R.id.topReserve);
        int[] topLeftTopRow = new int[2];
        topRow.getLocationOnScreen(topLeftTopRow);

        // Instantiate bottom TableRow object, get coords of top left
        TableRow bottomRow = findViewById(R.id.bottomReserve);
        int[] topLeftBottomRow = new int[2];
        bottomRow.getLocationOnScreen(topLeftBottomRow);

        displayBoard();
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

            // process click
            int xPos = (int) (x / v.getWidth() * 8);
            int yPos = (int) (y / v.getHeight() * 8);
            board.clickOnBoard(xPos, yPos);
        }
    };

    // TODO - finish this method (with board and maybe top/bottom rows)
    public void displayBoard() {
        // Fill reserve rows, place piece
        displayPiece(0, 0);

    }

    // need method that places a piece on board display piece with coords
    public void displayPiece(int xPosBoard, int yPosBoard) {
        // int xPosToRender
        ImageView newPiece = new ImageView(this);
        newPiece.setImageResource(R.drawable.blackbishoppiece);
        ConstraintLayout chessBoardView = findViewById(R.id.chessboardview);
        chessBoardView.addView(newPiece);
    }
}


