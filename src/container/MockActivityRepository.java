package container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockActivityRepository implements ActivityRepository{
	private Logger logger = new Logger("mock-activities", "mock-activities-err");
	private Map<Integer, Activity> activities = new HashMap<>();
	
	@Override
	public Activity getActivity(int id) {
		logger.log(getClass(), "Get activity: " + id);
		return activities.get(id);
	}

	@Override
	public Activity createActivity(Activity activity) {
		logger.log(getClass(), "Create activity: " + activity);
		return activities.put(activity.getId(), activity);
	}

	@Override
	public List<Activity> getActivities() {
		logger.log(getClass(), "Get activities: count = " + activities.values().size());
		return new ArrayList<>(activities.values());
	}

}
