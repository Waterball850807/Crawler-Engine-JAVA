package container;

public interface Crawler {
	/**
	 * @param activityRepository repository pattern used connecting the database
	 * @param logger logging util
	 */
	void crawl(ActivityRepository activityRepository, Logger logger);
	
	
	/**
	 * @return data source name 
	 */
	String getSource();
}
