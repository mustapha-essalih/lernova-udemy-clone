package dev.api.courses.model.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("resources")
public class CacheResources {

    @Id
    private String id;
    private String title;
    private boolean isPreview;
    
    public CacheResources() {
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

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }

}
    
