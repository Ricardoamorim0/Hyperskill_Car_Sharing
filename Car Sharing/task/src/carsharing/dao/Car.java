package carsharing.dao;

import carsharing.data.CarEntry;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("CallToPrintStackTrace")
public class Car implements DataAccessObject {

    private final DataSource dataSource;

    private List<CarEntry> cars = new ArrayList<>();
    private int selectedIndex = 0;

    public Car(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isIndexOnRange(int index) {
        return index >= 0 && index < cars.size();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void add(String name, int companyId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DbClient.INSERT_CAR)
        ) {
            connection.setAutoCommit(true);
            statement.setString(1, name);
            statement.setInt(2, companyId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAll(int companyId) {

        cars = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            try (ResultSet resultSet = statement.executeQuery(DbClient.SELECT_ALL_CAR_NAMES.formatted(companyId))){
                if (resultSet != null) {
                    while (resultSet.next()) {
                        cars.add(new CarEntry(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getInt("COMPANY_ID"), ""));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder tmp = new StringBuilder();

        if (cars.isEmpty()) {
            tmp.append("\nThe car list is empty!");
        } else {
            for (int i = 0; i < cars.size(); i++) {
                tmp.append("\n").append(i + 1).append(". ").append(cars.get(i).name());
            }
        }

        return tmp.toString();
    }

    public int getSelectedId() {
        return (!cars.isEmpty() && isIndexOnRange(selectedIndex))? cars.get(selectedIndex).id() : 0;
    }

    public String getSelectedName() {
        return (!cars.isEmpty() && isIndexOnRange(selectedIndex))? cars.get(selectedIndex).name() : "";
    }

    public int getSize() {
        return cars.size();
    }

    public List<CarEntry> getCarByCompanyId(int companyId) {
        getAll(companyId);
        return cars.stream().filter(x -> x.companyId() == companyId).collect(Collectors.toList());
    }

    public CarEntry getCarById(int id) {
        CarEntry carEntry = null;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            try (ResultSet resultSet = statement.executeQuery(DbClient.SELECT_CAR_BY_ID.formatted(id))){
                if (resultSet != null) {
                    while (resultSet.next()) {
                        carEntry = new CarEntry(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getInt("COMPANY_ID"), resultSet.getString("COMPANY.NAME"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carEntry;
    }

}
