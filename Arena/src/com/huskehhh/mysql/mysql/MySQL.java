package com.huskehhh.mysql.mysql;

import com.huskehhh.mysql.*;
import java.sql.*;

public class MySQL extends Database
{
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String hostname;
    
    public MySQL(final String hostname, final String port, final String database, final String username, final String password) {
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
