package carsharing;

import carsharing.controller.Menu;
import carsharing.dao.DbClient;
import org.h2.jdbcx.JdbcDataSource;

public class Main {

    public static void main(String[] args) {

        String dbName = "test"; // Default database name

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-databaseFileName") && i + 1 < args.length) {
                dbName = args[i + 1];
                break;
            }
        }

        JdbcDataSource dataSource = new JdbcDataSource();
        //noinspection SpellCheckingInspection
        dataSource.setUrl("jdbc:h2:./src/carsharing/db/%s".formatted(dbName));

        DbClient client = new DbClient(dataSource);
        client.run(DbClient.CREATE_TABLE_COMPANY);
        client.run(DbClient.CREATE_TABLE_CAR);
        client.run(DbClient.CREATE_TABLE_CUSTOMER);

        Menu menu = new Menu(dataSource);
        menu.start();

    }
}