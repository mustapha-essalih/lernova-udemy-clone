package dev.api.instructors.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SectionsInitRequest {

    private String title;
    private List<VideosInitRequest> videos;
    private List<ResourcesInitRequest> resources;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<VideosInitRequest> getVideos() {
        return videos;
    }
    public void setVideos(List<VideosInitRequest> videos) {
        this.videos = videos;
    }
    public List<ResourcesInitRequest> getResources() {
        return resources;
    }
    public void setResources(List<ResourcesInitRequest> resources) {
        this.resources = resources;
    }
    @Override
    public String toString() {
        return "SectionsInitRequest [title=" + title + ", videos=" + videos + ", resources=" + resources + "]";
    }


    

}
