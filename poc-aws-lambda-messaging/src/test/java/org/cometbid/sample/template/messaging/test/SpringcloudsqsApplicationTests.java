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
package org.cometbid.sample.template.messaging.test;

import lombok.extern.log4j.Log4j2;
import org.cometbid.sample.template.messaging.springcloudsqs.MessageSender;
import org.cometbid.sample.template.messaging.springcloudsqs.MessageSenderWithTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author samueladebowale
 */
@Log4j2
@SpringBootTest
class SpringcloudsqsApplicationTests {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private MessageSenderWithTemplate messageSenderWithTemplate;

    @Test
    void contextLoads() {
    }

    void send_message_with_message_template() {
        log.info("test with message template.");
        messageSenderWithTemplate.send("Test Message1");
    }

    @Test
    void send_message_with_message_channel() {
        log.info("test with message channel.");
        messageSender.send("Test msg");
    }

}
