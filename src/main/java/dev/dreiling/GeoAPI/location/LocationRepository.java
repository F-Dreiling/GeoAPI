package dev.dreiling.GeoAPI.location;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.geo.Distance;

import java.util.List;
import java.time.LocalDate;

public interface LocationRepository extends MongoRepository<Location, String> {

    @Query("{ $or: [ " +
            "{ 'name': { $regex: ?0, $options: 'i' } }, " +
            "{ 'address': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Location> search(String term);

    @Query("{ 'dateVisited': { $gte: ?0, $lt: ?1 } }")
    List<Location> findByYear(LocalDate start, LocalDate end);

    List<Location> findByGeoPointNear(GeoJsonPoint point, Distance distance);

}