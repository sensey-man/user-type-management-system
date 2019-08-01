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

    @Qualifier("datasource")
    @Autowired
    private DataSource dataSource;


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
        Boolean changeUserType(@Bind("id") UUID id, @Bind("type") Integer type, @Bind("enable") Boolean enable);

        @SqlUpdate("DELETE FROM Users WHERE id = :id")
        @GetGeneratedKeys
        Boolean deleteUser(@Bind("id") UUID id);

    }

    private Jdbi getJdbi() {

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
    }

    public Boolean createTable() {
        var jdbi = getJdbi();

        return jdbi.withExtension(UserSQLs.class, UserSQLs::createTable);
    }

    public User getUserById(UUID id) {
        var jdbi = getJdbi();

        return jdbi.withExtension(UserSQLs.class, extension -> extension.getUserById(id));
    }


    public List<User> list() {
        var jdbi = getJdbi();

        return jdbi.withExtension(UserSQLs.class, UserSQLs::list);
    }

    public Boolean insert(User item) {
        var jdbi = getJdbi();

        return jdbi.withExtension(UserSQLs.class, extension -> extension.insert(item));
    }

    public void changeUserType(UUID userId, UserType type, Boolean enable) {
        var jdbi = getJdbi();

        jdbi.withExtension(UserSQLs.class, extension -> extension.changeUserType(userId, type.ordinal(), enable));
    }

    public void deleteUser(UUID userId){
        var jdbi = getJdbi();

        jdbi.withExtension(UserSQLs.class, extension -> extension.deleteUser(userId));
    }

}
