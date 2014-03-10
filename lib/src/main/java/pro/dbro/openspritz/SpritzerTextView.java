package pro.dbro.openspritz;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by andrewgiang on 3/3/14.
 */
public class SpritzerTextView extends TextView implements View.OnClickListener {

    public static final int PAINT_WIDTH = 10;         // thickness of spritz guide bars in pixels
                                                      // For optimal drawing should be an even number
    private Spritzer mSpritzer;
    private Paint mPaintGuides;
    private String mTestString;
    private boolean mDefaultClickListener = false;

    public SpritzerTextView(Context context) {
        super(context);
        init();
    }

    public SpritzerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SpritzerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SpritzerTextView, 0, 0);
        try {
            mDefaultClickListener = a.getBoolean(R.styleable.SpritzerTextView_clickControls, false);
        } finally {
            a.recycle();
        }
        init();

    }

    private void init() {
        mSpritzer = new Spritzer(this);
        mPaintGuides = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintGuides.setColor(getCurrentTextColor());
        mPaintGuides.setStrokeWidth(PAINT_WIDTH);
        mPaintGuides.setAlpha(128);
        if (mDefaultClickListener) {
            this.setOnClickListener(this);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Pad the Spritz chrome so the
        // pivot circle doesn't clip
        int chromePadding = 15;

        // Measurements for top & bottom guide line
        int beginTopX = 0;
        int endTopX = getMeasuredWidth();
        int topY = chromePadding;

        int beginBottomX = 0;
        int endBottomX = getMeasuredWidth();
        int bottomY = getMeasuredHeight() - chromePadding;
        // Paint the top guide and bottom guide bars
        canvas.drawLine(beginTopX, topY, endTopX, topY, mPaintGuides);
        canvas.drawLine(beginBottomX, bottomY, endBottomX, bottomY, mPaintGuides);

        // Measurements for pivot indicator
        final float textSize = getTextSize();
        float centerX = calculatePivotXOffset() + getPaddingLeft();
        final int pivotIndicatorLength = 15;

        // Paint the pivot indicator
        canvas.drawLine(centerX, topY + (PAINT_WIDTH / 2), centerX, topY + (PAINT_WIDTH / 2) + (pivotIndicatorLength * 2), mPaintGuides); //line through center of circle
        canvas.drawLine(centerX, bottomY - (PAINT_WIDTH / 2), centerX, bottomY - (PAINT_WIDTH / 2) - (pivotIndicatorLength * 2), mPaintGuides);
    }

    private float calculatePivotXOffset() {
        // Craft a test String of precise length
        // to reach pivot character
        if (mTestString == null) {
            // Spritzer requires monospace font so character is irrelevant
            mTestString = "a";
        }
        // Measure the rendered distance of CHARS_LEFT_OF_PIVOT chars
        // plus half the pivot character
        return (getPaint().measureText(mTestString, 0, 1) * (Spritzer.CHARS_LEFT_OF_PIVOT + .50f));
    }

    /**
     * This determines the words per minute the sprizter will read at
     *
     * @param wpm the number of words per minute
     */
    public void setWpm(int wpm) {
        mSpritzer.setWpm(wpm);
    }


    /**
     * Set a custom spritzer
     *
     * @param spritzer
     */
    public void setSpritzer(Spritzer spritzer) {
        mSpritzer = spritzer;
        mSpritzer.swapTextView(this);
    }

    /**
     * Pass input text to spritzer object
     *
     * @param input
     */
    public void setSpritzText(String input) {
        mSpritzer.setText(input);
    }

    /**
     * Will play the spritz text that was set in setSpritzText
     */
    public void play() {
        mSpritzer.start();
    }

    public void pause() {
        mSpritzer.pause();
    }


    public Spritzer getSpritzer() {
        return mSpritzer;
    }

    @Override
    public void onClick(View v) {
        if (mSpritzer.isPlaying()) {
            pause();
        } else {
            play();
        }

    }
}