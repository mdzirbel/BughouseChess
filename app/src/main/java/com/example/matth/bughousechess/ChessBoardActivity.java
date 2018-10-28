package com.example.matth.bughousechess;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.util.DisplayMetrics;

public class ChessBoardActivity extends AppCompatActivity {

    // class member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    private int[] topLeftCoordsImageView = new int[2];
    private int[] topLeftCoordsTopRow = new int[2];
    private int[] topLeftCoordsBottomRow = new int[2];
    private int[] imageViewWidthHeight = new int[2];
    private int[] topRowWidthHeight = new int[2];
    private int[] bottomRowWidthHeight = new int[2];

    // class member to save board variable
    public static ChessBoard board = new ChessBoard("w");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        // Instantiate ImageView object, set touch/click listener, get coords of top left
        ImageView imageView = findViewById(R.id.boardImage);
        imageView.setOnTouchListener(touchListener);
        imageView.setOnClickListener(clickListener);
        imageViewWidthHeight[0] = imageView.getWidth();
        imageViewWidthHeight[1] = imageView.getHeight();
        imageView.getLocationOnScreen(topLeftCoordsImageView);

        // Instantiate top TableRow object, get coords of top left
        TableRow topRow = findViewById(R.id.topReserve);
        topRowWidthHeight[0] = topRow.getWidth();
        topRowWidthHeight[1] = topRow.getHeight();
        topRow.getLocationOnScreen(topLeftCoordsTopRow);

        // Instantiate bottom TableRow object, get coords of top left
        TableRow bottomRow = findViewById(R.id.bottomReserve);
        bottomRowWidthHeight[0] = topRow.getWidth();
        bottomRowWidthHeight[1] = topRow.getHeight();
        bottomRow.getLocationOnScreen(topLeftCoordsBottomRow);

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
        displayPiece(1, 1);

    }

    // need method that places a piece on board display piece with coords
    public void displayPiece(int xPosBoard, int yPosBoard) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int xPosToRender = (int) (width * .02 + imageViewWidthHeight[0] / 8 * xPosBoard);
        int yPosToRender = (int) (height * .02 + imageViewWidthHeight[1] / 8 * yPosBoard);
        ImageView newPiece = new ImageView(this);
        newPiece.setId(3);
        newPiece.setImageResource(R.drawable.blackbishoppiece);
        ConstraintLayout chessBoardView = findViewById(R.id.chessboardview);
        ImageView chessBoardImage = findViewById(R.id.boardImage);
        chessBoardView.addView(newPiece);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) chessBoardImage.getLayoutParams();
        params.topMargin = 300;
////        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newPiece.getLayoutParams();
////        // params.setMargins(100, 300, 0, 0);
////        params.leftMargin = 100;
////        params.topMargin = 300;
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.connect(newPiece.getId(), ConstraintSet.LEFT, chessBoardView.getId(), ConstraintSet.LEFT, 0);
//        constraintSet.applyTo(chessBoardView);
//         newPiece.bringToFront();
    }
}