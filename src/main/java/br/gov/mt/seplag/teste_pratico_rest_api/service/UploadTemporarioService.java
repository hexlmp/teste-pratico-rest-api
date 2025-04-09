package br.gov.mt.seplag.teste_pratico_rest_api.service;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UploadTemporarioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.temp}")
    private String bucketNameTemp;

    @Value("${minio.bucket.name}")
    private String bucketNameDefinitivo;



    public String uploadTemporario(MultipartFile file) throws Exception {
        // Verifica se o bucket temporário existe
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketNameTemp).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketNameTemp).build());
        }

        // Gera um UUID para o arquivo temporário
        String tempObjectName = "temp-" + UUID.randomUUID() + getFileExtension(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketNameTemp)
                            .object(tempObjectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        }

        return tempObjectName;
    }

    public String getTempUrl(String tempObjectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketNameTemp)
                        .object(tempObjectName)
                        .expiry(1, TimeUnit.HOURS) // URL temporária válida por 1 hora
                        .build());
    }

    public void moverParaDefinitivo(String tempObjectName, String destinoObjectName) throws Exception {
        // Verifica se o objeto temporário existe
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketNameTemp)
                            .object(tempObjectName)
                            .build());
        } catch (ErrorResponseException e) {
            throw new Exception("Arquivo temporário não encontrado: " + tempObjectName);
        }

        // Verifica se o bucket definitivo existe
        boolean bucketDefExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketNameDefinitivo)
                        .build());

        if (!bucketDefExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketNameDefinitivo)
                            .build());
        }

        // Copia do temporário para definitivo
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucketNameDefinitivo)  // Bucket DESTINO
                        .object(destinoObjectName)     // Nome do objeto no destino
                        .source(CopySource.builder()
                                .bucket(bucketNameTemp)    // Bucket ORIGEM
                                .object(tempObjectName)     // Nome do objeto na origem
                                .build())
                        .build());

        // Remove o arquivo temporário após cópia bem-sucedida
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketNameTemp)
                        .object(tempObjectName)
                        .build());
    }

    public void removerTemporario(String tempObjectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketNameTemp)
                        .object(tempObjectName)
                        .build());
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}