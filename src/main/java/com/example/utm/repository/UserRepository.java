package com.example.utm.repository;

import com.example.utm.dto.dao.User;
import com.example.utm.dto.enums.UserType;
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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);


    @Qualifier("datasource")
    @Autowired
    private DataSource dataSource;


    interface UserSQLs {

        @SqlUpdate("Create table IF NOT EXISTS  Users (id varchar PRIMARY KEY, name varchar(32) NOT NULL, enable bool, type INTEGER NOT NULL);")
        @GetGeneratedKeys
        Boolean createTable();

        @SqlQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name = 'Users';")
        @GetGeneratedKeys
        Integer checkCreatedTable();

        @SqlQuery("select * from users where id = :id")
        User getUserById(@Bind("id") UUID id);


        @SqlQuery("select * from users")
        List<User> list();

        @SqlUpdate("INSERT INTO users (id, name, enable, type) VALUES (:id, :name, :enable, :type)")
        @GetGeneratedKeys
        @Enumerated(EnumType.ORDINAL)
        Boolean insert(@BindBean User u);

        @SqlUpdate("UPDATE users SET enable = :enable, type = :type WHERE id = :id")
        @GetGeneratedKeys
        Boolean changeUserType(@Bind("id") UUID id, @Bind("type") Integer type, @Bind("enable") Boolean enable);

        @SqlUpdate("DELETE FROM Users WHERE id = :id")
        @GetGeneratedKeys
        Boolean deleteUser(@Bind("id") UUID id);

    }

    private Jdbi getJdbi() {
        try {


            Jdbi jdbi = Jdbi.create(dataSource);
            jdbi.installPlugin(new SqlObjectPlugin());
            jdbi.registerRowMapper(User.class, (r, ctx) -> {
                User bean = new User();
                bean.setId(UUID.fromString(r.getString("id")));
                bean.setName(r.getString("name"));
                bean.setEnable(r.getBoolean("enable"));
                bean.setType(UserType.from(r.getInt("type")));
                return bean;
            });

            return jdbi;
        } catch (Exception e) {
            logger.error("Cannot create jdbi for userRepository");
            throw e;
        }
    }

    public Boolean createTable() {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(UserSQLs.class, UserSQLs::createTable);
        } catch (Exception e) {
            logger.error("Cannot execute createTable for userRepository");
            e.printStackTrace();
            return null;
        }
    }

    public Integer checkCreatedTable() {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(UserSQLs.class, UserSQLs::checkCreatedTable);
        } catch (Exception e) {
            logger.error("Cannot execute checkCreatedTable for userRepository");
            e.printStackTrace();
            return -1;
        }
    }

    public User getUserById(UUID id) {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(UserSQLs.class, extension -> extension.getUserById(id));
        } catch (Exception e) {
            logger.error("Cannot execute getUserById for userRepository");
            e.printStackTrace();
            return null;
        }
    }


    public List<User> list() {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(UserSQLs.class, UserSQLs::list);
        } catch (Exception e) {
            logger.error("Cannot execute list for userRepository");
            e.printStackTrace();
            return null;
        }
    }

    public Boolean insert(User item) {
        try {
            var jdbi = getJdbi();
            return jdbi.withExtension(UserSQLs.class, extension -> extension.insert(item));
        } catch (Exception e) {
            logger.error("Cannot execute insert for userRepository");
            e.printStackTrace();
            return null;
        }
    }

    public void changeUserType(UUID userId, UserType type, Boolean enable) {
        try {
            var jdbi = getJdbi();
            jdbi.withExtension(UserSQLs.class, extension -> extension.changeUserType(userId, type.ordinal(), enable));
        } catch (Exception e) {
            logger.error("Cannot execute changeUserType for userRepository");
            e.printStackTrace();
        }
    }

    public void deleteUser(UUID userId) {
        try {
            var jdbi = getJdbi();
            jdbi.withExtension(UserSQLs.class, extension -> extension.deleteUser(userId));
        } catch (Exception e) {
            logger.error("Cannot execute deleteUser for userRepository");
            e.printStackTrace();
        }
    }

}
