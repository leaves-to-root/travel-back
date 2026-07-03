package com.travel.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮箱验证码（HTML 格式）
     *
     * @param toEmail 收件人邮箱
     * @param code    验证码
     */
    public void sendVerifyCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("【旅行网】邮箱验证码");
            String html = """
                    <div style="max-width:480px;margin:0 auto;padding:32px;background:#f8f9fc;border-radius:16px;font-family:Arial,sans-serif">
                      <div style="text-align:center;font-size:32px;margin-bottom:16px">✈️</div>
                      <h2 style="text-align:center;color:#2d3436;margin-bottom:8px">旅行网 · 邮箱验证</h2>
                      <p style="text-align:center;color:#a0a8ac;margin-bottom:24px">您正在使用邮箱验证码登录/注册</p>
                      <div style="background:#fff;border-radius:12px;padding:24px;text-align:center;box-shadow:0 4px 12px rgba(0,0,0,.05)">
                        <p style="color:#a0a8ac;margin:0 0 8px">验证码（5分钟内有效）</p>
                        <div style="font-size:36px;font-weight:700;letter-spacing:8px;color:#667eea">%s</div>
                      </div>
                      <p style="text-align:center;color:#c0c6cc;font-size:12px;margin-top:20px">如非本人操作，请忽略此邮件</p>
                    </div>
                    """.formatted(code);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("验证码邮件已发送至 {}", toEmail);
        } catch (MessagingException e) {
            log.error("邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}
