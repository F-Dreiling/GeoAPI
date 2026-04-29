package dev.dreiling.GeoAPI.location;

public class UploadResponse {
    public Location location;
    public String status;
    public String message;

    public UploadResponse( Location location, String status, String message ) {
        this.location = location;
        this.status = status;
        this.message = message;
    }
}