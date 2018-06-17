package container.crawler;

import container.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class TaoYuanTravelCrawler implements Crawler {
    private static final String ACTIVITYURL = "https://travel.tycg.gov.tw/zh-tw/event/calendar/";
    private static final String SOURCE = "桃園觀光導覽網";
    private int activitiesYear = 2017;
    private ArrayList<Activity> activities = new ArrayList<>();
    private int id = 0;

    @Override
    public void crawl(ActivityRepository activityRepository, Logger logger) {
        do {
            String url = ACTIVITYURL + activitiesYear;
            try {
                Document document = Jsoup.connect(url).get();
                System.out.println(document.title());
                Elements seasons = document.select("div.season-event-info-blk").select("a.link");

                for(int i = 4; i < seasons.size(); i++){
                    Element season = seasons.get(i);
                    Elements activitiesHref = season.select("a.link");
                    for(Element element : activitiesHref){
                        String href = element.attr("href");
                        String link = "https://travel.tycg.gov.tw" + href;
                        activities.add(getActivity(link));
                        System.out.println(activities.size());
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            activitiesYear++;
        } while (activitiesYear != 2019);

        for(Activity activity : activities)
            activityRepository.createActivity(activity);

    }

    private Activity getActivity(String link) throws IOException {
        Document document = Jsoup.connect(link).get();
        String title = document.select("div.unit-title-blk").select("h2.unit-title").html();
        String activityDate = document.select("time.shine").html();
        String address = document.select("a.btn-location").attr("href");
        String updatedDate = document.select("p.date").first().html();
        String content = document.select("div.manual-script-blk").html();
        Date activityStartDate;
        Date activityEndDate;
        Date activityUpdatedDate;
        String[] addresses = address.split(" ");

        if (isSingleDate(activityDate)) {
            activityStartDate = convertToDate(activityDate.substring(0, 10));
            activityEndDate = convertToDate(activityDate.substring(0, 10));
        } else {
            activityStartDate = convertToDate(activityDate.substring(0, 10));
            activityEndDate = convertToDate(activityDate.substring(11, 21));
        }
        activityUpdatedDate = convertToDate(updatedDate.substring(3, 13));

        id++;

        return new Activity(id, title, activityStartDate, activityEndDate, activityUpdatedDate, content, SOURCE, link, addresses[1], null);
    }

    public boolean isSingleDate(String activityDate){
        String[] date = activityDate.split("");
        for (int i = 0; i < date.length; i++)
            if (date[i].equals("~") && i == 10)
                return false;
        return true;
    }

    private Date convertToDate(String date) {
        return new Date(Integer.parseInt(date.substring(0, 4)) - 1900, Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
    }

    @Override
    public String getSource() {
        return SOURCE;
    }

    public static void main(String[] args) {
        Crawler crawler = new TaoYuanTravelCrawler();
        crawler.crawl(new JdbcProxy(new JdbcActivityRepository()), new Logger("log1.txt", "err1.txt"));
    }

}
