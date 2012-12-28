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

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.util.FS_eXistdb;
import org.exist.util.io.Resource;

/**
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 *
 */
public class EXistRepositoryBuilder extends BaseRepositoryBuilder<EXistRepositoryBuilder, EXistRepository> {

	public EXistRepositoryBuilder() {
		setFS(new FS_eXistdb());
	}
	
    @Override
    public EXistRepository build() throws IOException {
        EXistRepository repo = new EXistRepository(setup());
        if (isMustExist() && !repo.getObjectDatabase().exists())
            throw new RepositoryNotFoundException(getGitDir());
        return repo;
    }
    
	public EXistRepositoryBuilder setGitDir(File gitDir) {
		return super.setGitDir(new Resource(gitDir.getPath()));
	}
}
