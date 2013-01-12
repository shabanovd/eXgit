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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.FS_eXistdb;
import org.exist.dom.QName;
import org.exist.memtree.MemTreeBuilder;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;
import org.xml.sax.helpers.AttributesImpl;

import static org.exist.git.xquery.Module.*;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Log extends BasicFunction {

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			new QName("log", NAMESPACE_URI, PREFIX), 
			"", 
			new SequenceType[] { 
                new FunctionParameterSequenceType(
                    "localPath", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "Local path"
                )
			}, 
			new FunctionReturnSequenceType(
				Type.NODE, 
				Cardinality.ZERO_OR_MORE, 
				"Logs"
			)
		)
	};

	public Log(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
            String localPath = args[0].getStringValue();
            if (!(localPath.endsWith("/")))
                localPath += File.separator;

//	        Repository localRepo = new FileRepository(localPath + ".git");
//	        Git git = new Git(localRepo);
	        
	        Git git = Git.open(new Resource(localPath), new FS_eXistdb());

            MemTreeBuilder builder = context.getDocumentBuilder();

            AttributesImpl attribs = new AttributesImpl();
            attribs.addAttribute(NAMESPACE_URI, 
                "local-path", PREFIX+":local-path", 
                "CDATA", localPath);
            
            int nodeNr = builder.startElement(LOG_ELEMENT, attribs);
            
            for (RevCommit commit : git.log().call()) {
//                commit.getParentCount();
//                commit.getParents();
                
                attribs = new AttributesImpl();
                attribs.addAttribute(NAMESPACE_URI, 
                    "id", PREFIX+":id", 
                    "CDATA", commit.name());

                attribs.addAttribute(NAMESPACE_URI, 
                        "time", PREFIX+":time", 
                        "CDATA", String.valueOf( commit.getCommitTime() ));

                builder.startElement(COMMIT_ELEMENT, attribs);

                PersonIdent authorIdent = commit.getAuthorIdent();
                builder.startElement(AUTHOR_ELEMENT, null);
                    builder.startElement(AUTHOR_NAME_ELEMENT, null);
                    builder.characters(authorIdent.getName());
                    builder.endElement();
                    builder.startElement(AUTHOR_EMAIL_ELEMENT, null);
                    builder.characters(authorIdent.getEmailAddress());
                    builder.endElement();
                builder.endElement();

                builder.startElement(MESSAGE_ELEMENT, null);
                builder.characters(commit.getFullMessage());
                builder.endElement();
                
                builder.endElement();
	        }

            builder.endElement();

	        return builder.getDocument().getNode(nodeNr);
		} catch (Throwable e) {
			throw new XPathException(this, Module.EXGIT001, e);
		}
	}
	
    private final static QName LOG_ELEMENT = new QName("log", NAMESPACE_URI, PREFIX);
    private final static QName COMMIT_ELEMENT = new QName("commit", NAMESPACE_URI, PREFIX);
    private final static QName AUTHOR_ELEMENT = new QName("author", NAMESPACE_URI, PREFIX);
    private final static QName AUTHOR_NAME_ELEMENT = new QName("name", NAMESPACE_URI, PREFIX);
    private final static QName AUTHOR_EMAIL_ELEMENT = new QName("email", NAMESPACE_URI, PREFIX);
	private final static QName MESSAGE_ELEMENT = new QName("message", NAMESPACE_URI, PREFIX);
}