package dev.dreiling.GeoAPI.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final long MAX_SIZE = 2 * 1024 * 1024;

    private static final Set<String> ALLOWED_FORMATS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    public String saveImage( MultipartFile file ) throws IOException {
        if ( file.getSize() <= 0 || file.getSize() > MAX_SIZE ) {
            throw new IOException( "Invalid file size" );
        }

        BufferedImage img = ImageIO.read( file.getInputStream() );
        if ( img == null ) {
            throw new IOException( "Invalid image content" );
        }

        String extension = getExtension( file.getOriginalFilename() ).toLowerCase();

        if ( !ALLOWED_FORMATS.contains(extension) ) {
            throw new IOException( "Unsupported format" );
        }

        String outputFormat = ( extension.equals("png") || extension.equals("gif") ) ? "png" : "jpg";

        String filename = UUID.randomUUID() + "." + outputFormat;

        Path path = Paths.get( uploadDir, filename );
        Files.createDirectories( path.getParent() );

        ImageIO.write( img, outputFormat, path.toFile() );

        return filename;
    }

    private String getExtension( String filename ) {
        if ( filename == null || !filename.contains(".") ) return "jpg";

        return filename.substring( filename.lastIndexOf(".") + 1 );
    }
}