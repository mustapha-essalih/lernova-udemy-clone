package dev.api.courses.mapper;


public class PaymenetResponse {
    
    private String sessionId;   
    private String sessionUrl;


    public PaymenetResponse(String sessionId, String sessionUrl) {
        this.sessionId = sessionId;
        this.sessionUrl = sessionUrl;
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public String getSessionUrl() {
        return sessionUrl;
    }


    public void setSessionUrl(String sessionUrl) {
        this.sessionUrl = sessionUrl;
    }
    
    
    
    
    
    
    
}