package dev.api.instructors.request;

import lombok.Getter;
import lombok.Setter;

public class ResourcesInitRequest {

    private String title;
    private boolean isPreview;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isPreview() {
        return isPreview;
    }
    public void setPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }
    @Override
    public String toString() {
        return "ResourcesInitRequest [title=" + title + ", isPreview=" + isPreview + "]";
    }


    
}
