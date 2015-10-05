package Database;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConnector {
		//用户登录信息
		String driver = "com.mysql.jdbc.Driver";
		Connection con;
		String url = "jdbc:mysql://localhost:3306/file";
		public String user = "root";
		public String pwd = "061956hm";
		
		public ResultSet rt;
		private  PreparedStatement st;
		
		public String dbName = "";
		public String tableName ="";

		//连接数据库
		public void connection() {
			try {
				Class.forName(driver);

				con = DriverManager.getConnection(url, user, pwd);
				
				if (!con.isClosed())
					System.out.println("connect to mysql success！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//查询记录
		public void select(){
			try {
				Statement statement = con.createStatement();
				//sql语句
				String sql = "select * from student";
				ResultSet rs = statement.executeQuery(sql);
				String name = "";
				int age = 0;
				while (rs.next()){
					name = rs.getString("name");
					age = rs.getInt("age");
					System.out.println("name ="+name+" age="+age);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		//新增一行记录
//	    public void add(String name,int age){
//	    	try{
//	    		PreparedStatement sql;
//	         	sql = con.prepareStatement("insert into student(name,age) value(?,?)");
//	         	sql.setString(1,name);
//	         	sql.setInt(2,age);
//	         	sql.executeUpdate();
//	    	
//	    	}catch(Exception e){
//	    		e.printStackTrace();
//	    	}
//	    }
	    public void add( String File_Name,String Security_Class,String Create_Time,String Validate_Time,int Creater_ID,int Validate_Domain){
			try {
				st= getcon().prepareStatement("insert into fileinform (File_Name,Security_Class,Create_Time,Validate_Time,Creater_ID,Validate_Domain)values(?,?,?,?,?,?)");
				st.setString(1,File_Name);
				st.setString(2, Security_Class);
				st.setString(3, Create_Time);
				st.setString(4, Validate_Time );
				st.setInt(5, Creater_ID);
				st.setInt(6,Validate_Domain);
				st.execute();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void updata(){
			try {
				st= getcon().prepareStatement("updata student set name=kris where name=���ෲ");
				st.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    
	    public Connection getcon() throws SQLException{
			if(con==null){
				con=DriverManager.getConnection(url, user,pwd);		
				}
			return con;
		}
	    //删除一行记录
//	    public void delete(int id){
//	    	try{
//	    		PreparedStatement sql;
//	         	sql = con.prepareStatement("delete from student where id?");
//	         	//设置
//	         	sql.setInt(1,id);       
//	         	sql.executeUpdate();
//	    	
//	    	}catch(Exception e){
//	    		e.printStackTrace();
//	    	}
//	    }
	    
	    //更新数据 
	    public void update(String name,int age)  
	    {  
	        try {  
	            PreparedStatement sql;  
	            sql = con.prepareStatement("update student set name=?,age=?");  
	            sql.setString(1,name);  
	            sql.setInt(2,age);  
	            sql.executeUpdate();   
	              
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	          
	    }  
	    
	    //函数入口
		public static void main(String [] args){
			DBConnector sql = new DBConnector();
			sql.connection();
			sql.select();
//			sql.add("hm", 20);
			sql.select();
		}
}
