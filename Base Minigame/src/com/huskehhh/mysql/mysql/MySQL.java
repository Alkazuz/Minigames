package com.huskehhh.mysql.mysql;

import com.huskehhh.mysql.*;
import java.sql.*;

public class MySQL extends Database
{
    private String user;
    private String database;
    private String password;
    private String port;
    private String hostname;
    
    public MySQL(String hostname, String port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }
    
    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (this.checkConnection()) {
            return this.connection;
        }
        String connectionURL = "jdbc:mysql://" + this.hostname + ":" + this.port;
        if (this.database != null) {
            connectionURL = String.valueOf(String.valueOf(connectionURL)) + "/" + this.database;
        }
        Class.forName("com.mysql.jdbc.Driver");
        return this.connection = DriverManager.getConnection(connectionURL, this.user, this.password);
    }
}
