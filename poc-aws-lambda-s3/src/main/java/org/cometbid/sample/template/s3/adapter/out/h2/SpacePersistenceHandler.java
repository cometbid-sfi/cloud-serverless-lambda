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
package org.cometbid.sample.template.s3.adapter.out.h2;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.cometbid.sample.template.s3.space.S3Space;
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
@Component
@Log4j2
public class SpacePersistenceHandler
        implements CheckSpaceExistence,
        SaveSpace,
        RetrieveAllSpaces,
        ResolveSpaceName,
        DeleteSpace,
        RetrieveSpaceByName {

    private final SpaceRepository spaceRepository;

    public SpacePersistenceHandler(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    /**
     * 
     * @param name
     * @return 
     */
    @Override
    public boolean doesExist(String name) {
        return this.spaceRepository.findById(name).isPresent();
    }

    /**
     * 
     * @param name
     * @return 
     */
    @Override
    public S3Space save(S3Space name) {
        this.spaceRepository.save(SpacePersistenceHandler.mapPojoToJpa(name));
        return name;
    }

    /**
     * 
     * @return 
     */
    @Override
    public List<S3Space> findAll() {
        return spaceRepository.findAll().stream()
                .map(SpacePersistenceHandler::mapJpaToPojo)
                .collect(Collectors.toList());
    }

    private static S3SpaceEntity mapPojoToJpa(S3Space space) {
        return new S3SpaceEntity(space.getName(), space.getBucket(), space.getTtl());
    }

    private static S3Space mapJpaToPojo(S3SpaceEntity entity) {
        return new S3Space(entity.getName(), entity.getBucket(), entity.getTtl());
    }

    /**
     * 
     * @param name
     * @return 
     */
    @Override
    public String resolve(String name) {
        var bucket = spaceRepository.findById(name).get().getBucket();
        log.info("Space " + name + " was resolved to " + bucket);
        return bucket;
    }

    /**
     * 
     * @param name 
     */
    @Override
    public void delete(String name) {
        spaceRepository.deleteById(name);
    }

    /**
     * 
     * @param name
     * @return 
     */
    @Override
    public S3Space retrieveByName(String name) {
        return mapJpaToPojo(spaceRepository.findById(name).orElseThrow());
    }
}
