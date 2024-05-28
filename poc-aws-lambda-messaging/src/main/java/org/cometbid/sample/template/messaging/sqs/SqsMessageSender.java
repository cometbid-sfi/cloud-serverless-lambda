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
package org.cometbid.sample.template.messaging.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author samueladebowale
 */
@Service
@RequiredArgsConstructor
public class SqsMessageSender {

    @Value("${aws.queueName}")
    private String queueName;
    
    private final AmazonSQS amazonSQSClient;

    private final QueueMessagingTemplate queueMessagingTemplate;

    /**
     * 
     * @param message
     * @param groupId
     * @param deDupId 
     */
    public void sendMessage(String message, String groupId, String deDupId) {
         String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();
         
        Message payload = MessageBuilder.withPayload(message)
                .setHeader("message-group-id", groupId)
                .setHeader("message-deduplication-id", deDupId)
                .build();

        queueMessagingTemplate.send(queueUrl, payload);
    }
}
