package edu.puj.popinapp.models;

public class DatabaseRoutes {
    public final static String USERS_PATH = "users";

    public final static String getUser (String uuid){
        return String.format("%s/%s", USERS_PATH, uuid);
    }
}
