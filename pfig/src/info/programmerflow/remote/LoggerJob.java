package info.programmerflow.remote;

import java.io.File;
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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * LoggerJob is a daemon that takes incoming log messages on a queue and sends them out to the server.
 * @author jalawran
 *
 */
public class LoggerJob extends Job {
	private ConcurrentLinkedQueue<Object[]> queue;
	private XmlRpcClient client;
	private Connection conn;

	private static LoggerJob instance;

	/**
	 * Log the incoming message
	 * @param params
	 * @throws Exception 
	 */
	public static void log(Object[] params) throws Exception {
		if (instance == null) {
			instance = new LoggerJob();
			instance.schedule();
		}
		instance.enqueue(params);
	}
	public void shutdown() throws SQLException {

		Statement st = conn.createStatement();

		// db writes out to files and performs clean shuts down
		// otherwise there will be an unclean shutdown
		// when program ends
		st.execute("SHUTDOWN");
		conn.close();    // if there are no other open connection
	}
	protected void finalize() {
		try {
			shutdown();
		} catch (SQLException e) {
		}
	}
	/**
	 * Construct a new LoggerJob. LoggerJob maintains a queue of items to log, and uses XMLRPC to push to the server.
	 * @param s Information about the type of event being logged
	 */
	private LoggerJob() throws Exception {
		super("PFIS Remote Logger");
		queue = new ConcurrentLinkedQueue<Object[]>();
		setSystem(true);
		setPriority(Job.DECORATE);
		
		Class.forName("org.hsqldb.jdbcDriver");
	}
	/**
	 * Push an item onto the queue.
	 * @param params
	 */
	private void enqueue(Object[] params) {
		if (params[3] == null || params[4] == null) {
			try {
				throw new Exception("hey");
			} catch (Exception e) {
			}
		} else queue.add(params);
	}
	public void query(String expression) throws SQLException {

		Statement st = null;
		ResultSet rs = null;

		st = conn.createStatement();
		rs = st.executeQuery(expression);
		dump(rs);
		st.close();
	}


	public static void dump(ResultSet rs) throws SQLException {
		ResultSetMetaData meta   = rs.getMetaData();
		int               colmax = meta.getColumnCount();
		int               i;
		Object            o = null;

		for (; rs.next(); ) {
			for (i = 0; i < colmax; ++i) {
				o = rs.getObject(i + 1);
				System.out.print(o.toString() + " ");
			}

			System.out.println(" ");
		}
	}

	/**
	 * While the queue is not empty, push the queue to the server. Reschedule thread when the queue is emptied.
	 */
	protected IStatus run(IProgressMonitor arg0) {
		Object[] params = null;
		LinkedList<Object[]> list = new LinkedList<Object[]>();
		try {
			if (client == null) {
				// Initialize XML-RPC client
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				try {
					config.setServerURL(new URL(Activator.getServer()));
				} catch (MalformedURLException e) {
//					e.printStackTrace();
				}
				client = new XmlRpcClient();
				client.setConfig(config);
			}
			if (conn == null) {
				File path = new File(Activator.getDatabase(), "pfis");
				conn = DriverManager.getConnection("jdbc:hsqldb:file:" + path.getAbsolutePath(), "sa", "");

				try {
					Statement st = null;
					st = conn.createStatement();
					st.executeUpdate("CREATE TABLE logger_log ( id INTEGER IDENTITY, user VARCHAR(50), timestamp DATETIME, action VARCHAR(50), target VARCHAR, referrer VARCHAR, agent VARCHAR(50))");
					st.close();
				} catch (Exception e) {
				}
			}
			PreparedStatement st = conn.prepareStatement("INSERT INTO logger_log(user,timestamp,action,target,referrer,agent) VALUES(?,?,?,?,?,?)");

//			long elapsed = -System.currentTimeMillis();
			try {
				while (!queue.isEmpty()) {
					params = queue.remove();
					list.add(params);

					st.setString(1, params[0].toString());
					st.setTimestamp(2, new Timestamp(Long.parseLong(params[1].toString())));
					st.setString(3, params[2].toString());
					st.setString(4, params[3].toString());
					st.setString(5, params[4].toString());
					st.setString(6, params[5].toString());

					// TODO: FIXME: Uncomment for debugging
//					System.out.println(params[2].toString() + "\t" + params[3].toString() + "\t" + params[4].toString());

					st.execute();
				}
				client.execute("logAll",new Object[]{list});
			} catch (Exception e) {
//				e.printStackTrace();
				System.gc();
			} finally {
				list.clear();
			}
			st.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		this.schedule(5000);
		return Status.OK_STATUS;
	}
}