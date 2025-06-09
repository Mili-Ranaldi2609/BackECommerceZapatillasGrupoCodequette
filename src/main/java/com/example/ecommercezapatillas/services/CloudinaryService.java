package com.example.ecommercezapatillas.services; 

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service 
public class CloudinaryService {

    private final Cloudinary cloudinary; 
    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                             @Value("${cloudinary.api-key}") String apiKey,
                             @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

   public String uploadFile(MultipartFile file) throws IOException { 
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío.");
        }
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "ecommerce-zapatillas-uploads"
                ));
        return uploadResult.get("secure_url").toString();
    }
    public String deleteFile(String publicId) throws IOException {
        // Realiza la eliminación del archivo.
        Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return deleteResult.get("result").toString();
    }

    public String extractPublicId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null; 
            }
            String pathAfterUpload = url.substring(uploadIndex + "/upload/".length());

           
            if (pathAfterUpload.matches("^v\\d+/.*")) {
                int firstSlashAfterVersion = pathAfterUpload.indexOf('/');
                if (firstSlashAfterVersion != -1) {
                    pathAfterUpload = pathAfterUpload.substring(firstSlashAfterVersion + 1);
                }
            }

            int dotIndex = pathAfterUpload.lastIndexOf('.');
            if (dotIndex != -1) {
                // Remove the extension
                return pathAfterUpload.substring(0, dotIndex);
            }
            return pathAfterUpload; 
        } catch (Exception e) {
            System.err.println("Error al extraer public ID de la URL: " + url + " - " + e.getMessage());
            return null;
        }
    }
}