package dev.dreiling.GeoAPI.location;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.geo.Distance;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface LocationRepository extends MongoRepository<Location, String> {

    List<Location> findByUserId( String userId );

    Optional<Location> findByIdAndUserId( String id, String userId );

    @Query( "{ 'userId': ?1, $or: [ " +
                "{ 'name': { $regex: ?0, $options: 'i' } }, " +
                "{ 'address': { $regex: ?0, $options: 'i' } } " +
            "] }" )
    List<Location> findByTermAndUserId( String term, String userId );

    @Query( "{ 'userId': ?2, 'dateVisited': { $gte: ?0, $lt: ?1 } }" )
    List<Location> findByYearAndUserId( LocalDate start, LocalDate end, String userId );

    List<Location> findByGeoPointNear( GeoJsonPoint point, Distance distance );

    boolean existsByIdAndUserId( String id, String userId );

}