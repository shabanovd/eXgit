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
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.merge.ResolveMerger.MergeFailureReason;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.exist.dom.QName;
import org.exist.memtree.MemTreeBuilder;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Pull extends BasicFunction {

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			new QName("pull", Module.NAMESPACE_URI, Module.PREFIX), 
			"", 
			new SequenceType[] { 
                new FunctionParameterSequenceType(
                    "localPath", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "Local path"
                ),
                new FunctionParameterSequenceType(
                    "username", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "Username"
                ),
                new FunctionParameterSequenceType(
                    "password", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "Password"
                )
			}, 
			new FunctionReturnSequenceType(
				Type.BOOLEAN, 
				Cardinality.EXACTLY_ONE, 
				"true if success, false otherwise"
			)
		)
	};

    private final static QName PULL = new QName("pull", NAMESPACE_URI, PREFIX);
    
    private final static QName MERGE = new QName("merge", NAMESPACE_URI, PREFIX);
    private final static QName REBASE = new QName("rebase", NAMESPACE_URI, PREFIX);

    private final static QName CHECKOUT_CONFLICT = new QName("checkoutConflict", NAMESPACE_URI, PREFIX);

    private final static QName COMMIT = new QName("commit", NAMESPACE_URI, PREFIX);
    private final static QName ID = new QName("id", NAMESPACE_URI, PREFIX);
    private final static QName STATUS = new QName("status", NAMESPACE_URI, PREFIX);
    private final static QName IS_SUCCESSFUL = new QName("isSuccessful", NAMESPACE_URI, PREFIX);
    
    private final static QName FAILING_PATH = new QName("failingPath", NAMESPACE_URI, PREFIX);
    private final static QName PATH = new QName("path", NAMESPACE_URI, PREFIX);
    private final static QName REASON = new QName("reason", NAMESPACE_URI, PREFIX);

    public Pull(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
            String localPath = args[0].getStringValue();
            if (!(localPath.endsWith("/")))
                localPath += File.separator;

	        Git git = Git.open(new Resource(localPath), FS);
		    
	        PullResult answer = git.pull()
               .setCredentialsProvider(
                   new UsernamePasswordCredentialsProvider(
                       args[1].getStringValue(), 
                       args[2].getStringValue()
                   )
               )
	           .call();
	        
            MemTreeBuilder builder = getContext().getDocumentBuilder();
            
            int nodeNr = builder.startElement(PULL, null);
            builder.addAttribute(IS_SUCCESSFUL, Boolean.toString( answer.isSuccessful() ));
            
            MergeResult merge = answer.getMergeResult();
            
            if (merge != null) {
                builder.startElement(MERGE, null);
                builder.addAttribute(STATUS, merge.getMergeStatus().toString());
                builder.addAttribute(IS_SUCCESSFUL, Boolean.toString( merge.getMergeStatus().isSuccessful() ));
                
    	        for (ObjectId commit : merge.getMergedCommits()) {
    	            builder.startElement(COMMIT, null);
    	            
    	            builder.addAttribute(ID, commit.name());
    	            
    	            builder.endElement();
    	        }
                builder.endElement();
                
                if (merge.getConflicts() != null) {
                    for (Entry<String, int[][]> entry : merge.getConflicts().entrySet()) {
                        builder.startElement(CHECKOUT_CONFLICT, null);
                        builder.addAttribute(PATH, entry.getKey());
                        
                        builder.endElement();
                    }
                    
                }
                
                if (merge.getCheckoutConflicts() != null) {
                    for (String path : merge.getCheckoutConflicts()) {
                        builder.startElement(CHECKOUT_CONFLICT, null);
                        builder.addAttribute(PATH, path);
                        
                        builder.endElement();
                    }
                }


                if (merge.getFailingPaths() != null) {
                    for (Entry<String, MergeFailureReason> entry : merge.getFailingPaths().entrySet()) {
                        builder.startElement(FAILING_PATH, null);
                        builder.addAttribute(PATH, entry.getKey());
                        builder.addAttribute(REASON, entry.getValue().name());
            
                        builder.endElement();
                    }
                }
            }
            
            RebaseResult rebase = answer.getRebaseResult();

            if (rebase != null) {
                builder.startElement(REBASE, null);
                builder.addAttribute(STATUS, rebase.getStatus().toString());
                builder.addAttribute(IS_SUCCESSFUL, Boolean.toString( rebase.getStatus().isSuccessful() ));
                
                //rebase.getConflicts()
                
                if (rebase.getFailingPaths() != null) {
                    for (Entry<String, MergeFailureReason> entry : rebase.getFailingPaths().entrySet()) {
                        builder.startElement(FAILING_PATH, null);
                        builder.addAttribute(PATH, entry.getKey());
                        builder.addAttribute(REASON, entry.getValue().name());
            
                        builder.endElement();
                    }
                }
                builder.endElement();
            }

            return builder.getDocument().getNode(nodeNr);
		} catch (Throwable e) {
		    e.printStackTrace();
			throw new XPathException(this, Module.EXGIT001, e);
		}
	}
}