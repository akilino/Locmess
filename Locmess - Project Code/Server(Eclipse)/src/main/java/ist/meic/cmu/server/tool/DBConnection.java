package ist.meic.cmu.server.tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import ist.meic.cmu.server.storage.GPSLocation;

public class DBConnection {

	final private String host = "localhost:3306/locmessdb";
	private String user = "root";
	private String passwd = "";

	private static DBConnection instance=null;

	private DBConnection() {
		super();

	}	
	public static DBConnection getInstance() {
		if(instance == null) {
			instance = new DBConnection();
		}
		return instance;
	}


	public synchronized Connection connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + host,user,passwd);
			return connection;
		} catch (Exception e) {
		}
		return null;
	}

	public void disconnect(Connection connection) {
		try {
			connection.close();
		} catch (Exception e) {
			System.out.println("Fail to close DataBase connection ");

		}
	}
	
	
	public ArrayList<GPSLocation> getAllLocation(){
		ArrayList<GPSLocation> locationList = new ArrayList<GPSLocation>();
		Connection connect = connect();
		// PreparedStatements can use variables and are more efficient
		try {
			Statement statement = connect.createStatement();

			ResultSet rs=statement.executeQuery("SELECT * FROM locmessdb.location");
			while(rs.next()){
				GPSLocation location=new GPSLocation(rs.getString(1), rs.getFloat(2), rs.getFloat(3), rs.getInt(4));
				
				locationList.add(location);

			}
			return locationList;

		} catch (SQLException e) {
			System.out.println("DBConnection::getAllUsers:FAIL to execute Query");
			e.printStackTrace();
		}
		finally {
			disconnect(connect);
		}
		return null;
	}

	public boolean verify(String user, String password) {

		Connection connection = connect();

		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM locmessdb.users WHERE username = ? LIMIT 1");

			preparedStatement.setString(1, user);
			ResultSet rs=preparedStatement.executeQuery();
			if(rs.next()){
				String pass=rs.getNString("password");
				if(pass.equals(password)){
					return true;
				}
				System.out.println(pass+" != "+password);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			disconnect(connection);
		}
		return false;

	}

	public boolean createUser(String usertoInsert, String password)  {
		Connection connect = connect();
		// PreparedStatements can use variables and are more efficient
		try {
			
			PreparedStatement preparedStatement = connect .prepareStatement("insert into  locmessdb.user(`username`, `password`) values (? , ?)");

			preparedStatement.setString(1, usertoInsert);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();
			return true;

		} catch (SQLException e) {
			System.out.println("DBConnection::createUser: FAIL to create User");
		}
		finally {
			disconnect(connect);
		}
		return false;
	}

	public boolean createLocation(String name, float longitude, float latitude, int radius)  {
		Connection connect = connect();
		// PreparedStatements can use variables and are more efficient
		try {
			PreparedStatement preparedStatement = connect .prepareStatement("insert into locmessdb.location (name,longitude,latitude,radius) values (?, ?, ?, ?)");

			preparedStatement.setString(1, name);
			preparedStatement.setFloat(2, longitude);
			preparedStatement.setFloat(3, latitude);
			preparedStatement.setInt(4, radius);
			preparedStatement.executeUpdate();
			return true;

		} catch (SQLException e) {
			System.out.println("DBConnection::createLocation FAIL");
			e.printStackTrace();
		}
		finally {
			disconnect(connect);
		}
		return false;
	}

	public boolean removeLocation(String name){
		Connection connect = connect();
		// PreparedStatements can use variables and are more efficient
		try {
			PreparedStatement	preparedStatement = connect .prepareStatement("DELETE FROM location WHERE id=?");
			preparedStatement.setString(1, name);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("DBConnection::removeLocation FAIL");
			e.printStackTrace();
		}
		finally {
			disconnect(connect);
		}
		return false;
	}

}
