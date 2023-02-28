package com.jun.app.modules.notification.endpoint;

import com.jun.app.modules.account.domain.entity.Account;
import com.jun.app.modules.account.support.CurrentUser;
import com.jun.app.modules.notification.application.NotificationService;
import com.jun.app.modules.notification.domain.entity.Notification;
import com.jun.app.modules.notification.infra.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class JsonNotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/json-notifications")
    @ResponseBody
    public HashMap<String, Object> getNotifications(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDesc(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
        putCategorizedNotifications(json, notifications, numberOfChecked, notifications.size());
        json.put("isNew", true);
        notificationService.markAsRead(notifications);
        return json;
    }

    @GetMapping("/json-notifications/old")
    @ResponseBody
    public HashMap<String, Object> getOldNotifications(@CurrentUser Account account, Model model) {
    	HashMap<String, Object> json = new HashMap<>();
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDesc(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        putCategorizedNotifications(json, notifications, notifications.size(), numberOfNotChecked);
        json.put("isNew", false);
        return json;
    }

    @DeleteMapping("/json-notifications")
    @ResponseBody
    public HashMap<String, Object> deleteNotifications(@CurrentUser Account account) {
    	HashMap<String, Object> json = new HashMap<>();
        notificationRepository.deleteByAccountAndChecked(account, true);
        json.put("redirect", "notifications");
        return json;
    }

    private void putCategorizedNotifications(HashMap<String, Object> json, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        ArrayList<Notification> newStudyNotifications = new ArrayList<>();
        ArrayList<Notification> eventEnrollmentNotifications = new ArrayList<>();
        ArrayList<Notification> watchingStudyNotifications = new ArrayList<>();
        for (Notification notification : notifications) {
            switch (notification.getNotificationType()) {
                case STUDY_CREATED: {
                    newStudyNotifications.add(notification);
                    break;
                }
                case EVENT_ENROLLMENT: {
                    eventEnrollmentNotifications.add(notification);
                    break;
                }
                case STUDY_UPDATED: {
                    watchingStudyNotifications.add(notification);
                    break;
                }
            }
        }
        json.put("numberOfNotChecked", numberOfNotChecked);
        json.put("numberOfChecked", numberOfChecked);
        json.put("notifications", notifications);
        json.put("newStudyNotifications", newStudyNotifications);
        json.put("eventEnrollmentNotifications", eventEnrollmentNotifications);
        json.put("watchingStudyNotifications", watchingStudyNotifications);
    }
}
