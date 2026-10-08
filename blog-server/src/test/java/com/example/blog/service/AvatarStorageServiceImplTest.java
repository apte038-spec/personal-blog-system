package com.example.blog.service;

import com.example.blog.common.BusinessException;
import com.example.blog.service.impl.AvatarStorageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvatarStorageServiceImplTest {
    @TempDir Path tempDir;

    @Test
    void storesWithGeneratedUuidAndSafelyDeletesManagedFile() {
        AvatarStorageServiceImpl service = new AvatarStorageServiceImpl(tempDir.toString());
        byte[] jpeg = {(byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xe0, 0, 0, 0, 0, 0, 0, 0, 0};
        MockMultipartFile first = new MockMultipartFile("file", "avatar.jpeg", "image/jpeg", jpeg);
        MockMultipartFile second = new MockMultipartFile("file", "avatar.jpeg", "image/jpeg", jpeg);

        String firstPath = service.store(first).publicPath();
        String secondPath = service.store(second).publicPath();

        assertTrue(firstPath.matches("/uploads/avatars/[0-9a-f-]{36}\\.jpg"));
        assertNotEquals(firstPath, secondPath);
        Path firstFile = tempDir.resolve(firstPath.substring("/uploads/avatars/".length()));
        assertTrue(Files.exists(firstFile));

        service.deleteManagedAvatar(firstPath);
        assertFalse(Files.exists(firstFile));
    }

    @Test
    void acceptsPngAndWebpSignatures() {
        AvatarStorageServiceImpl service = new AvatarStorageServiceImpl(tempDir.toString());
        byte[] png = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0, 0, 0, 0};
        byte[] webp = {0x52, 0x49, 0x46, 0x46, 0, 0, 0, 0, 0x57, 0x45, 0x42, 0x50};

        assertTrue(service.store(new MockMultipartFile("file", "a.png", "image/png", png)).publicPath().endsWith(".png"));
        assertTrue(service.store(new MockMultipartFile("file", "a.webp", "image/webp", webp)).publicPath().endsWith(".webp"));
    }

    @Test
    void rejectsMismatchedContentAndOversizedFile() {
        AvatarStorageServiceImpl service = new AvatarStorageServiceImpl(tempDir.toString());
        BusinessException invalid = assertThrows(BusinessException.class, () -> service.store(
                new MockMultipartFile("file", "fake.jpg", "image/jpeg", "not-an-image".getBytes())));
        assertTrue(invalid.getMessage().contains("JPG、PNG 或 WebP"));

        byte[] oversized = new byte[5 * 1024 * 1024 + 1];
        oversized[0] = (byte) 0xff; oversized[1] = (byte) 0xd8; oversized[2] = (byte) 0xff;
        BusinessException tooLarge = assertThrows(BusinessException.class, () -> service.store(
                new MockMultipartFile("file", "large.jpg", "image/jpeg", oversized)));
        assertTrue(tooLarge.getMessage().contains("5MB"));
    }

    @Test
    void neverDeletesExternalOrTraversalPaths() throws Exception {
        AvatarStorageServiceImpl service = new AvatarStorageServiceImpl(tempDir.resolve("avatars").toString());
        Path outside = tempDir.resolve("outside.jpg");
        Files.writeString(outside, "keep");

        service.deleteManagedAvatar("https://example.com/outside.jpg");
        service.deleteManagedAvatar("/uploads/avatars/../outside.jpg");

        assertTrue(Files.exists(outside));
    }
}
