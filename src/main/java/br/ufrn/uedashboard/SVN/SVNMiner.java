package br.ufrn.uedashboard.SVN;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import br.ufrn.uedashboard.model.Commit;
import br.ufrn.uedashboard.model.Modification;

public class SVNMiner {

	private SVNConnector connector;

	public SVNMiner(SVNConnector connector) {
		this.connector = connector;
	}

	public List<Commit> getCommits(Date startDate, Date endDate) {

		List<Commit> commits = new LinkedList<Commit>();

		SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		svnOperationFactory.setAuthenticationManager(connector.getEncapsulation().getAuthenticationManager());

		try {
			SvnLog log = svnOperationFactory.createLog();
			log.addTarget(SvnTarget.fromURL(SVNURL.parseURIEncoded(connector.getUrl())));
			log.addRange(SvnRevisionRange.create(SVNRevision.create(startDate), SVNRevision.create(endDate)));
			log.setDiscoverChangedPaths(true);
			log.setDepth(SVNDepth.INFINITY);

			ArrayList<SVNLogEntry> svnLogEntries = new ArrayList<SVNLogEntry>();
			log.run(svnLogEntries);

			for (SVNLogEntry svnLogEntry : svnLogEntries) {
				if (svnLogEntry.getDate().after(startDate) && svnLogEntry.getDate().before(endDate)) {
					
					Commit commit = new Commit();
					commit.setDeveloper(svnLogEntry.getAuthor());
					commit.setComment(svnLogEntry.getMessage());
					commit.setDate(svnLogEntry.getDate());
					commit.setRevision(svnLogEntry.getRevision());
					
					List<Modification> modifications = new ArrayList<Modification>();

					Collection<SVNLogEntryPath> changedPaths = svnLogEntry.getChangedPaths().values();

					for (SVNLogEntryPath svnLogEntryPath : changedPaths) {
						Modification modification = new Modification();
						modification.setPath(svnLogEntryPath.getPath());
						modification.setType(String.valueOf(svnLogEntryPath.getType()));

						modifications.add(modification);
					}

					commit.setModifications(modifications);
					commits.add(commit);
				}
			}
		} catch (SVNException e) {
			e.printStackTrace();
		} finally {
			svnOperationFactory.dispose();
		}

		return commits;
	}
	
	@SuppressWarnings("rawtypes")
	public List<Commit> getCommitsAfterRevision(long revisionNumber) {
		List<Commit> commits = new ArrayList<Commit>();

		DAVRepositoryFactory.setup();

        long startRevision = revisionNumber;
        long endRevision = -1; //HEAD (the latest) revision

        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(connector.getUrl()));
            ISVNAuthenticationManager authManager = connector.getEncapsulation().getAuthenticationManager();
            repository.setAuthenticationManager(authManager);

			Collection svnLogEntries = new ArrayList<SVNLogEntry>();
            
            svnLogEntries = repository.log(new String[] {""}, null, startRevision, endRevision, true, true);
            
            for (Iterator entries = svnLogEntries.iterator(); entries.hasNext();) {
                SVNLogEntry svnLogEntry = (SVNLogEntry) entries.next();
                
                Commit commit = new Commit();
				commit.setDeveloper(svnLogEntry.getAuthor());
				commit.setComment(svnLogEntry.getMessage());
				commit.setDate(svnLogEntry.getDate());
				commit.setRevision(svnLogEntry.getRevision());
				
				List<Modification> modifications = new ArrayList<Modification>();

				Collection<SVNLogEntryPath> changedPaths = svnLogEntry.getChangedPaths().values();

				for (SVNLogEntryPath svnLogEntryPath : changedPaths) {
					Modification modification = new Modification();
					modification.setPath(svnLogEntryPath.getPath());
					modification.setType(String.valueOf(svnLogEntryPath.getType()));

					modifications.add(modification);
				}

				commit.setModifications(modifications);
				commits.add(commit);
            }
        } catch (SVNException e) {
			e.printStackTrace();
		}
        
        return commits;
	}
	
	public List<String> getRevision(String path, long revisionNumber) {
		List<SVNFileRevision> revisions = new ArrayList<SVNFileRevision>();
		List<String> currentPreviousRevision = null;

		try {
			connector.getEncapsulation().getFileRevisions(path, revisions, 1, revisionNumber);
			
			if (revisions.size() > 1) {
				SVNFileRevision revision = revisions.get(revisions.size() - 1);
				SVNFileRevision previousRevision = revisions.get(revisions.size() - 2);
				
				ByteArrayOutputStream revisionOutput = new ByteArrayOutputStream();
				ByteArrayOutputStream prevRevisionOutput = new ByteArrayOutputStream();
	
				connector.getEncapsulation().getFile(revision.getPath(), revision.getRevision(), new SVNProperties(), revisionOutput);
				String revisionContent = revisionOutput.toString();
				
				connector.getEncapsulation().getFile(previousRevision.getPath(), previousRevision.getRevision(), new SVNProperties(), prevRevisionOutput);
				String prevRevisionContent = prevRevisionOutput.toString();
	
				currentPreviousRevision = new LinkedList<String>(); 
				currentPreviousRevision.add(revisionContent);
				currentPreviousRevision.add(prevRevisionContent);
			}
			
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		return currentPreviousRevision;
	}

	@SuppressWarnings("unused")
	private List<SVNFileRevision> getAllRevisionsFromFile(String filePath, Long currentRevision) {
		List<SVNFileRevision> revisions = new ArrayList<SVNFileRevision>();

		try {
			connector.getEncapsulation().getFileRevisions(filePath, revisions, 1, currentRevision);
			for (SVNFileRevision revision : revisions) {
				ByteArrayOutputStream revisionOutput = new ByteArrayOutputStream();
				connector.getEncapsulation().getFile(revision.getPath(), revision.getRevision(), new SVNProperties(), revisionOutput);
				System.out.println(revisionOutput.toString());
			}
		} catch (SVNException e) {
			e.printStackTrace();
		}

		return revisions;
	}
}
