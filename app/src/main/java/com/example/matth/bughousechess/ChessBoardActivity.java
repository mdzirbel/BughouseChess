package com.example.matth.bughousechess;

import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableRow;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashMap;

public class ChessBoardActivity extends AppCompatActivity {

    // class member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];
    private int[] topLeftCoordsImageView = new int[2];
    private int[] topLeftCoordsTopRow = new int[2];
    private int[] topLeftCoordsBottomRow = new int[2];
    private int[] imageViewWidthHeight = new int[2];
    private int[] topRowWidthHeight = new int[2];
    private int[] bottomRowWidthHeight = new int[2];
    ArrayList<ImageView> currentBoardImages = new ArrayList<>();
    ConstraintLayout parentContrainView;

    // class member to save board variable
    public static ChessBoard board = new ChessBoard("w");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        parentContrainView = findViewById(R.id.chessboardview);

        parentContrainView.post(new Runnable() {
            @Override
            public void run() {
                // Instantiate ImageView object, set touch/click listener, get coords of top left
                ImageView imageView = findViewById(R.id.boardImage);
                imageView.setOnTouchListener(touchListener);
                imageView.setOnClickListener(clickListener);
                imageViewWidthHeight[0] = imageView.getWidth();
                imageViewWidthHeight[1] = imageView.getHeight();
                imageView.getLocationOnScreen(topLeftCoordsImageView);

                /*// Instantiate top TableRow object, get coords of top left
                TableRow topRow = findViewById(R.id.topReserve);
                topRowWidthHeight[0] = topRow.getWidth();
                topRowWidthHeight[1] = topRow.getHeight();
                topRow.getLocationOnScreen(topLeftCoordsTopRow);

                // Instantiate bottom TableRow object, get coords of top left
                TableRow bottomRow = findViewById(R.id.bottomReserve);
                bottomRowWidthHeight[0] = topRow.getWidth();
                bottomRowWidthHeight[1] = topRow.getHeight();
                bottomRow.getLocationOnScreen(topLeftCoordsBottomRow);*/
                displayEverything();
            }
        });



    }

    void updateBlackReserve(HashMap<String, Integer> pieces)
    {
        int i = 0;
        int offset = 1056;
        ConstraintLayout blackReserve = ((ConstraintLayout) findViewById(R.id.blackReserve));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(blackReserve);

        ImageView newPiece = new ImageView(this);
        newPiece.getHeight();
        Resources res = getResources();
        String mDrawableName = "black" + "pawn" + "piece";
        int resID = res.getIdentifier(mDrawableName , "drawable", getPackageName());
        newPiece.setImageResource(resID);


        newPiece.setId(offset+i);

        blackReserve.addView(newPiece);
        if(i > 0) {
            constraintSet.connect(offset+i, ConstraintSet.LEFT, offset+i-1, ConstraintSet.RIGHT, 0);
        }
        constraintSet.connect(10, ConstraintSet.TOP, R.id.blackReserve, ConstraintSet.TOP, 0);
        constraintSet.applyTo(blackReserve);
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

            // process click
            int xPos = (int) (x / v.getWidth() * 8);
            int yPos = (int) (y / v.getHeight() * 8);
            board.clickOnBoard(xPos, yPos);
            displayEverything();
        }
    };

    public void displayEverything() {
        // Clear out everything
        clearBoard();
        // Refill board and reserve
        displayBoard();

    }

    public void clearBoard() {
        for (int i=currentBoardImages.size()-1; i>=0; i--) {
            parentContrainView.removeView(currentBoardImages.get(i));
            currentBoardImages.remove(i);
        }
    }

    public void displayBoard() {
        ChessPiece[][] currentBoard = board.getBoard();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (board.tileHasPiece(x, y)) {
                    displayPiece(x, y, currentBoard[x][y]);
                }
            }
        }
    }

    // need method that places a piece on board display piece with coords
    public void displayPiece(int xPosBoard, int yPosBoard, ChessPiece chessPiece) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int xPosToRender = (imageViewWidthHeight[0] / 8 * xPosBoard);
        int yPosToRender = (imageViewWidthHeight[1] / 8 * yPosBoard);

        ImageView newPiece = new ImageView(this);
        newPiece.getHeight();

        Resources res = getResources();
        String mDrawableName = chessPiece.getTeam() + chessPiece.getPieceType() + "piece";
        int resID = res.getIdentifier(mDrawableName , "drawable", getPackageName());
        newPiece.setImageResource(resID);


        newPiece.setId(xPosBoard * 10 + yPosBoard);

        parentContrainView.addView(newPiece);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parentContrainView);
        constraintSet.connect(newPiece.getId(), ConstraintSet.LEFT, R.id.boardImage, ConstraintSet.LEFT, xPosToRender);
        constraintSet.connect(newPiece.getId(), ConstraintSet.TOP, R.id.boardImage, ConstraintSet.TOP, yPosToRender);
        constraintSet.applyTo(parentContrainView);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newPiece.getLayoutParams();
        params.height = (int) (findViewById(R.id.boardImage).getHeight() / 8.0);
        params.width = (int) (findViewById(R.id.boardImage).getWidth() / 8.0);

        currentBoardImages.add(newPiece);
    }
}