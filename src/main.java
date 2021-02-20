/********************************************
 * Project description
 *
 * Created by: Lasse J. Kongsdal
 * Date: 08-02-2021
 *
 * Description of program
 ********************************************/

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class main {

    private Connection connect() {

        // SQLite connection string
        String url = "jdbc:sqlite:db/MobilePay";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static void main(String[] args) {

        main app = new main();

        while (true) {

            System.out.println("What action do you want to take?\n" +
                    "1. Print all users.\n" +
                    "2. Create new user.\n" +
                    "3. Print all transactions\n" +
                    "4. Begin transaction\n" +
                    "5. End program"
            );

            Scanner input = new Scanner(System.in);

            int choice = input.nextInt();

            if (choice == 1) { app.printUsers(); }
            else if (choice == 2) {

                System.out.print("Name: ");
                String name = input.next();
                System.out.print("Phone number: ");
                int phoneNumber = input.nextInt();
                // I used nextLine() but it would not work as intended
                System.out.print("Address (name of road only): ");
                String address = input.next();
                System.out.print("Card number: ");
                long cardNumber = input.nextLong();
                System.out.print("Balance: ");
                int balance = input.nextInt();
                app.createUser(name, phoneNumber, address, cardNumber, balance);

            } else if (choice == 3) { app.printTransactions(); }
            else if (choice == 4) {

                    System.out.print("Write the id of the account you want to send money from: ");
                    int fromUserIndex = input.nextInt();
                    System.out.print("Write the id of the account you want to send money to: ");
                    int toUserIndex = input.nextInt();
                    System.out.print("Write the amount of money that you want to send: ");
                    int amountOfMoney = input.nextInt();
                    app.transferMoney(fromUserIndex, toUserIndex, amountOfMoney);

            } else if (choice == 5) { break; }
            else { System.out.println("Something went wrong, write 1,2,3... to choose the appropriate action."); }

        }

    }

    public void printUsers() {

        String sql = "SELECT * FROM Users";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {

                System.out.println(rs.getInt("id") +  ". " +
                        rs.getString("name") + "\n" +
                        rs.getString("phoneNumber") + "\n" +
                        rs.getString("address") + "\n" +
                        rs.getString("cardNumber") + "\n" +
                        rs.getString("registrationDate") + "\n" +
                        rs.getString("balance") + "\n\n"
                );

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void printTransactions() {

        String sql = "SELECT * FROM Transactions";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {

                System.out.println(rs.getInt("id") +  ". " +
                        rs.getString("fromUser") + "\n" +
                        rs.getString("toUser") + "\n" +
                        rs.getString("amount") + "\n" +
                        rs.getString("date") + "\n\n"
                );

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void transferMoney(int fromUserIndex, int toUserIndex, int amountOfMoney) {

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = dateFormat.format(date);

        String values = fromUserIndex + ", " + toUserIndex + "," + amountOfMoney + ", '" + strDate + "'";
        String sql_transaction = "INSERT INTO Transactions (fromUser, toUser, amount, date) VALUES ("+values+")";
        String sql_updateFromUser = "UPDATE Users SET balance -= "+amountOfMoney+" WHERE id = "+fromUserIndex;
        String sql_updateToUser = "UPDATE Users SET balance += "+amountOfMoney+" WHERE id = "+toUserIndex;

        try {

            Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            stmt.executeQuery(sql_transaction);
            stmt.executeQuery(sql_updateFromUser);
            stmt.executeQuery(sql_updateToUser);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    // p_ = parameter
    public void createUser(String p_name, int p_phoneNumber, String p_address, long p_cardNumber, int p_balance) {

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date);

        String values = "'" + p_name + "', " + p_phoneNumber + ", '" + p_address + "', " + p_cardNumber + ", '" + strDate + "', " + p_balance;
        String sql_createUser = "INSERT INTO Users (name, phoneNumber, address, cardNumber, registrationDate, balance) VALUES ("+values+")";

        try {

            Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            stmt.executeQuery(sql_createUser);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

}

