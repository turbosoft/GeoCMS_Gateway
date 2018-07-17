package kr.co.turbosoft.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import kr.co.turbosoft.dao.DataDao;

public class VideoSaveController extends Thread{
	private DataDao dataDao = null;
	
	private String loginId;
	private String logKey;
	private String saveFileNameOrg;
	private String parentIndex;
	private List<Map<String,String>> files;
	private List<String> filesXml;
	private ArrayList<FileItem> fileItemList;
	
	private String saveUserPath;
	private String b_serverUrl;
	private String b_serverId;
	private String b_serverPass;
	private String b_serverPort;
	private String b_serverPath;
	private String gpsFileTypeData;
	private String fileType;
	private boolean isServerUrl;
	
	public void setDataAPI(DataDao dataDao){
		this.dataDao = dataDao;
	}
	
	public VideoSaveController(String loginId, String logKey, String saveFileNameOrg, String parentIndex, List<Map<String,String>> files, List<String> filesXml, ArrayList<FileItem> fileItemList,
			String saveUserPath, String b_serverUrl, String b_serverId, String b_serverPass, String b_serverPort, String b_serverPath, String gpsFileTypeData, String fileType, boolean isServerUrl) {
        this.loginId = loginId;
        this.logKey = logKey;
        this.saveFileNameOrg = saveFileNameOrg;
        this.parentIndex = parentIndex;
        this.files = files;
        this.filesXml = filesXml;
        this.fileItemList = fileItemList;
        
        this.saveUserPath = saveUserPath;
        this.b_serverUrl = b_serverUrl;
        this.b_serverId = b_serverId;
        this.b_serverPass = b_serverPass;
        this.b_serverPort = b_serverPort;
        this.b_serverPath = b_serverPath;
        this.gpsFileTypeData = gpsFileTypeData;
        this.fileType = fileType;
        this.isServerUrl = isServerUrl;
    }

