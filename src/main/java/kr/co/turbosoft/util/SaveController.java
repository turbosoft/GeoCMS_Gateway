package kr.co.turbosoft.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.taglibs.standard.extra.spath.Path;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.FileRenamePolicy;

import kr.co.turbosoft.dao.DataDao;
import kr.co.turbosoft.util.ImageExtract;
import kr.co.turbosoft.util.VideoEncoding;

public class SaveController extends Thread{
	private DataDao dataDao = null;
	private List<Map<String,String>> fileList;
	private List<String> saveFiles;
	
	private String loginId;
	private String logKey;
	
	private String b_serverUrl;
	private String b_serverId;
	private String b_serverPass;
	private String b_serverPort;
	private String b_serverPath;
	private String fileType;
	private boolean isServerUrl;
	
	
	public void setDataAPI(DataDao dataDao){
		this.dataDao = dataDao;
	}
	
	public SaveController(String loginId, String logKey, List<Map<String,String>> fileList, List<String> saveFiles,
			String b_serverUrl, String b_serverId, String b_serverPass, String b_serverPort, String b_serverPath, String fileType, boolean isServerUrl) {
		this.loginId = loginId;
		this.logKey = logKey;
		
        this.fileList = fileList;
        this.saveFiles = saveFiles;
        this.b_serverUrl = b_serverUrl;
        this.b_serverId = b_serverId;
        this.b_serverPass = b_serverPass;
        this.b_serverPort = b_serverPort;
        this.b_serverPath = b_serverPath;
        this.fileType = fileType;
        this.isServerUrl = isServerUrl;
    }
	
