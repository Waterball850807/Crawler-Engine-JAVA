package container;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JdbcProxy implements ActivityRepository{
	private JdbcActivityRepository jdbcRp;
	private Logger logger;
	private Connection con;
	private long activitiesCacheExpireTime = TimeUnit.SECONDS.toMillis(10);
	private long activitiesGetLastTime = 0;
	private List<Activity> activitiesCache = new ArrayList<>();
	
	public JdbcProxy(JdbcActivityRepository jdbcRp) {
		logger = new Logger("jdbcLog.txt", "jdbcErr.txt");
		logger.start();
		logger.log(getClass(), "Proxy created ...");
		this.jdbcRp = jdbcRp;
		jdbcInit();
	}

	private void jdbcInit() {
		try {
	        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			logger.log(getClass(), "initializing JDBC ...");
			con = DriverManager.getConnection(Secret.SQL_CONNECTION, Secret.SQL_ACCOUNT, Secret.SQL_PASSWORD);
			jdbcRp.setConnection(con);
			logger.log(getClass(), "Connection setup seccessfully ...");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.err(getClass(), "SQL Exception ...", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.err(getClass(), "Class not found ...", e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (con != null && !con.isClosed())
			con.close();
		logger.log(getClass(), "jdbc connection closed.");
		logger.stop();
		super.finalize();
	}
	
	@Override
	public Activity createActivity(Activity activity) {
		if (validateActivity(activity))
		{
			logger.log(getClass(), "creating activity, " + activity);
			activity.setUpdatedDate(new Date());
			activity = jdbcRp.createActivity(activity);
			this.activitiesCache.add(activity);
		}
		else
			logger.log(getClass(), "activity invalid: " + activity);
		return activity;
	}

	private boolean validateActivity(Activity activity){
		return activity.getTitle() != null 
				&& activity.getTitle().length() > 3
				&& activity.getContent() != null 
				&& activity.getContent().length() > 15
				&& activity.getLink() != null
				&& activity.getSource() != null
				&& !isActivityDuplicate(activity);
	}
	
	private boolean isActivityDuplicate(Activity activity){
		for (Activity at : getActivities())
			if (activity.getTitle().trim().contains(at.getTitle().trim()))
				return true;
		return false;
	}
	
	@Override
	public List<Activity> getActivities() {
		long now = System.currentTimeMillis();
		if (now - activitiesGetLastTime >= activitiesCacheExpireTime)
		{
			logger.log(getClass(), "cache expires, request latest activities..." );
			this.activitiesCache = jdbcRp.getActivities();
			this.activitiesGetLastTime = now;
			return activitiesCache;
		}
		
		logger.log(getClass(), "getting all activities, count: " + activitiesCache.size());
		return activitiesCache;
	}

	@Override
	public Activity getActivity(int id) {
		return jdbcRp.getActivity(id);
	}
}
