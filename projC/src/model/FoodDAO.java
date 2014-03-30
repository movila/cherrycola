package model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

public class FoodDAO {

	protected static final String DBURL = "jdbc:derby://roumani.eecs.yorku.ca:9999/CSE;user=student;password=secret";
	private int progress = 0;
	
	public FoodDAO() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
	}
	
	
	/**
	 * Query database and generate database picture files into given path
	 *
	 * @param  path  		a String path indicate where the database image should be stored
	 * @throws SQLException query database failed
	 * @throws IOException 	create database images failed
	 */
	protected void generateImage(String path) throws SQLException, IOException { 
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			
			connection = DriverManager.getConnection(DBURL);
			statement = connection.createStatement();
			statement.execute("SET SCHEMA roumani");
			String query = "SELECT name, picture FROM category";
			rs = statement.executeQuery(query);
			
			while (rs.next()) {
				String name = rs.getString("name");
				InputStream is = rs.getBinaryStream("picture");
				
				BufferedImage imageBuffer = ImageIO.read(is);
				ImageIO.write(imageBuffer, "png", new File(path + "/" + name + ".png"));
			}
			
		} catch (SQLException e) {
			
			throw new SQLException(e);
			
		} finally {
			
			// release database resources immediately 
			rs.close();
			statement.close();
			connection.close();
			
		}
	}
	
	
	/**
	 * Query database and generate database picture files into given path,
	 * return the progress in terms of percentage
	 *
	 * @param  path  		a String path indicate where the database image should be stored
	 * @throws SQLException query database failed
	 * @throws IOException 	create database images failed
	 * return 				percentage of progress (100 for finished)
	 */
	protected int itemImageGenerator(String outPath) throws SQLException, IOException { 
		final String URL_ROOT = "https://ajax.googleapis.com/ajax/services/search/images?";
		String filetype = "jpg";
    	String imageSize = "medium";
    	int resultSize = 1;
    	int startCursor = 0;
    	//String keyword = "";
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		//ResultSet rs2 = null;
		
		if (this.progress >= 100) return this.progress; // if finished return;
		
		// otherwise,
		try {
			
			connection = DriverManager.getConnection(DBURL);
			statement = connection.createStatement();
			statement.execute("SET SCHEMA roumani");
			//rs2 = statement.executeQuery("SELECT COUNT(*) AS size FROM item");
			int dataSize = 99;
			String query = "SELECT number, name FROM item";
			rs = statement.executeQuery(query);
			
			
			int i = 0;
			while (rs.next()) {
				String number = rs.getString("number");
				String name = rs.getString("name");
				
				int retry = 0;
				while (retry < 5) { // for each query, retry 5 times on error pulling images
					try {
						startCursor = retry;
				    	// keyword = "Chicken+Meat";
						// note: Google only allow max 8 response per query on its legacy API
			        	String imgQuery = "v=1.0&q=" + URLEncoder.encode(name, "UTF-8") + "&as_filetype=" + 
			        							filetype + "&imgsz="+ imageSize + "&rsz=" + resultSize + 
			        							"&start=" + startCursor;
		
			            URL url = new URL(URL_ROOT + imgQuery);
			            URLConnection con = url.openConnection();
		
			            String line;
			            StringBuilder builder = new StringBuilder();
			            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			            while((line = reader.readLine()) != null) {
			                builder.append(line);
			            }
			            
			            System.out.println("JSON-resp: " + builder.toString());
			            
			            JSONObject json = new JSONObject(builder.toString());
			            JSONArray jarr = json.getJSONObject("responseData").getJSONArray("results");
			            String imageUrl = jarr.getJSONObject(0).getString("url");
		            
		            
		            	BufferedImage image = ImageIO.read(new URL(imageUrl));
			            ImageIO.write(image, filetype, new File(outPath + "/" + number + "." + filetype));
		            
			            
			            System.out.println("Output: " + number);
			            System.out.println("Url: " + jarr.getJSONObject(0).getString("url"));
			            System.out.println("Thumbnail url: " + jarr.getJSONObject(0).getString("tbUrl"));
			            
			            retry = 6; // Give it up, no result's going to return
					} catch (Exception e) {
						retry++;
		            }
				}
				
				// Get progress (percentage) of current generating
				++i;
				this.progress = (i / dataSize) * 100;
			}
			
			System.out.println(" --- IMAGE OUTPUT FINISHED ---");

		} catch (SQLException e) {
			
			throw new SQLException(e);
			
		} finally {
			// release database resources immediately
			//rs2.close();
			rs.close();
			statement.close();
			connection.close();
		}		
		
		return this.progress;
	}
	
	
	/**
	 * Validate client information against database
	 *
	 * @param  name  		client name (can be either client name or client number)
	 * @param  passwd  		client password
	 * @return 				ClientBean object; otherwise null
	 * @throws SQLException query database failed
	 */
	protected ClientBean clientValidation(String name, String passwd) throws SQLException {
		
		String partCond = (name.matches("\\d{5,5}")) ? ("number = " + name) : ("name = '" + name + "'");
		ClientBean client = null;
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			
			connection = DriverManager.getConnection(DBURL);
			statement = connection.createStatement();
			statement.execute("SET SCHEMA roumani");
			String query = "SELECT number, name FROM client WHERE " + partCond + " AND password = '" + passwd + "'";
			
			System.out.println("Query: " + query);
			
			rs = statement.executeQuery(query);
			
			while (rs.next()) {
				client = new ClientBean(rs.getInt("number"), rs.getString("name"));
			}
			
		} catch (SQLException e) {
			
			throw new SQLException(e);	
			
		} finally {
			// release database resources immediately 
			rs.close();
			statement.close();
			connection.close();
		}
		return client;
	}
	
	
	/**
	 * Retrieve list of categories from database
	 *
	 * @return 				list of category
	 * @throws SQLException query database failed
	 */
	protected List<CategoryBean> retrieveCategory() throws SQLException 
	{
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		List<CategoryBean> list = new LinkedList<CategoryBean>();
		
		try	{
			connection = DriverManager.getConnection(DBURL);
			statement = connection.createStatement();
			statement.executeUpdate("SET SCHEMA roumani");
			rs=statement.executeQuery("SELECT id, name FROM category");
			
			while(rs.next())
			{
				CategoryBean cate = new CategoryBean(rs.getInt("ID"), rs.getString("NAME"));
				list.add(cate);
			}						
		} catch(SQLException e) {
			throw new SQLException(e);
		} finally {
			rs.close();
			statement.close();
			connection.close();
		}
		return list;
	}
	
	
	/**
	 * Retrieve list of items from database based on a optional filter 'catID'
	 *
	 * @param  catID		category id
	 * @return 				list of items
	 * @throws SQLException query database failed
	 */
	protected List<ItemBean> retrieveItem(String identifier, String sender) throws SQLException 
	{
		String filter = "";
		
		// Pull related items based on identifier filter
		if (identifier != null && identifier.matches("\\d+") && sender.equals("path"))
				filter = " WHERE catid = " + Integer.parseInt(identifier);
		else if (identifier != null && sender.equals("search"))
		{	
			if (identifier.matches("\\d{4,4}[a-zA-Z]\\d{3,3}"))
				filter = " WHERE number like '" + identifier + "%'";
			else
				filter = " WHERE name like '%" + identifier + "%'";
		}

		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		List<ItemBean> list = new LinkedList<ItemBean>();
		
		try	{
			connection = DriverManager.getConnection(DBURL);
			statement = connection.createStatement();
			statement.executeUpdate("SET SCHEMA roumani");
			rs=statement.executeQuery("SELECT number, name, price, qty FROM item" + filter);
			
			while(rs.next())
			{
				ItemBean item = new ItemBean(rs.getString("number"), rs.getString("name"), 
											rs.getDouble("price"), rs.getInt("qty"), 0.0);
				list.add(item);
			}						
		} catch(SQLException e) {
			
			throw new SQLException(e);
			
		} finally {
			
			rs.close();
			statement.close();
			connection.close();
		}
		return list;
	}
	
	
}
