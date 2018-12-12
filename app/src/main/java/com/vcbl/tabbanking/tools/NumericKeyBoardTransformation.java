package com.vcbl.tabbanking.tools;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by ecosoft2 on 23-Mar-18.
 */

public class NumericKeyBoardTransformation extends PasswordTransformationMethod {

    public NumericKeyBoardTransformation() {
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return source;
    }
}
