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
package org.eclipse.jgit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.eclipse.jgit.util.FS;
import org.exist.Database;
import org.exist.collections.Collection;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.util.Configuration;
import org.exist.util.io.Resource;
import org.exist.util.io.ResourceInputStream;
import org.exist.util.io.ResourceOutputStream;
import org.exist.util.io.ResourceRandomAccess;
import org.exist.xmldb.XmldbURI;

/**
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 *
 */
public class FS_eXistdb extends FS {
	
//	static {
//		Transport.register(org.eclipse.jgit.transport.TransportEXist.PROTO_LOCAL);
//	}
	
	public FS_eXistdb() {
		String conFile = System.getProperty("eXistDB-config");
		if (conFile != null) {
			startDB(conFile);
		}
	}
	
    private void startDB(String file) {
        try {
            Configuration config = new Configuration(file);
            BrokerPool.configure(1, 5, config);
            Database db = BrokerPool.getInstance();
            
            DBBroker broker = db.get(db.getSecurityManager().getSystemSubject());
            try {
            	Collection root = broker.getCollection(XmldbURI.DB);
            	root.getPermissions().setMode(0777);
    			broker.saveCollection(null, root);

            } finally {
            	db.release(broker);
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

//    private void stopDB() {
//    	try {
//    		BrokerPool.stopAll(false);
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
//    }    

	@Override
	public FS newInstance() {
		return new FS_eXistdb();
	}

	public File resolve(String name) {
		return new Resource(name);
	}

	public File resolve(final File dir, final String name) {
//		System.out.println("FS_eXistdb "+dir+" ~ "+name);
		
		final File abspn = new Resource(name);
		if (abspn.isAbsolute())
			return abspn;
		return new Resource(dir, name);
	}

	@Override
	public boolean supportsExecute() {
		return true;
	}

	@Override
	public boolean isCaseSensitive() {
		return true;
	}

	@Override
	public boolean canExecute(File f) {
		return f.canExecute();
	}

	@Override
	public boolean setExecute(File f, boolean canExec) {
		f.setExecutable(canExec);

		return true;
	}

	@Override
	public boolean retryFailedLockFileCommit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected File discoverGitPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessBuilder runInShell(String cmd, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public File userHome() {
		//XXX: solution?
		return new Resource("/db");
	}

	public FileOutputStream fileOutputStream(File file) throws FileNotFoundException {
//		System.out.println("fileOutputStream "+file);
		return new ResourceOutputStream((Resource)file);
	}
	
	public FileOutputStream fileOutputStream(File file, boolean append)
			throws FileNotFoundException {
//		System.out.println("fileOutputStream ("+append+") "+file);
		return new ResourceOutputStream((Resource)file, append);
	}
	
	public File createTempFile(String prefix, String suffix, File directory) throws IOException {
		return Resource.createTempFile(prefix, suffix, directory);
	}
	
	public RandomAccessFile randomAccessFile(File file, String mode) throws FileNotFoundException {
//		System.out.println("randomAccessFile ("+mode+") "+file);
		return new ResourceRandomAccess((Resource)file, mode);
	}
	
	public FileInputStream fileInputStream(File file) throws FileNotFoundException {
//		System.out.println("fileInputStream "+file);
		return new ResourceInputStream((Resource)file);
	}

	public BufferedReader getBufferedReader(File f) throws FileNotFoundException {
//		System.out.println("getBufferedReader "+f);
		try {
			return ((Resource)f).getBufferedReader();
		} catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}
}