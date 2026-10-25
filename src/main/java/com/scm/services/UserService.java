package com.scm.services;

import java.util.Optional;
import java.util.List;

import com.scm.entities.User;
import com.scm.forms.UserForm;

public interface UserService {

    User saveUser(User user);
Optional<User> getUserById(String id);

Optional<User> updateUser(User user);

void deleteUser(String id);

boolean isUserExist(String userId);

boolean isUserExistByEmail(String email);

List<User> getAllUsers();
void saveUser(UserForm userForm);

User getUserByEmail(String email);

// add more methods here related user service [logic]

}
