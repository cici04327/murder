package com.murder.script.controller;

import com.murder.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/admin/file")
@Slf4j
public class FileUploadController {

    @Value("${file.upload.path:/data/upload}")
    private String uploadPath;

    @Value("${file.upload.base-url:http://localhost:8081}")
    private String baseUrl;

    /**
     * 上传单张图片
     */
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传文件：{}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 验证文件大小（限制5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过5MB");
        }

        try {
            String url = saveFile(file, "images");
            log.info("文件上传成功：{}", url);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传图片
     */
    @PostMapping("/upload/batch")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        log.info("批量上传文件，数量：{}", files.length);

        if (files.length == 0) {
            return Result.error("请选择要上传的文件");
        }

        if (files.length > 10) {
            return Result.error("一次最多上传10张图片");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件：" + file.getOriginalFilename());
            }

            // 验证文件大小
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("文件大小不能超过5MB：" + file.getOriginalFilename());
            }

            try {
                String url = saveFile(file, "images");
                urls.add(url);
            } catch (IOException e) {
                log.error("文件上传失败：{}", file.getOriginalFilename(), e);
                return Result.error("文件上传失败：" + file.getOriginalFilename());
            }
        }

        log.info("批量上传成功，数量：{}", urls.size());
        return Result.success(urls);
    }

    /**
     * 上传剧本封面
     */
    @PostMapping("/upload/script-cover")
    public Result<String> uploadScriptCover(@RequestParam("file") MultipartFile file) {
        try {
            String url = saveFile(file, "scripts/covers");
            return Result.success(url);
        } catch (IOException e) {
            log.error("剧本封面上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传角色头像
     */
    @PostMapping("/upload/role-avatar")
    public Result<String> uploadRoleAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String url = saveFile(file, "roles/avatars");
            return Result.success(url);
        } catch (IOException e) {
            log.error("角色头像上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传角色立绘
     */
    @PostMapping("/upload/role-character")
    public Result<String> uploadRoleCharacter(@RequestParam("file") MultipartFile file) {
        try {
            String url = saveFile(file, "roles/characters");
            return Result.success(url);
        } catch (IOException e) {
            log.error("角色立绘上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        // 创建日期目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dirPath = uploadPath + File.separator + subDir + File.separator + dateDir;
        
        Path directory = Paths.get(dirPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(dirPath, filename);
        file.transferTo(filePath.toFile());

        // 返回访问URL
        String relativePath = subDir + "/" + dateDir + "/" + filename;
        return baseUrl + "/upload/" + relativePath.replace("\\", "/");
    }
}
