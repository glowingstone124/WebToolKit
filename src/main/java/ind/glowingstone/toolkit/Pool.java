package ind.glowingstone.toolkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class Pool {

    private final Queue<Connection> connectionPool;
    private final String jdbcUrl;
    private final String jdbcDriver;
    private final int maxPoolSize;
    private final int initialPoolSize;

    public Pool(String jdbcUrl, String jdbcDriver, int maxPoolSize, int initialPoolSize) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcDriver = jdbcDriver;
        this.maxPoolSize = maxPoolSize;
        this.initialPoolSize = initialPoolSize;
        this.connectionPool = new LinkedList<>();
        initializePool();
    }

    private void initializePool() {
        try {
            Class.forName(jdbcDriver);
            for (int i = 0; i < initialPoolSize; i++) {
                connectionPool.add(createNewConnection());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (connectionPool.size() < maxPoolSize) {
                return createNewConnection();
            } else {
                throw new SQLException("Connection pool is exhausted.");
            }
        } else {
            return connectionPool.poll();
        }
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            connectionPool.offer(connection);
        }
    }

    public synchronized void closeAllConnections() {
        while (!connectionPool.isEmpty()) {
            try {
                connectionPool.poll().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
