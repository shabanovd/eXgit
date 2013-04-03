/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012-2013 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.git.xquery;

import static org.exist.git.xquery.Module.FS;
import static org.exist.git.xquery.Module.NAMESPACE_URI;
import static org.exist.git.xquery.Module.PREFIX;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.IndexDiffFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.SkipWorkTreeFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.exist.dom.QName;
import org.exist.memtree.MemTreeBuilder;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;
import org.xml.sax.helpers.AttributesImpl;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Status extends BasicFunction {

	private static final QName STATUS = new QName("status", Module.NAMESPACE_URI, Module.PREFIX);

	private static final QName UNTRACKED = new QName("status-untracked", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName ADDED = new QName("status-added", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName CHANGED = new QName("status-changed", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName CONFLICTING = new QName("status-conflicting", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName IGNORED = new QName("status-ignored", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName MISSING = new QName("status-missing", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName MODIFIED = new QName("status-modified", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName REMOVED = new QName("status-removed", Module.NAMESPACE_URI, Module.PREFIX);
	private static final QName UNTRACKED_FOLDERS = new QName("status-untracked-folders", Module.NAMESPACE_URI, Module.PREFIX);
	
	private static final SequenceType[] PARAMS = 
		new SequenceType[] { 
	        new FunctionParameterSequenceType(
	            "localPath", 
	            Type.STRING, 
	            Cardinality.EXACTLY_ONE, 
	            "Local path"
	        )
		};
	
	private static final SequenceType[] _PARAMS = 
		new SequenceType[] { 
	        new FunctionParameterSequenceType(
	            "localPath", 
	            Type.STRING, 
	            Cardinality.EXACTLY_ONE, 
	            "Local path"
	        ),
	        new FunctionParameterSequenceType(
	            "gitPath", 
	            Type.STRING, 
	            Cardinality.EXACTLY_ONE, 
	            "Repository path"
	        ),
	        new FunctionParameterSequenceType(
	            "recursive", 
	            Type.BOOLEAN, 
	            Cardinality.EXACTLY_ONE, 
	            "Recursive"
	        )
		};

	private static final FunctionReturnSequenceType RETURN = 
		new FunctionReturnSequenceType(
			Type.STRING, 
			Cardinality.ZERO_OR_MORE, 
			""
		);

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			STATUS, 
			"", 
			_PARAMS, RETURN
		),
		new FunctionSignature(
			UNTRACKED, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			ADDED, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			CHANGED, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			CONFLICTING, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			IGNORED, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			MISSING, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			MODIFIED, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			REMOVED, 
			"", 
			PARAMS, RETURN
		),
		new FunctionSignature(
			UNTRACKED_FOLDERS, 
			"", 
			PARAMS, RETURN
		),
	};

	public Status(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
            String localPath = args[0].getStringValue();
            if (!(localPath.endsWith("/")))
                localPath += File.separator;

	        Git git = Git.open(new Resource(localPath), FS);
	        
	        if (getName().equals(STATUS)) {
	        	MemTreeBuilder builder = getContext().getDocumentBuilder();
	        	
	            int nodeNr = builder.startElement(REPOSITORY, null);
	            StatusBuilder statusBuilder = new StatusBuilder(builder);
	            
	            final Repository repo = git.getRepository();
	            diff(repo, Constants.HEAD, new FileTreeIterator(repo), statusBuilder, args[1].getStringValue(), args[2].effectiveBooleanValue());
	        	
	            builder.endElement();

		        return builder.getDocument().getNode(nodeNr);
	        }
	        
	        org.eclipse.jgit.api.Status status = git.status().call();
	        
	        Set<String> list;
	        
	        if (getName().equals(UNTRACKED)) {
	        	list = status.getUntracked();
	        
	        } else if (getName().equals(ADDED)) {
	        	list = status.getAdded();
	        
	        } else if (getName().equals(CHANGED)) {
	        	list = status.getChanged();
	        
	        } else if (getName().equals(CONFLICTING)) {
	        	list = status.getConflicting();
	        
	        } else if (getName().equals(IGNORED)) {
	        	list = status.getIgnoredNotInIndex();
	        
	        } else if (getName().equals(MISSING)) {
	        	list = status.getMissing();
	        
	        } else if (getName().equals(MODIFIED)) {
	        	list = status.getModified();
	        
	        } else if (getName().equals(REMOVED)) {
	        	list = status.getRemoved();

	        } else if (getName().equals(UNTRACKED)) {
	        	list = status.getUntracked();

	        } else if (getName().equals(UNTRACKED_FOLDERS)) {
	        	list = status.getUntrackedFolders();

	        } else {
	        	return Sequence.EMPTY_SEQUENCE;
	        }

    		Sequence result = new ValueSequence();
	        for (String modified : list) {
	            result.add(new StringValue(modified));
	        }

	        return result;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new XPathException(this, Module.EXGIT001, e);
		}
	}
	
    private final static QName REPOSITORY = new QName("repository", NAMESPACE_URI, PREFIX);
    private final static QName COLLECTION = new QName("collection", NAMESPACE_URI, PREFIX);
    private final static QName RESOURCE = new QName("resource", NAMESPACE_URI, PREFIX);
	
	class StatusBuilder {
		MemTreeBuilder builder;
		
		public StatusBuilder(MemTreeBuilder _builder) {
			builder = _builder;
		}
		
		private void element(QName element, String status, String path) {
            AttributesImpl attribs = new AttributesImpl();
            attribs.addAttribute(NAMESPACE_URI, 
                "status", PREFIX+":status", 
                "CDATA", status);

            attribs.addAttribute(NAMESPACE_URI, 
                    "path", PREFIX+":path", 
                    "CDATA", path);

            builder.startElement(element, attribs);
            builder.endElement();
			
		}

		private void collection(String status, String path) {
			element(COLLECTION, status, path);
		}

		private void resource(String status, String path) {
			element(RESOURCE, status, path);
		}

		public void conflict(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("conflict", path);
			} else {
				resource("conflict", path);
			}
		}

		public void changed(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("changed", path);
			} else {
				resource("changed", path);
			}
		}

		public void unchanged(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("unchanged", path);
			} else {
				resource("unchanged", path);
			}
		}

		public void removed(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("removed", path);
			} else {
				resource("removed", path);
			}
		}

		public void untracked(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("untracked", path);
			} else {
				resource("untracked", path);
			}
		}

		public void added(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("added", path);
			} else {
				resource("added", path);
			}
		}

		public void missing(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("missing", path);
			} else {
				resource("missing", path);
			}
		}

		public void modified(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("modified", path);
			} else {
				resource("modified", path);
			}
		}

		public void ignored(FileMode fileMode, String path) {
			if (FileMode.TREE.equals(fileMode)) {
				collection("ignored", path);
			} else {
				resource("ignored", path);
			}
		}
	}
	
	private final static int TREE = 0;
	private final static int INDEX = 1;
	private final static int WORKDIR = 2;

	public void diff(final Repository repository, 
			final String revstr, final WorkingTreeIterator initialWorkingTreeIterator,
			final StatusBuilder builder, final String folder, boolean recursive)
			throws IOException {

		RevTree tree = null;

		ObjectId objectId = repository.resolve(revstr);
		if (objectId != null)
			tree = new RevWalk(repository).parseTree(objectId);
		else
			tree = null;

		PathFilter filter = folder == null || folder.isEmpty() ? null : PathFilter.create(folder);
		IndexDiffFilter indexDiffFilter;
		
		DirCache dirCache = repository.readDirCache();

		TreeWalk treeWalk = new TreeWalk(repository);
//		TreeWalk treeWalk = TreeWalk.forPath(repository, folder, tree);
		treeWalk.setRecursive(recursive);
		// add the trees (tree, dirchache, workdir)
		if (tree != null)
			treeWalk.addTree(tree);
		else
			treeWalk.addTree(new EmptyTreeIterator());
		treeWalk.addTree(new DirCacheIterator(dirCache));
		treeWalk.addTree(initialWorkingTreeIterator);
		Collection<TreeFilter> filters = new ArrayList<TreeFilter>(4);

		if (filter != null)
			filters.add(filter);
		filters.add(new SkipWorkTreeFilter(INDEX));
		indexDiffFilter = new IndexDiffFilter(INDEX, WORKDIR);
		filters.add(indexDiffFilter);
		treeWalk.setFilter(AndTreeFilter.create(filters));
		if (filter != null) {
			while (treeWalk.next()) {
				if (filter.include(treeWalk)) {
					if (filter.isDone(treeWalk)) {
						if (treeWalk.isSubtree()) {
							treeWalk.enterSubtree();
						}
						break;
					} else if (treeWalk.isSubtree()) {
						treeWalk.enterSubtree();
					}
				}
			}
		}
		
		while (treeWalk.next()) {
			AbstractTreeIterator treeIterator = treeWalk.getTree(TREE, AbstractTreeIterator.class);
			DirCacheIterator dirCacheIterator = treeWalk.getTree(INDEX, DirCacheIterator.class);
			WorkingTreeIterator workingTreeIterator = treeWalk.getTree(WORKDIR, WorkingTreeIterator.class);

			if (dirCacheIterator != null) {
				final DirCacheEntry dirCacheEntry = dirCacheIterator.getDirCacheEntry();
				if (dirCacheEntry != null && dirCacheEntry.getStage() > 0) {
					builder.conflict(treeWalk.getFileMode(2), treeWalk.getPathString());
					continue;
				}
			}

			if (treeIterator != null) {
				if (dirCacheIterator != null) {
					if (!treeIterator.idEqual(dirCacheIterator)
							|| treeIterator.getEntryRawMode()
							!= dirCacheIterator.getEntryRawMode()) {
						// in repo, in index, content diff => changed
						builder.changed(treeWalk.getFileMode(2), treeWalk.getPathString());
						continue;
					}
				} else {
					// in repo, not in index => removed
					builder.removed(treeWalk.getFileMode(2), treeWalk.getPathString());
					if (workingTreeIterator != null) //XXX: 2 statuses
						builder.untracked(treeWalk.getFileMode(2), treeWalk.getPathString());
					continue;
				}
			} else {
				if (dirCacheIterator != null) {
					// not in repo, in index => added
					builder.added(treeWalk.getFileMode(2), treeWalk.getPathString());
					continue;
				} else {
					// not in repo, not in index => untracked
					if (workingTreeIterator != null
							&& !workingTreeIterator.isEntryIgnored()) {
						builder.untracked(treeWalk.getFileMode(2), treeWalk.getPathString());
						continue;
					}
				}
			}

			if (dirCacheIterator != null) {
				if (workingTreeIterator == null) {
					// in index, not in workdir => missing
					builder.missing(treeWalk.getFileMode(2), treeWalk.getPathString());
					continue;
				} else {
					if (dirCacheIterator.getDirCacheEntry() == null) {
						//XXX: null on collections - to fix
//						builder.unchanged(treeWalk.getFileMode(2), treeWalk.getPathString());

					} else if (workingTreeIterator.isModified(
							dirCacheIterator.getDirCacheEntry(), true)) {
						// in index, in workdir, content differs => modified
						builder.modified(treeWalk.getFileMode(2), treeWalk.getPathString());
						continue;
					}
				}
			}
			builder.unchanged(treeWalk.getFileMode(2), treeWalk.getPathString());
		}

//		for (String path : indexDiffFilter.getUntrackedFolders()) {
//			builder.untrackedFolders(path);
//		}
//		
		for (String path : indexDiffFilter.getIgnoredPaths()) {
			//XXX: to fix FileMode
			builder.ignored(FileMode.REGULAR_FILE, path);
		}
	}
}