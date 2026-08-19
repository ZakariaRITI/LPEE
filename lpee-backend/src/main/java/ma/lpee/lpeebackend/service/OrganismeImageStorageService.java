package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class OrganismeImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "jpg", "image/jpeg", "jpeg", "image/jpeg", "png", "image/png", "webp", "image/webp");

    private final Path storageDirectory;

    public OrganismeImageStorageService(
            @Value("${app.upload.organismes-dir:uploads/organismes}") String storageDirectory) {
        this.storageDirectory = Paths.get(storageDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible d'initialiser le stockage des images.", exception);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Veuillez choisir une image.");
        }
        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int extensionIndex = originalName.lastIndexOf('.');
        String extension = extensionIndex >= 0 ? originalName.substring(extensionIndex + 1).toLowerCase(Locale.ROOT) : "";
        if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Format invalide. Choisissez une image JPG, JPEG, PNG ou WEBP.");
        }

        String filename = UUID.randomUUID() + "." + extension;
        Path destination = storageDirectory.resolve(filename).normalize();
        if (!destination.getParent().equals(storageDirectory)) {
            throw new BadRequestException("Nom de fichier invalide.");
        }
        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new BadRequestException("L’enregistrement de l’image a échoué.");
        }
        return filename;
    }

    public Resource load(String filename) {
        if (!filename.matches("[0-9a-fA-F-]+\\.(jpg|jpeg|png|webp)")) {
            throw new BadRequestException("Nom de fichier invalide.");
        }
        Path file = storageDirectory.resolve(filename).normalize();
        if (!file.getParent().equals(storageDirectory) || !Files.isRegularFile(file)) {
            throw new BadRequestException("Image introuvable.");
        }
        try {
            return new UrlResource(file.toUri());
        } catch (MalformedURLException exception) {
            throw new BadRequestException("Image introuvable.");
        }
    }

    public String contentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return CONTENT_TYPES.getOrDefault(extension, "application/octet-stream");
    }
}
