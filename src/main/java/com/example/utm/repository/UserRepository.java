package com.example.utm.repository;

import com.example.utm.dto.dao.User;
import com.example.utm.dto.enums.UserType;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    @Qualifier("datasource")
    @Autowired
    private DataSource dataSource;


    @RegisterMapper(UserMapper.class)
    interface UserSQLs {

        @SqlUpdate("Create table IF NOT EXISTS  Users (id varchar PRIMARY KEY, name varchar(32) NOT NULL, enable bool, type INTEGER NOT NULL);")
        @GetGeneratedKeys
        Boolean createTable();

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
        Integer changeUserType(@Bind("id") UUID id, @Bind("type") Integer type, @Bind("enable") Boolean enable);

    }

    public static class UserMapper implements ResultSetMapper<User> {

        @Override
        public User map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
            User bean = new User();
            bean.setId(UUID.fromString(r.getString("id")));
            bean.setName(r.getString("name"));
            bean.setEnable(r.getBoolean("enable"));
            bean.setType(UserType.from(r.getInt("type")));
            return bean;
        }
    }

    private UserSQLs getUserSQLs() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        Handle handle = DBI.open(conn);
        return handle.attach(UserSQLs.class);
    }

    public Boolean createTable() {
        var userSQLs = getUserSQLs();
        return userSQLs.createTable();
    }

    public User getUserById(UUID id) {
        var userQLs = getUserSQLs();
        return userQLs.getUserById(id);
    }


    public List<User> list() {
        var userQLs = getUserSQLs();
        return userQLs.list();
    }

    public Boolean insert(User item) {
        var userSQLs = getUserSQLs();
        return userSQLs.insert(item);
    }

    public Integer changeUserType(UUID userId, UserType type, Boolean enable) {
        var userSQLs = getUserSQLs();
        return userSQLs.changeUserType(userId, type.ordinal(), enable);
    }
}
