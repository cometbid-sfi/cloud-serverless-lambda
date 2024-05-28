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
package org.cometbid.sample.template.s3.controller;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Stream;
import org.cometbid.sample.template.s3.file.FileInfo;
import org.cometbid.sample.template.s3.file.FileStorageService;
import org.cometbid.sample.template.s3.file.ResponseMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Uses FileStorageService to export Rest APIs: POST a file, GET all files’
 * information, GET a File.
 *
 * @author samueladebowale
 */
@Controller
@CrossOrigin("http://localhost:8081")
public class FileController {

    private FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     *
     * @param filePartMono
     * @return
     */
    @PostMapping("/upload")
    public Mono<ResponseEntity<ResponseMessage>> uploadFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return storageService.save(filePartMono).map(
                (filename) -> ResponseEntity.ok().body(new ResponseMessage("Uploaded the file successfully: " + filename)));
    }

    /**
     *
     * @return
     */
    @GetMapping("/files")
    public ResponseEntity<Flux<FileInfo>> getListFiles() {
        Stream<FileInfo> fileInfoStream = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = UriComponentsBuilder.newInstance().path("/files/{filename}")
                    .buildAndExpand(filename).toUriString();
            return new FileInfo(filename, url);
        });

        Flux<FileInfo> fileInfosFlux = Flux.fromStream(fileInfoStream);

        return ResponseEntity.status(HttpStatus.OK).body(fileInfosFlux);
    }

    /**
     *
     * @param filename
     * @return
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Flux<DataBuffer>> getFile(@PathVariable String filename) {
        Flux<DataBuffer> file = storageService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
    }

    /**
     * 
     * @param filename
     * @return 
     */
    @DeleteMapping("/files/{filename:.+}")
    public Mono<ResponseEntity<ResponseMessage>> deleteFile(@PathVariable String filename) {
        String message = "";

        try {
            boolean existed = storageService.delete(filename);

            if (existed) {
                message = "Delete the file successfully: " + filename;
                return Mono.just(ResponseEntity.ok().body(new ResponseMessage(message)));
            }

            message = "The file does not exist!";
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message)));
        } catch (Exception e) {
            message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(message)));
        }
    }
}
