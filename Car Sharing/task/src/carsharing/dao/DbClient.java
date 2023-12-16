package carsharing.dao;

import javax.sql.DataSource;
import java.sql.*;

@SuppressWarnings("CallToPrintStackTrace")
public record DbClient(DataSource dataSource) {

    public static final String CREATE_TABLE_COMPANY = """
            CREATE TABLE IF NOT EXISTS COMPANY (
                ID INT PRIMARY KEY AUTO_INCREMENT,
                NAME VARCHAR(60) NOT NULL,
                UNIQUE KEY COMPANY_NAME_UNIQUE (NAME)
            );
            """;
    public static final String CREATE_TABLE_CAR = """
            CREATE TABLE IF NOT EXISTS CAR (
                ID INT PRIMARY KEY AUTO_INCREMENT,
                NAME VARCHAR(60) NOT NULL,
                COMPANY_ID INT NOT NULL,
                UNIQUE KEY CAR_NAME_UNIQUE (NAME),
                FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID)
            );
            """;

    public static final String CREATE_TABLE_CUSTOMER = """
            CREATE TABLE IF NOT EXISTS CUSTOMER (
                ID INT PRIMARY KEY AUTO_INCREMENT,
                NAME VARCHAR(60) NOT NULL,
                RENTED_CAR_ID INT,
                UNIQUE KEY CUSTOMER_NAME_UNIQUE (NAME),
                FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID)
            );
            """;

    public static final String INSERT_COMPANY = """
            INSERT INTO COMPANY (NAME) VALUES (?);
            """;

    public static final String SELECT_ALL_COMPANY_NAMES = """
            SELECT ID, NAME FROM COMPANY ORDER BY ID;
            """;

    public static final String INSERT_CAR = """
            INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?);
            """;

    public static final String SELECT_ALL_CAR_NAMES = """
            SELECT ID, NAME, COMPANY_ID FROM CAR WHERE COMPANY_ID = %s ORDER BY ID;
            """;

    public static final String INSERT_CUSTOMER = """
            INSERT INTO CUSTOMER (NAME, RENTED_CAR_ID) VALUES (?, NULL);
            """;

    public static final String SELECT_ALL_CUSTOMERS_NAMES = """
            SELECT ID, NAME, RENTED_CAR_ID FROM CUSTOMER ORDER BY ID;
            """;

    public static final String UPDATE_CUSTOMER_RENTED_CAR_ID = """
            UPDATE CUSTOMER SET RENTED_CAR_ID = %s WHERE ID = %s LIMIT 1;""";

    public static final String SELECT_CAR_BY_ID = """
            SELECT CAR.ID, CAR.NAME, CAR.COMPANY_ID, COMPANY.NAME FROM CAR JOIN COMPANY ON CAR.COMPANY_ID = COMPANY.ID WHERE CAR.ID = %s LIMIT 1;
            """;
    public void run(String string) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            statement.executeUpdate(string);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
