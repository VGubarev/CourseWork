package com.company.Airport;

import com.company.OracleConnection;
import com.company.Predicate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class Owners {
    OracleConnection m_connection;

    public static class Owner {

        private Integer m_owner_id;
        private String m_shortname;
        private String m_fullname;
        
        public Integer GetOwner_id() {
            return m_owner_id;
        }

        public String GetShortname() {
            return m_shortname;
        }

        public String GetFullname() {
            return m_fullname;
        }
        
        public Owner(Integer owner_id, String shortname, String fullname) {
            if (shortname.isEmpty() || fullname.isEmpty()) {
                throw new IllegalArgumentException();
            }
            this.m_owner_id = owner_id;
            this.m_shortname = shortname;
            this.m_fullname = fullname;
        }

        public Owner(String shortname, String fullname) {
            if (shortname.isEmpty() || fullname.isEmpty()) {
                throw new IllegalArgumentException();
            }
            this.m_shortname = shortname;
            this.m_fullname = fullname;
        }
    }

    public Owners(OracleConnection connection) {
        m_connection = connection;
    }

    public Integer InsertOwners(Owner owner) throws SQLException {
        PreparedStatement statement = m_connection.GetConnection().prepareStatement(
                "INSERT INTO owners(shortname, fullname) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, owner.GetShortname());
        statement.setString(2, owner.GetFullname());

        int rows = statement.executeUpdate();

        if(rows == 0) {
            throw new SQLException("Creating new owner failed, no rows affected");
        }

        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt("owner_id");
            } else {
                throw new SQLException("Creating new owner failed, no owner_id obtained");
            }
        }
    }
    
    public Integer UpdateOwners(Owner owner,
                                  Predicate<Integer> owner_id,
                                  Predicate<String> Shortname,
                                  Predicate<String> Fullname) throws SQLException {
        String query = "UPDATE owners SET owner_id = ?, shortname = ?, fullname = ? ";
        String subquery1 = (owner_id != null)?owner_id.SelectWhereStatement("owner_id", true) + " ":"";
        String subquery2 = (Shortname != null)?Shortname.SelectWhereStatement("shortname", false) + " ":"";
        String subquery3 = (Fullname != null)?Fullname.SelectWhereStatement("fullname", false):"";

        if (!subquery1.isEmpty() ||
                !subquery2.isEmpty() ||
                !subquery3.isEmpty()) {
            query += "WHERE ";
        }

        query += subquery1 + subquery2 + subquery3;

        PreparedStatement statement = m_connection.GetConnection().prepareStatement(query);
        statement.setInt(1, owner.GetOwner_id());
        statement.setString(2, owner.GetShortname());
        statement.setString(3, owner.GetFullname());

        Integer result = statement.executeUpdate();

        return result;
    }

    public ArrayList<Owner> GetOwners(Predicate<Integer> owner_id,
                                          Predicate<String> shortname,
                                          Predicate<String> fullname) throws SQLException {
        String query = "SELECT owner_id, shortname, fullname FROM owners ";

        String subquery1 = (owner_id != null)?owner_id.SelectWhereStatement("owner_id", true) + " ":"";
        String subquery2 = (shortname != null)?shortname.SelectWhereStatement("shortname", false) + " ":"";
        String subquery3 = (fullname != null)?fullname.SelectWhereStatement("fullname", false):"";

        if (!subquery1.isEmpty() ||
                !subquery2.isEmpty() ||
                !subquery3.isEmpty()) {
            query += "WHERE ";
        }

        query += subquery1 + subquery2 + subquery3;

        Statement statement = m_connection.GetConnection().createStatement();
        ResultSet result = statement.executeQuery(query);

        ArrayList<Owner> owners = new ArrayList<>();
        while(result.next()) {
            owners.add(new Owner(result.getInt("owner_id"),
                    result.getString("shortname"),
                    result.getString("fullname")));
        }

        return owners;
    }

    public void DeleteOwners(Predicate<Integer> owner_id,
                               Predicate<String> shortname,
                               Predicate<String> fullname) throws SQLException {
        String query = "DELETE FROM owners ";
        String subquery1 = (owner_id != null)?owner_id.SelectWhereStatement("owner_id", true) + " ":"";
        String subquery2 = (shortname != null)?shortname.SelectWhereStatement("shortname", false) + " ":"";
        String subquery3 = (fullname != null)?fullname.SelectWhereStatement("fullname", false):"";

        if (!subquery1.isEmpty() ||
                !subquery2.isEmpty() ||
                !subquery3.isEmpty()) {
            query += "WHERE ";
        }

        query += subquery1 + subquery2 + subquery3;

        Statement statement = m_connection.GetConnection().createStatement();
        statement.executeQuery(query);
    }

}
