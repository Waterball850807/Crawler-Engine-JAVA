package container;

import java.util.List;

public interface ActivityRepository {
	public Activity getActivity(int id);
	public Activity createActivity(Activity activity);
	public List<Activity> getActivities();
}
