package kr.co.turbosoft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import kr.co.turbosoft.dao.DataDao;
import kr.co.turbosoft.util.ImageExtract;
import kr.co.turbosoft.util.VideoEncoding;

public class EncodingController extends Thread{
	private DataDao dataDao = null;
	private String loginId;
	private String logKey;
	private List<Map<String, Object>> fileNameList;
	
	private String saveUserPath;
	private String b_serverUrl;
	private String b_serverId;
	private String b_serverPass;
	private String b_serverPort;
	private String b_serverPath;
	private boolean isServerUrl;
	
	public void setDataAPI(DataDao dataDao){
		this.dataDao = dataDao;
	}
	
	public EncodingController(String loginId, String logKey, List<Map<String, Object>> fileNameList,
			String saveUserPath, String b_serverUrl, String b_serverId, String b_serverPass, String b_serverPort, String b_serverPath, boolean isServerUrl) {
        this.loginId = loginId;
        this.logKey = logKey;
        this.fileNameList = fileNameList;
        this.saveUserPath = saveUserPath;
        this.b_serverUrl = b_serverUrl;
        this.b_serverId = b_serverId;
        this.b_serverPass = b_serverPass;
        this.b_serverPort = b_serverPort;
        this.b_serverPath = b_serverPath;
        this.isServerUrl = isServerUrl;
    }
	
	@Override
	public void run() {
        System.out.println("thread run.");
		
        Map<String, Object> fileNameMap = new HashMap<String, Object>();
		HashMap<String, String> param = new HashMap<String, String>();
		HashMap<String, Object> objParam = new HashMap<String, Object>();
		ArrayList<Object> resIdx = new ArrayList<Object>();
		String fileNameStr = "";
		List<String> removeFileList = new ArrayList<String>();
		
		File userTempDir = new File(saveUserPath+File.separator+"GeoVideo");
		
		param.put("loginId", loginId);
		param.put("idx", logKey);
		
		ImageExtract imageExtract = new ImageExtract();
		VideoEncoding videoEncoding = new VideoEncoding();
		String uploadType = "GeoVideo";
		FTPClient ftp = null; // FTP Client 객체 
		FileInputStream fis = null; // File Input Stream 
		int reply = 0;
		String thumFile = "";
		String oggFile = "";
		String gpxFile = "";
		String gpxModifyFile = "";
		List<String> tmpFileList = new ArrayList<String>();
		String ftpfileName = "";
		boolean isSuccess = false;
		
		if(fileNameList != null && fileNameList.size() > 0){
			try{
				for(Map<String, Object> tmpFileMap : fileNameList){
					fileNameStr = String.valueOf(tmpFileMap.get("file"));
					//이미지 추출
					imageExtract.ImageExtractor(fileNameStr);
					
					//자동 인코딩 (1차 : ogg)
					int resEncoding = videoEncoding.convertToOgg(fileNameStr);
					System.out.println("resEncoding : " + resEncoding);
					if(resEncoding != 1){
						ftpError(param);
						return;
					}
				}
				
				if(isServerUrl){
					ftp = new FTPClient(); // FTP Client 객체 생성 
					ftp.setControlEncoding("UTF-8"); // 문자 코드를 UTF-8로 인코딩 
					ftp.setConnectTimeout(3000);
					ftp.connect(b_serverUrl, Integer.parseInt(b_serverPort)); // 서버접속 " "안에 서버 주소 입력 또는 "서버주소", 포트번호 
					
					reply = ftp.getReplyCode();//
					if(!FTPReply.isPositiveCompletion(reply)) {
						ftp.disconnect();
						ftpError(param);
						return;
				    }
					
					if(!ftp.login(b_serverId, b_serverPass)) {
						ftp.logout();
						ftpError(param);
						return;
				    }
					
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					ftp.enterLocalPassiveMode();
				    

				    ftp.changeWorkingDirectory(b_serverPath+"/"+uploadType); // 작업 디렉토리 변경
				    reply = ftp.getReplyCode();
				    if (reply == 550) {
				    	ftp.makeDirectory(b_serverPath+"/"+uploadType);
				    	reply = ftp.getReplyCode();
				    	if (reply == 550) {
				    		ftpError(param);
							return;
				    	}
				    	ftp.changeWorkingDirectory(b_serverPath+"/"+uploadType); // 작업 디렉토리 변경
				    	reply = ftp.getReplyCode();
				    	if (reply == 550) {
				    		ftpError(param);
							return;
				    	}
				    }
				}
				
			    int fileNameStrCnt = 0;
			    File checkFile = null;
			    File checkFile2 = null;
			    for(Map<String, Object> tmpFileMap : fileNameList){
			    	fileNameStr = "";
			    	thumFile = "";
			    	oggFile = "";
			    	gpxFile = "";
			    	gpxModifyFile = "";
			    	ftpfileName = "";
			    	isSuccess = false;
			    	
			    	try{
			    		fileNameStr = String.valueOf(tmpFileMap.get("file"));
			    		thumFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + "_thumb.jpg";
			    		oggFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + "_mp4.mp4";
			    		gpxFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + ".txt";
			    		gpxModifyFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + "_modify.txt";
			    		tmpFileList = new ArrayList<String>();
			    		tmpFileList.add(thumFile);
			    		tmpFileList.add(oggFile);
			    		tmpFileList.add(gpxFile);
			    		tmpFileList.add(gpxModifyFile);
			    		//removeFileList
			    		removeFileList.add(thumFile);
			    		removeFileList.add(oggFile);
			    		removeFileList.add(gpxFile);
			    		removeFileList.add(gpxModifyFile);
			    		
			    		for(int k=0; k<tmpFileList.size();k++){
			    			if(k < 2 || fileNameStrCnt == 0 && k == 2  || k == 3){
			    				if(fileNameStrCnt == 0 && k == 2){
			    					fileNameStrCnt = 1;
			    					checkFile = new File(tmpFileList.get(k));
			    					if(!checkFile.exists()){
			    						continue;
			    					}
			    				};
			    				
			    				if(k == 3){
			    					checkFile2 = new File(tmpFileList.get(k));
			    					if(!checkFile2.exists()){
			    						continue;
			    					}
			    				}
			    				
			    				fis = new FileInputStream(tmpFileList.get(k));
			    				if(fis != null){
//			    					ftpfileName = tmpFileList.get(k).substring(tmpFileList.get(k).lastIndexOf("\\")+1);	//git
			    					ftpfileName = tmpFileList.get(k).substring(tmpFileList.get(k).lastIndexOf(File.separator)+1);	//linux
			    					if(isServerUrl){
			    						isSuccess = ftp.storeFile(ftpfileName, fis);
			    					}else{
			    						isSuccess = true;
			    					}
								    
								    if(k == 1){
								    	checkFile = new File(tmpFileList.get(2));
				    					if(!checkFile.exists()){
				    						if(isSuccess) {
												resIdx = new ArrayList<Object>();
												resIdx.add(tmpFileMap.get("idx"));
												objParam.clear();
												objParam.put("fileIdxs", resIdx);
												objParam.put("status", "COMPLETE");
											    dataDao.updateContentChildStatus(objParam);
										    	System.out.println(tmpFileList.get(k) + "파일 FTP 업로드 성공");
										    }
				    					}
								    }else if(k == 2){
								    	if(isSuccess) {
											resIdx = new ArrayList<Object>();
											resIdx.add(tmpFileMap.get("idx"));
											objParam.clear();
											objParam.put("fileIdxs", resIdx);
											objParam.put("status", "COMPLETE");
										    dataDao.updateContentChildStatus(objParam);
									    	System.out.println(tmpFileList.get(k) + "파일 FTP 업로드 성공");
									    }
								    }
			    				}
			    			}
			    		}
					} catch(IOException ie) {
						ie.printStackTrace(); 
						if(isServerUrl){
							ftpError(param);	
						}
						return;
					} finally {
						if(fis != null) {
							try {
								fis.close();
							} catch(IOException ie) {
								ie.printStackTrace();
								if(isServerUrl){
									ftpError(param);	
								}
								return;
							}
						}
					}
			    }
			    
			    param.put("status", "COMPLETE");
		    	dataDao.updateLog(param);
		    	
				
			}catch(Exception e){
				e.printStackTrace();
				if(isServerUrl){
					ftpError(param);	
				}
			}finally{
				if(isServerUrl && userTempDir.exists() && removeFileList != null){
					for(String rFile: removeFileList){
						File t1 = new File(rFile);
						if(t1.exists()){
							t1.delete();
						}
					}
				}
			}
		}
    }
	
