package dev.dreiling.GeoAPI.location;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.geo.Distance;

import java.util.List;

public interface LocationRepository extends MongoRepository<Location, String> {

    List<Location> findByName(String name);
    List<Location> findByGeoPointNear(GeoJsonPoint point, Distance distance);

}