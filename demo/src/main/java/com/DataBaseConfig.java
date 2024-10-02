package com;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class DataBaseConfig {
    private static final Properties properties = new Properties();

    static{
        try (InputStream input = DataBaseConfig.class.getClassLoader().getResourceAsStream("config.properties")){
            if(input == null){
                throw new RuntimeException("No se puedo encontrar config.properties");
            }
            properties.load(input);
        }catch(IOException ex){
            throw new RuntimeException("Error al leer archivo de configuracion", ex);
        }
    }
    public static String getUrl(){
        return properties.getProperty("db.url");
    }
    public static String getUser(){
        return properties.getProperty("db.user");
    }
    public static String getPassword(){
        return properties.getProperty("db.password");
    }





    
}
