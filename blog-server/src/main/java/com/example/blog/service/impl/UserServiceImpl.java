package com.example.blog.service.impl;

import com.example.blog.common.BusinessException;
import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.dto.UpdateProfileRequest;
import com.example.blog.dto.ChangePasswordRequest;
import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import com.example.blog.security.JwtService;
import com.example.blog.service.UserService;
import com.example.blog.service.AvatarStorageService;
import com.example.blog.vo.AvatarVO;
import com.example.blog.vo.LoginVO;
import com.example.blog.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String ACTIVE = "ACTIVE";
    private static final String USER_ROLE = "USER";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AvatarStorageService avatarStorageService;

    @Override
    @Transactional
    public UserVO register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        String username = request.username().trim();
        if (userMapper.selectByUsernameIncludingDeleted(username) != null) {
            throw new BusinessException("用户名已被使用");
        }
        if (userMapper.selectByEmailIncludingDeleted(request.email()) != null) {
            throw new BusinessException("邮箱已被使用");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(username);
        user.setEmail(request.email());
        user.setRole(USER_ROLE);
        user.setStatus(ACTIVE);
        user.setTokenVersion(0);
        user.setDeleted(0);
        userMapper.insert(user);
        return UserVO.from(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectByUsernameIncludingDeleted(request.username());
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1
                || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if ("DISABLED".equals(user.getStatus())) {
            throw new BusinessException("该账号已被禁用，暂时无法登录");
        }
        if (!ACTIVE.equals(user.getStatus())) throw new BusinessException("用户名或密码错误");
        String token = jwtService.create(user);
        return new LoginVO(token, "Bearer", jwtService.getExpirationSeconds(), UserVO.from(user));
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectActiveById(userId);
        if (user == null) {
            throw new BusinessException("当前用户不存在或已被禁用");
        }
        return UserVO.from(user);
    }

    @Override
    @Transactional
    public UserVO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectActiveById(userId);
        if (user == null) {
            throw new BusinessException("当前用户不存在或已被禁用");
        }
        String username = request.username().trim();
        User sameName = userMapper.selectByUsernameIncludingDeleted(username);
        if (sameName != null && !sameName.getId().equals(userId)) {
            throw new BusinessException("用户名已被使用");
        }
        user.setUsername(username);
        user.setNickname(username);
        if (userMapper.updateById(user) != 1) throw new BusinessException("个人资料更新失败，请稍后重试");
        return UserVO.from(user);
    }

    @Override
    @Transactional
    public AvatarVO updateAvatar(Long userId, MultipartFile file) {
        User user = userMapper.selectActiveById(userId);
        if (user == null) throw new BusinessException("当前用户不存在或已被禁用");

        String oldAvatar = user.getAvatar();
        AvatarStorageService.StoredAvatar stored = avatarStorageService.store(file);
        String newAvatar = stored.publicPath();
        registerAvatarCleanup(oldAvatar, newAvatar);

        user.setAvatar(newAvatar);
        if (userMapper.updateById(user) != 1) {
            throw new BusinessException("头像更新失败，请稍后重试");
        }
        return new AvatarVO(newAvatar);
    }

    private void registerAvatarCleanup(String oldAvatar, String newAvatar) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                avatarStorageService.deleteManagedAvatar(oldAvatar);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    avatarStorageService.deleteManagedAvatar(newAvatar);
                }
            }
        });
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectActiveById(userId);
        if (user == null) throw new BusinessException("当前用户不存在或已被禁用");
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("当前密码不正确");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException("新密码不能与当前密码相同");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setTokenVersion((user.getTokenVersion() == null ? 0 : user.getTokenVersion()) + 1);
        userMapper.updateById(user);
    }
}
