package dev.dreiling.GeoAPI.location;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;

import java.time.LocalDate;

@Data
@Document(collection = "locations")
public class Location {

    @Id
    private String id;

    private String name;
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint geoPoint;

    private LocalDate dateVisited;
    private String note;
    private String imageUrl;
}