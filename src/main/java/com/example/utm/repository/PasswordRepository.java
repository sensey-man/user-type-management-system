package com.example.utm.repository;

import com.example.utm.dto.dao.Passwords;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.UUID;

@Repository
public class PasswordRepository {

    private final Logger logger = LoggerFactory.getLogger(PasswordRepository.class);

    @Qualifier("datasource")
    @Autowired
    private DataSource dataSource;


    interface PasswordSQLs {

        @SqlUpdate("Create table IF NOT EXISTS  Passwords (userId varchar PRIMARY KEY, password varchar(32) , passwordA varchar(32), passwordB varchar(32), FOREIGN KEY (userId) REFERENCES Users(id));")
        @GetGeneratedKeys
        Boolean createTable();

        @SqlQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name = 'Passwords';")
        @GetGeneratedKeys
        Integer checkCreatedTable();

        @SqlUpdate("INSERT INTO Passwords(userId, password, passwordA, passwordB) VALUES (:userId, :password, :passwordA, :passwordB)")
        @GetGeneratedKeys
        Boolean insert(@BindBean Passwords u);

        @SqlUpdate("UPDATE Passwords SET password = :password, passwordA = :passwordA, passwordB = :passwordB WHERE userId = :userId")
        @GetGeneratedKeys
        Boolean update(@BindBean Passwords u);

        @SqlQuery("SELECT * FROM Passwords WHERE userId = :userId")
        @GetGeneratedKeys
        Passwords getUserPasswords(@Bind("userId") UUID userId);

        @SqlUpdate("DELETE FROM Passwords WHERE userId = :userId")
        Boolean deleteUserPasswords(@Bind("userId") UUID userId);

    }

    private Jdbi getJdbi() {
        try {

            Jdbi jdbi = Jdbi.create(dataSource);
            jdbi.installPlugin(new SqlObjectPlugin());
            jdbi.registerRowMapper(Passwords.class, (r, ctx) -> {
                Passwords bean = new Passwords();
                bean.setUserId(UUID.fromString(r.getString("userId")));
                bean.setPassword(r.getString("password"));
                bean.setPasswordA(r.getString("passwordA"));
                bean.setPasswordB(r.getString("passwordB"));
                return bean;

            });
            return jdbi;
        } catch (Exception e) {
            logger.error("Cannot create jdbi for passwordRepository");
            throw e;
        }
    }

    public Boolean createTable() {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(PasswordSQLs.class, PasswordSQLs::createTable);
        } catch (Exception e) {
            logger.error("Cannot execute createTable for userRepository");
            e.printStackTrace();
            return null;
        }
    }

    public Integer checkCreatedTable() {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(PasswordSQLs.class, PasswordSQLs::checkCreatedTable);
        } catch (Exception e) {
            logger.error("Cannot execute checkCreatedTable for userRepository");
            e.printStackTrace();
            return -1;
        }
    }

    public Boolean insert(Passwords item) {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(PasswordSQLs.class, extension -> extension.insert(item));
        } catch (Exception e) {
            logger.error("Cannot execute insert for userRepository");
            e.printStackTrace();
            return null;
        }
    }

    public void update(Passwords item) {
        try {
            var jdbi = getJdbi();
            jdbi.withExtension(PasswordSQLs.class, extension -> extension.update(item));
        } catch (Exception e) {
            logger.error("Cannot execute createTable for userRepository");
            e.printStackTrace();
        }
    }

    public Passwords getUserPasswords(UUID userId) {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(PasswordSQLs.class, extension -> extension.getUserPasswords(userId));
        } catch (Exception e) {
            logger.error("Cannot execute getUserPasswords for userRepository");
            e.printStackTrace();
            return null;
        }
    }

    public void deleteUserPassword(UUID userId) {
        try {
            var jdbi = getJdbi();
            jdbi.withExtension(PasswordSQLs.class, extension -> extension.deleteUserPasswords(userId));
        } catch (Exception e) {
            logger.error("Cannot execute deleteUserPassword for userRepository");
            e.printStackTrace();
        }
    }
}
