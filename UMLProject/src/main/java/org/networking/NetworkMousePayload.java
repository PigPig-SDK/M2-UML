package org.networking;

import java.util.UUID;
import javafx.geometry.Point2D;

public class NetworkMousePayload {

    private String username;
    private UUID userId;
    double x,y;
    private boolean isDragging;

    public NetworkMousePayload(String username, UUID userId, double x, double y, boolean isDragging) {
        this.username = username;
        this.userId = userId;
        this.x = x;
        this.y = y;
        this.isDragging = isDragging;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public Point2D getMouseLocation() {
        return new Point2D(this.x, this.y);
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
