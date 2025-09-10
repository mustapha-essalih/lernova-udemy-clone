package dev.api.instructors.request;

import lombok.Getter;
import lombok.Setter;

public class VideosInitRequest {

    private String title;
    private int durationMinutes;
    private boolean isPreview;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public boolean isPreview() {
        return isPreview;
    }
    public void setPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }
    @Override
    public String toString() {
        return "VideosInitRequest [title=" + title + ", durationMinutes=" + durationMinutes + ", isPreview=" + isPreview
                + "]";
    }


    

}
