package com.example.utm.service.db;

import com.example.utm.dto.dao.Passwords;
import com.example.utm.dto.dao.User;
import com.example.utm.dto.enums.UserType;
import com.example.utm.dto.rest.response.ErrorMessageDto;
import com.example.utm.dto.rest.response.ResultDto;
import com.example.utm.repository.PasswordRepository;
import com.example.utm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.utm.util.ErrMsgs.*;

@Component
public class DbService {

    private final Logger logger = LoggerFactory.getLogger(DbService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;


    /**
     * Method get all users from DB
     * @return lits of user OR error message if it was not successful
     */
    @Transactional
    public ResultDto<List<User>> list() {
        var l = userRepository.list();
        if (l == null) {
            return new ResultDto<>(null, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SQL_CANNOT_EXECUTE_SQL_REQUEST, "list"))));
        }
        return new ResultDto<>(l, null);
    }

    /**
     *
     * @param user - dto to insert into DB
     * @param p - dto of user password to insert into DB
     * @return true - if all success, else - error message
     */
    @Transactional
    public ResultDto<Boolean> addUser(User user, Passwords p) {
        var okUser = userRepository.insert(user);
        if (okUser == null) {
            return new ResultDto<>(null, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SQL_CANNOT_EXECUTE_SQL_REQUEST, "addUserWithPassword : insert user"))));
        }

        var okPass = passwordRepository.insert(p);

        if (okPass == null) {
            return new ResultDto<>(null, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SQL_CANNOT_EXECUTE_SQL_REQUEST, "addUserWithPassword : insert password"))));
        }

        return new ResultDto<>(okUser && okPass, null);
    }

    /**
     *
     * @param id - user id which use for sql request
     * @return - User dto if user was found, else - null
     */
    @Transactional
    public ResultDto<User> getUserById(UUID id) {
        var u = userRepository.getUserById(id);
        if (u == null) {
            return new ResultDto<>(null, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SQL_CANNOT_EXECUTE_SQL_REQUEST, "getUserById"))));
        }
        return new ResultDto<>(u, null);
    }

    /**
     *
     * @param id - User id to find it password in DB
     * @return Password dto, if it was found. Else - null
     */
    @Transactional
    public ResultDto<Passwords> getUserPasswords(UUID id) {
        var p = passwordRepository.getUserPasswords(id);

        if (p == null) {
            return new ResultDto<>(null, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SQL_CANNOT_EXECUTE_SQL_REQUEST, "getUserPasswords"))));
        }
        return new ResultDto<>(p, null);
    }

    /**
     *
     * @param userId - User id in DB
     * @param type - new user type
     * @param enable - new state of account
     * @param passwords - new password (different user type require different passwords)
     * @return - true, if changing was successful. False - if something went wrong
     */
    @Transactional
    public Boolean changeUserType(UUID userId, UserType type, Boolean enable, Passwords passwords) {
        userRepository.changeUserType(userId, type, enable);
        passwordRepository.update(passwords);

        var dbUser = getUserById(userId);

        if (!dbUser.getSuccess()) {
            logger.error(String.format(ERR_USER_NOT_FOUND, userId));
            return false;
        }

        var changed = dbUser.getResult().getTypeName() == type;
        if (!changed) {
            logger.error(String.format(ERR_CONVET_TYPE_ERROR, userId, dbUser.getResult().getTypeName(), type));
            return false;
        }

        var dbPass = passwordRepository.getUserPasswords(userId);

        if (dbPass == null) {
            logger.error(String.format(ERR_PASSWORD_NOT_SET, userId, dbUser.getResult().getTypeName()));
            return false;
        }

        if (!dbPass.equals(passwords)) {
            logger.error(String.format(ERR_PASSWORD_NOT_SET, userId, dbUser.getResult().getTypeName()));
            return false;
        }

        return true;
    }

    /**
     *
     * @param userId - user id to find and delete record in DB (users and passwords tables)
     * @return True, if success, False - if something went wrong
     */
    @Transactional
    public Boolean deleteUser(UUID userId) {
        passwordRepository.deleteUserPassword(userId);
        userRepository.deleteUser(userId);

        var dbUser = getUserById(userId);
        if (dbUser.getSuccess()) {
            logger.error(String.format(ERR_USER_NOT_DELETED, userId));
            return false;
        }

        var dbPass = passwordRepository.getUserPasswords(userId);
        if (dbPass != null) {
            logger.error(String.format(ERR_USER_PASSWORD_NOT_DELETED, userId));
            return false;
        }

        return true;
    }

    /**
     *
     * @param u - just for logging if something will go wrong
     * @param pass - Password dto to setting it for user
     * @return True, if passwords were changed, else - false
     */
    @Transactional
    public Boolean setUserPassword(User u, Passwords pass) {
        passwordRepository.update(pass);

        var dbPass = passwordRepository.getUserPasswords(u.getId());

        if (dbPass == null) {
            logger.error(String.format(ERR_PASSWORD_NOT_SET, u.getId(), u.getTypeName()));
            return false;
        }

        if (!dbPass.equals(pass)) {
            logger.error(String.format(ERR_PASSWORD_NOT_SET, u.getId(), u.getTypeName()));
            return false;
        }

        return true;
    }
}
