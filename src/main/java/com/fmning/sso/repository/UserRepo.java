package com.fmning.sso.repository;

import com.fmning.sso.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends CrudRepository<User, Integer> {

    User findByUsername(String username);
    List<User> findAllByOrderByIdAsc();

}
