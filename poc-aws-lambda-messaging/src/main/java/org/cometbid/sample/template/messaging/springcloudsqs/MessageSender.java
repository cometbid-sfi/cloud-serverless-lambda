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

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.aws.messaging.core.QueueMessageChannel;

/**
 *
 * @author samueladebowale
 */
@Log4j2
@Service
public class MessageSender {

    private static final String QUEUE_NAME = "https://sqs.us-east-1.amazonaws.com/474715013863/testQueue";

    private final AmazonSQSAsync amazonSqs;

    public MessageSender(final AmazonSQSAsync amazonSQSAsync) {
        this.amazonSqs = amazonSQSAsync;
    }

    /**
     * 
     * @param messagePayload
     * @return 
     */
    public boolean send(final String messagePayload) {
        MessageChannel messageChannel = new QueueMessageChannel(amazonSqs, QUEUE_NAME);

        Message<String> msg = MessageBuilder.withPayload(messagePayload)
                .setHeader("sender", "app1")
                .setHeaderIfAbsent("country", "AE")
                .build();

        long waitTimeoutMillis = 5000;
        boolean sentStatus = messageChannel.send(msg, waitTimeoutMillis);
        log.info("message sent");
        return sentStatus;
    }

}
