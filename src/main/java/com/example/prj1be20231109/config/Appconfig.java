package com.example.prj1be20231109.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;



@Configuration
public class Appconfig {

    @Value("${aws.acessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials credentials=AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        AwsCredentialsProvider provider= StaticCredentialsProvider.create(credentials);

    S3Client s3=S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(provider)
            .build();

            return s3;
    }
}
