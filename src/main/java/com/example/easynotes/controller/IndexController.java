package com.example.easynotes.controller;


import com.example.easynotes.pojo.Compress;
import com.example.easynotes.pojo.Decompress;
import com.example.easynotes.utils.CommonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/")
public class IndexController {
    @RequestMapping(path = "/compress", method = RequestMethod.POST, produces = "application/zip",consumes ={"multipart/form-data"})
    public ResponseEntity<Object> generateCompression(@Valid @RequestBody Compress requestBody)
            throws Exception {

        File dest;
        HashMap<String, String> map = new HashMap<>();
        MultipartFile file = requestBody.getFile();

        if (file.isEmpty()) {
            map.put("Error", "Please select a directory or a folder");
            return ResponseEntity.ok(map);
        }

        try {
            String filePath = System.getProperty("java.io.tmpdir");
            dest = new File(filePath);
            file.transferTo(dest);
        } catch (Exception exc) {
            map.put("Error", "Error while saving file");
            return ResponseEntity.ok(map);
        }
        //creating byteArray stream, make it bufforable and passing this buffor to ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        CommonUtil.zipFile(dest, requestBody.getInputDir(), zipOutputStream);

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", requestBody.getOutputDir()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ResponseEntity<Object>
                responseEntity = ResponseEntity.ok().headers(headers).contentLength(byteArrayOutputStream.toByteArray().length).contentType(
                MediaType.parseMediaType("application/zip")).body(byteArrayOutputStream.toByteArray());

        return responseEntity;

    }

    @PostMapping("/decompress")
    public Map<String, String> generateDecompression(@Valid @RequestBody Decompress requestBody) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        map.put("foo", "bar");
        map.put("aa", "bb");
        return map;
    }

}
