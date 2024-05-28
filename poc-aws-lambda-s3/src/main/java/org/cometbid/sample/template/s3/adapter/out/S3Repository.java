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
package org.cometbid.sample.template.s3.adapter.out;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.waiters.WaiterParameters;
import java.io.File;
import org.cometbid.sample.template.s3.bucket.out.CreateBucket;
import org.cometbid.sample.template.s3.bucket.out.DeleteBucket;
import org.cometbid.sample.template.s3.bucket.out.ListObjects;
import org.cometbid.sample.template.s3.bucket.out.RemoveVisibilityInObjectLifecycle;
import org.cometbid.sample.template.s3.bucket.out.SetVisibilityInObjectLifecycle;
import org.cometbid.sample.template.s3.object.S3Object;
import org.cometbid.sample.template.s3.object.out.CreatePresignedUrl;
import org.cometbid.sample.template.s3.object.out.DeleteObject;
import org.cometbid.sample.template.s3.object.out.MakeObjectPrivate;
import org.cometbid.sample.template.s3.object.out.MakeObjectPublic;
import org.cometbid.sample.template.s3.object.out.SaveObject;

/**
 *
 * @author samueladebowale
 */
@Repository
@Log4j2
public class S3Repository
        implements CreateBucket,
        DeleteBucket,
        ListObjects,
        SaveObject,
        MakeObjectPublic,
        MakeObjectPrivate,
        DeleteObject,
        CreatePresignedUrl,
        SetVisibilityInObjectLifecycle,
        RemoveVisibilityInObjectLifecycle {

    private final AmazonS3Client s3Client;

    public S3Repository(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     *
     * @param bucket
     */
    @Override
    public void create(String bucket) {
        // send bucket creation request
        s3Client.createBucket(bucket);
        log.info("Request to create " + bucket + " sent");

        // assure that bucket is available
        s3Client.waiters().bucketExists().run(new WaiterParameters<>(new HeadBucketRequest(bucket)));
        log.info("Bucket " + bucket + " is ready");
    }

    /**
     *
     * @param bucket
     */
    @Override
    public void delete(String bucket) {
        // send deletion request
        s3Client.deleteBucket(bucket);
        log.info("Request to delete " + bucket + " sent");

        // assure bucket is deleted
        s3Client.waiters().bucketNotExists().run(new WaiterParameters(new HeadBucketRequest(bucket)));
        log.info("Bucket " + bucket + " is deleted");
    }

    /**
     * 
     * @param bucket
     * @return 
     */
    @Override
    public List<S3Object> listObjectsInBucket(String bucket) {
        var items
                = s3Client.listObjectsV2(bucket)
                        .getObjectSummaries().stream()
                        .parallel()
                        .map(S3ObjectSummary::getKey)
                        .map(key -> mapS3ToObject(bucket, key))
                        .collect(Collectors.toList());

        log.info("Found " + items.size() + " objects in the bucket " + bucket);
        return items;
    }

    /**
     * 
     * @param bucket
     * @param key
     * @param name
     * @param payload
     * @return 
     */
    @Override
    public S3Object save(String bucket, String key, String name, InputStream payload) {
        var metadata = new ObjectMetadata();
        metadata.addUserMetadata("name", name);
        
        s3Client.putObject(bucket, key, payload, metadata);
        log.info("Sent the request");
        
        return S3Object.builder().name(name).key(key).url(s3Client.getUrl(bucket, key)).build();
    }
    
    /**
     * 
     * @param bucket
     * @param key
     * @param name
     * @param file
     * @return 
     */
    @Override
    public S3Object save(String bucket, String key, String name, File file) {
        s3Client.putObject(bucket, key, file);
        log.info("Sent the request");
        
        return S3Object.builder().name(name).key(key).url(s3Client.getUrl(bucket, key)).build();
    }

    /**
     * 
     * @param bucket
     * @param key
     * @param name
     * @param content
     * @return 
     */
    @Override
    public S3Object save(String bucket, String key, String name, String content) {
        
        s3Client.putObject(bucket, key, content);
        log.info("Sent the request");
        
        return S3Object.builder().name(name).key(key).url(s3Client.getUrl(bucket, key)).build();
    }

    /**
     * 
     * @param bucket
     * @param key 
     */
    @Override
    public void makePublic(String bucket, String key) {
        s3Client.setObjectAcl(bucket, key, CannedAccessControlList.PublicRead);
        log.info("Sent request to make object in bucket " + bucket + " with key " + key + " public");
    }

    /**
     * 
     * @param bucket
     * @param key 
     */
    @Override
    public void makePrivate(String bucket, String key) {
        s3Client.setObjectAcl(bucket, key, CannedAccessControlList.BucketOwnerFullControl);
        log.info("Sent request to make object in bucket " + bucket + " with key " + key + " private");
    }

    /**
     * 
     * @param bucket
     * @param key 
     */
    @Override
    public void delete(String bucket, String key) {
        s3Client.deleteObject(bucket, key);
        log.info("Sent request to delete file with key " + key + " in bucket " + bucket);
    }

    /**
     * 
     * @param bucket
     * @param key
     * @param duration
     * @return 
     */
    @Override
    public URL createURL(String bucket, String key, Long duration) {
        var date = new Date(new Date().getTime() + duration * 1000); // 1 s * 1000 ms/s
        var url = s3Client.generatePresignedUrl(bucket, key, date);
        log.info("Generated the signature " + url);
        return url;
    }

    /**
     * 
     * @param bucket
     * @param ttlInDays 
     */
    @Override
    public void setVisibility(String bucket, Integer ttlInDays) {
        s3Client.setBucketLifecycleConfiguration(
                bucket,
                new BucketLifecycleConfiguration()
                        .withRules(
                                new BucketLifecycleConfiguration.Rule()
                                        .withId("custom-expiration-id")
                                        .withFilter(new LifecycleFilter())
                                        .withStatus(BucketLifecycleConfiguration.ENABLED)
                                        .withExpirationInDays(ttlInDays)));
    }

    /**
     * 
     * @param bucket 
     */
    @Override
    public void removeVisibility(String bucket) {
        s3Client.deleteBucketLifecycleConfiguration(bucket);
    }

    private S3Object mapS3ToObject(String bucket, String key) {

        return S3Object.builder()
                .name(s3Client.getObjectMetadata(bucket, key).getUserMetaDataOf("name"))
                .key(key)
                .url(s3Client.getUrl(bucket, key))
                .isPublic(
                        s3Client.getObjectAcl(bucket, key).getGrantsAsList().stream()
                                .anyMatch(grant -> grant.equals(S3Repository.publicObjectReadGrant())))
                .build();
    }

    private static Grant publicObjectReadGrant() {
        return new Grant(
                GroupGrantee.parseGroupGrantee(GroupGrantee.AllUsers.getIdentifier()), Permission.Read);
    }

}
