package com.ericsson.ept.test.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZIPFile {

	private String outputDir;
	private String zipfilename;
	
	public ZIPFile(String outputDir, String zipfilename){
		this.outputDir = outputDir;
		this.zipfilename = zipfilename+".zip";
	}
	
	public synchronized void writeToZip(String parentdir, String filename, String filecontent){
		try{
			File zipfile = new File(outputDir, zipfilename);
			File tmpzipfile = new File(outputDir, zipfilename+".tmp");
			if(zipfile.exists()){
				zipfile.renameTo(tmpzipfile);
			}
			
			zipfile.createNewFile();
			
			//Create the latest file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			ZipEntry entry = new ZipEntry(parentdir + "/" + filename+".csv");
			out.putNextEntry(entry);
			
			byte[] data = filecontent.getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			
			if(tmpzipfile.exists()){
				byte[] buffer = new byte[4096];
				ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpzipfile));
				for(ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()){
					try{
						out.putNextEntry(ze);
		                for(int read = zin.read(buffer); read > -1; read = zin.read(buffer)){
		                    out.write(buffer, 0, read);
		                }
		                out.closeEntry();
					}catch(Exception e){
						//Suppress an exception where the file already exists in the ZIP
					}
		        }
				zin.close();
			}
			
			out.flush();
			out.close();
			tmpzipfile.delete();
			
		}catch(Exception e){
			System.err.println("Unable to write file to " + filename);
			e.printStackTrace();
		}
	}
	
	
	public void writeToZipold(String parentdir, String filename, String filecontent){
		try{
			File zipfile = new File(outputDir, zipfilename);
			if(!zipfile.exists()){
				zipfile.createNewFile();
			}
			
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			ZipEntry entry = new ZipEntry(parentdir + "/" + filename+".csv");
			out.putNextEntry(entry);
			
			byte[] data = filecontent.getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			out.close();
			
		}catch(Exception e){
			System.err.println("Unable to write file to " + filename);
		}

	}
	
	
	
	
	
	
	
	
}
