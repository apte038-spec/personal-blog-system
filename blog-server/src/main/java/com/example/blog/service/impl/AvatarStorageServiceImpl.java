package com.example.blog.service.impl;

import com.example.blog.common.BusinessException;
import com.example.blog.service.AvatarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AvatarStorageServiceImpl implements AvatarStorageService {
    private static final Logger log = LoggerFactory.getLogger(AvatarStorageServiceImpl.class);
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final String PUBLIC_PREFIX = "/uploads/avatars/";
    private static final Set<String> EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Pattern MANAGED_FILE = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp)$");

    private final Path avatarRoot;

    public AvatarStorageServiceImpl(@Value("${blog.upload.avatar-dir:uploads/avatars}") String avatarDir) {
        this.avatarRoot = Path.of(avatarDir).toAbsolutePath().normalize();
    }

    @Override
    public StoredAvatar store(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException("请选择头像图片");
        if (file.getSize() > MAX_SIZE) throw new BusinessException("头像图片不能超过 5MB");

        String originalName = file.getOriginalFilename();
        String extension = extensionOf(originalName);
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!EXTENSIONS.contains(extension) || !MIME_TYPES.contains(contentType)) throw invalidFormat();

        String detected = detectFormat(file);
        boolean matches = ("jpg".equals(detected) && ("jpg".equals(extension) || "jpeg".equals(extension))
                && "image/jpeg".equals(contentType))
                || ("png".equals(detected) && "png".equals(extension) && "image/png".equals(contentType))
                || ("webp".equals(detected) && "webp".equals(extension) && "image/webp".equals(contentType));
        if (!matches) throw invalidFormat();

        String fileName = UUID.randomUUID() + "." + detected;
        Path target = avatarRoot.resolve(fileName).normalize();
        if (!target.startsWith(avatarRoot)) throw invalidFormat();
        try {
            Files.createDirectories(avatarRoot);
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return new StoredAvatar(PUBLIC_PREFIX + fileName);
        } catch (IOException exception) {
            tryDelete(target);
            log.error("头像文件保存失败，target={}", target, exception);
            throw new BusinessException("头像保存失败，请稍后重试");
        }
    }

    @Override
    public void deleteManagedAvatar(String avatarPath) {
        if (avatarPath == null || !avatarPath.startsWith(PUBLIC_PREFIX)) return;
        String fileName = avatarPath.substring(PUBLIC_PREFIX.length());
        if (!MANAGED_FILE.matcher(fileName).matches()) return;
        Path target = avatarRoot.resolve(fileName).normalize();
        if (!target.startsWith(avatarRoot) || Files.isSymbolicLink(target)) return;
        tryDelete(target);
    }

    private String extensionOf(String name) {
        if (name == null) return "";
        int dot = name.lastIndexOf('.');
        return dot < 0 || dot == name.length() - 1 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String detectFormat(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(12);
            if (header.length >= 3 && unsigned(header[0]) == 0xff && unsigned(header[1]) == 0xd8 && unsigned(header[2]) == 0xff) return "jpg";
            if (header.length >= 8 && unsigned(header[0]) == 0x89 && header[1] == 0x50 && header[2] == 0x4e
                    && header[3] == 0x47 && header[4] == 0x0d && header[5] == 0x0a && header[6] == 0x1a && header[7] == 0x0a) return "png";
            if (header.length >= 12 && ascii(header, 0, "RIFF") && ascii(header, 8, "WEBP")) return "webp";
            throw invalidFormat();
        } catch (IOException exception) {
            throw new BusinessException("头像文件读取失败，请重新选择");
        }
    }

    private boolean ascii(byte[] bytes, int offset, String value) {
        for (int i = 0; i < value.length(); i++) if (bytes[offset + i] != (byte) value.charAt(i)) return false;
        return true;
    }

    private int unsigned(byte value) { return value & 0xff; }

    private BusinessException invalidFormat() {
        return new BusinessException("请选择 JPG、PNG 或 WebP 格式的图片");
    }

    private void tryDelete(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException exception) {
            log.warn("头像文件删除失败，target={}", target, exception);
        }
    }
}
