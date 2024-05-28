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
package org.cometbid.sample.template.s3.docs;

import java.io.IOException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author samueladebowale
 */
@Log4j2
@Service
public class S3DocService {
    
    private final AmazonS3 s3client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * 
     * @param s3client 
     */
    public S3DocService(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    /**
     * 
     * @param objectName
     * @param file
     * @throws IOException 
     */
    public void uploadFile(String objectName, MultipartFile file) throws IOException {
        var putObjectResult = s3client.putObject(bucketName, objectName, file.getInputStream(), null);
        log.info(putObjectResult.getMetadata());
    }

    /**
     * 
     * @param objectName
     * @return 
     */
    public S3Object getFile(String objectName) {
        return s3client.getObject(bucketName, objectName);
    }

    /**
     * 
     * @param objectName 
     */
    public void deleteObject(String objectName) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectName);
        s3client.deleteObject(deleteObjectRequest);
    }
}
