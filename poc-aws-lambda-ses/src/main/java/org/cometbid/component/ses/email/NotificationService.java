/*
 * The MIT License
 *
 * Copyright 2024 samueladebowale.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cometbid.component.ses.email;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author samueladebowale
 */
@Log4j2
@Service
public class NotificationService {

    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;

    public NotificationService(MailSender mailSender, JavaMailSender javaMailSender) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
    }

    /**
     * 
     * @param simpleMailMessage 
     */
    public void sendMailMessage(final SimpleMailMessage simpleMailMessage) {
        log.info("mailSender {}", mailSender.getClass().getName());
        this.mailSender.send(simpleMailMessage);
    }

    /**
     * 
     */
    public void sendMailMessageWithAttachments() {
        this.javaMailSender.send((MimeMessage mimeMessage) -> {
            MimeMessageHelper helper
                    = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.addTo("foo@bar.com");
            helper.setFrom("bar@baz.com");

            InputStreamSource data = new ByteArrayResource("".getBytes());
            helper.addAttachment("test.txt", data);
            helper.setSubject("test subject with attachment");
            helper.setText("mime body", false);
        });
    }
}
