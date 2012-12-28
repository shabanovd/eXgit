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
package org.eclipse.jgit.storage.file;

import java.io.File;
import java.io.IOException;

import org.exist.util.io.Resource;

/**
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 *
 */
public class EXistRepository extends FileRepository {

    public EXistRepository(final Resource gitDir) throws IOException {
        this(new EXistRepositoryBuilder().setGitDir(gitDir).setup());
    }

    public EXistRepository(String gitDir) throws IOException {
        this(new Resource(gitDir));
    }

    public EXistRepository(final EXistRepositoryBuilder options) throws IOException {
        super(options);
    }
    
	public File newFile(File parent, String child) {
		return new Resource(parent, child);
	}
}
