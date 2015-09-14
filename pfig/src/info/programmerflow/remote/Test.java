package info.programmerflow.remote;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Test {
	Connection conn;                                                //our connnection to the db - presist for life of program

	// we dont want this garbage collected until we are done
	public Test(String db_file_name_prefix) throws Exception {    // note more general exception

		// Load the HSQL Database Engine JDBC driver
		// hsqldb.jar should be in the class path or made part of the current jar
		Class.forName("org.hsqldb.jdbcDriver");

		// connect2 to the database.   This will load the db files and start the
		// database if it is not alread running.
		// db_file_name_prefix is used to open or create files that hold the state
		// of the db.
		// It can contain directory names relative to the
		// current working directory
		conn = DriverManager.getConnection("jdbc:hsqldb:"
				+ db_file_name_prefix,    // filenames
				"sa",                     // username
		"");                      // password
	}

	public void shutdown() throws SQLException {

		Statement st = conn.createStatement();

		// db writes out to files and performs clean shuts down
		// otherwise there will be an unclean shutdown
		// when program ends
		st.execute("SHUTDOWN");
		conn.close();    // if there are no other open connection
	}

//	use for SQL command SELECT
	public synchronized void query(String expression) throws SQLException {

		Statement st = null;
		ResultSet rs = null;

		st = conn.createStatement();         // statement objects can be reused with

		// repeated calls to execute but we
		// choose to make a new one each time
		rs = st.executeQuery(expression);    // run the query

		// do something with the result set.
		dump(rs);
		st.close();    // NOTE!! if you close a statement the associated ResultSet is

		// closed too
		// so you should copy the contents to some other object.
		// the result set is invalidated also  if you recycle an Statement
		// and try to execute some other query before the result set has been
		// completely examined.
	}


	public static String dump(ResultSet rs) throws SQLException {
		ResultSetMetaData meta   = rs.getMetaData();
		int               colmax = meta.getColumnCount();
		int               i;
		Object            o = null;
		StringBuffer b = new StringBuffer();

		for (; rs.next(); ) {
			b.append("<row>");
			for (i = 0; i < colmax; ++i) {
				o = rs.getObject(i + 1);
				b.append("<item>");
				b.append(o.toString());
				b.append("</item>");
			}
			b.append("</row>\n");
		}
		return b.toString();
	}

	public static void main(String[] args) {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL("http://cobra.watson.ibm.com/xmlrpc/"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		try {
			long elapsed = -System.currentTimeMillis();
			List good = new LinkedList();
			for (int i = 0; i < 1000; i++) {
			good.add(new Object[] {"bogus",i,"action"+i,"target"+i,"referrer"+i,"session"});
			}
			client.execute("logAll",new Object[] {good});
			elapsed += System.currentTimeMillis();
			System.out.print(elapsed);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}

	}
	
	public static void main2(String[] args) {

		Test db = null;

		try {
			db = new Test("C:\\Program Files\\IBM\\TeamConcert\\pfis");
		} catch (Exception ex1) {
			ex1.printStackTrace();    // could not start db

			return;                   // bye bye
		}

		try {
			// do a query
			long elapsed = -System.currentTimeMillis();
			db.query("SELECT * FROM logger_log");
			elapsed += System.currentTimeMillis();
			System.out.println(elapsed);

			// at end of program
			db.shutdown();
		} catch (SQLException ex3) {
			ex3.printStackTrace();
		}
	}    // main()

}
