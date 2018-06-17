package container;

import java.util.List;
import java.util.Scanner;

import container.crawler.CrawlerExample;
import container.crawler.TaipeiTravelCrawler;

public final class CrawlerEngine {
	private ActivityRepository activityRepository;
	private Logger logger;
	
	public void startEngine(final int frequency, boolean test) {
		activityRepository = test ? new MockActivityRepository() : 
				new JdbcProxy(new JdbcActivityRepository());
		
		logger = new Logger("log.txt", "err.txt");
		logger.start();
		logger.log(getClass(), "ActivityRepository chosen: " +  activityRepository.getClass());
		logger.log(getClass(), "Crawler engine started.");
		logger.log(getClass(), "preparing crawlers.....");
		
		List<Crawler> crawlers = CrawlerManager.getInstance().getCrawlers();
		logger.log(getClass(), "crawlers count: " + crawlers.size());
		for(Crawler crawler : crawlers)
			logger.log(getClass(), "Crawler [" + crawler.getClass().getSimpleName() + "] is prepared.");
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Would you like to run the crawler engine? (y/n)");
		if (scanner.nextLine().toLowerCase().trim().charAt(0) == 'y')
			scheduleCrawlers(crawlers);
		else
			System.out.println("The engine stopped and shutted down.");
		scanner.close();
	}
	
	private void scheduleCrawlers(List<Crawler> crawlers){
		logger.log(getClass(), "Start shceduling...");
		runAllCrawlersParallelly(crawlers);
	}
	
	private void runAllCrawlersParallelly(List<Crawler> crawlers){
		crawlers.parallelStream().forEach(crawler -> {
			try {
				logger.log(getClass(), "run crawler " + crawler.getClass().getSimpleName());
				crawler.crawl(activityRepository, logger);
			} catch (Exception e) {
				logger.err(getClass(), "Error", e);
			}
		});
	}
	
	public static void main(String[] argv){
		/*if (argv.length <= 0)
			System.out.println("[Frequency : int] should be given as a parameter to run the crawler engine.");
		int frequency = Integer.parseInt(argv[0]);*/
		CrawlerEngine crawlerEngine = new CrawlerEngine();
		crawlerEngine.startEngine(10, true);
	}
	
	@Override
	protected void finalize() throws Throwable {
		logger.close();
		super.finalize();
	}
}
