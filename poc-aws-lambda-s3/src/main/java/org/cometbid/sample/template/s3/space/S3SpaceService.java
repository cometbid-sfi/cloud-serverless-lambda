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
package org.cometbid.sample.template.s3.space;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.cometbid.sample.template.s3.bucket.out.CreateBucket;
import org.cometbid.sample.template.s3.bucket.out.DeleteBucket;
import org.cometbid.sample.template.s3.bucket.out.ListObjects;
import org.cometbid.sample.template.s3.bucket.out.RemoveVisibilityInObjectLifecycle;
import org.cometbid.sample.template.s3.bucket.out.SetVisibilityInObjectLifecycle;
import org.cometbid.sample.template.s3.object.S3Object;
import org.cometbid.sample.template.s3.object.in.GetAllObjects;
import org.cometbid.sample.template.s3.object.in.RemoveObject;
import org.cometbid.sample.template.s3.object.out.DeleteObject;
import org.cometbid.sample.template.s3.space.in.CreateSpace;
import org.cometbid.sample.template.s3.space.in.ForceRemoveSpace;
import org.cometbid.sample.template.s3.space.in.GetAllSpaces;
import org.cometbid.sample.template.s3.space.in.RemoveSpace;
import org.cometbid.sample.template.s3.space.in.RemoveTTL;
import org.cometbid.sample.template.s3.space.in.SetTTL;
import org.cometbid.sample.template.s3.space.out.CheckSpaceExistence;
import org.cometbid.sample.template.s3.space.out.DeleteSpace;
import org.cometbid.sample.template.s3.space.out.ResolveSpaceName;
import org.cometbid.sample.template.s3.space.out.RetrieveAllSpaces;
import org.cometbid.sample.template.s3.space.out.RetrieveSpaceByName;
import org.cometbid.sample.template.s3.space.out.SaveSpace;

/**
 *
 * @author samueladebowale
 */
@Service
@Log4j2
public class S3SpaceService
        implements CreateSpace,
        GetAllSpaces,
        GetAllObjects,
        RemoveSpace,
        ForceRemoveSpace,
        SetTTL,
        RemoveTTL {

    private final CheckSpaceExistence spaceExistenceChecker;
    private final SaveSpace spaceSaver;
    private final RetrieveAllSpaces allSpaceRetriever;
    private final CreateBucket bucketCreator;
    private final ResolveSpaceName bucketNameResolver;
    private final DeleteBucket bucketDeleter;
    private final DeleteSpace spaceDeleter;
    private final ListObjects objectLister;
    private final DeleteObject objectDeleter;
    private final SetVisibilityInObjectLifecycle objectLifecycleVisibilitySetter;
    private final RemoveVisibilityInObjectLifecycle objectLifecycleVisibilityRemover;
    private final RetrieveSpaceByName spaceByNameRetriever;

    public S3SpaceService(
            CheckSpaceExistence spaceExistenceChecker,
            SaveSpace spaceSaver,
            RetrieveAllSpaces allSpaceRetriever,
            CreateBucket bucketCreator,
            ResolveSpaceName bucketNameResolver,
            DeleteBucket bucketDeleter,
            DeleteSpace spaceDeleter,
            ListObjects objectLister,
            RemoveObject objectRemover,
            DeleteObject objectDeleter,
            SetVisibilityInObjectLifecycle objectLifecycleVisibilitySetter,
            RemoveVisibilityInObjectLifecycle objectLifecycleVisibilityRemover,
            RetrieveSpaceByName spaceByNameRetriever) {
        this.spaceExistenceChecker = spaceExistenceChecker;
        this.spaceSaver = spaceSaver;
        this.allSpaceRetriever = allSpaceRetriever;
        this.bucketNameResolver = bucketNameResolver;
        this.bucketCreator = bucketCreator;
        this.bucketDeleter = bucketDeleter;
        this.spaceDeleter = spaceDeleter;
        this.objectLister = objectLister;
        this.objectDeleter = objectDeleter;
        this.objectLifecycleVisibilitySetter = objectLifecycleVisibilitySetter;
        this.objectLifecycleVisibilityRemover = objectLifecycleVisibilityRemover;
        this.spaceByNameRetriever = spaceByNameRetriever;
    }

    @Override
    public S3Space create(String name) {
        // check if bucket exists
        if (spaceExistenceChecker.doesExist(name)) {
            log.info("Space " + name + " does already exist");
            return null;
        }

        // create
        S3Space space = new S3Space(name, "spring-boot-s3-tutorial-" + UUID.randomUUID().toString(), null);
        log.info("Mapped space to bucket " + space);
        bucketCreator.create(space.getBucket());

        // create bucket meta
        return this.spaceSaver.save(space);
    }

    @Override
    public List<S3Space> getAll() {
        var buckets = allSpaceRetriever.findAll();
        log.info("Found " + buckets.size() + " buckets");
        return buckets;
    }

    @Override
    public List<S3Object> getAllObjects(String space) {
        // get bucket from H2
        var bucket = bucketNameResolver.resolve(space);

        // return all files in bucket
        return objectLister.listObjectsInBucket(bucket);
    }

    @Override
    public void remove(String space) {
        // get bucket from H2
        var bucket = bucketNameResolver.resolve(space);

        // delete from S3
        bucketDeleter.delete(bucket);

        // delete from H2
        spaceDeleter.delete(space);
    }

    @Override
    public void forceRemove(String space) {
        // get bucket from H2
        var bucket = bucketNameResolver.resolve(space);

        // empty bucket
        getAllObjects(space).stream()
                .peek(log::info)
                .forEach(object -> objectDeleter.delete(bucket, object.getKey()));

        // get rid of bucket
        remove(space);
    }

    @Override
    public void setTTL(String space, Integer ttlInDays) {
        var bucket = bucketNameResolver.resolve(space);
        log.info("Going to adjust the TTL for the bucket " + bucket + " to " + ttlInDays + " day(s)");

        // S3
        objectLifecycleVisibilitySetter.setVisibility(bucket, ttlInDays);

        // H2
        var spaceEntity = spaceByNameRetriever.retrieveByName(space);
        spaceEntity.setTtl(ttlInDays);
        spaceSaver.save(spaceEntity);
    }

    @Override
    public void removeTTL(String space) {
        var bucket = bucketNameResolver.resolve(space);
        log.info("Going to remove TTL policy for bucket " + bucket);

        // S3
        objectLifecycleVisibilityRemover.removeVisibility(bucket);

        // H2
        var spaceEntity = spaceByNameRetriever.retrieveByName(space);
        spaceEntity.setTtl(null);
        spaceSaver.save(spaceEntity);
    }
}
