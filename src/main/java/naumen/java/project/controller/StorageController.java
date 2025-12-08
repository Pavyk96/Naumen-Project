package naumen.java.project.controller;

import naumen.java.project.service.FixedFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private static final Logger log = LoggerFactory.getLogger(StorageController.class);

    private final FixedFileUploadService uploadService;

    public StorageController(FixedFileUploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload-fixed")
    public ResponseEntity<String> uploadFixedFile() {
        log.info("POST /api/storage/upload-fixed called");
        try {
            String name = uploadService.uploadFixedFile();
            log.info("File uploaded as {}", name);
            return ResponseEntity.ok("Файл загружен как: " + name);
        } catch (Exception e) {
            log.error("Error in uploadFixedFile", e);
            return ResponseEntity.internalServerError().body("Ошибка: " + e.getMessage());
        }
    }
}
