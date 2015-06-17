package br.ufrn.uedashboard.SVN;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SubversionConnectorFactory {

	public SVNConnector createConnector(String user, String password, String url) {
		SVNRepository connector = null;

		try {
			DAVRepositoryFactory.setup();
			connector = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(url));

			SVNConnector repository = new SVNConnector();
		    repository.setUrl(url);
		    repository.setUser(user);
		    repository.setPassword(password);
		    
			ISVNAuthenticationManager authManager = new MyDefaultSVNAuthenticationManager(null, false, repository.getUser(), repository.getPassword(), null, null);

			connector.setAuthenticationManager(authManager);
			repository.setEncapsulation(connector);

			return repository;
		} catch (SVNException svne) {
			svne.printStackTrace();
		}
		return null;
	}

}
