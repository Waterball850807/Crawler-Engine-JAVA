package container.crawler;

import container.ActivityRepository;
import container.Crawler;
import container.CrawlerManager;
import container.Logger;

public class CrawlerExample implements Crawler{
	
	@Override
	public void crawl(ActivityRepository activityRepository, Logger logger) {
		// 這邊開始爬蟲工作
		// 如果需要儲存活動，或是取得活動，請使用 activityRepository
		// 爬蟲工作請積極紀錄日誌
		// 若只是debug用日誌，呼叫 logger.log(getClass(), "訊息")
		// 若是錯誤日至，呼叫 logger.err(getClass(), "錯誤訊息") 
		// 或是  logger.err(getClass(), "錯誤訊息", 例外物件) 
		logger.log(getClass(), "sdfsdf");
		System.out.println("Crawler Example is starting!");
	}


	@Override
	public String getSource() {
		return "三重區公所"; // 請回傳資料來源名稱
	}

}
