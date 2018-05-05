package container;

import java.util.ArrayList;
import java.util.List;

import container.crawler.CrawlerExample;

/**
 * @author Waterball
 */
public class CrawlerManager {
	private static CrawlerManager instance = new CrawlerManager();
	private List<Crawler> crawlers = new ArrayList<>();
	
	/**
	 * �U�쳣�ݭn��ۤv�����Τu�@���U��list�~�|�Q�Ƶ{��
	 */
	public CrawlerManager(){
		addCrawler(new CrawlerExample());
	}
	
	public static CrawlerManager register(Class<? extends Crawler> crawlerClz){
		try {
			instance.addCrawler(crawlerClz.newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	static CrawlerManager getInstance(){
		return instance;
	}
	
	List<Crawler> getCrawlers(){
		return crawlers;
	}
	
	void addCrawler(Crawler crawler){
		crawlers.add(crawler);
	}
}
