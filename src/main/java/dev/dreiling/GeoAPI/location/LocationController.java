package dev.dreiling.GeoAPI.location;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationRepository repository;
    private final LocationService service;

    public LocationController( LocationRepository repository, LocationService service ) {
        this.repository = repository;
        this.service = service;
    }

    private String getUserId( Authentication auth ) {
        if ( auth == null ) return null;

        String principal = (String) auth.getPrincipal();

        if ( "geo-tourist".equals(principal) ) return null;

        return principal;
    }

    @GetMapping
    public List<Location> getAll( Authentication auth ) {
        String userId = getUserId(auth);

        return repository.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public List<Location> getById( @PathVariable String id, Authentication auth ) {
        String userId = getUserId(auth);

        return repository.findByIdAndUserId( id, userId ).map(List::of).orElse(List.of());
    }

    @GetMapping("/search")
    public List<Location> search( @RequestParam String term, Authentication auth ) {
        String userId = getUserId(auth);

        return repository.findByTermAndUserId( term, userId );
    }

    @GetMapping("/date")
    public List<Location> date( @RequestParam int year, Authentication auth ) {
        String userId = getUserId(auth);

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);

        return repository.findByYearAndUserId( start, end, userId );
    }

    @GetMapping("/near")
    public List<Location> findNearby( @RequestParam double lon, @RequestParam double lat, @RequestParam(defaultValue = "5") double km, Authentication auth ) {
        String userId = getUserId(auth);

        GeoJsonPoint point = new GeoJsonPoint( lon, lat );
        //Distance distance = new Distance( km, Metrics.KILOMETERS );

        return service.findNearby( userId, point, km );
    }

    @PostMapping
    public Location create( @RequestBody Location location, Authentication auth ) {
        String userId = getUserId(auth);

        location.setUserId(userId);

        return repository.save( location );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete( @PathVariable String id, Authentication auth ) {
        String userId = getUserId(auth);

        if ( !repository.existsByIdAndUserId( id, userId ) ) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById( id );
        return ResponseEntity.noContent().build();
    }
}
