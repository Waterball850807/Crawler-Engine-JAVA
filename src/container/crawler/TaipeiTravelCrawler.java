package container.crawler;

import container.ActivityRepository;
import container.Crawler;
import container.Logger;
import container.MockActivityRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class TaipeiTravelCrawler implements Crawler {

    private final static String ACTIVITIES_URL = "https://www.travel.taipei/zh-tw/activity";
    private final static String SOURCE = "台北旅遊網";
    private ArrayList<String> activitiesUrl = new ArrayList<>();
    private int pageNum = 2;

    public TaipeiTravelCrawler() {
    }

    @Override
    public void crawl(ActivityRepository activityRepository, Logger logger) {
        try {
            Document doc = Jsoup.connect(ACTIVITIES_URL).get();
//            String lastPage = doc.select("a.last-page").attr("href");
//            System.out.println(lastPage);
            logger.log(getClass(), doc.title());

//            getAllActivitiesUrl(doc);
//            do {
//                doc = Jsoup.connect(ACTIVITIES_URL + "?page=" + pageNum).get();
//                System.out.println(ACTIVITIES_URL + "?page=" + pageNum);
//                getAllActivitiesUrl(doc);
//                System.out.println(activitiesUrl.size());
//                pageNum++;
//            } while (!lastPage.equals("/zh-tw/activity?page=" + (pageNum - 1)));
//
//            crawlerAllUrls(activitiesUrl);

            //找到activity名稱
            String url  = "https://www.travel.taipei/zh-tw/activity/details/17130";
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("div.main-container");
            Elements elements1 = elements.select("div.unit-title-blk");
            Elements element = elements1.select("h2.unit-title");
            String title = element.html();
            System.out.println(title);

            Elements el1 = document.select("dd.info");
            String date = el1.first().html();
            Elements el2 = el1.select("a.btn-location-link");
            String address = el2.attr("title");
            Elements e = document.select("p.date");
            String updatedDate = e.first().html();
            String content = document.select("div.manual-script-blk").html();

            System.out.println(date);
            System.out.println(address);
            System.out.println(updatedDate);
            System.out.println(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllActivitiesUrl(Document doc) {
        Elements activities = doc.select("ul.event-news-card-list");
        Elements activitiesURL = activities.select("a.link");

        //拿到所有活動的網址
        Element con = null;
        for (Iterator it = activitiesURL.iterator(); it.hasNext(); ) {
            con = (Element) it.next();
            String url = con.attr("href");
            activitiesUrl.add(url);
        }
    }

    private void crawlerAllUrls(ArrayList<String> activitiesUrl) throws IOException {
        String https = "https://www.travel.taipei";
        for (int i = 0; i < activitiesUrl.size(); i++) {
            Document document = Jsoup.connect(https + activitiesUrl.get(i)).get();
            Elements test = document.select("h2.unit-title icon-unit-event");
            System.out.println(test.html());
        }
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public String getSource() {
        return SOURCE;
    }

    public static void main(String[] args) {

        Crawler crawler = new TaipeiTravelCrawler();
        crawler.crawl(new MockActivityRepository(), new Logger("log1.txt", "err1.txt"));

    }

}
