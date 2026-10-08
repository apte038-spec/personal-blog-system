package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.TagRequest;
import com.example.blog.entity.Tag;
import com.example.blog.service.TagAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class TagAdminController {
    private final TagAdminService tagAdminService;

    @GetMapping public Result<List<Tag>> list() { return Result.success(tagAdminService.list()); }
    @PostMapping public Result<Tag> create(@Valid @RequestBody TagRequest request) { return Result.success(tagAdminService.create(request)); }
    @PutMapping("/{id}") public Result<Tag> update(@PathVariable Long id, @Valid @RequestBody TagRequest request) { return Result.success(tagAdminService.update(id, request)); }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) { tagAdminService.delete(id); return Result.success(); }
}
