package io.github.nestegg333.nestegg;

/**
 * Created by aqeelp on 3/29/16.
 */
public class PetState {
    private String title, action, label;
    private int imageId;

    public PetState(String t, String a, int i, String l) {
        title = t;
        action = a;
        imageId = i;
        label = l;
    }

    public String getTitle() {
        return title;
    }

    public String getAction() {
        return action;
    }

    public int getImageId() {
        return imageId;
    }

    public String getLabel() {
        return label;
    }
}
