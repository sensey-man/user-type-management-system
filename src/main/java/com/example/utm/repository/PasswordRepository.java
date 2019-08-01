package com.example.utm.repository;

import com.example.utm.dto.dao.Passwords;
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

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Repository
public class PasswordRepository {
    @Qualifier("datasource")
    @Autowired
    private DataSource dataSource;


    interface PasswordSQLs {

        @SqlUpdate("Create table IF NOT EXISTS  Passwords (userId varchar PRIMARY KEY, password varchar(32) , passwordA varchar(32), passwordB varchar(32), FOREIGN KEY (userId) REFERENCES Users(id));")
        @GetGeneratedKeys
        Boolean createTable();

        @SqlQuery("select * from passwords")
        List<Passwords> list();

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
    }

    public Boolean createTable() {
        var jdbi = getJdbi();

        return jdbi.withExtension(PasswordSQLs.class, PasswordSQLs::createTable);
    }

    public List<Passwords> list() {
        var jdbi = getJdbi();

        return jdbi.withExtension(PasswordSQLs.class, PasswordSQLs::list);
    }

    public Boolean insert(Passwords item) {
        var jdbi = getJdbi();

        return jdbi.withExtension(PasswordSQLs.class, extension -> extension.insert(item));
    }

    public void update(Passwords item) {
        var jdbi = getJdbi();

        jdbi.withExtension(PasswordSQLs.class, extension -> extension.update(item));
    }

    public Passwords getUserPasswords(UUID userId) {
        var jdbi = getJdbi();

        return jdbi.withExtension(PasswordSQLs.class, extension -> extension.getUserPasswords(userId));
    }

    public void deleteUserPassword(UUID userId){
        var jdbi = getJdbi();

        jdbi.withExtension(PasswordSQLs.class, extension -> extension.deleteUserPasswords(userId));
    }
}
