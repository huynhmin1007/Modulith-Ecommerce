package com.dev.minn.ecommerce.notification;

import com.dev.minn.ecommerce.notification.domain.model.NotificationTemplate;
import com.dev.minn.ecommerce.notification.domain.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationDataInitService {

    private final NotificationTemplateRepository repository;

    @Transactional
    public void init() {
        createVerifyOtpTemplate();
        createWelcomeTemplate();
    }

    private void createVerifyOtpTemplate() {

        if (repository.existsByCode(
                "user:registration:verify-otp"
        )) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                .code("user:registration:verify-otp")
                .channel("email")
                .subjectTemplate("Verify your account")
                .bodyTemplate("""
                        <html>
                        <body>
                            <h2>Hello {{username}}</h2>
                            <p>
                                Your OTP code is:
                            </p>
                            <h1>{{otp}}</h1>
                            <p>
                                This OTP will expire in 5 minutes.
                            </p>
                        </body>
                        </html>
                        """)
                .variables(List.of("username", "otp"))
                .active(true)
                .build();

        repository.save(template);
    }

    private void createWelcomeTemplate() {

        if (repository.existsByCode("user:registration:welcome")) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                .code("user:registration:welcome")
                .channel("email")
                .subjectTemplate("Welcome to Minn Ecommerce")
                .bodyTemplate("""
                        <html>
                        <body>
                            <h2>
                                Welcome {{username}} 🎉
                            </h2>
                            <p>
                                Your account has been created successfully.
                            </p>
                            <p>
                                Thank you for joining us.
                            </p>
                        </body>
                        </html>
                        """)
                .variables(List.of("username"))
                .active(true)
                .build();

        repository.save(template);
    }
}
