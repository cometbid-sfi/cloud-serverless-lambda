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
package org.cometbid.sample.template.s3.adapter.rest.controller;


import java.net.URL;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.cometbid.sample.template.s3.object.S3Object;
import org.cometbid.sample.template.s3.object.S3ObjectPartial;
import org.cometbid.sample.template.s3.object.in.CreateLink;
import org.cometbid.sample.template.s3.object.in.GetAllObjects;
import org.cometbid.sample.template.s3.object.in.RemoveObject;
import org.cometbid.sample.template.s3.object.in.UpdateObject;
import org.cometbid.sample.template.s3.object.in.UploadObject;
import org.cometbid.sample.template.s3.space.S3Space;
import org.cometbid.sample.template.s3.space.S3SpacePartial;
import org.cometbid.sample.template.s3.space.in.CreateSpace;
import org.cometbid.sample.template.s3.space.in.ForceRemoveSpace;
import org.cometbid.sample.template.s3.space.in.GetAllSpaces;
import org.cometbid.sample.template.s3.space.in.RemoveSpace;
import org.cometbid.sample.template.s3.space.in.RemoveTTL;
import org.cometbid.sample.template.s3.space.in.SetTTL;

/**
 *
 * @author samueladebowale
 */
@RestController
@RequestMapping("/api/v1")
@Log4j2
public class S3RestApi {

    private final CreateSpace spaceCreator;
    private final GetAllSpaces allSpaceGetter;
    private final RemoveSpace spaceRemover;
    private final GetAllObjects allObjectsInSpaceGetter;
    private final UploadObject objectUploader;
    private final UpdateObject objectUpdater;
    private final RemoveObject objectDeleter;
    private final ForceRemoveSpace forceSpaceRemover;
    private final CreateLink linkCreator;
    private final SetTTL ttlUpdater;
    private final RemoveTTL ttlRemover;

    public S3RestApi(
            CreateSpace spaceCreator,
            GetAllSpaces allSpaceGetter,
            RemoveSpace spaceRemover,
            GetAllObjects allObjectsInSpaceGetter,
            UploadObject objectUploader,
            UpdateObject objectUpdater,
            RemoveObject objectDeleter,
            ForceRemoveSpace forceSpaceRemover,
            CreateLink linkCreator,
            SetTTL ttlUpdater,
            RemoveTTL ttlRemover) {
        this.spaceCreator = spaceCreator;
        this.allSpaceGetter = allSpaceGetter;
        this.spaceRemover = spaceRemover;
        this.allObjectsInSpaceGetter = allObjectsInSpaceGetter;
        this.objectUploader = objectUploader;
        this.objectUpdater = objectUpdater;
        this.objectDeleter = objectDeleter;
        this.forceSpaceRemover = forceSpaceRemover;
        this.linkCreator = linkCreator;
        this.ttlUpdater = ttlUpdater;
        this.ttlRemover = ttlRemover;
    }

    /**
     * 
     * @return 
     */
    @GetMapping("/space")
    List<S3Space> getSpaces() {
        return allSpaceGetter.getAll();
    }

    /**
     * 
     * @param space
     * @return 
     */
    @PostMapping("/space/{space}")
    S3Space postSpace(@PathVariable String space) {
        return spaceCreator.create(space);
    }

    /**
     * 
     * @param space
     * @param force 
     */
    @DeleteMapping("/space/{space}")
    void deleteSpace(@PathVariable String space, @RequestParam Optional<Boolean> force) {
        log.info("Got the value " + force);
        force.ifPresentOrElse(
                value -> {
                    if (value) {
                        forceSpaceRemover.forceRemove(space);
                    } else {
                        spaceRemover.remove(space);
                    }
                },
                () -> spaceRemover.remove(space));
    }

    /**
     * 
     * @param space
     * @return 
     */
    @GetMapping("/space/{space}/object")
    List<S3Object> getObjectsInSpace(@PathVariable String space) {
        return allObjectsInSpaceGetter.getAllObjects(space);
    }

    /**
     * 
     * @param space
     * @param file
     * @param name
     * @return 
     */
    @SneakyThrows
    @PostMapping("/space/{space}/object")
    Object postObject(
            @PathVariable String space,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, name = "name") String name) {
        var key = name != null ? name : file.getOriginalFilename();
        return objectUploader.upload(space, key, file.getInputStream());
    }

    /**
     * 
     * @param space
     * @param key
     * @param body
     * @return 
     */
    @PatchMapping("/space/{space}/object/{key}")
    Object patchObject(
            @PathVariable String space, @PathVariable String key, @RequestBody S3ObjectPartial body) {
        log.info("Got the partial " + body);
        return objectUpdater.update(space, key, body);
    }

    /**
     * 
     * @param space
     * @param key 
     */
    @DeleteMapping("/space/{space}/object/{key}")
    void deleteObject(@PathVariable String space, @PathVariable String key) {
        objectDeleter.delete(space, key);
    }

    /**
     * 
     * @param space
     * @param key
     * @param duration
     * @return 
     */
    @PostMapping("/space/{space}/object/{key}/url")
    URL createLink(
            @PathVariable String space,
            @PathVariable String key,
            @RequestParam(required = false, name = "duration", defaultValue = "300") Long duration) {
        return linkCreator.createLink(space, key, duration);
    }

    /**
     * 
     * @param space
     * @param body 
     */
    @PatchMapping("/space/{space}")
    void patchSpace(@PathVariable String space, @RequestBody(required = false) S3SpacePartial body) {
        log.info("got " + body);
        if (body.getTtlInDays() > 1) {
            ttlUpdater.setTTL(space, body.getTtlInDays());
        } else {
            ttlRemover.removeTTL(space);
        }
    }
}
