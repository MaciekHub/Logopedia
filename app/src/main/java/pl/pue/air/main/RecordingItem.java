package pl.pue.air.main;

import androidx.recyclerview.widget.RecyclerView;

public class RecordingItem {

    private int imageResource;

    private String title;

    private String data;

    public RecordingItem(int imageResource, String title, String data) {
        this.imageResource = imageResource;
        this.title = title;
        this.data = data;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }
}
