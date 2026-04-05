package dev.dreiling.GeoAPI.location;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationRepository repository;

    public LocationController(LocationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Location> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Location getById(@PathVariable String id) {
        return repository.findById(id).orElseThrow();
    }

    @GetMapping("/search")
    public List<Location> search(@RequestParam String term) {
        return repository.search(term);
    }

    @GetMapping("/near")
    public List<Location> findNearby(@RequestParam double lon, @RequestParam double lat, @RequestParam(defaultValue = "5") double km) {
        GeoJsonPoint point = new GeoJsonPoint(lon, lat);
        Distance distance = new Distance(km, Metrics.KILOMETERS);

        return repository.findByGeoPointNear(point, distance);
    }

    @PostMapping
    public Location create(@RequestBody Location location) {
        return repository.save(location);
    }
}
