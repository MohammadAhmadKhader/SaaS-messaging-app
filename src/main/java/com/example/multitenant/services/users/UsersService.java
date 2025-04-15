package com.example.multitenant.services.users;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.users.UserViewDTO;
import com.example.multitenant.models.User;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.services.generic.GenericService;
import com.example.multitenant.utils.PageableHelper;
import com.example.multitenant.utils.ServicesHelper;

@Service
public class UsersService extends GenericService<User, Long> {
    private static String defaultSortBy = "createdAt";
    private static String defaultSortDir = "DESC";

    private UsersRepository usersRepository;
    private ServicesHelper<User> servicesHelper;

    public UsersService(UsersRepository usersRepository) {
        super(usersRepository);
        this.usersRepository = usersRepository;
    }

    public Page<User> findAllUsers(Integer page, Integer size, String sortBy, String sortDir) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir,sortBy, sortDir, page, size);
        var result = this.servicesHelper.findAllWithSpecifications(pageable, null, null);
        
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

    public UserViewDTO findByEmailAsDTO(String email) {
        return this.findByEmail(email).toViewDTO();
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
