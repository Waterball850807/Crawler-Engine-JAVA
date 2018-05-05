package container;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;


public class TestJbdcRepository {
	private static final String TESTNAME = "TESTTESTTESTTESTTESTTESTTESTTEST~";
	private JdbcProxy ar = new JdbcProxy(new JdbcActivityRepository());
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testGet() {
		IntStream.range(0, 200).parallel().forEach(i -> {
			List<Activity> ats = ar.getActivities();
			System.out.println("(" + i + ") Activities get: " + ats.size());
		});
	}

	public void testLarglyCreate() {
		/* ����o�q���շ|�s�W200�������ʸ�ƨ��Ʈw���Ф��H�K���� */
		IntStream.range(0, 200).parallel().forEach(i -> 
			{
				Activity at = ar.createActivity(new Activity(TESTNAME, new Date(), null, new Date(), 
						"fsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsd", 
						"here", "www","adas", "0987"));
			}
		);
	}
}
