package container.crawler;

import container.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaipeiTravelCrawler implements Crawler {

    private final static String ACTIVITIES_URL = "https://www.travel.taipei/zh-tw/activity";
    private final static String SOURCE = "台北旅遊網";
    private List<String> activityLinks = new ArrayList<>();
    private List<String> pageLinks = new ArrayList<>();
    private int pageNum = 2;
    private int id = 0;

    @Override
    public void crawl(ActivityRepository activityRepository, Logger logger) {
        try {
            Document doc = Jsoup.connect(ACTIVITIES_URL).get();
            logger.log(getClass(), doc.title());
            //台北旅遊網活動的最後一頁
            String lastPage = doc.select("a.last-page").attr("href");
            logger.log(getClass(), "台北旅遊網活動的最後一頁: " + lastPage);

            addAllPageLinks(lastPage);
            pageLinks.parallelStream().forEach(this::addAllActivityLinks);
            System.out.println(activityLinks.size());
            activityLinks.parallelStream().forEach(String -> {
                try {
                    activityRepository.createActivity(getActivity(String));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            logger.log(getClass(), "台北旅遊網的活動總數量: " + activityRepository.getActivities().size());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addAllPageLinks(String lastPage) {
        pageLinks.add("https://www.travel.taipei/zh-tw/activity");
        do {
            pageLinks.add(ACTIVITIES_URL + "?page=" + pageNum);
            pageNum++;
        } while (!lastPage.equals("/zh-tw/activity?page=" + (pageNum - 1)));
    }

    private void addAllActivityLinks(String string) {
        try {
            Elements elements = Jsoup.connect(string).get().select("div.info-card-item").select("a.link");
            for (Element element : elements)
                activityLinks.add("https://www.travel.taipei" + element.attr("href"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Activity getActivity(String link) throws IOException {
        Document document = Jsoup.connect(link).get();
        String title = document.select("div.unit-title-blk").select("h2.unit-title").html();
        if (title.equals(""))
            return new Activity(id, "404 Not found", null, null, null, null, null, null, null, null);
//        logger.log(getClass(), "activity title: " + title);
            //拿到活動的開始結束日期
        else {
            String activityDate = document.select("dd.info").first().html();
            //拿到活動地址
            String address = document.select("a.btn-location-link").attr("title");
            if (address.equals(""))
                address = document.select("dl.event-info-list").select("dd.info").last().html();

            //拿到更新日期所在的欄位的所有日期
            String allUpdatedTableDate = document.select("p.date").first().html();

            //拿到活動內文
            String content = document.select("div.manual-script-blk").html();

            Date activityStartDate;
            Date activityEndDate;
            Date activityUpdatedDate;

            if (!isInteger(activityDate)) {
                activityStartDate = null;
                activityEndDate = null;
            } else if (isSingleDate(activityDate)) {
                activityStartDate = convertStringToDate(activityDate.substring(0, 10));
                activityEndDate = convertStringToDate(activityDate.substring(0, 10));
            } else {
                activityStartDate = convertStringToDate(activityDate.substring(0, 10));
                activityEndDate = convertStringToDate(activityDate.substring(11, 21));
            }
            activityUpdatedDate = convertStringToDate(allUpdatedTableDate.substring(25, 35));

            id++;
            return new Activity(id, title, activityStartDate, activityEndDate, activityUpdatedDate, content, SOURCE, link, address, null);
        }
    }

    private boolean isInteger(String activityDate) {
        try {
            Integer.parseInt(activityDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Date convertStringToDate(String date) {
        return new Date(Integer.parseInt(date.substring(0, 4)) - 1900, Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
    }

    private boolean isSingleDate(String activityDate) {
        String[] date = activityDate.split("");
        for (int i = 0; i < date.length; i++)
            if (date[i].equals("~") && i == 10)
                return false;
        return true;
    }

    @Override
    public String getSource() {
        return SOURCE;
    }

    public static void main(String[] args) {
        Crawler crawler = new TaipeiTravelCrawler();
        crawler.crawl(new JdbcProxy(new JdbcActivityRepository()), new Logger("log.txt", "err.txt"));
//        crawler.crawl(new MockActivityRepository(), new Logger("log1.txt", "err1.txt"));
    }

}
