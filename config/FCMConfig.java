package com.hako.web.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FCMConfig {

  
    
	@Bean
	public FirebaseApp firebaseApp() throws IOException {
	    ClassPathResource resource = new ClassPathResource("firebase/campus-linker-95d41-firebase-adminsdk-gcsku-f8ad0efb49.json");

	    InputStream refreshToken = resource.getInputStream();

	    FirebaseOptions options = FirebaseOptions.builder()
	            .setCredentials(GoogleCredentials.fromStream(refreshToken))
	            .build();

	    // 이미 초기화되었는지 체크
	    if (FirebaseApp.getApps().isEmpty()) {
	        // FirebaseApp을 초기화합니다.
	        return FirebaseApp.initializeApp(options);
	    } else {
	        // 이미 초기화된 경우 기존 FirebaseApp을 반환합니다.
	        return FirebaseApp.getInstance();
	    }
	}

	@Bean
	public FirebaseMessaging firebaseMessaging() throws IOException {
	    return FirebaseMessaging.getInstance(firebaseApp());
	}


}