	@Override
	public void run() {
        System.out.println("thread run.");
		
        FTPClient ftp = null; // FTP Client 객체 
		FileInputStream fis = null; // File Input Stream 
		int reply = 0;
//		File removeFile = null;
		List<File> removeFileList = new ArrayList<File>();
		
		List<Object> resIdx = new ArrayList<Object>();
		HashMap<String, Object> objParam = new HashMap<String, Object>();
		HashMap<String, String> param2 = new HashMap<String, String>();
		HashMap<String, String> param3 = new HashMap<String, String>();
		List<Map<String, Object>> fileNameList = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> fileNameMap = new HashMap<String, Object>();
		
		param3.clear();
		param3.put("loginId", loginId);
		param3.put("idx", String.valueOf(logKey));
		
		if(isServerUrl){
			try{
				ftp = new FTPClient(); // FTP Client 객체 생성 
				ftp.setControlEncoding("UTF-8"); // 문자 코드를 UTF-8로 인코딩 
				ftp.setConnectTimeout(3000);
				ftp.connect(b_serverUrl, Integer.parseInt(b_serverPort)); // 서버접속 " "안에 서버 주소 입력 또는 "서버주소", 포트번호 
				
				reply = ftp.getReplyCode();//
				if(!FTPReply.isPositiveCompletion(reply)) {
					ftp.disconnect();
					if(fileType != null && "videoFile".equals(fileType)){
						param3.put("status", "ERROR");
						dataDao.updateLog(param3);
					}
					return;
			    }
				
				if(!ftp.login(b_serverId, b_serverPass)) {
					ftp.logout();
					if(fileType != null && "videoFile".equals(fileType)){
						param3.put("status", "ERROR");
						dataDao.updateLog(param3);
					}
					return;
			    }
				
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			    ftp.enterLocalPassiveMode();

			    ftp.changeWorkingDirectory(b_serverPath +"/GeoVideo"); // 작업 디렉토리 변경
			    reply = ftp.getReplyCode();
			    if (reply == 550) {
			    	ftp.makeDirectory(b_serverPath +"/GeoVideo");
			    	ftp.changeWorkingDirectory(b_serverPath +"/GeoVideo"); // 작업 디렉토리 변경
			    }
			}catch(Exception e){
				e.printStackTrace();
				if(fileType != null && "videoFile".equals(fileType)){
					param3.put("status", "ERROR");
					dataDao.updateLog(param3);
				}
				return;
			}
		}
		
		//--------------------------------------------------------------------------------------------
		HashMap<String, String> param4 = new HashMap<String, String>();
		Map<String, String> fileMap = new HashMap<String, String>();
		List<Object> removeList = new ArrayList<Object>();
		File newLogKeyPath = null;
		FileItem item = null;
	    String tmpGpxFilePathDirFull = "";
	    String tmpGpxFilePathDir = "";
	    String latitude = "";
		String longitude = "";
		String fileName = "";
		String tmpPathStr = "";
		String getVideoGpsFileFull = "";
		String nowFileName = "";
		saveFileNameOrg = saveFileNameOrg.substring(0, saveFileNameOrg.lastIndexOf("."));
		
		//video date time
		List<String> ffmpegFileArr = new ArrayList<String>();
		
		try{
			if(fileType != null && "videoFile".equals(fileType)){
				ffmpegFileArr = new ArrayList<String>();
				for(int j=0; j<fileItemList.size(); j++){
					newLogKeyPath = null;
					item = fileItemList.get(j);
					tmpGpxFilePathDirFull = "";
					fileNameMap = new HashMap<String, Object>();
					fileMap = new HashMap<String, String>();
					fileName = "";
					tmpPathStr = "";
					nowFileName = "";
					
					if(!item.isFormField()) {
						fileName = item.getName();
						System.out.println("FileName : "+fileName);
						if(fileName != null && !"".equals(fileName)){
							fileMap = files.get(j);
							tmpGpxFilePathDirFull = String.valueOf(fileMap.get("file"));
							System.out.println("tmpGpxFilePathDirFull : " + tmpGpxFilePathDirFull);
//							tmpGpxFilePathDir = tmpGpxFilePathDirFull.substring(0, tmpGpxFilePathDirFull.lastIndexOf("\\"));
							tmpGpxFilePathDir = tmpGpxFilePathDirFull.substring(0, tmpGpxFilePathDirFull.lastIndexOf(File.separator));
							nowFileName = tmpGpxFilePathDirFull.substring(tmpGpxFilePathDirFull.lastIndexOf(File.separator)+1);
							newLogKeyPath = new File(tmpGpxFilePathDir);
							if(!newLogKeyPath.exists()) newLogKeyPath.mkdir();
							
							System.out.println("nowFileName : "+nowFileName);
							
							if(fileName.indexOf(".gpx") >= 0){
								if("2".equals(gpsFileTypeData)){
									item.write(new File(tmpGpxFilePathDir + File.separator + nowFileName));
								}
							}else if(fileName.indexOf(".srt") >= 0){
								if("3".equals(gpsFileTypeData)){
									item.write(new File(tmpGpxFilePathDir + File.separator + nowFileName));
								}
							}else if(fileName.indexOf(".gpx") < 0 && fileName.indexOf(".srt") < 0){
								item.write(new File(tmpGpxFilePathDir + File.separator + nowFileName));
								tmpPathStr = tmpGpxFilePathDir + File.separator + nowFileName;
								tmpPathStr = tmpPathStr.replace("_mp4.mp4", "");
								
								fileNameMap.put("file", tmpPathStr);
								fileNameMap.put("idx", fileMap.get("idx"));
								fileNameList.add(fileNameMap);
								if(fileMap.get("gps") != null && "Y".equals(fileMap.get("gps"))){
									getVideoGpsFileFull = tmpGpxFilePathDir + File.separator + nowFileName;
								}
								
								if(filesXml != null && filesXml.size() > 0){
									ffmpegFileArr.add(tmpGpxFilePathDir + File.separator + nowFileName);
								}
							}
							//FTP end---------------------------------------------------------------------------------------------------------
							
						}//file name not null
					}
				}
				
				if("4".equals(gpsFileTypeData) || "5".equals(gpsFileTypeData)){
					if(getVideoGpsFileFull != null && !"".equals(getVideoGpsFileFull)){
						VideoEncoding vEncoding = new VideoEncoding();
						List<String> resGpsDataArr = new ArrayList<String>();
						JSONObject jsonObj = new JSONObject();
						JSONArray jsonArr = new JSONArray();
						JSONObject chJsonObj = new JSONObject();
						if(isServerUrl){
							jsonObj.put("filePath", b_serverUrl + File.separator + b_serverPath + File.separator + "GeoVideo");
						}else{
							System.out.println("saveUserPath : "+ saveUserPath);
							jsonObj.put("filePath", saveUserPath + File.separator + "GeoVideo");
						}
						
						String[] tmpStrArr = null;
						int resTime = 0;
						String tmGpsStr = "";
						String tmGpsStr1 = "";
						String tmGpsStr2 = "";
						String tmGpsStr3 = "";
						double tmGepInt = 0.0;
						boolean updatePostion = false;
						SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date oldDate = null;
						Date nowDate = null;
						boolean dateChk = false;
						long diff = 0;
						boolean isDji = false;
						int nowSec = 0;
						int oldSec = 0;
						String djiStr = "";
						
						resGpsDataArr = vEncoding.convertToGpx(getVideoGpsFileFull);
						if(resGpsDataArr != null && resGpsDataArr.size() > 0){
							for(int j=0;j<resGpsDataArr.size();j++){
								tmGpsStr = "";
								tmGpsStr1 = "";
								tmGpsStr2 = "";
								tmGpsStr3 = "";
								tmGepInt = 0.0;
								djiStr = "";
								
								System.out.println(resGpsDataArr.get(j));
								if(resGpsDataArr.get(j) != null && !"null".equals(resGpsDataArr.get(j)) && !"".equals(resGpsDataArr.get(j))){
									tmpStrArr = resGpsDataArr.get(j).split(":");
									if("Duration".equals(tmpStrArr[0].trim()) && tmpStrArr.length> 3 &&
											tmpStrArr[1] != null && tmpStrArr[2] != null && tmpStrArr[3] != null){
										if(!"".equals(tmpStrArr[1].trim()) && !"".equals(tmpStrArr[2].trim()) && !"".equals(tmpStrArr[3].trim())){
											resTime = Integer.valueOf(tmpStrArr[1].trim())*3600 + Integer.valueOf(tmpStrArr[2].trim())*60 + Integer.valueOf(tmpStrArr[3].trim()); 
										}
									}else if(!isDji && tmpStrArr.length> 5 &&  tmpStrArr[0] != null && tmpStrArr[0].contains("GPS Date Time") &&
										tmpStrArr[1] != null && tmpStrArr[1].trim() != null && !"".equals(tmpStrArr[1].trim()) &&
										tmpStrArr[2] != null && tmpStrArr[2].trim() != null && !"".equals(tmpStrArr[2].trim()) &&
										tmpStrArr[3] != null && tmpStrArr[3].trim() != null && !"".equals(tmpStrArr[3].trim()) &&
										tmpStrArr[4] != null && tmpStrArr[4].trim() != null && !"".equals(tmpStrArr[4].trim()) && 
										tmpStrArr[5] != null && tmpStrArr[5].trim() != null && !"".equals(tmpStrArr[5].trim())){
									
											tmGpsStr = tmpStrArr[1].trim() +"-"+ tmpStrArr[2].trim() +"-"+ tmpStrArr[3].trim();
											tmGpsStr += ":"+ tmpStrArr[4].trim()+":"+ tmpStrArr[5].trim().substring(0,2);
											nowDate = transFormat.parse(tmGpsStr);
											if(!dateChk){
												oldDate = transFormat.parse(tmGpsStr);
												dateChk = true;
												chJsonObj.put("time", 0);
											}else{
												diff = (nowDate.getTime() - oldDate.getTime())/1000;
												oldDate = transFormat.parse(tmGpsStr);
												chJsonObj.put("time", diff);
											}
									}else if(!isDji && tmpStrArr.length> 1 && tmpStrArr[0] != null && tmpStrArr[0].contains("Latitude") && tmpStrArr[1] != null){
										tmGpsStr = tmpStrArr[1].trim();
										tmGpsStr1 = tmGpsStr.substring(0, tmGpsStr.indexOf("deg")).trim();
										tmGpsStr2 = tmGpsStr.substring(tmGpsStr.indexOf("deg")+3, tmGpsStr.indexOf("'")).trim();
										tmGpsStr3 = tmGpsStr.substring(tmGpsStr.indexOf("'")+1, tmGpsStr.indexOf("\"")).trim();
										if(tmGpsStr1 != null && !"".equals(tmGpsStr1) && tmGpsStr2 != null && !"".equals(tmGpsStr2) && 
												tmGpsStr3 != null && !"".equals(tmGpsStr3)){
											tmGepInt = Integer.valueOf(tmGpsStr1) + (Double.valueOf(tmGpsStr2)/60) + (Double.valueOf(tmGpsStr3)/3600);
											chJsonObj.put("lat", tmGepInt);
										}
									}else if(!isDji && tmpStrArr.length> 1 && tmpStrArr[0] != null && tmpStrArr[0].contains("Longitude") && tmpStrArr[1] != null){
										tmGpsStr = tmpStrArr[1].trim();
										tmGpsStr1 = tmGpsStr.substring(0, tmGpsStr.indexOf("deg")).trim();
										tmGpsStr2 = tmGpsStr.substring(tmGpsStr.indexOf("deg")+3, tmGpsStr.indexOf("'")).trim();
										tmGpsStr3 = tmGpsStr.substring(tmGpsStr.indexOf("'")+1, tmGpsStr.indexOf("\"")).trim();
										if(tmGpsStr1 != null && !"".equals(tmGpsStr1) && tmGpsStr2 != null && !"".equals(tmGpsStr2) && 
												tmGpsStr3 != null && !"".equals(tmGpsStr3)){
											tmGepInt = Integer.valueOf(tmGpsStr1) + (Double.valueOf(tmGpsStr2)/60) + (Double.valueOf(tmGpsStr3)/3600);
											chJsonObj.put("lon", tmGepInt);
											
											if(!updatePostion){
												param4.put("idx", parentIndex);
												param4.put("latitude", String.valueOf(chJsonObj.get("lat")));
												param4.put("longitude", String.valueOf(chJsonObj.get("lon")));
												param4.put("xmlData", "");
												dataDao.updateVideo(param4);
												updatePostion = true;
												jsonArr.add(chJsonObj);
											}
											
											//시간 만큼 좌표 추가
											if(diff > 0){
												for(int kl=0; kl<diff;kl++){
													chJsonObj.put("time", 1);
													jsonArr.add(chJsonObj);
												}
											}
											diff = 0;
											chJsonObj = new JSONObject();
										}
									}else if(tmpStrArr[0] != null && "IS_DJI".equals(tmpStrArr[0])){
										isDji = true;
									}else if(isDji && tmpStrArr[0] != null && tmpStrArr[0].contains("Sample Time")){
										diff = 0;
										nowSec = 0;
										if(tmpStrArr.length> 1 && tmpStrArr[1] != null && tmpStrArr[1].contains("s") && tmpStrArr[1].trim() != null && !"".equals(tmpStrArr[1].trim())){
											djiStr = tmpStrArr[1].trim();
											djiStr = djiStr.replace(".00 s", "");
											djiStr = djiStr.replace("s", "");
											if(djiStr != null && !"".equals(djiStr)){
												djiStr = djiStr.trim();
												nowSec = Integer.parseInt(djiStr);
											}
											
										}else if(tmpStrArr.length> 3 && tmpStrArr[1] != null && tmpStrArr[1].trim() != null && !"".equals(tmpStrArr[1].trim()) && 
												 tmpStrArr[2] != null && tmpStrArr[2].trim() != null && !"".equals(tmpStrArr[2].trim()) &&
												 tmpStrArr[3] != null && tmpStrArr[3].trim() != null && !"".equals(tmpStrArr[3].trim())){
											nowSec = Integer.valueOf(tmpStrArr[1].trim())*3600 + Integer.valueOf(tmpStrArr[2].trim())*60 + Integer.valueOf(tmpStrArr[3].trim());
										}
										if(!dateChk){
											dateChk = true;
											chJsonObj.put("time", 0);
										}else{
											diff = nowSec - oldSec;
											if(diff < 0){diff = 0;}
											oldSec = nowSec;
											chJsonObj.put("time", diff);
										}
									}else if(isDji && tmpStrArr.length> 1 && tmpStrArr[0] != null && tmpStrArr[0].contains("Text") && tmpStrArr[1] != null && tmpStrArr[1].contains("GPS")){
										tmGpsStr = tmpStrArr[1];
										tmGpsStr = tmGpsStr.substring(tmGpsStr.indexOf("GPS"));
										tmpStrArr = tmGpsStr.split(",");
										tmGpsStr1 = tmpStrArr[0];
										tmGpsStr1 = tmGpsStr1.substring(tmGpsStr1.indexOf("(")+1);
										if(tmGpsStr1 != null && !"".equals(tmGpsStr1)){
											tmGpsStr1 = tmGpsStr1.trim();
										}
										if(tmpStrArr[1] != null && !"".equals(tmpStrArr[1])){
											tmGpsStr2 = tmpStrArr[1].trim();
										}
										if(tmGpsStr1 != null && !"".equals(tmGpsStr1) && tmGpsStr2 != null && !"".equals(tmGpsStr2)){
											
											chJsonObj.put("lon", tmGpsStr1);
											chJsonObj.put("lat", tmGpsStr2);
											
											if(!updatePostion){
												param4.put("idx", parentIndex);
												param4.put("latitude", String.valueOf(chJsonObj.get("lat")));
												param4.put("longitude", String.valueOf(chJsonObj.get("lon")));
												param4.put("xmlData", "");
												dataDao.updateVideo(param4);
												updatePostion = true;
											}
											jsonArr.add(chJsonObj);
											
											//시간 만큼 좌표 추가
											if(diff > 1){
												for(int kl=1; kl<diff;kl++){
													chJsonObj.put("time", 1);
													jsonArr.add(chJsonObj);
												}
											}
											chJsonObj = new JSONObject();
										}
									}
								}
							}
						}
						
						System.out.println(" gpsData : "+ jsonObj.toString());
						jsonObj.put("gpsData", jsonArr);
						int resLength = jsonArr.size();
						
						if(jsonArr != null && jsonArr.size() > 0){
							String newXmlFilePathStr = tmpGpxFilePathDir + File.separator + saveFileNameOrg;
							BufferedWriter fw = new BufferedWriter(new FileWriter(newXmlFilePathStr + ".txt", true));
					        // 파일안에 문자열 쓰기
					        fw.write(jsonObj.toString());
					        fw.flush();
					        // 객체 닫기
					        fw.close();
					        
					        BufferedWriter fw2 = new BufferedWriter(new FileWriter(newXmlFilePathStr + "_modify.txt", true));
					        // 파일안에 문자열 쓰기
					        fw2.write(jsonObj.toString());
					        fw2.flush();
					        // 객체 닫기
					        fw2.close();
					        
					        param4 = new HashMap<String, String>();
							param4.put("idx", parentIndex);
							param4.put("gpsData", jsonObj.toString());
							dataDao.updateVideo(param4);
					        
						}
					}
				}else if("2".equals(gpsFileTypeData) || "3".equals(gpsFileTypeData)){
					if(filesXml != null && filesXml.size() > 0){
						JSONObject jsonObj = new JSONObject();
						JSONArray jsonArr = new JSONArray();
						JSONObject chJsonObj = new JSONObject();
						if(isServerUrl){
							jsonObj.put("filePath", b_serverUrl + File.separator + b_serverPath + File.separator + "GeoVideo");
						}else{
							jsonObj.put("filePath", saveUserPath + File.separator + "GeoVideo");
						}
						
						if("2".equals(gpsFileTypeData)){
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							dbf.setIgnoringElementContentWhitespace(true);
							DocumentBuilder db = dbf.newDocumentBuilder();
							
							for(int k=0;k<filesXml.size();k++){
								tmpGpxFilePathDir = filesXml.get(k).substring(0, filesXml.get(k).lastIndexOf(File.separator));
								nowFileName = filesXml.get(k).substring(filesXml.get(k).lastIndexOf(File.separator)+1);
								
								Document firstDoc = db.parse(tmpGpxFilePathDir + File.separator + nowFileName);
								NodeList nList1 = firstDoc.getElementsByTagName("trkpt");
								System.out.println("nList1 : "+ nList1.getLength());
								
								latitude = "0";
								longitude = "0";
								if(k == 0){
									System.out.println("----------------------------");
									Element eElement = (Element)nList1.item(0);
									latitude = eElement.getAttribute("lat");
									longitude = eElement.getAttribute("lon");
									param4.put("idx", parentIndex);
									param4.put("latitude", latitude);
									param4.put("longitude", longitude);
									param4.put("xmlData", "");
									dataDao.updateVideo(param4);
								}
								latitude = "0";
								longitude = "0";
								
								if(nList1 != null && nList1.getLength() > 0){
									for(int j=0; j<nList1.getLength(); j++){
										Element eElement = (Element)nList1.item(j);
										latitude = eElement.getAttribute("lat");
										longitude = eElement.getAttribute("lon");
										System.out.println("latitude : " + latitude + " longitude : " + longitude);
										chJsonObj = new JSONObject();
										chJsonObj.put("lat",latitude);
										chJsonObj.put("lon",longitude);
										jsonArr.add(chJsonObj);
									}
								}
								
							}
							
							jsonObj.put("gpsData", jsonArr);
							
							String newXmlFilePathStr = tmpGpxFilePathDir + File.separator + saveFileNameOrg;
							if(jsonArr != null && jsonArr.size() > 0){
								BufferedWriter fw = new BufferedWriter(new FileWriter(newXmlFilePathStr + ".txt", true));
						        // 파일안에 문자열 쓰기
						        fw.write(jsonObj.toString());
						        fw.flush();
						        // 객체 닫기
						        fw.close();
							}
			
							System.out.println(" gpsData : "+ jsonObj.toString());
							if(jsonArr != null && jsonArr.size() > 0 && ffmpegFileArr.size() > 0){
								int resTime = getVideoMaxTime(ffmpegFileArr);
								String makeResult = getTrkptForList(jsonArr, resTime, newXmlFilePathStr);
							}
							
						}else if("3".equals(gpsFileTypeData)){
							BufferedReader br = null;
							String readStr = "";
							String[] readStrArr = null;
							boolean chkUpVideo = false;
							int nowSec = 0;
							int resRow = 0;
							int tmpSec = 0;
							
							for(int k=0;k< filesXml.size();k++){
								tmpGpxFilePathDir = filesXml.get(k).substring(0, filesXml.get(k).lastIndexOf(File.separator));
								nowFileName = filesXml.get(k).substring(filesXml.get(k).lastIndexOf(File.separator)+1);
								br = new BufferedReader(new FileReader(tmpGpxFilePathDir + File.separator + nowFileName));
								readStr = "";
								
								while ((readStr = br.readLine()) != null){
									readStrArr = null;
									latitude = "0";
									longitude = "0";
									System.out.println("readStr : " + readStr);
							        if(readStr != null ){
							        	if(readStr.contains("GPS")){
							        		readStr = readStr.substring(readStr.indexOf("GPS")+4, readStr.indexOf(")"));
								        	readStrArr = readStr.split(",");
								        	if(readStrArr != null && readStrArr.length > 2){
								        		System.out.println("----------------------------");
								        		latitude = readStrArr[0].trim();
									        	longitude = readStrArr[1].trim();
									        	if(!chkUpVideo){
									        		param4.put("idx", parentIndex);
													param4.put("latitude", latitude);
													param4.put("longitude", longitude);
													dataDao.updateVideo(param4);
													chkUpVideo = true;
									        	}
									        	System.out.println("latitude : " + latitude + " longitude : " + longitude);
												if(resRow < nowSec){
													for(int lk=resRow;lk<nowSec;lk++){
														chJsonObj = new JSONObject();
														chJsonObj.put("lat",latitude);
														chJsonObj.put("lon",longitude);
														jsonArr.add(chJsonObj);
														resRow++;
													}
												}
								        	}
							        	}else if(readStr.contains("-->")){
							        		readStr = readStr.substring(readStr.indexOf("-->")+3, readStr.lastIndexOf(","));
							        		readStrArr = readStr.split(":");
							        		if(readStrArr[0] != null && !"".equals(readStrArr[0]) && readStrArr[1] != null && !"".equals(readStrArr[1]) &&
							        				readStrArr[2] != null && !"".equals(readStrArr[2])){
							        			tmpSec = Integer.parseInt(readStrArr[0].trim())*3600 + Integer.parseInt(readStrArr[1].trim())*60 + Integer.parseInt(readStrArr[2].trim());
							        			if(tmpSec > nowSec){
							        				nowSec = tmpSec;
							        			}else{
							        				continue;
							        			}
							        		}
							        	}
							        }
							    }
								br.close();
							}
							
							jsonObj.put("gpsData", jsonArr);
							
							String newXmlFilePathStr = tmpGpxFilePathDir + File.separator + saveFileNameOrg;
							if(jsonArr != null && jsonArr.size() > 0){
								BufferedWriter fw = new BufferedWriter(new FileWriter(newXmlFilePathStr + ".txt", true));
						        // 파일안에 문자열 쓰기
						        fw.write(jsonObj.toString());
						        fw.flush();
						        // 객체 닫기
						        fw.close();
						        
						        BufferedWriter fw2 = new BufferedWriter(new FileWriter(newXmlFilePathStr + "_modify.txt", true));
						        // 파일안에 문자열 쓰기
						        fw2.write(jsonObj.toString());
						        fw2.flush();
						        // 객체 닫기
						        fw2.close();
						        
						        param4 = new HashMap<String, String>();
								param4.put("idx", parentIndex);
								param4.put("gpsData", jsonObj.toString());
								dataDao.updateVideo(param4);
							}
			
						}
						
					} //filesXml not null
				}
			}else if(fileType != null && "worldFile".equals(fileType)){
				String idxArrStr = "";
				String[] idxArr = null;
				File fXmlSaveFile = null;
				Element eElement = null;
				boolean isSuccess = false;
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringElementContentWhitespace(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				for(int j=0; j<fileItemList.size(); j++){
					item = fileItemList.get(j);
					tmpGpxFilePathDirFull = "";
					fileNameMap = new HashMap<String, Object>();
					fileMap = new HashMap<String, String>();
					tmpPathStr = "";
					idxArrStr = "";
					idxArr = null;
					fXmlSaveFile = null;
					eElement = null;
					isSuccess = false;
					nowFileName = "";
					
					if(!item.isFormField() && files.get(j) != null) {
						fileMap = files.get(j);
						if(fileMap != null && fileMap.get("file") != null && fileMap.get("file") != ""){
							tmpGpxFilePathDirFull = fileMap.get("file");
//							tmpGpxFilePathDir = tmpGpxFilePathDirFull.substring(tmpGpxFilePathDirFull.lastIndexOf("\\")+1, tmpGpxFilePathDirFull.length());
							tmpGpxFilePathDir = tmpGpxFilePathDirFull.substring(0, tmpGpxFilePathDirFull.lastIndexOf(File.separator));
							nowFileName  = tmpGpxFilePathDirFull.substring(tmpGpxFilePathDirFull.lastIndexOf(File.separator)+1);
							
							item.write(new File(tmpGpxFilePathDir + File.separator + nowFileName));
							item.delete();
							
							removeFileList.add(new File(tmpGpxFilePathDir + File.separator + nowFileName));
							
							fis = new FileInputStream(tmpGpxFilePathDir + File.separator + nowFileName);
							isSuccess = ftp.storeFile(nowFileName, fis);
							
							if(isSuccess) {
								JSONObject jsonObj = new JSONObject();
								JSONArray jsonArr = new JSONArray();
								JSONObject chJsonObj = new JSONObject();
								jsonObj.put("filePath", saveUserPath + File.separator + "GeoVideo");
								
								fXmlSaveFile = new File(tmpGpxFilePathDir + File.separator + nowFileName);
								Document firstDoc = db.parse(fXmlSaveFile);
								NodeList nList1 = firstDoc.getElementsByTagName("trkpt");
								eElement = (Element)nList1.item(0);
								latitude = eElement.getAttribute("lat");
								longitude = eElement.getAttribute("lon");
								
								param4 = new HashMap<String, String>();
								param4.put("latitude", latitude);
								param4.put("longitude", longitude);
								
								idxArrStr = fileMap.get("idx");
								if(idxArrStr != null && !"".equals(idxArrStr)){
									idxArr = idxArrStr.split(",");
									if(idxArr != null && idxArr.length > 0){
										for(int m=0; m<idxArr.length; m++){
											param4.put("idx", idxArr[m]);
											param4.put("xmlData", "");
											dataDao.updateVideo(param4);
										    System.out.println(tmpGpxFilePathDir + File.separator + nowFileName + "파일 FTP 업로드 성공");
										}
										////////////////////////////////////////////
										Node nextResults = firstDoc.getElementsByTagName("trkseg").item(0);
									      while (nextResults.hasChildNodes()) {
									        Node kid = nextResults.getFirstChild();
									        if(kid != null){
									        	Element tmpElement = (Element)kid;
									        	latitude = tmpElement.getAttribute("lat");
												longitude = tmpElement.getAttribute("lon");
												
												chJsonObj = new JSONObject();
												chJsonObj.put("lat",latitude);
												chJsonObj.put("lon",longitude);
												jsonArr.add(chJsonObj);
									        }
									      }
									    jsonObj.put("gpsData", jsonArr);
										String newXmlFilePathStr = tmpGpxFilePathDir + File.separator + saveFileNameOrg;
										BufferedWriter fw = new BufferedWriter(new FileWriter(newXmlFilePathStr  + ".txt", true));
								        // 파일안에 문자열 쓰기
								        fw.write(jsonObj.toString());
								        fw.flush();
								        // 객체 닫기
								        fw.close();
										////////////////////////////////////////////
										if(nList1 != null && nList1.getLength() > 0){
											ffmpegFileArr = new ArrayList<String>();
											ffmpegFileArr = getIdxForFilePath(idxArr);
											if(ffmpegFileArr != null && ffmpegFileArr.size() > 0){
												int resTime = getVideoMaxTime(ffmpegFileArr);
												String makeResult = getTrkptForList(jsonArr, resTime, newXmlFilePathStr);
												System.out.println("makeResult : " +makeResult);
												if(makeResult != null && "success".equals(makeResult)){
													String makrFileRes = newXmlFilePathStr +"_modify.gpx";
													removeFileList.add(new File(makrFileRes));
													fis = new FileInputStream(makrFileRes);
//													String makeFileFtp = makrFileRes.substring(makrFileRes.lastIndexOf("\\")+1, makrFileRes.length());
													String makeFileFtp = makrFileRes.substring(makrFileRes.lastIndexOf(File.separator)+1, makrFileRes.length());
													isSuccess = ftp.storeFile(makeFileFtp, fis);
													System.out.println("makrFileRes : " +makrFileRes + " makeFileFtp :" +makeFileFtp + " isSuccess :"+ isSuccess);
												}
											}
										}
									}//idx arr for end
								}
					       }
						}
						//FTP end---------------------------------------------------------------------------------------------------------
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			if(fileType != null && "videoFile".equals(fileType)){
				removeList = new ArrayList<Object>();
				if(isServerUrl){
					for(int m=0;m<files.size();m++){
				    	if(files.get(m) != null){
				    		if(files.get(m).get("idx") != null && !"".equals(files.get(m).get("idx")) && !"null".equals(files.get(m).get("idx"))){
				    			removeList.add(files.get(m).get("idx"));
				    		}
				    	}
				    }	
				}
			    
			    objParam.clear();
			    objParam.put("fileIdxs", removeList);
			    objParam.put("status", "ERROR");
			    dataDao.updateContentChildStatus(objParam);
			    
			    param2.clear();
			    param2.put("loginId", loginId);
			    param2.put("idx", String.valueOf(logKey));
			    param2.put("status", "ERROR");
			    dataDao.updateLog(param2);
			}
			fileType = null;
		}finally {
			if(fileType != null && "worldFile".equals(fileType) && removeFileList != null && removeFileList.size() > 0){
				for(int m=0;m<removeFileList.size();m++){
					removeFileList.get(m).delete();
				}
			}
		}
		
		if(fileType != null && "videoFile".equals(fileType)){
			EncodingController encodingController = new EncodingController(loginId, logKey, fileNameList, saveUserPath, b_serverUrl, b_serverId, b_serverPass, b_serverPort, b_serverPath, isServerUrl);
			encodingController.setDataAPI(dataDao);
			encodingController.start();
		}
    }
	
	//nodeList to arraylist
	private String getTrkptForList(JSONArray getNodeList, int resTime, String orgFileName) {
		List<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
		Map<String, String> trkpMap = new HashMap<String, String>();
		String makeResult = "error";
		JSONObject tmpObj = new JSONObject();
		
		if(getNodeList != null && getNodeList.size() > 0){
			for(int n=0;n<getNodeList.size(); n++){
				if(getNodeList.get(n) != null){
					tmpObj = (JSONObject)getNodeList.get(n);
					trkpMap = new HashMap<String, String>();
					Iterator<String> keysItr = tmpObj.keys();
				    while(keysItr.hasNext()) {
				        String key = keysItr.next();
				        String value = String.valueOf(tmpObj.get(key));
				        trkpMap.put(key, value);
				    }
					resultList.add(trkpMap);
				}
			}
			
			if(resultList != null && resultList.size() > 0 && resTime > 0){
				makeResult = makeFileToList(resultList, resTime, orgFileName);
			}
		}
		
		return makeResult;
	}
	
	// file full path list to make max time
	private int getVideoMaxTime(List<String> gpxFilePathDirFullArr) {
		String osName = System.getProperty("os.name").toLowerCase();
		String osffmpeg = "";
		
		if(osName.indexOf("win") >= 0){
			osffmpeg = "win";
		}else if(osName.indexOf("mac") >= 0){
			osffmpeg = "mac";
		}else if(osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0 ){
			osffmpeg = "linux";
		}else if(osName.indexOf("sunos") >= 0){
			osffmpeg = "sunos";
		}
		
		int resTime = 0;
		FFmpegSetting ffmpegSetting = new FFmpegSetting();
		String ffmpegPathStr = ffmpegSetting.getFfmpeg_dir_and_file_name();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		FFmpeg ffmpeg = new FFmpeg();
		
		String tmpGpxFilePathDirFull = "";
		String[] message = null;
		String ffmpegNowFilePath = "";
		String ffmpegVal = "";
		String [] ffmpegTmpArr = null;
		String totalDurationStr = null;
		Date maxDate = null;
		Date nowDate = null;
		int compare = 0;
		
		try {
			if(gpxFilePathDirFullArr != null && gpxFilePathDirFullArr.size()>0){
				for(int i=0; i<gpxFilePathDirFullArr.size();i++){
					tmpGpxFilePathDirFull = gpxFilePathDirFullArr.get(i);
					message = null;
					ffmpegNowFilePath = "";
					ffmpegVal = "";
					ffmpegTmpArr = null;
					totalDurationStr = null;
					nowDate = null;
					compare = 0;
					
					if(tmpGpxFilePathDirFull != null && !"".equals(tmpGpxFilePathDirFull) && !"null".equals(tmpGpxFilePathDirFull)){
						if(osffmpeg != null && !"".equals(osffmpeg)){
							if("win".equals(osffmpeg))
							{
								message = new String[] {
										ffmpegPathStr,
										"-i",
										tmpGpxFilePathDirFull,
										"2>&1 | grep Duration | cut -d ' ' -f 4 | sed s/,//"
								};
								
								ffmpegNowFilePath = tmpGpxFilePathDirFull.substring(0,tmpGpxFilePathDirFull.lastIndexOf("\\"));
								ffmpegVal = ffmpeg.runFFmpeg(tmpGpxFilePathDirFull, ffmpegNowFilePath, message, "getTime");
							}else if("linux".equals(osffmpeg))
							{
								message = new String[] {
										"ffmpeg",
										"-i",
										tmpGpxFilePathDirFull,
										"2>&1 | grep Duration | cut -d ' ' -f 4 | sed s/,//"
								};
								
								ffmpegNowFilePath = tmpGpxFilePathDirFull.substring(0,tmpGpxFilePathDirFull.lastIndexOf(File.separator));
//								ffmpegVal = ffmpeg.runFFmpeg_linux(tmpGpxFilePathDirFull, ffmpegNowFilePath, message, "getTime");
								ffmpegVal = ffmpeg.runFFmpeg_linux(tmpGpxFilePathDirFull, message, "getTime");
							}
						}
						System.out.println("ffmpegNowFilePath : " + ffmpegNowFilePath + " ffmpegVal : " + ffmpegVal);
						
						if(ffmpegVal != null && !"".equals(ffmpegVal) && !"null".equals(ffmpegVal)){
							ffmpegVal = ffmpegVal.split(",")[0];
							if(ffmpegVal != null && !"".equals(ffmpegVal) && !"null".equals(ffmpegVal)){
								ffmpegTmpArr = ffmpegVal.split(":");
								totalDurationStr = ffmpegTmpArr[1].trim() + ":"+ ffmpegTmpArr[2].trim() +":" + ffmpegTmpArr[3].trim();
								if(totalDurationStr != null && !"::".equals(totalDurationStr)){
									nowDate = sdf.parse(totalDurationStr);
									if(maxDate != null){
										compare = nowDate.compareTo(maxDate);
										if(compare > 0){
											maxDate = nowDate;
										}
									}else{
										maxDate = nowDate;
									}
								}
							}
						}
					}
				}
			}
			
			resTime = maxDate.getHours()*60*60;
			resTime += maxDate.getMinutes()*60;
			resTime += maxDate.getSeconds();
			
			System.out.println("종료 정보 resTime : "+ resTime);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return resTime;
	}
	
	//video idx to filePath
	private List<String> getIdxForFilePath(String[] videoChildIdx){
		List<String> resList = new ArrayList<String>();
		List<Object> childList = new ArrayList<Object>();
		HashMap<String,String> childParam = new HashMap<String, String>();
		
		if(videoChildIdx != null && videoChildIdx.length > 0){
			for(int i=0; i< videoChildIdx.length;i++){
				String nowVideoIdx = videoChildIdx[i];
				if(nowVideoIdx != null && !"".equals(nowVideoIdx) && !"null".equals(nowVideoIdx)){
					childParam.put("parentIdx", nowVideoIdx);
					childList = dataDao.selectContentChildList(childParam);
					if(childList != null && childList.size() > 0){
						for(int j=0; j< childList.size();j++){
							childParam = new HashMap<String, String>();
							childParam = (HashMap)childList.get(j);
							if(childParam != null){
								if(isServerUrl){
									String file_dir = "http://"+ b_serverUrl + "/shares/"+b_serverPath +"/GeoVideo/"+childParam.get("filename");
									System.out.println("file_dir = "+file_dir);
									File file = new File(saveUserPath);
									if(!file.exists()) file.mkdir();
									file = new File(saveUserPath+"/GeoVideo/"+ childParam.get("filename"));
									
									try {
										URL gamelan = new URL(file_dir);
										Authenticator.setDefault(new Authenticator()
										{
										  @Override
										  protected PasswordAuthentication getPasswordAuthentication()
										  {
										    return new PasswordAuthentication(b_serverId, b_serverPass.toCharArray());
										  }
										});
										HttpURLConnection urlConnection = (HttpURLConnection)gamelan.openConnection();
										
							            urlConnection.connect();
							            FileUtils.copyURLToFile(gamelan, file);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										if(file.exists()){file.delete();}
									}
								}								
								
								resList.add(saveUserPath+ File.separator + "GeoVideo"+  File.separator + childParam.get("filename"));
							}
						}
					}
				}
			}
		}
		
		return resList;
	}
	
	// nodelist maxTime -> makeNew File
	private String makeFileToList(List<Map<String,String>> gpxList, int maxTimeInt, String orgFileName){
		System.out.println("makeFileToList ");
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		JSONObject chJsonObj = new JSONObject();
		if(isServerUrl){
			jsonObj.put("filePath", b_serverUrl + File.separator + b_serverPath + File.separator + "GeoVideo");
		}else{
			jsonObj.put("filePath", saveUserPath + File.separator + "GeoVideo");
		}
		
		double lat1 = 0;
		double lon1 = 0;
		double lat2 = 0;
		double lon2 = 0;
		BigDecimal distance = new BigDecimal(0); 
		BigDecimal totalDistance = new BigDecimal(0); 
		List<BigDecimal> totalDistanceArr = new ArrayList<BigDecimal>();
		BigDecimal resDis = new BigDecimal(0);
		List<Double> angleArr = new ArrayList<Double>();
		BigDecimal maxTime = new BigDecimal(maxTimeInt);
		String makeResult = "error";
		
		if(gpxList != null && gpxList.size() > 1 && maxTimeInt > 1 ){
			if(gpxList.size() > maxTimeInt || gpxList.size() < maxTimeInt){
				for(int i=0; i<gpxList.size()-1; i++){
					lat1 = 0;
					lon1 = 0;
					lat2 = 0;
					lon2 = 0;
					distance = new BigDecimal(0); 
					
					if(gpxList.get(i).get("lat") != null && !"".equals(gpxList.get(i).get("lat")) && !"null".equals(gpxList.get(i).get("lat")) &&
							gpxList.get(i).get("lon") != null && !"".equals(gpxList.get(i).get("lon")) && !"null".equals(gpxList.get(i).get("lon")) &&
							gpxList.get(i+1).get("lat") != null && !"".equals(gpxList.get(i+1).get("lat")) && !"null".equals(gpxList.get(i+1).get("lat")) &&
							gpxList.get(i+1).get("lon") != null && !"".equals(gpxList.get(i+1).get("lon")) && !"null".equals(gpxList.get(i+1).get("lon"))){
						lat1 = Double.parseDouble(gpxList.get(i).get("lat"));
						lon1 = Double.parseDouble(gpxList.get(i).get("lon"));
						lat2 = Double.parseDouble(gpxList.get(i+1).get("lat"));
						lon2 = Double.parseDouble(gpxList.get(i+1).get("lon"));
						distance = getDistance(lat1, lon1, lat2, lon2);
						totalDistance = totalDistance.add(distance);
						totalDistanceArr.add(totalDistance);
						angleArr.add(getAngel(lat1, lon1, lat2, lon2));
					}
				}//end for
				
				resDis = (totalDistance.divide(maxTime,13, BigDecimal.ROUND_HALF_UP));
				System.out.println("resDis :  " + resDis);
				
				double xa = 0;
				double ya = 0;
				BigDecimal disa = new BigDecimal(0);
				BigDecimal disa2 = new BigDecimal(0);
				int arrCnt = 0;
				for(int i=0; i<maxTimeInt; i++){
					disa2 = new BigDecimal(0);
					chJsonObj = new JSONObject();
					if(i == 0){
						chJsonObj.put("lat", gpxList.get(i).get("lat"));
						chJsonObj.put("lon", gpxList.get(i).get("lon"));
					}else if(i == maxTimeInt-1){
						chJsonObj.put("lat", gpxList.get(gpxList.size()-1).get("lat"));
						chJsonObj.put("lon", gpxList.get(gpxList.size()-1).get("lon"));
					}else{
						if(i == 1){
							xa = Double.valueOf(gpxList.get(0).get("lat"));
							ya = Double.valueOf(gpxList.get(0).get("lon"));
						}
						disa = disa.add(resDis);
						if(disa.compareTo(totalDistanceArr.get(arrCnt)) == 1){
							BigDecimal tmpDisa = disa;
							for(int j=arrCnt+1;j<totalDistanceArr.size();j++){
								System.out.println("tmpDisa : " + tmpDisa + " totalDistanceArr : " + totalDistanceArr.get(j) + " : " + j);
								if(tmpDisa.compareTo(totalDistanceArr.get(j)) == 1){
								}else{
									disa2 = tmpDisa.subtract(totalDistanceArr.get(j-1));
									arrCnt = j;
									xa = Double.valueOf(gpxList.get(arrCnt).get("lat"));
									ya = Double.valueOf(gpxList.get(arrCnt).get("lon"));
									break;
								}
							}
						}else{
							if(arrCnt > 0){
								disa2 = disa.subtract(totalDistanceArr.get(arrCnt-1));
							}else{
								disa2 = disa;
							}
						}
						
						List<Double> resDouble = getNowPosition(xa, ya, disa2, angleArr.get(arrCnt));
						chJsonObj.put("lat", String.valueOf(resDouble.get(0)));
						chJsonObj.put("lon", String.valueOf(resDouble.get(1)));
					}
					jsonArr.add(chJsonObj);
				}
				jsonObj.put("gpsData", jsonArr);
				
				try {				
					BufferedWriter fw = new BufferedWriter(new FileWriter(orgFileName + "_modify.txt", true));
					// 파일안에 문자열 쓰기
					fw.write(jsonObj.toString());
					fw.flush();
					// 객체 닫기
					fw.close();
					
					HashMap<String, String> param4 = new HashMap<String, String>();
					param4.put("idx", parentIndex);
					param4.put("gpsData", jsonObj.toString());
					dataDao.updateVideo(param4);
		        	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			makeResult = "success";
		}else if(gpxList != null && gpxList.size() == 1 && maxTimeInt > 1 ){
			chJsonObj = new JSONObject();
			chJsonObj.put("lat", gpxList.get(0).get("lat"));
			chJsonObj.put("lon", gpxList.get(0).get("lon"));
			jsonArr.add(chJsonObj);
			jsonObj.put("gpsData", jsonArr);
			try {				
				BufferedWriter fw = new BufferedWriter(new FileWriter(orgFileName + "_modify.txt", true));
				// 파일안에 문자열 쓰기
				fw.write(jsonObj.toString());
				fw.flush();
				// 객체 닫기
				fw.close();
				
				HashMap<String, String> param4 = new HashMap<String, String>();
				param4.put("idx", parentIndex);
				param4.put("gpsData", jsonObj.toString());
				dataDao.updateVideo(param4);
	        	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return makeResult;
	}
	
	private BigDecimal getDistance(double x, double y, double x1, double y1) {
		double resultDouble = (double)Math.sqrt(Math.pow(Math.abs(x1-x), 2)+ Math.pow(Math.abs(y1-y), 2)); //7.087060472661628E-4
		double b = (double)Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1)); //7.087060472661628E-4
		
		BigDecimal bigVal = new BigDecimal(resultDouble);
		return bigVal;
	}
	
	private double getAngel(double x, double y, double x1, double y1) {
		double dx = x1 - x;
		double dy = y1 - y;
		return Math.toDegrees(Math.atan2(dy, dx));
	}
	
	private List<Double> getNowPosition(double x, double y, BigDecimal distance, double angel) {
		List<Double> resArr = new ArrayList<Double>();
		double ag = angel*Math.PI/180;
		
		double distanceDouble = distance.doubleValue();
		double x2 = x + distanceDouble * Math.cos(ag);
		double y2 = y + distanceDouble * Math.sin(ag);
		resArr.add(x2);
		resArr.add(y2);
		return resArr;
	}
	
}
