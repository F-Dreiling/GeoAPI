package dev.dreiling.GeoAPI.location;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationRepository repository;

    public LocationController( LocationRepository repository ) {
        this.repository = repository;
    }

    @GetMapping
    public List<Location> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public List<Location> getById( @PathVariable String id ) {
        return repository.findById(id).map(List::of).orElse(List.of());
    }

    @GetMapping("/search")
    public List<Location> search( @RequestParam String term ) {
        return repository.search(term);
    }

    @GetMapping("/date")
    public List<Location> date( @RequestParam int year ) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);

        return repository.findByYear(start, end);
    }

    @GetMapping("/near")
    public List<Location> findNearby( @RequestParam double lon, @RequestParam double lat, @RequestParam(defaultValue = "5") double km ) {
        GeoJsonPoint point = new GeoJsonPoint(lon, lat);
        Distance distance = new Distance(km, Metrics.KILOMETERS);

        return repository.findByGeoPointNear(point, distance);
    }

    @PostMapping
    public Location create( @RequestBody Location location ) {
        return repository.save(location);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete( @PathVariable String id ) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
