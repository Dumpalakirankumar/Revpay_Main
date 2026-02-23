package com.revpay.controller.notification;

import com.revpay.entity.Notification;
import com.revpay.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> myNotifications() {
        return notificationService.myNotifications();
    }
}