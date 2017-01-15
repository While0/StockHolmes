package com.stock.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("StockTextHandler")
public class StockTextHandler {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	private FileWriter filewriter;
	private BufferedWriter bufferedwriter;
	private File file;

	public void writeTextFile(StringBuffer stringbuffer, String targetfilename, boolean appendwriting) {
		File targetfile = new File(targetfilename);
		FileWriter filewriter = null;
		try {
			filewriter = new FileWriter(targetfile, appendwriting);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
		try {
			if (!targetfile.exists()) {
				targetfile.createNewFile();
			}
			bufferedwriter.write(stringbuffer.toString());
			// bufferedwriter.newLine();
			bufferedwriter.flush();
			bufferedwriter.close();
			logger.info("Write message done: " + targetfilename);
		} catch (IOException e) {
			logger.error("Write message error: " + targetfilename);
			e.printStackTrace();
		}
	}

	public String readToString(String textfile) {
		File text = new File(textfile);
		if (text.exists() && text.canRead() && text.length() > 0) {
			BufferedReader reader = null;
			StringBuilder messagebuilder = new StringBuilder();
			try {
				reader = new BufferedReader(new FileReader(text));
				String textline = null;
				int lineseq = 0;
				while ((textline = reader.readLine()) != null) {
					lineseq++;
					logger.debug("Read line " + lineseq + " : " + textline);
					messagebuilder.append(textline + System.getProperty("line.separator"));
				}
				reader.close();
				return messagebuilder.toString();
			} catch (IOException e) {
				logger.error("Read text error: " + textfile);
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} else {
			logger.error("File doesn't exist,or can not be read,or is empty: ");
			logger.error(textfile);
		}
		return null;
	}

	public StringBuffer readToStringBuffer(String textfile) {
		File text = new File(textfile);
		if (text.exists() && text.canRead() && text.length() > 0) {
			BufferedReader reader = null;
			StringBuffer stringbuffer = new StringBuffer();
			try {
				reader = new BufferedReader(new FileReader(text));
				String textline = null;
				int lineseq = 0;
				while ((textline = reader.readLine()) != null) {
					lineseq++;
					logger.debug("Read line " + lineseq + " : " + textline);
					stringbuffer.append(textline + System.getProperty("line.separator"));
				}
				reader.close();
				return stringbuffer;
			} catch (IOException e) {
				logger.error("Read text error: " + textfile);
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
						logger.error("File doesn't close: " + textfile);
						e1.printStackTrace();
					}
				}
			}
		} else {
			logger.info("File doesn't exist,or can not be read,or is empty: " + textfile);
		}
		return null;
	}

