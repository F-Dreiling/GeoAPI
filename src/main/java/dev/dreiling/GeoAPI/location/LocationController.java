package dev.dreiling.GeoAPI.location;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.util.List;
import java.util.Calendar;
import java.util.Date;

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
    public List<Location> getById(@PathVariable String id) {
        return repository.findById(id).map(List::of).orElse(List.of());
    }

    @GetMapping("/search")
    public List<Location> search(@RequestParam String term) {
        return repository.search(term);
    }

    @GetMapping("/date")
    public List<Location> date(@RequestParam int year) {
        Calendar cal = Calendar.getInstance();

        cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        return repository.findByYear(start, end);
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
