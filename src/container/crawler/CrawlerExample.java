package container.crawler;

import container.ActivityRepository;
import container.Crawler;
import container.CrawlerManager;
import container.Logger;

public class CrawlerExample implements Crawler{
	
	@Override
	public void crawl(ActivityRepository activityRepository, Logger logger) {
		// �o��}�l���Τu�@
		// �p�G�ݭn�x�s���ʡA�άO���o���ʡA�Шϥ� activityRepository
		// ���Τu�@�пn��������x
		// �Y�u�Odebug�Τ�x�A�I�s logger.log(getClass(), "�T��")
		// �Y�O���~��ܡA�I�s logger.err(getClass(), "���~�T��") 
		// �άO  logger.err(getClass(), "���~�T��", �ҥ~����) 
		
		System.out.println("Crawler Example is starting!");
	}


	@Override
	public String getLink() {
		return "http://www.sanchong.ntpc.gov.tw/news/index.php?mode=data&id=958&parent_id=10010&type_id=10026";  //�^�ǧA���Ϊ��ӷ����}
	}

	@Override
	public String getSource() {
		return "�T���Ϥ���"; // �Ц^�Ǹ�ƨӷ��W��
	}

}