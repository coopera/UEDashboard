package br.ufrn.uedashboard.collector;

public class DataCollectorChain {
	
	private UnusualEventsDataCollector unusualEventsDataCollector;
	
	public DataCollectorChain() {
		UnusualEventsDataCollector unusualEventTimeBetweenCommits = new TimeBetweenCommits();
		UnusualEventsDataCollector unusualEventDevelopersFileModification = new DevelopersFileModification();
		UnusualEventsDataCollector unusualEventTimeBetweenFileModification = new TimeBetweenFileModification();
		UnusualEventsDataCollector unusualEventCodeModifications = new CodeModifications();
		UnusualEventsDataCollector unusualEventFileModificationsCommit = new FilesTouchedCommit();
		UnusualEventsDataCollector unusualEventFileChanges = new FileChanges();
		
		this.unusualEventsDataCollector = unusualEventTimeBetweenCommits;
		unusualEventTimeBetweenCommits.setSuccessor(unusualEventDevelopersFileModification);
		unusualEventDevelopersFileModification.setSuccessor(unusualEventTimeBetweenFileModification);
		unusualEventTimeBetweenFileModification.setSuccessor(unusualEventCodeModifications);
		unusualEventCodeModifications.setSuccessor(unusualEventFileModificationsCommit);
		unusualEventFileModificationsCommit.setSuccessor(unusualEventFileChanges);
	}

	public UnusualEventsDataCollector getUnusualEventsDataCollector() {
		return unusualEventsDataCollector;
	}

}
