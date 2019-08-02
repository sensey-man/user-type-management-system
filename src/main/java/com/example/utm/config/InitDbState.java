package com.example.utm.config;

import com.example.utm.repository.PasswordRepository;
import com.example.utm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

import static com.example.utm.util.ErrMsgs.ERR_SQL_CANNOT_CREATE_TABLE;

@Component
public class InitDbState {

    private final Logger logger = LoggerFactory.getLogger(InitDbState.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    @PostConstruct
    public void init() throws SQLException {
        var ok = userRepository.createTable();
        var un = userRepository.checkCreatedTable();
        if (ok == null || un == -1) {
            var errMsg = String.format(ERR_SQL_CANNOT_CREATE_TABLE, "Users");
            logger.error(errMsg);
            throw new SQLException(errMsg);
        }

        ok = passwordRepository.createTable();
        var pn = passwordRepository.checkCreatedTable();
        if (ok == null || pn == -1) {
            var errMsg = String.format(ERR_SQL_CANNOT_CREATE_TABLE, "Passwords");
            logger.error(errMsg);
            throw new SQLException(errMsg);
        }

    }

}
