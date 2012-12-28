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

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.EXistRepositoryBuilder;
import org.exist.dom.QName;
import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;
import org.exist.xquery.ErrorCodes.ErrorCode;

/**
 * XQuery module to work with git repository.
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 * 
 */
public class Module extends AbstractInternalModule {

	public final static String NAMESPACE_URI = "http://exist-db.org/git";
	public final static String PREFIX = "git";
	private final static String RELEASED_IN_VERSION = "eXist-2.0";
	private final static String DESCRIPTION = "Module for interacting with the git repository.";
	
	static {
		Git.setRepositoryBuilder(new EXistRepositoryBuilder());
	}

    public static ErrorCode EXGIT001 = new DebugErrorCode("EXGIT001", "Git command error.");

    public static class DebugErrorCode extends ErrorCode {
        private DebugErrorCode(String code, String description) {
            super(new QName(code, NAMESPACE_URI, PREFIX), description);
        }
    }

    private final static FunctionDef[] functions = {
		new FunctionDef(Create.signatures[0], Create.class),
        new FunctionDef(Clone.signatures[0], Clone.class),

        new FunctionDef(Add.signatures[0], Add.class),
        new FunctionDef(Commit.signatures[0], Commit.class),
        new FunctionDef(Tag.signatures[0], Tag.class),

        new FunctionDef(Checkout.signatures[0], Checkout.class),
        new FunctionDef(Merge.signatures[0], Merge.class),

        new FunctionDef(Push.signatures[0], Push.class),
        new FunctionDef(Pull.signatures[0], Pull.class),
        
        new FunctionDef(BranchCreate.signatures[0], BranchCreate.class),
        new FunctionDef(BranchDelete.signatures[0], BranchDelete.class),
        new FunctionDef(BranchList.signatures[0], BranchList.class),
        new FunctionDef(BranchRename.signatures[0], BranchRename.class),

        new FunctionDef(Log.signatures[0], Log.class),
        //new FunctionDef(Diff.signatures[0], Create.class),

		new FunctionDef(Reset.signatures[0], Reset.class)
	};

	public Module(Map<String, List<? extends Object>> parameters) {
		super(functions, parameters);
	}

	@Override
	public String getNamespaceURI() {
		return NAMESPACE_URI;
	}

	@Override
	public String getDefaultPrefix() {
		return PREFIX;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getReleaseVersion() {
		return RELEASED_IN_VERSION;
	}
}