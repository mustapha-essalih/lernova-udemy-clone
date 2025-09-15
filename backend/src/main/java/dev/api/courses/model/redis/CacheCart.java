package dev.api.courses.model.redis;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "cart", timeToLive = 604800) // 7 days in seconds
public class CacheCart {
    
    @Id
    private String cartId;

    private Set<String> itemIds = new HashSet<>();

    private BigDecimal totalAmount; 
    private LocalDateTime expiresAt;
    
    
    public String getCartId() {
        return cartId;
    }
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
    public Set<String> getItemIds() {
        return itemIds;
    }
    public void setItemIds(Set<String> itemIds) {
        this.itemIds = itemIds;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    

    
    
}
