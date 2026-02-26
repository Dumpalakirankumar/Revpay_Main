package com.revpay.controller.view;

import com.revpay.dto.response.PageResponse;
import com.revpay.entity.Notification;
import com.revpay.service.interfaces.NotificationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Use @Controller, NOT @RestController
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class NotificationViewController {
	
	@Autowired
    private NotificationService notificationService;

	@GetMapping("/notifications")
	public String viewNotifications(Model model) {

	    // 1️⃣ Get unread notifications ONLY
	    List<Notification> notifications =
	            notificationService.getUnreadNotifications();

	    // 2️⃣ Mark them as read
	    notificationService.markAllAsRead();

	    model.addAttribute("notifications", notifications);

	    return "notifications";
	}

}