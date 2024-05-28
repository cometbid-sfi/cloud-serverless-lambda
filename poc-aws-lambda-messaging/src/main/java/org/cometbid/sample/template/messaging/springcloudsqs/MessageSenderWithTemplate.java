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
package org.cometbid.sample.template.messaging.springcloudsqs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

/**
 *
 * @author samueladebowale
 */
@Log4j2
@Service
public class MessageSenderWithTemplate {

    private static final String TEST_QUEUE = "testQueue";

    @Autowired
    private QueueMessagingTemplate messagingTemplate;

    /**
     * 
     * @param messagePayload 
     */
    public void send(final String messagePayload) {

        Message<String> msg = MessageBuilder.withPayload(messagePayload)
                .setHeader("sender", "app1")
                .setHeaderIfAbsent("country", "AE")
                .build();
        
        messagingTemplate.convertAndSend(TEST_QUEUE, msg);
        log.info("message sent");
    }

    /**
     * 
     * @param messagePayload
     * @param messageGroupID
     * @param messageDedupID 
     */
    public void sendToFifoQueue(final String messagePayload, final String messageGroupID, final String messageDedupID) {

        Message<String> msg = MessageBuilder.withPayload(messagePayload)
                .setHeader("message-group-id", messageGroupID)
                .setHeader("message-deduplication-id", messageDedupID)
                .build();
        
        messagingTemplate.convertAndSend(TEST_QUEUE, msg);
        log.info("message sent");
    }
}
