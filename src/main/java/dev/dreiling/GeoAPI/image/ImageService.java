package dev.dreiling.GeoAPI.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveImage( MultipartFile file ) throws IOException {
        String extension = getExtension( file.getOriginalFilename() );
        String filename = UUID.randomUUID() + "." + extension;

        Path path = Paths.get( uploadDir, filename );
        Files.createDirectories( path.getParent() );
        Files.write( path, file.getBytes() );

        return filename;
    }

    private String getExtension( String filename ) {
        if ( filename == null || !filename.contains(".") ) return "jpg";
        return filename.substring( filename.lastIndexOf(".") + 1 );
    }
}