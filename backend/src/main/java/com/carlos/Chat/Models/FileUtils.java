package com.carlos.Chat.Models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {
    
    private FileUtils() {}

    public static byte[] readFileFromLocation(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) return new byte[0];

        try {
            java.nio.file.Path file = new File(fileUrl).toPath();
            return Files.readAllBytes(file);
        } catch (IOException e) {
            log.warn("No file dound int the path {}", fileUrl);
        }
        return new byte[0];
    }
}
