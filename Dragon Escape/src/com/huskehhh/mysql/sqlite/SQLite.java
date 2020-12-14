package com.huskehhh.mysql.sqlite;

import com.huskehhh.mysql.*;
import org.bukkit.plugin.*;
import java.io.*;
import java.sql.*;

public class SQLite extends Database
{
    private String dbLocation;
    private Plugin plugin;
    
    public SQLite(String dbLocation, Plugin main) {
        this.dbLocation = dbLocation;
        this.plugin = main;
    }
    
    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (this.checkConnection()) {
            return this.connection;
        }
        File dataFolder = new File(this.plugin.getDataFolder().getAbsolutePath());
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File file = new File(dataFolder, this.dbLocation);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                System.out.println("Unable to create database!");
            }
        }
        System.out.println(file.toString());
        Class.forName("org.sqlite.JDBC");
        return this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder + "/" + this.dbLocation);
    }
}
