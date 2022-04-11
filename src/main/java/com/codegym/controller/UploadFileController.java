package com.codegym.controller;

import com.codegym.model.dto.ImageForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/files")
public class UploadFileController {
    @Value("${file-upload}")
    private String uploadPath;

    @PostMapping
    public ResponseEntity<String> upload(@ModelAttribute(name="image") ImageForm imageForm){
        MultipartFile image = imageForm.getImage();
        String filename = image.getOriginalFilename();
        filename = addTimeStampToFilename(filename);
        try {
            FileCopyUtils.copy(image.getBytes(), new File(uploadPath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(filename, HttpStatus.OK);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> delete(@PathVariable String filename){
        File file = new File(uploadPath + filename);
        file.delete();
        return new ResponseEntity<>(filename, HttpStatus.OK);
    }

    public String addTimeStampToFilename(String filename) {
        Long currentTime = System.currentTimeMillis();
        return "" + currentTime + "_" + filename;
    }
}
