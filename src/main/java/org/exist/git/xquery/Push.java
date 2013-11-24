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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.exist.jgit.transport.UsernamePasswordCredentialsProvider;
import org.exist.dom.QName;
import org.exist.memtree.MemTreeBuilder;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Push extends BasicFunction {

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			new QName("push", Module.NAMESPACE_URI, Module.PREFIX), 
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

    private final static QName PUSH = new QName("push", NAMESPACE_URI, PREFIX);
    private final static QName RESULT = new QName("result", NAMESPACE_URI, PREFIX);
    
    private final static QName REMOTE_REF_UPDATE = new QName("remoteRefUpdate", NAMESPACE_URI, PREFIX);
    private final static QName REMOTE_NAME = new QName("remoteName", NAMESPACE_URI, PREFIX);
    private final static QName STATUS = new QName("status", NAMESPACE_URI, PREFIX);
    private final static QName EXPECTED_OLD_OBJECTID = new QName("expectedOldObjectId", NAMESPACE_URI, PREFIX);
    private final static QName NEW_OBJECTID = new QName("newObjectId", NAMESPACE_URI, PREFIX);
    private final static QName FAST_FORWARD = new QName("fastForward", NAMESPACE_URI, PREFIX);
    private final static QName FORCE_UPDATE = new QName("forceUpdate", NAMESPACE_URI, PREFIX);

    private final static QName TRACKING_REF_UPDATE = new QName("trackingRefUpdate", NAMESPACE_URI, PREFIX);
    private final static QName LOCAL_NAME = new QName("localName", NAMESPACE_URI, PREFIX);
    private final static QName OLD_OBJECTID = new QName("oldObjectId", NAMESPACE_URI, PREFIX);

    public Push(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
            String localPath = args[0].getStringValue();
            if (!(localPath.endsWith("/")))
                localPath += File.separator;

	        Git git = Git.open(new Resource(localPath), FS);
		    
	        Iterable<PushResult> answer = git.push()
               .setCredentialsProvider(
                   new UsernamePasswordCredentialsProvider(
                       args[1].getStringValue(), 
                       args[2].getStringValue()
                   )
               )
	           .call();

            MemTreeBuilder builder = getContext().getDocumentBuilder();
            
            int nodeNr = builder.startElement(PUSH, null);
            
            for (PushResult push : answer) {
                
                builder.startElement(RESULT, null);
                
                for (TrackingRefUpdate tracking : push.getTrackingRefUpdates()) {
                    
                    builder.startElement(TRACKING_REF_UPDATE, null);
                    
                    builder.addAttribute(REMOTE_NAME, tracking.getRemoteName());
                    builder.addAttribute(LOCAL_NAME, tracking.getLocalName());

                    //builder.addAttribute(FORCE_UPDATE, Boolean.toString(tracking.forceUpdate));

                    builder.addAttribute(OLD_OBJECTID, tracking.getOldObjectId().name());
                    builder.addAttribute(NEW_OBJECTID, tracking.getNewObjectId().name());
                }

                for (RemoteRefUpdate remote : push.getRemoteUpdates()) {

                    builder.startElement(REMOTE_REF_UPDATE, null);
                    
                    builder.addAttribute(REMOTE_NAME, remote.getRemoteName());
                    builder.addAttribute(STATUS, remote.getStatus().name());

                    if (remote.isExpectingOldObjectId())
                        builder.addAttribute(EXPECTED_OLD_OBJECTID, remote.getExpectedOldObjectId().name());
                    
                    if (remote.getNewObjectId() != null)
                        builder.addAttribute(NEW_OBJECTID, remote.getNewObjectId().name());

                    builder.addAttribute(FAST_FORWARD, Boolean.toString(remote.isFastForward()));
                    builder.addAttribute(FORCE_UPDATE, Boolean.toString(remote.isForceUpdate()));

                    if (remote.getMessage() != null)
                        builder.characters(remote.getMessage());
                    
                    builder.endElement();
                }
                builder.endElement();
            }
	        
            builder.endElement();

            return builder.getDocument().getNode(nodeNr);
		} catch (Throwable e) {
            e.printStackTrace();
			throw new XPathException(this, Module.EXGIT001, e);
		}
	}
}