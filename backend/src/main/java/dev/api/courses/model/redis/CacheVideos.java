package dev.api.courses.model.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("videos")
public class CacheVideos {



    @Id
    private String id;
    private String title;
    private int durationMinutes;
    private boolean isPreview;
    
    public CacheVideos() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    
    
}