	@Override
	public void run() {
        System.out.println("thread run.");
		
        FTPClient ftp = null; // FTP Client 객체 
		FileInputStream fis = null; // File Input Stream 
//		int reply = 0;
		
		List<String> resIdx = new ArrayList<String>();
		HashMap<String, Object> objParam = new HashMap<String, Object>();
		Map<String, String> fileMap = new HashMap<String, String>();
		HashMap<String, String> paramMap = new HashMap<String, String>();
		HashMap<String, String> param2 = new HashMap<String, String>();
		
//		for(int k=0;k<fileList.size();k++){
//			fileMap = new HashMap<String, String>();
//			fileMap = fileList.get(k);
//			if(fileMap != null && fileMap.get("idx") != null){
//				resIdx.add(fileMap.get("idx"));
//			}
//		}
//		objParam.put("fileIdxs", resIdx);
		
		if(isServerUrl){
			ftp = connectServer();
			if(ftp == null || (ftp != null && !ftp.isConnected())){
				return;
			}
			
//			try{
//				ftp = new FTPClient(); // FTP Client 객체 생성 
//				ftp.setControlEncoding("UTF-8"); // 문자 코드를 UTF-8로 인코딩 
//				ftp.setConnectTimeout(15000);
//				ftp.connect(b_serverUrl, Integer.parseInt(b_serverPort)); // 서버접속 " "안에 서버 주소 입력 또는 "서버주소", 포트번호 
//				
//				reply = ftp.getReplyCode();//
//				if(!FTPReply.isPositiveCompletion(reply)) {
//					objParam.put("status", "ERROR");
//					dataDao.updateImageStatus(objParam);
//					
//					if(fileType != null && "imageFile".equals(fileType)){
//						param2.clear();
//						param2.put("loginId", loginId);
//						param2.put("idx", String.valueOf(logKey));
//						param2.put("status", "ERROR");
//						dataDao.updateLog(param2);
//				    }
//					if(ftp.isConnected()){
//						ftp.disconnect();
//				    }
//					return;
//			    }
//				
//				if(!ftp.login(b_serverId, b_serverPass)) {
//					ftp.logout();
//					objParam.put("status", "ERROR");
//					dataDao.updateImageStatus(objParam);
//					
//					if(fileType != null && "imageFile".equals(fileType)){
//						param2.clear();
//						param2.put("loginId", loginId);
//						param2.put("idx", String.valueOf(logKey));
//						param2.put("status", "ERROR");
//						dataDao.updateLog(param2);
//				    }
//					if(ftp.isConnected()){
//						ftp.disconnect();
//				    }
//					return;
//			    }
//				
//				ftp.setFileType(FTP.BINARY_FILE_TYPE);
//			    ftp.enterLocalPassiveMode();
//
//			    ftp.changeWorkingDirectory(b_serverPath +"/GeoPhoto"); // 작업 디렉토리 변경
//			    reply = ftp.getReplyCode();
//			    if (reply == 550) {
//			    	ftp.makeDirectory(b_serverPath +"/GeoPhoto");
//			    	ftp.changeWorkingDirectory(b_serverPath +"/GeoPhoto"); // 작업 디렉토리 변경
//			    }
//			}catch(Exception e){
//				e.printStackTrace();
//				objParam.put("status", "ERROR");
//				dataDao.updateImageStatus(objParam);
//				
//				if(fileType != null && "imageFile".equals(fileType)){
//					param2.clear();
//					param2.put("loginId", loginId);
//					param2.put("idx", String.valueOf(logKey));
//					param2.put("status", "ERROR");
//					dataDao.updateLog(param2);
//			    }
//				if(ftp.isConnected()){
//					try {
//						ftp.disconnect();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//			    }
//				return;
//			}
		}
		
		//--------------------------------------------------------------------------------------------
	    
		List<File> removeFile = new ArrayList<File>();
		String tmpFtfFileName = "";
		boolean isSuccess = false;
		ContentsSave contentsSave = new ContentsSave();
		String filePathStr = "";
		String reseultData = "";
		String[] reseultDataArr = null;
		String datalongitude = "";
		String datalatitude = "";
		
		String idxArrStr = "";
		String[] idxArr = null;
		String savefullStr = "";
		
		int thumbnail_width1 = 110;
		int thumbnail_height1 = 110;
		int thumbnail_width2 = 600;
		int thumbnail_height2 = 442;
		FileOutputStream fs = null;
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream in = null;
		BufferedOutputStream baos2 = null;
		byte[] changeBytes = null;
		File changeFile = null; 
		String tmpPrefixa = "";
		String tmpLastfixa = "";
		String tmpPreThumb = "";
		File thumb_file_name1 = null;
		File thumb_file_name2 = null;
		BufferedImage sourceImage = null;
		BufferedImage sourceImage2 = null;
		Image scaledImage = null;
		Image scaledImage2 = null;
		BufferedImage newImage11 = null;
		BufferedImage newImage22 = null;
		Graphics g11 = null;
		Graphics g22 = null;
		
		for(int k=0;k<fileList.size();k++){
			tmpFtfFileName = "";
			filePathStr = "";
			reseultData = "";
			reseultDataArr = null;
			datalongitude = "";
			datalatitude = "";
			idxArrStr = "";
			idxArr = null;
			fileMap = new HashMap<String, String>();
			fis = null;
			savefullStr = saveFiles.get(k);
			removeFile = new ArrayList<File>();
			
			fs = null;
			baos = null;
			in = null;
			baos2 = null;
			changeBytes = null;
			changeFile = null; 
			tmpPrefixa = "";
			tmpLastfixa = "";
			tmpPreThumb = "";
			thumb_file_name1 = null;
			thumb_file_name2 = null;
			sourceImage = null;
			sourceImage2 = null;
			scaledImage = null;
			scaledImage2 = null;
			newImage11 = null;
			newImage22 = null;
			g11 = null;
			g22 = null;
			
			if(savefullStr != null && !"".equals(savefullStr) && fileList.get(k) != null && !"".equals(fileList.get(k))) {
				
				try {
					fileMap = fileList.get(k);
					if(fileMap != null && fileMap.get("file") != null && fileMap.get("file") != ""){
						filePathStr = fileMap.get("file");
						tmpFtfFileName = filePathStr.substring(filePathStr.lastIndexOf("/")+1, filePathStr.length());
						
						if(isServerUrl){
							removeFile.add(new File(savefullStr));
							fis = new FileInputStream(savefullStr);
							if(ftp == null || (ftp != null && !ftp.isConnected())){
								ftp = connectServer();
							}
							if(ftp == null || (ftp != null && !ftp.isConnected())){
								return;
							}
							isSuccess = ftp.storeFile(tmpFtfFileName, fis);
						}else{
							isSuccess = true;
						}
						
						if(isSuccess) {
							paramMap = new HashMap<String, String>();
							if(fileType != null && !"".equals(fileType)){
								if("imageFile".equals(fileType)){
									//썸네일 이미지
									changeFile = new File(savefullStr);
									tmpPrefixa = savefullStr.substring(0, savefullStr.lastIndexOf("."));
									tmpLastfixa = savefullStr.substring(savefullStr.lastIndexOf(".")+1);
									tmpPreThumb = tmpFtfFileName.substring(0,tmpFtfFileName.lastIndexOf("."));
									
									fs = new FileOutputStream(new File(tmpPrefixa + "_BASE_thumbnail."+tmpLastfixa));
									
									baos = new ByteArrayOutputStream();
									in = new ByteArrayInputStream(FileUtils.readFileToByteArray(changeFile));
									baos2 = null;
									Thumbnails.of(in).scale(1).toOutputStream(baos);
									changeBytes = baos.toByteArray();
									
									baos2 = new BufferedOutputStream(fs);
									baos2.write(changeBytes);
									baos2.close();
									baos2 = null;
								    
									thumb_file_name1 = new File(tmpPrefixa+"_thumbnail.png");
									thumb_file_name2 = new File(tmpPrefixa+"_thumbnail_600.png");
						        	
									sourceImage = ImageIO.read(new File(tmpPrefixa + "_BASE_thumbnail."+tmpLastfixa));
									scaledImage = sourceImage.getScaledInstance(thumbnail_width1,thumbnail_height1, Image.SCALE_DEFAULT);
						        	newImage11 = new BufferedImage(thumbnail_width1, thumbnail_height1, BufferedImage.TYPE_INT_RGB);
						        	g11 = newImage11.getGraphics();
						        	g11.drawImage(scaledImage, 0, 0, null);
						        	g11.dispose();
						        	ImageIO.write(newImage11, "jpg", thumb_file_name1);
						        	
						        	sourceImage2 = ImageIO.read(new File(tmpPrefixa + "_BASE_thumbnail."+tmpLastfixa));
						        	scaledImage2 = sourceImage2.getScaledInstance(thumbnail_width2,thumbnail_height2, Image.SCALE_DEFAULT);
						        	newImage22 = new BufferedImage(thumbnail_width2, thumbnail_height2, BufferedImage.TYPE_INT_RGB);
						        	g22 = newImage22.getGraphics();
						        	g22.drawImage(scaledImage2, 0, 0, null);
						        	g22.dispose();
						        	ImageIO.write(newImage22, "jpg", thumb_file_name2);
							        
						        	if(isServerUrl){
						        		removeFile.add(thumb_file_name1);
										removeFile.add(thumb_file_name2);
										removeFile.add(new File(tmpPrefixa + "_BASE_thumbnail."+tmpLastfixa));
										fis = new FileInputStream(thumb_file_name1);
										if(ftp == null || (ftp != null && !ftp.isConnected())){
											ftp = connectServer();
										}
										if(ftp == null || (ftp != null && !ftp.isConnected())){
											return;
										}
										isSuccess = ftp.storeFile(tmpPreThumb+"_thumbnail.png", fis);
										
										fis = new FileInputStream(thumb_file_name2);
										if(ftp == null || (ftp != null && !ftp.isConnected())){
											ftp = connectServer();
										}
										if(ftp == null || (ftp != null && !ftp.isConnected())){
											return;
										}
										isSuccess = ftp.storeFile(tmpPreThumb+"_thumbnail_600.png", fis);
										
										fis = new FileInputStream(new File(tmpPrefixa + "_BASE_thumbnail."+tmpLastfixa));
										if(ftp == null || (ftp != null && !ftp.isConnected())){
											ftp = connectServer();
										}
										if(ftp == null || (ftp != null && !ftp.isConnected())){
											return;
										}
										isSuccess = ftp.storeFile(tmpPreThumb+"_BASE_thumbnail."+tmpLastfixa, fis);
						        	}
									
									//좌표파일
									paramMap.put("idx", fileMap.get("idx"));
									reseultData = contentsSave.saveImageContent(savefullStr);
									if(reseultData != null && !"".equals(reseultData)){
										reseultDataArr = reseultData.split(",");
										
										datalongitude = reseultDataArr[0];
										datalatitude = reseultDataArr[1];
										paramMap.put("longitude", datalongitude);
										paramMap.put("latitude", datalatitude);
									}
									paramMap.put("status", "COMPLETE");
									dataDao.updateImage(paramMap);
								    System.out.println(tmpFtfFileName + "파일 FTP 업로드 성공");
								}else if("worldFile".equals(fileType)){
									idxArrStr = fileMap.get("idx");
									if(idxArrStr != null && !"".equals(idxArrStr)){
										idxArr = idxArrStr.split(",");
										if(idxArr != null && idxArr.length > 0){
											reseultData = contentsSave.saveFileContent(savefullStr);
											if(reseultData != null && !"".equals(reseultData)){
												reseultDataArr = reseultData.split(",");
												
												datalongitude = reseultDataArr[0];
												datalatitude = reseultDataArr[1];
												paramMap.put("longitude", datalongitude);
												paramMap.put("latitude", datalatitude);
											}
											paramMap.put("status", "COMPLETE");
											
											for(int m=0; m<idxArr.length; m++){
												paramMap.put("idx", idxArr[m]);
												dataDao.updateImage(paramMap);
											    System.out.println(tmpFtfFileName + "파일 FTP 업로드 성공");
											}
										}
									}
								}
							}
				       }
					}
					
			    } catch(Exception ie) {
			       ie.printStackTrace();
			       if(fileType != null && "imageFile".equals(fileType)){
			    	   param2.clear();
					   param2.put("loginId", loginId);
					   param2.put("idx", String.valueOf(logKey));
					   param2.put("status", "ERROR");
					   dataDao.updateLog(param2);
			       }
					
			       objParam.clear();
			       for(int m=k;m<fileList.size();m++){
						fileMap = new HashMap<String, String>();
						fileMap = fileList.get(m);
						if(fileMap != null && fileMap.get("idx") != null){
							resIdx.add(fileMap.get("idx"));
						}
					}
					objParam.put("fileIdxs", resIdx);
					objParam.put("status", "ERROR");
					dataDao.updateImageStatus(objParam);
					
					if(ftp != null && ftp.isConnected()){
						try {
							ftp.disconnect();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
			    } finally {
			       if(fis != null) {
			          try {
			             fis.close();
			          } catch(IOException ie) {
			        	  ie.printStackTrace();
			        	  if(fileType != null && "imageFile".equals(fileType)){
					    	   param2.clear();
							   param2.put("loginId", loginId);
							   param2.put("idx", String.valueOf(logKey));
							   param2.put("status", "ERROR");
							   dataDao.updateLog(param2);
					       }
						   
			        	  for(int m=k;m<fileList.size();m++){
								fileMap = new HashMap<String, String>();
								fileMap = fileList.get(m);
								if(fileMap != null && fileMap.get("idx") != null){
									resIdx.add(fileMap.get("idx"));
								}
							}
							objParam.put("fileIdxs", resIdx);
							objParam.put("status", "ERROR");
							dataDao.updateImageStatus(objParam);
			          }finally{
			        	  if(removeFile != null && removeFile.size() > 0){
			        		 for(int fi=0; fi<removeFile.size(); fi++){
			        			 if(removeFile.get(fi) != null && removeFile.get(fi).exists()){
			        				 removeFile.get(fi).delete();
			        			 }
			        		 }
			      		  }
			          }
			       }
			       
			       if(removeFile != null && removeFile.size() > 0){
		        		 for(int fi=0; fi<removeFile.size(); fi++){
		        			 if(removeFile.get(fi) != null && removeFile.get(fi).exists()){
		        				 removeFile.get(fi).delete();
		        			 }
		        		 }
			       }
			    }
			}
		}// end for
		
		if(fileType != null && "imageFile".equals(fileType)){
			param2.clear();
			param2.put("loginId", loginId);
			param2.put("idx", String.valueOf(logKey));
			param2.put("status", "COMPLETE");
			dataDao.updateLog(param2);
	    }
		
		if(ftp != null && ftp.isConnected()){
			try {
				ftp.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    }
	
	public FTPClient connectServer() {
		FTPClient ftp = null; // FTP Client 객체 
		int reply = 0;
		
		List<String> resIdx = new ArrayList<String>();
		HashMap<String, Object> objParam = new HashMap<String, Object>();
		Map<String, String> fileMap = new HashMap<String, String>();
		HashMap<String, String> param2 = new HashMap<String, String>();
		
		for(int k=0;k<fileList.size();k++){
			fileMap = new HashMap<String, String>();
			fileMap = fileList.get(k);
			if(fileMap != null && fileMap.get("idx") != null){
				resIdx.add(fileMap.get("idx"));
			}
		}
		objParam.put("fileIdxs", resIdx);
		
		if(isServerUrl){
			try{
				ftp = new FTPClient(); // FTP Client 객체 생성 
				ftp.setControlEncoding("UTF-8"); // 문자 코드를 UTF-8로 인코딩 
				ftp.setConnectTimeout(3000);
				ftp.connect(b_serverUrl, Integer.parseInt(b_serverPort)); // 서버접속 " "안에 서버 주소 입력 또는 "서버주소", 포트번호 
				
				reply = ftp.getReplyCode();//
				if(!FTPReply.isPositiveCompletion(reply)) {
					objParam.put("status", "ERROR");
					dataDao.updateImageStatus(objParam);
					
					if(fileType != null && "imageFile".equals(fileType)){
						param2.clear();
						param2.put("loginId", loginId);
						param2.put("idx", String.valueOf(logKey));
						param2.put("status", "ERROR");
						dataDao.updateLog(param2);
				    }
					if(ftp != null && ftp.isConnected()){
						ftp.disconnect();
				    }
					return ftp;
			    }
				
				if(!ftp.login(b_serverId, b_serverPass)) {
					ftp.logout();
					objParam.put("status", "ERROR");
					dataDao.updateImageStatus(objParam);
					
					if(fileType != null && "imageFile".equals(fileType)){
						param2.clear();
						param2.put("loginId", loginId);
						param2.put("idx", String.valueOf(logKey));
						param2.put("status", "ERROR");
						dataDao.updateLog(param2);
				    }
					if(ftp != null && ftp.isConnected()){
						ftp.disconnect();
				    }
					return ftp;
			    }
				
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			    ftp.enterLocalPassiveMode();

			    ftp.changeWorkingDirectory(b_serverPath +"/GeoPhoto"); // 작업 디렉토리 변경
			    reply = ftp.getReplyCode();
			    if (reply == 550) {
			    	ftp.makeDirectory(b_serverPath +"/GeoPhoto");
			    	ftp.changeWorkingDirectory(b_serverPath +"/GeoPhoto"); // 작업 디렉토리 변경
			    }
			    
			}catch(Exception e){
				e.printStackTrace();
				objParam.put("status", "ERROR");
				dataDao.updateImageStatus(objParam);
				
				if(fileType != null && "imageFile".equals(fileType)){
					param2.clear();
					param2.put("loginId", loginId);
					param2.put("idx", String.valueOf(logKey));
					param2.put("status", "ERROR");
					dataDao.updateLog(param2);
			    }
				if(ftp != null && ftp.isConnected()){
					try {
						ftp.disconnect();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    }
				return ftp;
			}
		}
		return ftp;		
	}
}
