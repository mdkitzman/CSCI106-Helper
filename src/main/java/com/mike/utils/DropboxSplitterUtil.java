package com.mike.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;


public class DropboxSplitterUtil implements IsHelperUtil {

	private final File zipFile; 
	
	public DropboxSplitterUtil(File zipFile){
		this.zipFile = zipFile;
	}
	
	@Override
	public void doWork(PrintStream output) {
		
		File tmp = null;
		
		if(zipFile.isFile() && zipFile.getName().endsWith(".zip")){
			String outdirPath = zipFile.getPath().substring(0, zipFile.getPath().length() - 4);
			unzip(zipFile.getPath(), outdirPath);
			tmp = new File(outdirPath);
		} else {
			tmp = zipFile;
		}
		
		final File dropboxFilesDir = tmp;
		Pattern filePattern = Pattern.compile("[0-9-]+ - (.+)- (.+) - (.+)");
		
		Map<String, Set<Path>> nameFiles = new HashMap<>();
		try {
			Files.walkFileTree(dropboxFilesDir.toPath(), new SimpleFileVisitor<Path>(){
				@Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        	Matcher m = filePattern.matcher(file.toFile().getName());
		        	if(!m.find())
		        		return FileVisitResult.CONTINUE;
		        	String name = m.group(1);
		        	if(!nameFiles.containsKey(name))
		        		nameFiles.put(name, new HashSet<Path>());
		        	nameFiles.get(name).add(file);
		        	
		        	return FileVisitResult.CONTINUE;
		        }
			});
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		nameFiles.forEach((name, paths) -> {
			File newDir = new File(dropboxFilesDir.getAbsolutePath()+File.separator+name);
        	newDir.mkdir();
        	paths.forEach(file -> {
        		Matcher m = filePattern.matcher(file.toFile().getName());
    			if(!m.find())
    				return;
    			String fileName = m.group(3);
    			File newFile = new File(newDir.getAbsolutePath()+File.separator+fileName);
        		try {
    				Files.move(file, newFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
    			} catch (IOException e) {
    				System.err.println("Could not move file "+file.toFile().getName()+" \r\n\t"+e.getMessage());
    			}
        	});
		});
	}
	
	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 * @param zipFilePath
	 * @param destDirectory
	 */
	private static void unzip(String zipFilePath, String destDirectory)  {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath)))
		{
			ZipEntry entry = zipIn.getNextEntry();
			// iterates over entries in the zip file
			while (entry != null) {
				String filePath = destDirectory + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					// if the entry is a file, extracts it
					try(FileOutputStream toFile = new FileOutputStream(filePath)){
						IOUtils.copy(zipIn, toFile);
					} catch (Exception e){}
				} else {
					// if the entry is a directory, make the directory
					File dir = new File(filePath);
					dir.mkdir();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
		} 
		catch (IOException e){
			e.printStackTrace();
		}
		
	}

}
