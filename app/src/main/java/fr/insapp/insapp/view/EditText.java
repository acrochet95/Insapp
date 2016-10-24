package fr.insapp.insapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Antoine on 13/10/2016.
 */
public class EditText extends android.widget.EditText {

    private int currentCursorPosition = 0;

    public EditText(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);

    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditText(Context context) {
        super(context);

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        currentCursorPosition = selEnd;
    }

    public int getCurrentCursorPosition(){
        return currentCursorPosition;
    }
}