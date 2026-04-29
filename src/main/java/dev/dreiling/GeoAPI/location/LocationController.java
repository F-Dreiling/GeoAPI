package dev.dreiling.GeoAPI.location;

import dev.dreiling.GeoAPI.image.ImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationRepository repository;
    private final LocationService service;
    private final ImageService imageService;
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    public LocationController( LocationRepository repository, LocationService service, ImageService imageService ) {
        this.repository = repository;
        this.service = service;
        this.imageService = imageService;
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

        List<Location> locations = repository.findByUserId(userId);

        return locations;
    }

    @GetMapping("/{id}")
    public List<Location> getById( @PathVariable String id, Authentication auth ) {
        String userId = getUserId(auth);

        List<Location> locations = repository.findByIdAndUserId( id, userId ).map(List::of).orElse(List.of());

        return locations;
    }

    @GetMapping("/search")
    public List<Location> search( @RequestParam String term, Authentication auth ) {
        String userId = getUserId(auth);

        List<Location> locations = repository.findByTermAndUserId( term, userId );

        return locations;
    }

    @GetMapping("/date")
    public List<Location> date( @RequestParam int year, Authentication auth ) {
        String userId = getUserId(auth);

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);

        List<Location> locations = repository.findByYearAndUserId( start, end, userId );

        return locations;
    }

    @GetMapping("/near")
    public List<Location> findNearby( @RequestParam double lon, @RequestParam double lat, @RequestParam(defaultValue = "5") double km, Authentication auth ) {
        String userId = getUserId(auth);

        GeoJsonPoint point = new GeoJsonPoint( lon, lat );
        //Distance distance = new Distance( km, Metrics.KILOMETERS );

        List<Location> locations = service.findNearby( userId, point, km );

        return locations;
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage( @PathVariable String filename, Authentication auth ) throws IOException {
        String userId = getUserId(auth);

        boolean exists = repository.findByUserId(userId).stream().anyMatch( loc -> loc.getImageUrl() != null && loc.getImageUrl().endsWith(filename) );
        if (!exists) return ResponseEntity.status(403).build();

        Path path = Paths.get("uploads").resolve(filename);
        if ( !Files.exists(path) ) return ResponseEntity.notFound().build();

        byte[] image = Files.readAllBytes(path);

        return ResponseEntity.ok().header( "Content-Type", Files.probeContentType(path) ).body(image);
    }

    @PostMapping
    public List<Location> create( @RequestBody Location location, Authentication auth ) {
        String userId = getUserId(auth);

        location.setUserId(userId);

        location = repository.save( location );

        return List.of( location );
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<UploadResponse> uploadImage( @PathVariable String id, @RequestParam("file") MultipartFile file, Authentication auth ) throws IOException {
        String userId = getUserId(auth);

        Location location = repository.findByIdAndUserId( id, userId ).orElseThrow( () -> new RuntimeException( "Location not found" ) );

        String status = "success";
        String message = "Success";

        try {
            String filename = imageService.saveImage(file);
            location.setImageUrl(filename);

            repository.save(location);
        }
        catch ( Exception e ) {
            status = "error";
            message = "Image upload rejected: " + e.getMessage();
            log.warn( message );
        }

        return ResponseEntity.ok( new UploadResponse( location, status, message ) );
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
