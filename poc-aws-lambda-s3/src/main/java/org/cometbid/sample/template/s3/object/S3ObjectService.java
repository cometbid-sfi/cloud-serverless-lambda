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
package org.cometbid.sample.template.s3.object;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import org.cometbid.sample.template.s3.object.in.CreateLink;
import org.cometbid.sample.template.s3.object.in.RemoveObject;
import org.cometbid.sample.template.s3.object.in.UpdateObject;
import org.cometbid.sample.template.s3.object.in.UploadObject;
import org.cometbid.sample.template.s3.object.out.CreatePresignedUrl;
import org.cometbid.sample.template.s3.object.out.DeleteObject;
import org.cometbid.sample.template.s3.object.out.MakeObjectPrivate;
import org.cometbid.sample.template.s3.object.out.MakeObjectPublic;
import org.cometbid.sample.template.s3.object.out.SaveObject;
import org.cometbid.sample.template.s3.space.out.ResolveSpaceName;

/**
 *
 * @author samueladebowale
 */
@Service
@Log4j2
public class S3ObjectService implements UploadObject, UpdateObject, RemoveObject, CreateLink {

    private final ResolveSpaceName bucketNameResolver;
    private final SaveObject objectSaver;
    private final MakeObjectPublic objectPublicMaker;
    private final MakeObjectPrivate objectPrivateMaker;
    private final DeleteObject objectDeleter;
    private final CreatePresignedUrl presignedUrlCreator;

    public S3ObjectService(
            ResolveSpaceName bucketNameResolver,
            SaveObject objectSaver,
            MakeObjectPublic objectPublicMaker,
            MakeObjectPrivate objectPrivateMaker,
            DeleteObject objectDeleter,
            CreatePresignedUrl presignedUrlCreator) {
        this.bucketNameResolver = bucketNameResolver;
        this.objectSaver = objectSaver;
        this.objectPublicMaker = objectPublicMaker;
        this.objectPrivateMaker = objectPrivateMaker;
        this.objectDeleter = objectDeleter;
        this.presignedUrlCreator = presignedUrlCreator;
    }

    /**
     * 
     * @param space
     * @param name
     * @param payload
     * @return 
     */
    @Override
    public Object upload(String space, String name, InputStream payload) {
        // check if bucket exists
        var bucket = bucketNameResolver.resolve(space);

        // generate a id & store in lookup table
        var key = UUID.randomUUID().toString();

        // save
        log.info(
                "Going to upload the file into to "
                + bucket
                + "/"
                + key
                + " with metadata name of "
                + name);

        return objectSaver.save(bucket, key, name, payload);
    }

    /**
     * 
     * @param space
     * @param key
     * @param updates
     * @return 
     */
    @Override
    public Object update(String space, String key, S3ObjectPartial updates) {
        var bucket = bucketNameResolver.resolve(space);

        if (updates.getIsPublic() != null) {
            if (updates.getIsPublic()) {
                log.info("going to open up to public");
                objectPublicMaker.makePublic(bucket, key);
            } else {
                log.info("going to remove public access");
                objectPrivateMaker.makePrivate(bucket, key);
            }
        }

        return null;
    }

    /**
     * 
     * @param space
     * @param key 
     */
    @Override
    public void delete(String space, String key) {
        var bucket = bucketNameResolver.resolve(space);

        log.info("Going to delete the file with the key " + key + " in the bucket " + bucket);

        objectDeleter.delete(bucket, key);
    }

    /**
     * 
     * @param space
     * @param key
     * @param duration
     * @return 
     */
    @Override
    public URL createLink(String space, String key, Long duration) {
        var bucket = bucketNameResolver.resolve(space);

        log.info(
                "Going to generate a link for the file "
                + key
                + " in bucket "
                + bucket
                + " with visibility duration of "
                + duration
                + " seconds");

        return presignedUrlCreator.createURL(bucket, key, duration);
    }
}