	public void ftpError(HashMap<String, String> param){
		List<Object> removeList = new ArrayList<Object>();
		List<String> removeFileList = new ArrayList<String>();
		Map<String, Object> fileNameMap = new HashMap<String, Object>();
		File userTempDir = new File(saveUserPath+File.separator+"GeoVideo");
		HashMap<String, Object> objParam = new HashMap<String, Object>();
		String fileNameStr = "";
		String thumFile = "";
		String oggFile = "";
		String gpxFile = "";
		String gpxModifyFile = "";
		
		param.put("status", "ERROR");
		dataDao.updateLog(param);
		
	    for(int m=0;m<fileNameList.size();m++){
	    	fileNameMap = fileNameList.get(m);
	    	removeList.add(fileNameMap.get("idx"));
	    	
	    	fileNameStr = String.valueOf(fileNameMap.get("file"));
	    	thumFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + "_thumb.jpg";
    		oggFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + "_mp4.mp4";
    		gpxFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + ".txt";
    		gpxModifyFile = fileNameStr.substring(0,fileNameStr.lastIndexOf(".")) + "_modify.txt";
    		removeFileList.add(thumFile);
    		removeFileList.add(oggFile);
    		removeFileList.add(gpxFile);
    		removeFileList.add(gpxModifyFile);
	    }
	    
	    objParam.put("fileIdxs", removeList);
	    objParam.put("status", "ERROR");
	    dataDao.updateContentChildStatus(objParam);
	    
	    if(isServerUrl && userTempDir.exists() && removeFileList != null){
			for(String rFile: removeFileList){
				File t1 = new File(rFile);
				if(t1.exists()){
					t1.delete();
				}
			}
		}
	}
}
