package ind.glowingstone.toolkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static void main(String[] args) throws Exception {
        String username =Toolkit.genRandomUsername();
        String password = Toolkit.genRandomPassword();
        String email = Toolkit.genRandomEmail();
    }

}
