package com.example.multitenant.services.users;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.dtos.shared.CursorPage;
import com.example.multitenant.dtos.users.UsersFilter;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.GlobalRole;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.DefaultGlobalRole;
import com.example.multitenant.models.enums.FilesPath;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.services.files.FilesService;
import com.example.multitenant.specificationsbuilders.UsersSpecBuilder;
import com.example.multitenant.utils.PageableHelper;
import com.example.multitenant.utils.VirtualThreadsUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsersService {
    private static String defaultSortBy = "createdAt";
    private static String defaultSortDir = "DESC";

    private final UsersRepository usersRepository;
    private final UsersSpecService usersSpecificationsService;
    private final UsersCrudService usersCrudService;
    private final FilesService filesService;

    public Page<User> findAllUsers(Integer page, Integer size, String sortBy, String sortDir) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir, sortBy, sortDir, page, size);
        var result = this.usersSpecificationsService.findAllWithSpecifications(pageable, null, null);
        
        return result;
    }

    // curr user always assumed to be there because its authenticated
    // TODO: refactor logic inside with a better performance by reducing un-necessary queries
    @Transactional
    public List<User> removeFriend(Long currUserId, Long userIdToUnFriend) {
        var tasksResult = VirtualThreadsUtils.run(
            () -> this.usersCrudService.findById(userIdToUnFriend),
            () -> this.isUserFriend(currUserId, userIdToUnFriend),
            () -> this.usersRepository.findAllByIdsWithFriends(List.of(currUserId, userIdToUnFriend))
        );
    
        var userToRemove = tasksResult.getLeft();
        if(userToRemove == null) {
            throw new ResourceNotFoundException("user", userIdToUnFriend);
        }

        var areUsersFriends = tasksResult.getMiddle();
        if(!areUsersFriends) {
            throw new InvalidOperationException("user must be a friend to be removed");
        }

        var users = tasksResult.getRight();
        if (users.size() != 2) {
            throw new UnknownException("An error occurred while fetching the users.");
        }

        var currUser = users.get(0).getId() == currUserId.longValue() ? users.get(0) : users.get(1);
        var userToUnFriend = currUser == users.get(0) ? users.get(1) : users.get(0);
        if(currUser == null) {
            throw new UnknownException("error has occured during attempt to fetch the user who has attempted to remove a friend the user has been fetched as null");
        }

        if(userToUnFriend == null) {
            throw new UnknownException("error has occured during attempt to fetch the user who was being removed the user has been fetched as null");
        }

        currUser.getFriends().remove(userToUnFriend);
        userToUnFriend.getFriends().remove(currUser);

        return this.usersRepository.saveAll(List.of(currUser, userToUnFriend));
    }

    public CursorPage<User, Long> search(UsersFilter filter, Long cursor, int size) {
        var spec = UsersSpecBuilder.build(filter);
        var result = this.usersSpecificationsService.findAllWithCursor(spec, cursor, size, "id");

        return result;
    }

    public User findByEmail(String email) {
        return this.usersRepository.findByEmail(email);
    }

    public User findOneByEmailWithRolesAndPermissions(String email) {
        return this.usersRepository.findOneByEmailWithRolesAndPermissions(email);
    }

    public boolean isUserFriend(Long userId, Long friendId) {
        return this.usersRepository.isFriend(userId, friendId);
    }

    public User findUserWithRolesAndPermissions(Long id) {
        return this.usersRepository.findOneByIdWithRolesAndPermissions(id);
    }

    public User findUserWithRoles(Long id) {
        return this.usersRepository.findOneByIdWithRoles(id);
    }

    public User save(User user) {
        if(user == null) {
            throw new IllegalStateException("an error has occured during attempt to save a null user");
        }

        return this.usersRepository.save(user);
    }

    public Boolean existsByEmail(String email) {
        return this.usersRepository.existsByEmail(email);
    }

    public Boolean existsById(Long userId) {
        return this.usersRepository.existsById(userId);
    }

    public User findThenUpdate(long id, User user, MultipartFile avatar) {
        var updatedUser = this.usersCrudService.findThenUpdate(id, (existingUser) -> patcher(existingUser, user));
        if(updatedUser == null) {
            throw new ResourceNotFoundException("user");
        }

        if(avatar != null && updatedUser.getAvatarUrl() == null) {
            var fileResponse = this.filesService.uploadFile(avatar, FilesPath.USERS_AVATARS);
            updatedUser.setAvatarUrl(fileResponse.getUrl());
            return this.usersRepository.save(updatedUser);

        } else if(avatar != null && updatedUser.getAvatarUrl() != null) {
            var fileResponse = this.filesService.updateFile(avatar, FilesPath.USERS_AVATARS, updatedUser.getAvatarUrl());
            updatedUser.setAvatarUrl(fileResponse.getUrl());
            return this.usersRepository.save(updatedUser);
        }

        return updatedUser;
    }

    public User findById(long id) {
        return this.usersCrudService.findById(id);
    }

    public User create(User user) {
        return this.usersCrudService.create(user);
    }

    @Transactional
    public User softDeleteAndAnonymizeUserById(Long id) {
        var user = this.usersRepository.findOneByIdWithRoles(id);
        if (user == null) {
            throw new ResourceNotFoundException("user", id);
        }

        if(user.isDeleted()) {
            throw new InvalidOperationException("user is already deleted");
        }

        var superAdmin = DefaultGlobalRole.SUPERADMIN.getRoleName();
        var admin = DefaultGlobalRole.ADMIN.getRoleName();
        var isAdmin = user.getRoles().stream().anyMatch((role) -> {
            return role.getName().equals(superAdmin) || role.getName().equals(admin);
        });

        if(isAdmin) {
           throw new InvalidOperationException("can not delete a user with admin privileges");
        }

        user.setDeleted(true);

        // clearing sensitive data
        user.setEmail(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setPassword(null);
        var avatarUrl = user.getAvatarUrl();
        user.setAvatarUrl(null);

        // clearing relations
        user.setFriends(new HashSet<User>());
        user.setFriendOf(new HashSet<User>());
        user.setRoles(new HashSet<GlobalRole>());

        this.usersRepository.save(user);
        if(avatarUrl != null) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        filesService.deleteFile(FilesPath.USERS_AVATARS, avatarUrl);
                    }
                }
            );  
        }

        return user;
    }

    private void patcher(User target, User source) {
        var newEmail = source.getEmail();
        var newFirstName = source.getFirstName();
        var newLastName = source.getLastName();

        if(newEmail != null) {
            target.setEmail(newEmail);
        }
        if(newFirstName != null) {
            target.setFirstName(newFirstName);
        }

        if(newLastName != null) {
            target.setLastName(newLastName);
        }
    }
}