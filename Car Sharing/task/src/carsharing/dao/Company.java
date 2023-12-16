package carsharing.dao;

import carsharing.data.CompanyEntry;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class Company implements DataAccessObject {

    private final DataSource dataSource;

    private List<CompanyEntry> companies = new ArrayList<>();
    private int selectedIndex = 0;

    public Company(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isIndexOnRange(int index) {
        return index >= 0 && index < companies.size();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void add(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DbClient.INSERT_COMPANY)
        ) {
            connection.setAutoCommit(true);
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAll() {
        companies = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(true);
            try (ResultSet resultSet = statement.executeQuery(DbClient.SELECT_ALL_COMPANY_NAMES)){
                if (resultSet != null) {
                    while (resultSet.next()) {
                        companies.add(new CompanyEntry(resultSet.getInt("ID"), resultSet.getString("NAME")));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder tmp = new StringBuilder();

        if (companies.isEmpty()) {
            tmp.append("\nThe company list is empty");
        } else {
            for (int i = 0; i < companies.size(); i++) {
                tmp.append("\n").append(i + 1).append(". ").append(companies.get(i).name());
            }
            tmp.append("\n0. Back");
        }

        return tmp.toString();
    }

    public int getSelectedId() {
        return (!companies.isEmpty() && isIndexOnRange(selectedIndex))? companies.get(selectedIndex).id() : 0;
    }

    public String getSelectedName() {
        return (!companies.isEmpty() && isIndexOnRange(selectedIndex))? companies.get(selectedIndex).name() : "";
    }

    public int getSize() {
        return companies.size();
    }

}
