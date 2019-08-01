package com.example.utm.repository;

import com.example.utm.dto.dao.Passwords;
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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class PasswordRepository {
    @Qualifier("datasource")
    @Autowired
    private DataSource dataSource;

    @RegisterMapper(PasswordsMapper.class)
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
        Integer update(@BindBean Passwords u);

        @SqlQuery("SELECT * FROM Passwords WHERE userId = :userId")
        @GetGeneratedKeys
        Passwords getUserPasswords(@Bind("userId") UUID userId);



    }

    public static class PasswordsMapper implements ResultSetMapper<Passwords> {
        @Override
        public Passwords map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
            Passwords bean = new Passwords();
            bean.setUserId(UUID.fromString(r.getString("userId")));
            bean.setPassword(r.getString("password"));
            bean.setPasswordA(r.getString("passwordA"));
            bean.setPasswordB(r.getString("passwordB"));
            return bean;
        }
    }

    private PasswordRepository.PasswordSQLs getPasswordSQLs() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        Handle handle = DBI.open(conn);
        return handle.attach(PasswordRepository.PasswordSQLs.class);
    }

    public Boolean createTable() {
        PasswordRepository.PasswordSQLs passwordSQLs = getPasswordSQLs();
        return passwordSQLs.createTable();
    }

    public List<Passwords> list() {
        PasswordRepository.PasswordSQLs passwordSQLs = getPasswordSQLs();
        return passwordSQLs.list();
    }

    public Boolean insert(Passwords item) {
        PasswordRepository.PasswordSQLs passwordSQLs = getPasswordSQLs();
        return passwordSQLs.insert(item);
    }

    public Integer update(Passwords item) {
        PasswordRepository.PasswordSQLs passwordSQLs = getPasswordSQLs();
        return passwordSQLs.update(item);
    }

    public Passwords getUserPasswords(UUID userId) {
        PasswordRepository.PasswordSQLs passwordSQLs = getPasswordSQLs();
        return passwordSQLs.getUserPasswords(userId);
    }


}
