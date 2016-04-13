package io.github.nestegg333.nestegg;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by aqeelp on 4/12/16.
 */
public class Utils {
    public static String amountToString(int amount) {
        String intString = Integer.toString(amount);
        String finalCost = "";
        for (int i = 0; i < intString.length(); i++) {
            if (i == intString.length() - 2)
                finalCost += '.';
            finalCost += intString.charAt(i);
        }
        return finalCost;
    }

    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
