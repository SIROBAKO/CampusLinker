package com.hako.web.cl.service;

import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.hako.web.cl.dto.FCMNotificationRequestDto;

@Service
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    public FCMService(FirebaseApp firebaseApp) {
        this.firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
    }

    public int sendNotification(FCMNotificationRequestDto notificationRequest) {
//        Notification notification = Notification.builder()
//                .setTitle(notificationRequest.getTitle())
//                .setBody(notificationRequest.getBody())
//                .build();

        Message message = Message.builder()
                .setToken(notificationRequest.getDeviceToken())
//                .setNotification(notification)
                .putData("title", notificationRequest.getTitle())
                .putData("body", String.valueOf(notificationRequest.getBody()))
                .putData("purpose", notificationRequest.getPurpose())
                .putData("purpose_num", String.valueOf(notificationRequest.getPurpose_num()))
                .putData("purpose_title", notificationRequest.getPurpose_title())
                .build();

        try {
            firebaseMessaging.send(message);
            return 1;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
