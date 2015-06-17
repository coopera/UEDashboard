package br.ufrn.uedashboard.dao;

import br.ufrn.uedashboard.model.Modification;

public interface ModificationDAO {
	
	public void saveModification(Modification modification, int id, int id_commit);

}
