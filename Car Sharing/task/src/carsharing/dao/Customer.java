package carsharing.dao;

import carsharing.data.CustomerEntry;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class Customer implements DataAccessObject {

    private final DataSource dataSource;

    private List<CustomerEntry> customers = new ArrayList<>();
    private int selectedIndex = 0;

    public Customer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isIndexOnRange(int index) {
        return index >= 0 && index < customers.size();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void add(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DbClient.INSERT_CUSTOMER)
        ) {
            connection.setAutoCommit(true);
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRented(int carId) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            statement.executeUpdate(DbClient.UPDATE_CUSTOMER_RENTED_CAR_ID.formatted(carId, getSelectedId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getAll();
    }

    public void returnRented() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            statement.executeUpdate(DbClient.UPDATE_CUSTOMER_RENTED_CAR_ID.formatted("NULL", getSelectedId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getAll();
    }

    public String getAll() {
        customers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            try (ResultSet resultSet = statement.executeQuery(DbClient.SELECT_ALL_CUSTOMERS_NAMES)){
                if (resultSet != null) {
                    while (resultSet.next()) {
                        int rentedId = resultSet.getInt("RENTED_CAR_ID");
                        if (resultSet.wasNull()) {
                            rentedId = -1;
                        }

                        customers.add(new CustomerEntry(resultSet.getInt("ID"), resultSet.getString("NAME"), rentedId));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder tmp = new StringBuilder();

        if (customers.isEmpty()) {
            tmp.append("\nThe customer list is empty!");
        } else {
            for (int i = 0; i < customers.size(); i++) {
                tmp.append("\n").append(i + 1).append(". ").append(customers.get(i).name());
            }
            tmp.append("\n0. Back");
        }

        return tmp.toString();
    }

    public int getSelectedId() {
        return (!customers.isEmpty() && isIndexOnRange(selectedIndex))? customers.get(selectedIndex).id() : 0;
    }

    public String getSelectedName() {
        return (!customers.isEmpty() && isIndexOnRange(selectedIndex))? customers.get(selectedIndex).name() : "";
    }

    public int getSize() {
        return customers.size();
    }

    public int getSelectedCustomerRentedCar() {
        return (!customers.isEmpty() && isIndexOnRange(selectedIndex))? customers.get(selectedIndex).rentedCarId() : -1;
    }

    public List<Integer> getCustomersRentedCars() {
        List<Integer> tmp = new ArrayList<>();
        customers.forEach(x -> tmp.add(x.rentedCarId()));
        return tmp;
    }

}
