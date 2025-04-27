package com.example.multitenant.services.users;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.User;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.services.generic.GenericService;
import com.example.multitenant.utils.PageableHelper;

@Service
public class UsersService extends GenericService<User, Long> {
    private static String defaultSortBy = "createdAt";
    private static String defaultSortDir = "DESC";

    private UsersRepository usersRepository;
    private UsersServicesHelper usersServicesHelper;

    public UsersService(UsersRepository usersRepository, UsersServicesHelper usersServicesHelper) {
        super(usersRepository);
        this.usersRepository = usersRepository;
        this.usersServicesHelper = usersServicesHelper;
    }

    public Page<User> findAllUsers(Integer page, Integer size, String sortBy, String sortDir) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir,sortBy, sortDir, page, size);
        var result = this.usersServicesHelper.findAllWithSpecifications(pageable, null, null);
        
        return result;
    }

    public Page<User> findAllWithContents(Integer page, Integer size, String sortBy, String sortDir) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir,sortBy, sortDir, page, size);
        var result = this.usersRepository.findAll(pageable);
    
        return result;
    }

    public User findByEmail(String email) {
        var optional = this.usersRepository.findByEmail(email);
        if(!optional.isPresent()) {
            return null;
        }

        return optional.get();
    }

    public boolean isUserFriend(Long userId, Long friendId) {
        return this.usersRepository.isFriend(userId, friendId);
    }

    // curr user always assumed to be there because its authenticated
    public List<User> removeFriend(Long currUserId, Long userIdToUnFriend) {
        var userToRemove = this.findById(userIdToUnFriend);
        if(userToRemove == null) {
            throw new ResourceNotFoundException("user", userIdToUnFriend);
        }

        var areUsersFriends = this.isUserFriend(currUserId, userIdToUnFriend);
        if(!areUsersFriends) {
            throw new InvalidOperationException("user must be a friend to be removed");
        }

        var users = this.usersRepository.findAllByIdsWithFriends(List.of(currUserId, userIdToUnFriend));
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

    public User save(User user) {
        if(user == null) {
            throw new IllegalStateException("an error has occured during attempt to save a null user");
        }

        return this.usersRepository.save(user);
    }

    public Boolean existsByEmail(String email) {
        return this.usersRepository.existsByEmail(email);
    }

    public User findThenUpdate(long id, User user) {
        return this.findThenUpdate(id, (existingUser) -> patcher(existingUser, user));
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
