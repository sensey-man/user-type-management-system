package com.example.utm.util;

public class ErrMsgs {

    public static final String ERR_ADD_USER_ERROR = "Cannot add user to DB. Request params %s";
    public static final String ERR_USER_NOT_FOUND = "User not found. UserId %s";
    public static final String ERR_USER_NOT_DELETED = "User not deleted. UserId %s";
    public static final String ERR_USER_PASSWORD_NOT_DELETED = "User password not deleted. UserId %s";
    public static final String ERR_PASSWORD_NOT_SET = "Password not set. UserId %s, user type %s";
    public static final String ERR_INCORRECT_NEW_USER_TYPE = "Incorrect new user type. UserId %s, user type %s";
    public static final String ERR_SAME_USER_TYPE_FOR_CHANGE = "New user type equals current. UserId %s, user type %s";
    public static final String ERR_CONVET_TYPE_RULE_NOT_FOUND = "User type convert rule not found. UserId %s, current type %s, new type %s";
    public static final String ERR_CONVET_TYPE_ERROR = "Cannot change user type. UserId %s, current type %s, new type %s";
    public static final String ERR_DELETE_USER_ERROR = "Cannot delete user from DB. UserId %s";
    public static final String ERR_SET_PASSWORD = "Password not set for user. UserId %s, user type %s, passwords %s";
    public static final String ERR_SET_PASSWORD_ILLEGAL = "Illegal setting password for user. UserId %s, user type %s, passwords %s";
    public static final String ERR_SET_USER_PASSWORD = "Cannot setting password for user. UserId %s, user type %s, passwords %s";

    // SQL

    public static final String ERR_SQL_CANNOT_EXECUTE_SQL_REQUEST = "Cannot execute sql request to DB. Method %s";
    public static final String ERR_SQL_CANNOT_CREATE_TABLE = "Cannot create table  %s";


}

