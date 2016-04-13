package io.github.nestegg333.nestegg;

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
}
