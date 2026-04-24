package dev.dreiling.GeoAPI.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final MongoTemplate mongoTemplate;

    public List<Location> findNearby( String userId, GeoJsonPoint point, double km ) {
        double maxDistanceInMeters = km * 1000;

        Query query = new Query();
        query.addCriteria( new Criteria().andOperator(
                Criteria.where("userId").is(userId),
                Criteria.where("geoPoint").near(point).maxDistance( maxDistanceInMeters )
        ) );

        return mongoTemplate.find( query, Location.class );
    }
}