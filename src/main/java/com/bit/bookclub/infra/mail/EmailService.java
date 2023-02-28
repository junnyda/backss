package com.bit.bookclub.infra.mail;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
