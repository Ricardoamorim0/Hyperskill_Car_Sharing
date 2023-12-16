package carsharing.dao;

interface DataAccessObject {

    boolean isIndexOnRange(int index);

    void setSelectedIndex(int selectedIndex);

    int getSelectedId();
    String getSelectedName();
    int getSize();

}
