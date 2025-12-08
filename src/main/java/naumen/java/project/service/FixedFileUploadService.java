package naumen.java.project.service;

import naumen.java.project.config.YandexS3Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FixedFileUploadService {

    private final S3Client s3Client;
    private final YandexS3Properties props;

    public FixedFileUploadService(S3Client s3Client, YandexS3Properties props) {
        this.s3Client = s3Client;
        this.props = props;
    }

    public String uploadFixedFile() {
        String resourceName = "photo_2025-12-06_20-49-19.jpg"; // имя файла ВНУТРИ src/main/resources

        try {
            Resource resource = new ClassPathResource(resourceName);

            if (!resource.exists()) {
                throw new IllegalStateException("Ресурс не найден в classpath: " + resourceName);
            }

            String randomName = UUID.randomUUID() + "-" + resourceName;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(randomName)
                    .build();

            try (InputStream is = resource.getInputStream()) {
                s3Client.putObject(
                        request,
                        RequestBody.fromInputStream(is, resource.contentLength())
                );
            }

            return randomName;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла в Object Storage", e);
        }
    }
}
