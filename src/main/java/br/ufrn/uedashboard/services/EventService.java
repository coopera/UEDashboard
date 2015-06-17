package br.ufrn.uedashboard.services;

import br.ufrn.uedashboard.dao.EventDAO;
import br.ufrn.uedashboard.dao.EventMySQLDAO;
import br.ufrn.uedashboard.model.Event;

public class EventService {
	
	private static EventService instance;
	
	private EventService() { }
	
	public static EventService getEventService() {
		if (instance == null) {
			instance = new EventService();
		}
		return instance;
	}
	
	public void saveEvent(Event event) {
		EventDAO eventDAO = new EventMySQLDAO();
		eventDAO.saveEvent(event);
	}

}
