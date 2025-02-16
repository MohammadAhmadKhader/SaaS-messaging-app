package com.example.demo.services.users;

import org.springframework.stereotype.Service;

import com.example.demo.dtos.shared.FindAllResult;
import com.example.demo.dtos.users.UserViewDTO;
import com.example.demo.models.User;
import com.example.demo.repository.users.UsersRepository;
import com.example.demo.services.generic.GenericService;
import com.example.demo.utils.PageableHelper;

@Service
public class UsersService extends GenericService<User, Long, UserViewDTO> {
    private static String defaultSortBy = "createdAt";
    private static String defaultSortDir = "DESC";

    private UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        super(usersRepository);
        this.usersRepository = usersRepository;
    }

    public FindAllResult<UserViewDTO> findAllUsers(Integer page, Integer size, String sortBy, String sortDir) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir,sortBy, sortDir, page, size);

        var result =  this.usersRepository.findAllWithSpecifications(null,pageable);
        var count = result.getTotalElements();
        
        var usersView = result.getContent().stream().map((u) -> {
            return u.toViewDTO();
        }).toList();

        return new FindAllResult<>(usersView, count, page, size);
    }

    public FindAllResult<UserViewDTO> findAllWithContents(Integer page, Integer size, String sortBy, String sortDir) {
        var pageable = PageableHelper.HandleSortWithPagination(defaultSortBy, defaultSortDir,sortBy, sortDir, page, size);

        var result = this.usersRepository.findAll(pageable);
        var count = result.getTotalElements();
        
        var usersView = result.getContent().stream().map((u) -> {
            return u.toViewDTO();
        }).toList();

        return new FindAllResult<>(usersView, count, page, size);
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

    public void deleteUser(Long id) {
        var user = this.usersRepository.findById(id);
        if(!user.isPresent()) {
            throw new RuntimeException(String.format("Error during deletion: User with id:'%s' was not found", id));
        }
        
        this.usersRepository.delete(user.get());
    }
}
