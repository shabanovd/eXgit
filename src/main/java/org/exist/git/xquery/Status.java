/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012 The eXist Project
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

import java.io.File;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.util.FS_eXistdb;
import org.exist.dom.QName;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Status extends BasicFunction {

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
	
	private static final FunctionReturnSequenceType RETURN = 
		new FunctionReturnSequenceType(
			Type.STRING, 
			Cardinality.ZERO_OR_MORE, 
			""
		);

	public final static FunctionSignature signatures[] = { 
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

	        Git git = Git.open(new Resource(localPath), new FS_eXistdb());
	        
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
			throw new XPathException(this, Module.EXGIT001, e);
		}
	}
}