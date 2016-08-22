package dao;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.net.ssl.HttpsURLConnection;

import beans.User;


public class DbConnect {

	public static Connection connectDB(){
		Connection connection=null;
		try{
			String host="ec2-54-225-246-33.compute-1.amazonaws.com";
			String dbName="df6qteidc6i5cg";
			String userName="qcsqjvxvpxbxfz";
			String password="x8a1YCcHO-2kGbB6BJE1gRbxJC";
			String url="jdbc:postgresql://"+host+":5432/"+dbName;
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url,userName, password);
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
		return connection;
	}

	public static String registerUser(User user,String token,String secret, String pin){
		Connection connection=connectDB();
		String list="insert into smart_cards(";
		try{
			String status=connectPA(token,secret,pin,user.getAuthId());
			if(status.equals("false")){
				return "Auth Failed";}
			else	
				if(connection==null)
					return "error";
				else{
					if(!user.getName().equals("none"))
						list=list+"name,";
					if(!user.getEmail().equals("none"))
						list=list+"email,";
					if(!user.getAuthId().equals("none"))
						list=list+"auth_id,";
					if(!user.getPhoneNumber().equals("none"))
						list=list+"ph_no,";
					if(!user.getCredit().equals("none"))
						list=list+"credit,";
					if(!user.getOrganization().equals("none"))
						list=list+"org,";
					if(!user.getDetails().equals("none"))
						list=list+"details,";
					if(!list.endsWith(","))
						list=list.substring(0,list.length()-1)+") values (";
					if(list.contains("name"))
						list=list+"'"+user.getName()+"',";
					if(list.contains("email"))
						list=list+"'"+user.getEmail()+"',";
					if(list.contains("auth_id"))
						list=list+"'"+user.getAuthId()+"',";
					if(list.contains("ph_no"))
						list=list+"'"+user.getPhoneNumber()+"',";
					if(list.contains("credit"))
						list=list+"'0',";
					if(list.contains("org"))
						list=list+"'"+user.getOrganization()+"',";
					if(list.contains("details"))
						list=list+"'"+user.getDetails()+"',";

					if(list.endsWith(","))
						list=list.substring(0,list.length()-1)+")";
					Statement statement=connection.createStatement();

					if(statement.executeUpdate(list)==0)
						return "failed";
					else 
						return "success";
				}
		}catch(Exception exception){
			exception.printStackTrace();
			return "failed";
		}
	}
	public static String connectPA(String token,String secret, String pin,String authId){
		try{
			String link="https://primeauth.com/api/v1/smart_card/edit_auth.json?"
					+ "token="+token+"&secret="+secret+"&auth_id="+authId;
			URL url=new URL(link);
			HttpsURLConnection httpsURLConnection=(HttpsURLConnection) url.openConnection();
			httpsURLConnection.setRequestMethod("POST");
			httpsURLConnection.setDoInput(true);
			httpsURLConnection.setDoOutput(true);
			httpsURLConnection.addRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			return httpsURLConnection.getInputStream().toString();
		}catch(Exception exception){

		}
		return "false";
	}

	public static User getUser(String authId){
		User user=new User();
		Connection connection=connectDB();
		String sql="select name,email,ph_no,credit,org,details from smart_cards where auth_id like ?";
		try{	
			if(connection!=null){
				PreparedStatement query=connection.prepareStatement(sql);
				query.setMaxRows(1);
				query.setString(1,authId);
				ResultSet result=query.executeQuery();
				while(result.next()){
					user.setAuthId(authId);
					user.setName(result.getString(1));
					user.setEmail(result.getString(2));
					user.setPhoneNumber(result.getString(3));
					user.setCredit(result.getString(4));
					user.setOrganization(result.getString(5));
					user.setDetails(result.getString(6));
				}
				return user;
			}
		}catch(Exception exception){
			exception.printStackTrace();

		}
		return null;
	}
	public static String editUser(User user){
		Connection connection=connectDB();
		String sql="update smart_cards set ";
		try{
			if(connection==null)
				return "error";
			else{
				if(!user.getName().equals("none"))
					sql=sql+" name = '"+user.getName()+"' ,";
				if(!user.getEmail().equals("none"))
					sql=sql+" email = '"+user.getEmail()+"' ,";
				if(!user.getPhoneNumber().equals("none"))
					sql=sql+" ph_no = '"+user.getPhoneNumber()+"' ,";
				if(!user.getCredit().equals("none"))
					sql=sql+" credit = '"+user.getCredit()+"' ,";
				if(!user.getOrganization().equals("none"))
					sql=sql+" org = '"+user.getOrganization()+"' ,";
				if(!user.getDetails().equals("none"))
					sql=sql+" details = '"+user.getDetails()+"'";
				if (sql.endsWith(",")) {
					sql=sql.substring(0,sql.length()-2)+" where auth_id = '"+user.getAuthId()+"';";
				}
				else
					sql=sql+" where auth_id = '"+user.getAuthId()+"';";
				Statement statement=connection.createStatement();
				statement.executeUpdate(sql);
				return "true";
			}
		}catch(Exception exception){
			exception.printStackTrace();
			return "failed";

		}
	}
}


