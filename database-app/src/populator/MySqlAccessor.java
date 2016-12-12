package populator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class MySqlAccessor {
        private Connection connect = null;
        private Statement statement = null;
        private PreparedStatement preparedStatement = null;
        private ResultSet resultSet = null;
        
        private String title;
        private String doi;
        private String shortAbstract;
        private String authors;
        private String keywords;
        private Date date;
        private String mesh;
        
        
        public MySqlAccessor() {
        	try {
        		System.out.print("Enter mysql password: ");
        		Scanner in = new Scanner(System.in);
        		String password = in.next();
	        	connect = DriverManager
	                    .getConnection("jdbc:mysql://192.168.88.11/cochraneLibrary?"
	                                    + "user=mysqluser&password="+password);
	        	in.close();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        
        protected void setArticleInfo(String doi, String title, String shortAbstract, LinkedList<String> authors, LinkedList<String> keywords, LinkedList<String> mesh, Date date) {
        	this.title = title;
        	this.doi = doi;
        	this.shortAbstract = shortAbstract;
        	this.authors = authors.toString();
        	this.keywords = keywords.toString();
        	this.date = date;
        	this.mesh = mesh.toString();
        	
        }
        
        public void writeDataBase(String doi, String title, String shortAbstract, LinkedList<String> authors, LinkedList<String> keywords, LinkedList<String> mesh, Date date) throws Exception {
        	preparedStatement = connect.prepareStatement("insert into  cochraneLibrary.articles values (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, doi);
            preparedStatement.setString(2, title);
            preparedStatement.setDate(3, new java.sql.Date(date.getTime()));
            preparedStatement.setString(4, keywords.toString());
            preparedStatement.setString(5, mesh.toString());
            preparedStatement.setString(6, authors.toString());                        
            preparedStatement.setString(7, shortAbstract);
            preparedStatement.executeUpdate();
            
            //preparedStatement = connect
                    //.prepareStatement("SELECT title, date, keywords, authors, short_abstract from cochraneLibrary.articles");
            //preparedStatement.executeQuery();
            System.out.println("Article: "+title.substring(0, Math.min(title.length(), 20))+"... not in database, adding");
            //writeResultSet(resultSet);
            
        	
        }
        
        public boolean duplicate(String doi) throws Exception {
        	//Check if entry already in database, if true don't try enter it again
            preparedStatement = connect.prepareStatement("select * from cochraneLibrary.articles where id = (?)");
            preparedStatement.setString(1, doi);
            resultSet = preparedStatement.executeQuery();            
            if (!resultSet.next()) {
            	return false;
            } else {
            	return true;
            }
            
        }
        
        public void readDataBase() throws Exception {
                try {
                        // This will load the MySQL driver, each DB has its own driver
                        Class.forName("com.mysql.jdbc.Driver");
                        // Setup the connection with the DB
                        Scanner passwordScanner = new Scanner(System.in);
                        String password = passwordScanner.next();
                        connect = DriverManager
                                        .getConnection("jdbc:mysql://localhost/feedback?"
                                                        + "user=sqluser&password="+password);
                        passwordScanner.close();
                        // Statements allow to issue SQL queries to the database
                        statement = connect.createStatement();
                        // Result set get the result of the SQL query
                        resultSet = statement
                                        .executeQuery("select * from cochraneLibrary.articles");
                        writeResultSet(resultSet);
                        
                        //Check if entry already in database, if true don't try enter it again
                        System.out.println(duplicate("10.1002/14651858.CD002204.pub4")+" :real doi");
                        System.out.println(duplicate("12345321")+" :fake doi");
                        //
                        /*preparedStatement = connect.prepareStatement("select * from cochraneLibrary.articles where id in (?)");
                        preparedStatement.setString(1, this.doi);
                        resultSet = preparedStatement.executeQuery();
                        writeResultSet(resultSet);*/
                        
                        // PreparedStatements can use variables and are more efficient
                        preparedStatement = connect
                                        .prepareStatement("insert into  cochraneLibrary.articles values (?, ?, ?, ?, ?, ?, ?)");
                        // "title, date, keywords, authors, shortAbstract");
                        // Parameters start with 1
                        preparedStatement.setString(1, this.doi);
                        preparedStatement.setString(2, this.title);
                        preparedStatement.setDate(3, new java.sql.Date(this.date.getTime()));
                        preparedStatement.setString(4, this.keywords);
                        preparedStatement.setString(5, this.mesh);
                        preparedStatement.setString(6, this.authors);                        
                        preparedStatement.setString(7, this.shortAbstract);
                        preparedStatement.executeUpdate();

                        preparedStatement = connect
                                        .prepareStatement("SELECT title, date, keywords, authors, short_abstract from cochraneLibrary.articles");
                        resultSet = preparedStatement.executeQuery();
                        writeResultSet(resultSet);

                        // Remove again the insert comment
                        preparedStatement = connect
                        .prepareStatement("delete from cochraneLibrary.articles where title= 'test title' ; ");
                        //preparedStatement.setString(1, "Test");
                        preparedStatement.executeUpdate();

                        resultSet = statement
                        .executeQuery("select * from cochraneLibrary.articles");
                        writeMetaData(resultSet);

                } catch (Exception e) {
                        throw e;
                } finally {
                        close();
                }

        }

        private void writeMetaData(ResultSet resultSet) throws SQLException {
                //         Now get some metadata from the database
                // Result set get the result of the SQL query

                System.out.println("The columns in the table are: ");

                System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
                for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
                        System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
                }
        }

        private void writeResultSet(ResultSet resultSet) throws SQLException {
                // ResultSet is initially before the first data set
        		int counter = 1;
                while (resultSet.next()) {
                        // It is possible to get the columns via name
                        // also possible to get the columns via the column number
                        // which starts at 1
                        // e.g. resultSet.getSTring(2);
                	System.out.println(resultSet.getString(counter));
                	System.out.println(counter);
                	counter += 1;
                		/*
                        String title = resultSet.getString("title");
                        String authors = resultSet.getString("authors");
                        String keywords = resultSet.getString("keywords");
                        String mesh = resultSet.getString("mesh");
                        Date date = resultSet.getDate("date");
                        String shortAbstract = resultSet.getString("short_abstract");
                        System.out.println("Title: " + title);
                        System.out.println("Authors: " + authors);
                        System.out.println("Keywords: " + keywords);
                        System.out.println("MeSH: " + mesh);
                        System.out.println("Date: " + date);
                        System.out.println("Short Abstract: " + shortAbstract);
                        */
                }
        }

        // You need to close the resultSet
        private void close() {
                try {
                        if (resultSet != null) {
                                resultSet.close();
                        }

                        if (statement != null) {
                                statement.close();
                        }

                        if (connect != null) {
                                connect.close();
                        }
                } catch (Exception e) {

                }
        }

}
