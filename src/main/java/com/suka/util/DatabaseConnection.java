package com.suka.util;

import java.sql.Connection;
import java.sql.DriverManager;

import static com.sun.javafx.css.FontFaceImpl.FontFaceSrcType.URL;

public class DatabaseConnection {


    public static final String URL = "jdbc:mysql://localhost:3306/javafx_app";
    public static final String USER = "root";
    public static final String PASSWORD = "Death";


    public static Connection connect(){
        try{
            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