	public void retrieveText(Map<String, String> linkmap, String textfile) {
		File text = new File(textfile);
		if (text.exists() && text.canRead()) {
			StringBuilder stringbuilder = new StringBuilder();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(text));
				String textline = null;
				String noticelink = null;
				while ((textline = reader.readLine()) != null) {
					stringbuilder.append(textline + "|");
				}
				Iterator<Map.Entry<String, String>> iterator = linkmap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, String> entry = iterator.next();
					noticelink = entry.getValue();
					int index = stringbuilder.lastIndexOf(noticelink);
					if (index < 0) {
						logger.debug("New Link: " + noticelink);
					} else {
						logger.debug("Old Link: " + noticelink);
						iterator.remove();
					}
				}
				reader.close();
			} catch (IOException e) {
				logger.error("Read text error: " + textfile);
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} else {
			logger.error("File doesn't exist,or can not be read: " + textfile);
		}
	}

	public void writeText(String string, String targetfilename) {
		File targetfile = new File(targetfilename);
		file = targetfile;
		try {
			filewriter = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bufferedwriter = new BufferedWriter(filewriter);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			bufferedwriter.write(string);
			bufferedwriter.newLine();
			bufferedwriter.flush();
			// bufferedwriter.close();
			logger.info("Write message done: " + string);
		} catch (IOException e) {
			logger.error("Write message error: " + string);
			e.printStackTrace();
		}
	}

	public void closeText() {
		try {
			if (bufferedwriter != null) {
				bufferedwriter.close();
				logger.debug("Close: " + file.toString());
			}
		} catch (IOException e) {
			logger.error("Close error: " + file.toString());
			e.printStackTrace();
		}
	}

	public void deleteSourceFile(File sourcefile, String targetextfile) {
		File targetext = new File(targetextfile);
		if (targetext.exists() && targetext.length() > 0) {
			logger.debug("Delete: " + sourcefile.toString());
			sourcefile.delete();
		} else {
			logger.error("TargetFile doesn't exist,or can not be deleted,or is empty. ");
		}
	}

	public void deleteFile(String targetfile) {
		File file = new File(targetfile);
		if (file.exists() && file.length() > 0) {
			file.delete();
		} else {
			logger.error("TargetFile doesn't exist,or can not be deleted,or is empty: ");
			logger.error(targetfile);
		}
	}

	public void clearTextFile(String targetfilename) {
		File targetfile = new File(targetfilename);
		FileWriter filewriter = null;
		try {
			filewriter = new FileWriter(targetfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
		try {
			if (!targetfile.exists()) {
				targetfile.createNewFile();
			}
			String emptystring = "";
			bufferedwriter.write(emptystring);
			bufferedwriter.flush();
			bufferedwriter.close();
			logger.info("Clear file done: " + targetfilename);
		} catch (IOException e) {
			logger.error("Clear file error: " + targetfilename);
			logger.error("The file remain size: " + targetfile.length());
			e.printStackTrace();
		}
	}

	public void clearPath(String localpath) {
		File path = new File(localpath);
		if (path.isDirectory()) {
			File[] fileArray = path.listFiles();
			if (fileArray != null) {
				logger.debug("Clear the path: " + localpath);
				for (int i = 0; i < fileArray.length; i++) {
					if (!fileArray[i].isHidden()) {
						logger.debug("Delete: " + fileArray[i]);
						fileArray[i].delete();
					}
				}
			}
		}
	}

	public void clearOldFile(String localpath, long pastime) {
		logger.info("Start to purge old file......");
		File path = new File(localpath);
		if (path.isDirectory()) {
			logger.info("Scan the path: " + localpath);
			File[] fileArray = path.listFiles();
			int filecounter = 0;
			if (fileArray != null) {
				for (int i = 0; i < fileArray.length; i++) {
					if (!fileArray[i].isHidden()) {
						long filepastlong = System.currentTimeMillis() - fileArray[i].lastModified();
						if (filepastlong > pastime) {
							logger.debug("Delete: " + fileArray[i]);
							fileArray[i].delete();
							filecounter++;
						}
					}

				}
				logger.info(filecounter + " files deleted.");
			} else {
				logger.info("No file to clear.");
			}
		}
	}

	public void copyFile(String sourcepath, String targetpath, String srcfile) {
		File targetfile = new File(sourcepath.concat(srcfile));
		logger.debug("Handle: " + targetfile.toString());
		File destDir = new File(targetpath);
		if (targetfile.isFile() && targetfile.exists()) {
			try {
				FileUtils.copyFileToDirectory(targetfile, destDir);
				logger.debug("Copy done. " + targetfile);
			} catch (IOException e) {
				logger.error("Copy fail. " + targetfile);
				e.printStackTrace();
			}
		}
	}

	/*
	 * public void retrieveText(Map<String, String> linkmap,String textfile){
	 * File text=new File(textfile); if(text.exists()&&text.canRead()){
	 * StringBuilder stringbuilder = new StringBuilder(); BufferedReader reader
	 * = null; try { reader = new BufferedReader(new FileReader(text));
	 * FileWriter writer = new FileWriter(text, true); String textline = null;
	 * String noticelink=null; while ((textline = reader.readLine()) != null) {
	 * stringbuilder.append(textline+"|"); } Iterator<Map.Entry<String, String>>
	 * iterator = linkmap.entrySet().iterator(); while(iterator.hasNext()){
	 * Map.Entry<String, String> entry=iterator.next();
	 * noticelink=entry.getValue(); int
	 * index=stringbuilder.lastIndexOf(noticelink); if(index<0){ logger.debug(
	 * "New Link: "+noticelink); //
	 * writer.write(noticelink+System.getProperty("line.separator")); } else{
	 * logger.debug("Old Link: "+noticelink); iterator.remove(); } }
	 * writer.close(); reader.close(); } catch (IOException e) { logger.error(
	 * "Read OR Write text error: "+textfile); e.printStackTrace(); } finally {
	 * if (reader != null) { try { reader.close(); } catch (IOException e1) {
	 * e1.printStackTrace(); } } } } else{ logger.error(
	 * "File doesn't exist,or can not be read: "); logger.error(textfile); } }
	 * 
	 */

}
