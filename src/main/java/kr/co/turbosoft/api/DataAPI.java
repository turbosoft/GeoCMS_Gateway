package kr.co.turbosoft.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.turbosoft.dao.DataDao;
import kr.co.turbosoft.dao.UserDao;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class DataAPI  {
	static Logger log = Logger.getLogger(DataAPI.class.getName());

	static DataDao dataDao = null;
	static UserDao userDao = null;
	
	private HashMap<String, String> param, result, result2, param2;
	private List<Object> resultList;
	private int resultIntegerValue;
	private String resultStringValue;

	public void setDataDao(DataDao dataDao){
		this.dataDao = dataDao;
	}
	public void setUserDao(UserDao userDao){
		this.userDao = userDao;
	}
	
	@RequestMapping(value = "/cms/getbase", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String baseService(@RequestParam("callback") String callback
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		
		//get Base
		
		result = dataDao.selectBase();
		
		if(result != null) {
			resultJSON.put("Code", 100);
			resultJSON.put("Message", Message.code100);
			resultJSON.put("Data", result);
		}
		else {
			resultJSON.put("Code", 200);
			resultJSON.put("Message", Message.code200);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/updateBase/{token}/{contentTab}/{contentTabType}/{boardTab}/{contentNum}/{boardNum}/{openAPI}/{latestView}/{mapZoom}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String updateBaseService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("contentTab") String contentTab
			, @PathVariable("contentTabType") String contentTabType
			, @PathVariable("boardTab") String boardTab
			, @PathVariable("contentNum") String contentNum
			, @PathVariable("boardNum") String boardNum
			, @PathVariable("openAPI") String openAPI
			, @PathVariable("latestView") String latestView
			, @PathVariable("mapZoom") String mapZoom
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		//token
		param = new HashMap<String, String>();
		param.put("token", token);
		
		result = userDao.selectUid(param);
		
		if(result != null){
			if("ADMIN".equals(result.get("TYPE"))){
				param.clear();
				param.put("contentTab", contentTab);
				param.put("contentTabType", contentTabType);
				param.put("boardTab", boardTab);
				param.put("contentNum", contentNum);
				param.put("boardNum", boardNum);
				param.put("openAPI", openAPI);
				param.put("latestView", latestView);
				param.put("mapZoom", mapZoom);
				
				resultIntegerValue = dataDao.updateBase(param);
				
				if(resultIntegerValue == 1) {
					resultJSON.put("Code", 100);
					resultJSON.put("Message", Message.code100);
					resultJSON.put("Data", JSONArray.fromObject(resultList));
				}else {
					resultJSON.put("Code", 200);
					resultJSON.put("Message", Message.code200);
				}
			}else{
				resultJSON.put("Code", 500);
				resultJSON.put("Message", Message.code500);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/updateTabName/{token}/{tempOldNameArr}/{tempTabArr}/{nowRightTabName}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String updateBaseService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("tempOldNameArr") List<String> tempOldNameArr
			, @PathVariable("tempTabArr") List<String> tempTabArr
			, @PathVariable("nowRightTabName") String nowRightTabName
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		//token
		param = new HashMap<String, String>();
		param.put("token", token);
		
		result = userDao.selectUid(param);
		
		if(result != null){
//			ObjectMapper mapper = new ObjectMapper();
//	        List<String> oldList = new ArrayList<String>();
//	        List<String> tempList = new ArrayList<String>();
//	        oldList = mapper.readValue(tempOldNameArr, new TypeReference<ArrayList<String>>() {});
//	        tempList = mapper.readValue(tempTabArr, new TypeReference<ArrayList<String>>() {});
//			param2 = new HashMap<String, List<String>>();
//			param2.put("newTabName", tempOldNameArr);
//			param2.put("oldTabName", tempTabArr);
			
			if(nowRightTabName != null){
				for(int i=0;i<tempOldNameArr.size();i++){
					param2 = new HashMap<String, String>();
					param2.put("newTabName", (String)tempTabArr.get(i));
					param2.put("oldTabName", (String)tempOldNameArr.get(i));
					
					if("content".equals(nowRightTabName)){
						resultIntegerValue += dataDao.updateTabNameImage(param2);
						resultIntegerValue += dataDao.updateTabNameVideo(param2);
					}else{
						resultIntegerValue += dataDao.updateTabNameBoard(param2);
					}
				}
			}
			
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
				resultJSON.put("Data", JSONArray.fromObject(resultList));
			}else {
				resultJSON.put("Code", 200);
				resultJSON.put("Message", Message.code200);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getBorder/{type}/{token}/{loginId}/{pageNum}/{contentNum}/{tabName}/{idx}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getBorderService(@RequestParam("callback") String callback
			, @PathVariable("type") String type
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("contentNum") String contentNum
			, @PathVariable("tabName") String tabName
			, @PathVariable("idx") String idx
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		List<Object> shareList = new ArrayList<Object>();
		
		if(type != null){
			if("list".equals(type)){
				loginId = loginId.replace("&nbsp", "");
				pageNum = pageNum.replace("&nbsp", "");
				contentNum = contentNum.replace("&nbsp", "");
				tabName = tabName.replace("&nbsp", "");
				
				param.put("type", type);
				param.put("pageNum", pageNum);
				param.put("contentNum", contentNum);
				param.put("tabName", tabName);
				param.put("loginId", loginId);
				
				if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(contentNum) && !"null".equals(contentNum)){
					int tmpPage = Integer.valueOf(pageNum);
					int tmpContent = Integer.valueOf(contentNum);
					int offset = tmpContent * (tmpPage-1);
					param.put("offset", String.valueOf(offset));
				}
				
				result = dataDao.selectBoardListLen(param);
			}else if("one".equals(type)){
				param.put("loginId", loginId);
				param.put("idx", idx);
				param.put("token", token);
				result = userDao.selectUid(param);
				if(userDao.selectUid(param) != null){
					param.put("shareIdx", idx);
					param.put("shareKind", "GeoCMS");
					shareList = userDao.selectShareUserList(param);
				}
			}else if("latest".equals(type)){
				param.put("loginId", loginId);
				param.put("contentNum", contentNum);
			}
			
			resultList = dataDao.selectBoardList(param);
		}
		
		if(resultList != null && resultList.size() != 0) {
			resultJSON.put("Code", 100);
			resultJSON.put("Message", Message.code100);
			resultJSON.put("Data", JSONArray.fromObject(resultList));
			if(result != null){
				resultJSON.put("DataLen", result.get("TOTAL_CNT"));
			}
			if(shareList != null && shareList.size() > 0){
				resultJSON.put("shareList", shareList);
			}
		}
		else {
			resultJSON.put("Code", 200);
			resultJSON.put("Message", Message.code200);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/saveBorder/{token}/{loginId}/{title}/{content}/{files}/{filePath}/{tabName}/{idx}/{shareType}/{addShare}/{removeShare}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String saveBorderService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("title") String title
			, @PathVariable("content") String content
//			, Map<String, String> content
//			, @RequestParam Map<String, String> content
//			, @PathVariable("content") Object content
//			, @RequestParam(value = "content") Map<String, String> content
//			, @PathVariable("content") String content
//			, @PathVariable("content") Map<String, String> content
//			, @RequestParam(value = "content[]") Set<String> tags
//			, @PathVariable Map<String, String> varsMap
			, @PathVariable("files") String files
			, @PathVariable("filePath") String filePath
			, @PathVariable("tabName") String tabName
			, @PathVariable("idx") String idx
			, @PathVariable("shareType") String shareType
			, @PathVariable("addShare") String addShare
			, @PathVariable("removeShare") String removeShare
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
try{		
//		ObjectMapper m = new ObjectMapper();
//		String iasdasd = m.writeValueAsString(content);
//		
//		Field[] fields = content.getClass().getDeclaredFields();
//		Map resultMap = new HashMap();
//		for(int i=0; i<=fields.length-1;i++){
//			fields[i].setAccessible(true);
//			resultMap.put(fields[i].getName(), fields[i].get(content));
//		}
//		
//		LinkedHashMap<String,Object> mappedObject = m.convertValue(content, LinkedHashMap.class);
//		String contents = (String)mappedObject.get("conent");
	
//		ObjectMapper m = new ObjectMapper();
//		HashMap<String,Object> mappedObject = m.readValue(content.toString(), HashMap.class);
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	 
//	Field asdwe = content.getClass().getField("content");
//		JSONObject tmpJson = (JSONObject)content;
//		String contents = (String)tmpJson.getString("content");
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			if(title != null && !"".equals(title) && !"null".equals(title)){ title = title.replaceAll("&sbsp","/"); }
			if(content != null && !"".equals(content) && !"null".equals(content)){ content = content.replaceAll("&sbsp","/"); }
			if(filePath != null && !"".equals(filePath) && !"null".equals(filePath)){ filePath = filePath.replaceAll("&sbsp","/"); }
			if(addShare != null && !"".equals(addShare) && !"null".equals(addShare)){ addShare = addShare.replaceAll("&nbsp",""); }
			if(removeShare != null && !"".equals(removeShare) && !"null".equals(removeShare)){ removeShare = removeShare.replaceAll("&nbsp",""); }
			
			param.clear();
			param.put("loginId", loginId);
			param.put("title", title);
			param.put("content", content);
			param.put("files", files);
			param.put("filePath", filePath);
			param.put("tabName", tabName);
			param.put("shareType", shareType);
			resultIntegerValue = dataDao.insertBoard(param);
			
			if(resultIntegerValue == 1) {
				if(param != null){
					if(shareType != null && "2".equals(shareType) && addShare != null && !"".equals(addShare) && !"null".equals(addShare)){
						HashMap<String, Object> tmpParam = new HashMap<String, Object>();
//						
						String[] shareTList = addShare.split(",");
						tmpParam.put("shareTList", shareTList);
						tmpParam.put("shareIdx", param.get("idx"));
						tmpParam.put("shareKind", "GeoCMS");
						resultIntegerValue = userDao.insertShare(tmpParam);
					}
				}
			}
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/updateBorder/{token}/{loginId}/{title}/{content}/{files}/{filePath}/{tabName}/{idx}/{shareType}/{addShare}/{removeShare}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String updateBorderService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("title") String title
			, @PathVariable("content") String content
			, @PathVariable("files") String files
			, @PathVariable("filePath") String filePath
			, @PathVariable("tabName") String tabName
			, @PathVariable("idx") String idx
			, @PathVariable("shareType") String shareType
			, @PathVariable("addShare") String addShare
			, @PathVariable("removeShare") String removeShare
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		//token
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			if(title != null && !"".equals(title) && !"null".equals(title)){ title = title.replaceAll("&sbsp","/"); }
			if(content != null && !"".equals(content) && !"null".equals(content)){ content = content.replaceAll("&sbsp","/"); }
			if(filePath != null && !"".equals(filePath) && !"null".equals(filePath)){ filePath = filePath.replaceAll("&sbsp","/"); }
			if(addShare != null && !"".equals(addShare) && !"null".equals(addShare)){ addShare = addShare.replaceAll("&nbsp",""); }
			if(removeShare != null && !"".equals(removeShare) && !"null".equals(removeShare)){ removeShare = removeShare.replaceAll("&nbsp",""); }
			
			param.clear();
			param.put("loginId", loginId);
			param.put("title", title);
			param.put("content", content);
			param.put("files", files);
			param.put("filePath", filePath);
			param.put("tabName", tabName);
			param.put("idx", idx);
			param.put("shareType", shareType);
			resultIntegerValue = dataDao.updateBoard(param);
			
			if(resultIntegerValue == 1) {
				if(shareType != null && !"".equals(shareType) && !"null".equals(shareType)){
					HashMap<String, Object> tmpParam = new HashMap<String, Object>();
					tmpParam.put("shareIdx", idx);
					tmpParam.put("shareKind", "GeoCMS");
					
					if("2".equals(shareType)){
						if(addShare != null && !"".equals(addShare) && !"null".equals(addShare)){
							String[] shareTList = addShare.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.insertShare(tmpParam);
						}
						if(removeShare != null && !"".equals(removeShare) && !"null".equals(removeShare)){
							String[] shareTList = removeShare.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.deleteShare(tmpParam);
						}
					}else{
						resultIntegerValue = userDao.deleteShare(tmpParam);
					}
				}
				
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getContent/{type}/{token}/{loginId}/{pageNum}/{contentNum}/{tabName}/{idx}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getContentService(@RequestParam("callback") String callback
			, @PathVariable("type") String type
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("contentNum") String contentNum
			, @PathVariable("tabName") String tabName
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		if(type != null){
			loginId = loginId.replace("&nbsp", "");
			pageNum = pageNum.replace("&nbsp", "");
			contentNum = contentNum.replace("&nbsp", "");
			tabName = tabName.replace("&nbsp", "");
			
			param.put("type", type);
			param.put("loginId", loginId);
			param.put("pageNum", pageNum);
			param.put("contentNum", contentNum);
			param.put("tabName", tabName);
			
			if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(contentNum) && !"null".equals(contentNum)){
				int tmpPage = Integer.valueOf(pageNum);
				int tmpContent = Integer.valueOf(contentNum);
				int offset = tmpContent * (tmpPage-1);
				param.put("offset", String.valueOf(offset));
			}
			
			resultList = dataDao.selectContentList(param);
			if("list".equals(type)){
				result = dataDao.selectContentListLen(param);
			}
		}
		
		if(resultList != null && resultList.size() != 0) {
			resultJSON.put("Code", 100);
			resultJSON.put("Message", Message.code100);
			resultJSON.put("Data", JSONArray.fromObject(resultList));
			if(result != null){
				resultJSON.put("DataLen", result.get("TOTAL_CNT"));
			}
		}
		else {
			resultJSON.put("Code", 200);
			resultJSON.put("Message", Message.code200);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getImage/{type}/{token}/{loginId}/{pageNum}/{contentNum}/{tabName}/{idx}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getImageService(@RequestParam("callback") String callback
			, @PathVariable("type") String type
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("contentNum") String contentNum
			, @PathVariable("tabName") String tabName
			, @PathVariable("idx") String idx
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		List<Object> shareList = new ArrayList<Object>();
		
		if(type != null){
			if("one".equals(type)){
				//token
				param.clear();
				param.put("token", token);
				result = userDao.selectUid(param);
				
				if(result == null){
					resultJSON.put("Code", 203);
					resultJSON.put("Message", Message.code203);
					return callback + "(" + resultJSON.toString() + ")";
				}
			}
			
			loginId = loginId.replace("&nbsp", "");
			pageNum = pageNum.replace("&nbsp", "");
			contentNum = contentNum.replace("&nbsp", "");
			tabName = tabName.replace("&nbsp", "");
			idx = idx.replace("&nbsp", "");
			
			param.put("type", type);
			param.put("loginId", loginId);
			param.put("pageNum", pageNum);
			param.put("contentNum", contentNum);
			param.put("tabName", tabName);
			param.put("idx", idx);
			
			if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(contentNum) && !"null".equals(contentNum)){
				int tmpPage = Integer.valueOf(pageNum);
				int tmpContent = Integer.valueOf(contentNum);
				int offset = tmpContent * (tmpPage-1);
				param.put("offset", String.valueOf(offset));
			}
			
			resultList = dataDao.selectImageList(param);
			if("list".equals(type)){
				result = dataDao.selectImageListLen(param);
			}else if("one".equals(type)){
				param.clear();
				param.put("shareIdx", idx);
				param.put("shareKind", "GeoPhoto");
				shareList = userDao.selectShareUserList(param);
			}
		}
		
		if(resultList != null && resultList.size() != 0) {
			resultJSON.put("Code", 100);
			resultJSON.put("Message", Message.code100);
			resultJSON.put("Data", JSONArray.fromObject(resultList));
			if(result != null){
				resultJSON.put("DataLen", result.get("TOTAL_CNT"));
			}
			if(shareList != null && shareList.size() > 0){
				resultJSON.put("shareList", shareList);
			}
		}
		else {
			resultJSON.put("Code", 200);
			resultJSON.put("Message", Message.code200);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
//	@RequestMapping(value = "/cms/saveImage/{token}/{loginId}/{title}/{content}/{filesStr}/{filePath}/{latitude}/{longitude}/{tabName}/{shareType}/{shareUser}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@RequestMapping(value = "/cms/saveImage/{token}/{loginId}/{title}/{content}/{filesStr}/{filePath}/{latitude}/{longitude}/{tabName}/{projectIdx}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String saveImageService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("title") String title
			, @PathVariable("content") String content
			, @PathVariable("filesStr") String filesStr
			, @PathVariable("filePath") String filePath
			, @PathVariable("latitude") String latitude
			, @PathVariable("longitude") String longitude
			, @PathVariable("tabName") String tabName
			, @PathVariable("projectIdx") String projectIdx
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		String shareType = "";
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			title = title.replaceAll("&sbsp","/");
			content = content.replaceAll("&sbsp","/");
			longitude = longitude.replace("&nbsp", "");
			latitude = latitude.replace("&nbsp", "");
			filePath = filePath.replaceAll("&sbsp","/");
			
			param.clear();
			param.put("loginId", loginId);
			param.put("title", title);
			param.put("content", content);
			param.put("filesStr", filesStr);
			param.put("filePath", filePath);
			param.put("longitude", longitude);
			param.put("latitude", latitude);
			param.put("tabName", tabName);
			param.put("projectIdx", projectIdx);
			resultList = dataDao.selectProjectList(param);
			
			if(resultList != null && resultList.size()>0){
				HashMap<String, String> tmpMap =  (HashMap<String, String>)resultList.get(0);
				if(tmpMap != null){
					shareType = String.valueOf(tmpMap.get("SHARETYPE"));
					param.put("shareType", shareType);
					
					resultIntegerValue = dataDao.insertImage(param);
					
					if(resultIntegerValue == 1) {
						if(param != null){
							if(shareType != null && "2".equals(shareType)){
								param.put("shareIdx", String.valueOf(param.get("idx")));
								param.put("shareKind", "GeoPhoto");
								resultIntegerValue = userDao.insertShareFormProject(param);
							}
						}
					}
				}
			}
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}

	@RequestMapping(value = "/cms/updateImage/{token}/{loginId}/{idx}/{title}/{content}/{tabName}/{shareType}/{addShareUser}/{removeShareUser}/{xmlData}/{longitude}/{latitude}/{editYes}/{editNo}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String updateImageService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("idx") String idx
			, @PathVariable("title") String title
			, @PathVariable("content") String content
			, @PathVariable("tabName") String tabName
			, @PathVariable("shareType") String shareType
			, @PathVariable("addShareUser") String addShareUser
			, @PathVariable("removeShareUser") String removeShareUser
			, @PathVariable("xmlData") String xmlData
			, @PathVariable("longitude") String longitude
			, @PathVariable("latitude") String latitude
			, @PathVariable("editYes") String editYes
			, @PathVariable("editNo") String editNo
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			title = title.replaceAll("&sbsp","/");
			content = content.replaceAll("&sbsp","/");
			addShareUser = addShareUser.replace("&nbsp", "");
			removeShareUser = removeShareUser.replace("&nbsp", "");
			if(xmlData != null && !"".equals(xmlData) && !"null".equals(xmlData)){
				xmlData = xmlData.replaceAll("&sbsp","/").replaceAll("&mbsp", "?").replaceAll("&pbsp", "#").replace("&obsp", ".");
			}
			editYes = editYes.replace("&nbsp", "");
			editNo = editNo.replace("&nbsp", "");
			
			longitude = longitude.replace("&nbsp", "");
			latitude = latitude.replace("&nbsp", "");
			
			param.clear();
			param.put("loginId", loginId);
			param.put("idx", idx);
			param.put("title", title);
			param.put("content", content);
			param.put("tabName", tabName);
			param.put("shareType", shareType);
			param.put("xmlData", xmlData);
			param.put("longitude", longitude);
			param.put("latitude", latitude);
			resultIntegerValue = dataDao.updateImage(param);
			
			if(resultIntegerValue == 1) {
				if(shareType != null && !"".equals(shareType) && !"null".equals(shareType)){
					HashMap<String, Object> tmpParam = new HashMap<String, Object>();
					tmpParam.put("shareIdx", idx);
					tmpParam.put("shareKind", "GeoPhoto");
					
					if("2".equals(shareType)){
						if(addShareUser != null && !"".equals(addShareUser) && !"null".equals(addShareUser)){
							String[] shareTList = addShareUser.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.insertShare(tmpParam);
						}
						if(removeShareUser != null && !"".equals(removeShareUser) && !"null".equals(removeShareUser)){
							String[] shareTList = removeShareUser.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.deleteShare(tmpParam);
						}
						if(editYes != null && !"".equals(editYes) && !"null".equals(editYes)){
							String[] editList = editYes.split(",");
							tmpParam.put("editType", "Y");
							tmpParam.put("editList", editList);
							resultIntegerValue = userDao.updateShareEdit(tmpParam);
						}
						if(editNo != null && !"".equals(editNo) && !"null".equals(editNo)){
							String[] editList = editNo.split(",");
							tmpParam.put("editType", "N");
							tmpParam.put("editList", editList);
							resultIntegerValue = userDao.updateShareEdit(tmpParam);
						}
					}else{
						resultIntegerValue = userDao.deleteShare(tmpParam);
					}
				}
				
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/deleteContent/{token}/{loginId}/{type}/{idxArr}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteImageService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("type") String type
			, @PathVariable("idxArr") String idxArr
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		String[] idxArray = null;
		String fileFull = "";
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			if(idxArr != null && !"".equals(idxArr) && !"null".equals(idxArr)){
				idxArray = idxArr.split(",");
			}
			
			if(idxArray != null && idxArray.length > 0){
				resultIntegerValue = 0;
				param.clear();
				fileFull = "";
				
				param.put("loginId", loginId);
				for(int i=0;i<idxArray.length;i++){
					param.put("idx", idxArray[i]);
					if(type != null){
						if("GeoCMS".equals(type)){
							resultList = dataDao.selectBoardList(param);
						}else if("GeoPhoto".equals(type)){
							resultList = dataDao.selectImageList(param);
						}else if("GeoVideo".equals(type)){
//							resultList = dataDao.selectImageList(param);
						}
					}
//					resultList = dataDao.selectImageList(param);
					
					if(resultList != null && resultList.size() > 0){
						HashMap<String, String> tmpMap = (HashMap<String, String>)resultList.get(0);
						if(tmpMap != null){
							String[] tmpFileArr = null;
							if(tmpMap.get("FILENAME") != null && !"".equals(tmpMap.get("FILENAME")) && !"null".equals(tmpMap.get("FILENAME"))){
								String tmpFileName = tmpMap.get("FILENAME").replace("&nbsp", "");
								if(tmpFileName != null && !"".equals(tmpFileName)){
									tmpFileArr = tmpMap.get("FILENAME").split(",");
								}
							}
							if(tmpFileArr != null && tmpFileArr.length > 0){
								for(int k=0;k<tmpFileArr.length;k++){
									fileFull = tmpMap.get("FILEPATH") + type + "/"+ tmpFileArr[k];
								}
								if("GeoCMS".equals(type)){
									resultIntegerValue = dataDao.deleteBoard(param);
								}else if("GeoPhoto".equals(type)){
									resultIntegerValue = dataDao.deleteImage(param);
								}else if("GeoVideo".equals(type)){
//									resultList = dataDao.selectImageList(param);
								}
							}
							
//							fileFull = tmpMap.get("FILEPATH") + "/" + tmpMap.get("FILENAME");
							
//							resultIntegerValue = dataDao.deleteImage(param);
							if(resultIntegerValue == 1 && fileFull != null && !"".equals(fileFull)) {
								File f = new File(fileFull);
								
								resultJSON.put("Code", 400);
								resultJSON.put("Message", Message.code400);
								
								if (f.delete()) {
									String tmpF = tmpMap.get("FILENAME");
									String tmpXml = tmpF.split("\\.")[0] + ".xml";
									fileFull = tmpMap.get("FILEPATH") + type + "/" + tmpXml;
									f = new File(fileFull);
									if(f.exists()){
										if (f.delete()) {
											resultJSON.put("Code", 100);
											resultJSON.put("Message", Message.code100);
									    }
									}else{
										resultJSON.put("Code", 100);
										resultJSON.put("Message", Message.code100);
									}
							    }
							}else{
								if("GeoCMS".equals(type)){
									resultIntegerValue = dataDao.deleteBoard(param);
								}
								
								if(resultIntegerValue == 1){
									resultJSON.put("Code", 100);
									resultJSON.put("Message", Message.code100);
								}else{
									resultJSON.put("Code", 300);
									resultJSON.put("Message", Message.code300);
									break;
								}
							}
						}
					}
				}
			}else{
				resultJSON.put("Code", 203);
				resultJSON.put("Message", Message.code203);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/saveXmlData/{token}/{xmlData}/{idx}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String saveXmlDataService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("xmlData") String xmlData
			, @PathVariable("idx") String idx
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			if(xmlData != null && !"".equals(xmlData) && !"null".equals(xmlData)){
				xmlData = xmlData.replaceAll("&sbsp","/").replaceAll("&mbsp", "?").replaceAll("&pbsp", "#");
			}
			
			param.clear();
			param.put("xmlData", xmlData);
			param.put("idx", idx);
			resultIntegerValue = dataDao.updateXmlData(param);
			
			if(resultIntegerValue == 1) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getMyContents/{viewType}/{token}/{loginId}/{pageNum}/{contentNum}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getMyContentsService(@RequestParam("callback") String callback
			, @PathVariable("viewType") String viewType
//			, @PathVariable("contentViewType") String contentViewType
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("contentNum") String contentNum
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		resultList = new ArrayList<Object>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			pageNum = pageNum.replace("&nbsp", "");
			contentNum = contentNum.replace("&nbsp", "");
			
			if(viewType != null && !"".equals(viewType)){
				param.put("type", "list");
//				param.put("myContent", "Y");
				param.put("loginId", loginId);
				param.put("pageNum", pageNum);
				param.put("contentNum", contentNum);
				
				if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(pageNum) && !"null".equals(contentNum)){
					int tmpPage = Integer.valueOf(pageNum);
					int tmpContent = Integer.valueOf(contentNum);
					int offset = tmpContent * (tmpPage-1);
					param.put("offset", String.valueOf(offset));
				}
				
				if("GeoCMS".equals(viewType)){
					resultList = dataDao.selectBoardList(param);
					result = dataDao.selectBoardListLen(param);
				}
				else if("GeoPhoto".equals(viewType)){
					resultList = dataDao.selectImageList(param);
					result = dataDao.selectImageListLen(param);
				}else if("GeoVideo".equals(viewType)){
					resultList = dataDao.selectVideoList(param);
					result = dataDao.selectVideoListLen(param);
				}else if("marker".equals(viewType)){
					param.put("type", "marker");
					param.put("myContentMarker", "Y");
					resultList = dataDao.selectContentList(param);
				}
//				if("video".equals(type) || ("first".equals(type) && ("Both".equals(contentViewType) || "Video".equals(contentViewType)))){
//					resultList2 = dataDao.selectImageList(param);
//					imageLen = dataDao.selectImageListLen(param);
//					
//					if(resultList != null && resultList.size() > 0){
//						ListUtils.union(resultList, resultList2);
//					}else{
//						resultList = resultList2;
//					}
//				}
			}
			
			if(resultList != null && resultList.size() != 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
				resultJSON.put("Data", JSONArray.fromObject(resultList));
				resultJSON.put("DataLen", result.get("TOTAL_CNT"));
			}
			else {
				resultJSON.put("Code", 200);
				resultJSON.put("Message", Message.code200);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getVideo/{type}/{token}/{loginId}/{pageNum}/{contentNum}/{tabName}/{idx}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getVideoService(@RequestParam("callback") String callback
			, @PathVariable("type") String type
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("contentNum") String contentNum
			, @PathVariable("tabName") String tabName
			, @PathVariable("idx") String idx
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		List<Object> shareList = new ArrayList<Object>();
		
		if(type != null){
			if("one".equals(type)){
				//token
				param.clear();
				param.put("token", token);
				result = userDao.selectUid(param);
				
				if(result == null){
					resultJSON.put("Code", 203);
					resultJSON.put("Message", Message.code203);
					return callback + "(" + resultJSON.toString() + ")";
				}
			}
			
			loginId = loginId.replace("&nbsp", "");
			pageNum = pageNum.replace("&nbsp", "");
			contentNum = contentNum.replace("&nbsp", "");
			tabName = tabName.replace("&nbsp", "");
			idx = idx.replace("&nbsp", "");
			
			param.put("type", type);
			param.put("loginId", loginId);
			param.put("pageNum", pageNum);
			param.put("contentNum", contentNum);
			param.put("tabName", tabName);
			param.put("idx", idx);
			
			if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(contentNum) && !"null".equals(contentNum)){
				int tmpPage = Integer.valueOf(pageNum);
				int tmpContent = Integer.valueOf(contentNum);
				int offset = tmpContent * (tmpPage-1);
				param.put("offset", String.valueOf(offset));
			}
			
			resultList = dataDao.selectVideoList(param);
			if("list".equals(type)){
				result = dataDao.selectVideoListLen(param);
			}else if("one".equals(type)){
				param.clear();
				param.put("shareIdx", idx);
				param.put("shareKind", "GeoVideo");
				shareList = userDao.selectShareUserList(param);
			}
		}
		
		if(resultList != null && resultList.size() != 0) {
			resultJSON.put("Code", 100);
			resultJSON.put("Message", Message.code100);
			resultJSON.put("Data", JSONArray.fromObject(resultList));
			if(result != null){
				resultJSON.put("DataLen", result.get("TOTAL_CNT"));
			}
			if(shareList != null && shareList.size() > 0){
				resultJSON.put("shareList", shareList);
			}
		}
		else {
			resultJSON.put("Code", 200);
			resultJSON.put("Message", Message.code200);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/saveVideo/{token}/{loginId}/{title}/{content}/{filesStr}/{filePath}/{latitude}/{longitude}/{tabName}/{shareType}/{shareUser}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String saveVideoService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("title") String title
			, @PathVariable("content") String content
			, @PathVariable("filesStr") String filesStr
			, @PathVariable("filePath") String filePath
			, @PathVariable("latitude") String latitude
			, @PathVariable("longitude") String longitude
			, @PathVariable("tabName") String tabName
			, @PathVariable("shareType") String shareType
			, @PathVariable("shareUser") String shareUser
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			title = title.replaceAll("&sbsp","/");
			content = content.replaceAll("&sbsp","/");
			longitude = longitude.replace("&nbsp", "");
			latitude = latitude.replace("&nbsp", "");
			shareUser = shareUser.replace("&nbsp", "");
			filePath = filePath.replaceAll("&sbsp","/");
			
			String filename = "";
			String thumbnail = "";
			if(filesStr != null && !"".equals(filesStr)){
				String tmpFileName = filesStr.split("\\.")[0];
				filename = tmpFileName + "_ogg.ogg";
				thumbnail = tmpFileName + "_thumb.jpg";
			}
			param.clear();
			param.put("loginId", loginId);
			param.put("title", title);
			param.put("content", content);
			param.put("originname", filesStr);
			param.put("filename", filename);
			param.put("thumbnail", thumbnail);
			param.put("filePath", filePath);
			param.put("longitude", longitude);
			param.put("latitude", latitude);
			param.put("tabName", tabName);
			param.put("shareType", shareType);
			resultIntegerValue = dataDao.insertVideo(param);
			
			if(resultIntegerValue == 1) {
				if(param != null){
					if(shareType != null && "2".equals(shareType) && shareUser != null && !"".equals(shareUser) && !"null".equals(shareUser)){
						HashMap<String, Object> tmpParam = new HashMap<String, Object>();
//						
						String[] shareTList = shareUser.split(",");
						tmpParam.put("shareTList", shareTList);
						tmpParam.put("shareIdx", param.get("idx"));
						tmpParam.put("shareKind", "GeoVideo");
						resultIntegerValue = userDao.insertShare(tmpParam);
					}
				}
			}
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/updateVideo/{token}/{loginId}/{idx}/{title}/{content}/{tabName}/{shareType}/{addShareUser}/{removeShareUser}/{xmlData}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String updateVideoService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("idx") String idx
			, @PathVariable("title") String title
			, @PathVariable("content") String content
			, @PathVariable("tabName") String tabName
			, @PathVariable("shareType") String shareType
			, @PathVariable("addShareUser") String addShareUser
			, @PathVariable("removeShareUser") String removeShareUser
			, @PathVariable("xmlData") String xmlData
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			title = title.replaceAll("&sbsp","/");
			content = content.replaceAll("&sbsp","/");
			addShareUser = addShareUser.replace("&nbsp", "");
			removeShareUser = removeShareUser.replace("&nbsp", "");
			if(xmlData != null && !"".equals(xmlData) && !"null".equals(xmlData)){
				xmlData = xmlData.replaceAll("&sbsp","/").replaceAll("&mbsp", "?").replaceAll("&pbsp", "#").replace("&obsp", ".");
			}
			
			param.clear();
			param.put("loginId", loginId);
			param.put("idx", idx);
			param.put("title", title);
			param.put("content", content);
			param.put("tabName", tabName);
			param.put("shareType", shareType);
			param.put("xmlData", xmlData);
			resultIntegerValue = dataDao.updateVideo(param);
			
			if(resultIntegerValue == 1) {
				if(shareType != null && !"".equals(shareType) && !"null".equals(shareType)){
					HashMap<String, Object> tmpParam = new HashMap<String, Object>();
					tmpParam.put("shareIdx", idx);
					tmpParam.put("shareKind", "GeoVideo");
					
					if("2".equals(shareType)){
						if(addShareUser != null && !"".equals(addShareUser) && !"null".equals(addShareUser)){
							String[] shareTList = addShareUser.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.insertShare(tmpParam);
						}
						if(removeShareUser != null && !"".equals(removeShareUser) && !"null".equals(removeShareUser)){
							String[] shareTList = removeShareUser.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.deleteShare(tmpParam);
						}
					}else{
						resultIntegerValue = userDao.deleteShare(tmpParam);
					}
				}
				
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getProjectGroup/{token}/{loginId}/{orderIdx}/{shareEdit}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getProjectGroupService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("orderIdx") String orderIdx
			, @PathVariable("shareEdit") String shareEdit
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
				
		if(result != null){
			orderIdx = orderIdx.replace("&nbsp", "");
			shareEdit = shareEdit.replace("&nbsp", "");
			
			param.put("loginId", loginId);
			param.put("orderIdx", orderIdx);
			param.put("shareEdit", shareEdit);
			resultList = dataDao.selectProjectList(param);
			
//			param.put("projectType", projectType);
//			param.put("projectGroup", "Y");
//			resultList = dataDao.selectProjectContentList(param);
		}
		
		if(resultList != null && resultList.size() != 0) {
			resultJSON.put("Code", 100);
			resultJSON.put("Message", Message.code100);
			resultJSON.put("Data", JSONArray.fromObject(resultList));
		}
		else {
			resultJSON.put("Code", 200);
			resultJSON.put("Message", Message.code200);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getProject/{token}/{loginId}/{type}/{projectIdx}/{pageNum}/{contentNum}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getProjectService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("projectIdx") String projectIdx
			, @PathVariable("type") String type
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("contentNum") String contentNum
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
				
		if(result != null){
			pageNum = pageNum.replace("&nbsp", "");
			contentNum = contentNum.replace("&nbsp", "");
			
			param.put("loginId", loginId);
			param.put("projectIdx", projectIdx);
			param.put("pageNum", pageNum);
			param.put("contentNum", contentNum);
			param.put("getProject", "Y");
			param.put("type", type);
			
			if(type != null && "marker".equals(type) && projectIdx != null && !"".equals(projectIdx) && !"null".equals(projectIdx)){
				String[] projectIdxList = projectIdx.split(",");
				List<Object> resultList2 = new ArrayList<Object>();
				
				if(projectIdxList != null && projectIdxList.length > 0){
					for(int i=0; i<projectIdxList.length;i++){
						param.put("projectIdx", projectIdxList[i]);
						resultList = dataDao.selectProjectContentList(param);
						if(resultList != null && resultList.size()>0){
							resultList2.addAll(resultList);
						}
					}
				}
				resultList = resultList2;
			}else {
				if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(pageNum) && !"null".equals(contentNum)){
					int tmpPage = Integer.valueOf(pageNum);
					int tmpContent = Integer.valueOf(contentNum);
					int offset = tmpContent * (tmpPage-1);
					param.put("offset", String.valueOf(offset));
				}
				
				resultList = dataDao.selectProjectContentList(param);
				result = dataDao.selectProjectContentListLen(param);
			}
			
			if(resultList != null && resultList.size() != 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
				resultJSON.put("Data", JSONArray.fromObject(resultList));
				resultJSON.put("DataLen", result.get("TOTAL_CNT"));
			}
			else {
				resultJSON.put("Code", 200);
				resultJSON.put("Message", Message.code200);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/saveProject/{token}/{loginId}/{projectName}/{shareType}/{shareUser}/{projectEditYes}/{markerIcon}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String saveProjectService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("projectName") String projectName
			, @PathVariable("shareType") String shareType
			, @PathVariable("shareUser") String shareUser
			, @PathVariable("projectEditYes") String projectEditYes
			, @PathVariable("markerIcon") String markerIcon
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			projectName = projectName.replaceAll("&sbsp","/");
			shareUser = shareUser.replace("&nbsp", "");
			projectEditYes = projectEditYes.replace("&nbsp", "");
			markerIcon = markerIcon.replace("&nbsp", "");
			
			param.clear();
			param.put("loginId", loginId);
			param.put("projectName", projectName);
			param.put("shareType", shareType);
			param.put("markerIcon", markerIcon);
			resultIntegerValue = dataDao.insertProject(param);
			
			if(shareType != null && "2".equals(shareType) && shareUser != null && !"".equals(shareUser) && !"null".equals(shareUser)){
				HashMap<String, Object> tmpParam = new HashMap<String, Object>();
			
				String[] shareTList = shareUser.split(",");
				tmpParam.put("shareTList", shareTList);
				tmpParam.put("shareIdx", param.get("idx"));
				tmpParam.put("shareKind", "GeoProject");
				resultIntegerValue = userDao.insertShare(tmpParam);
				
				if(projectEditYes != null && !"".equals(projectEditYes) && !"null".equals(projectEditYes)){
					String[] editList = projectEditYes.split(",");
					tmpParam.put("editType", "Y");
					tmpParam.put("editList", editList);
					resultIntegerValue = userDao.updateShareEdit(tmpParam);
				}
			}
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/updateProject/{token}/{loginId}/{projectIdx}/{projectName}/{shareType}/{shareAddUser}/{shareRemoveUser}/{projectEditYes}/{projectEditNo}/{markerIcon}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String updateProjectService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("projectIdx") String projectIdx
			, @PathVariable("projectName") String projectName
			, @PathVariable("shareType") String shareType
			, @PathVariable("shareAddUser") String shareAddUser
			, @PathVariable("shareRemoveUser") String shareRemoveUser
			, @PathVariable("projectEditYes") String projectEditYes
			, @PathVariable("projectEditNo") String projectEditNo
			, @PathVariable("markerIcon") String markerIcon
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			projectName = projectName.replaceAll("&sbsp","/");
			shareAddUser = shareAddUser.replace("&nbsp", "");
			shareRemoveUser = shareRemoveUser.replace("&nbsp", "");
			projectEditYes = projectEditYes.replace("&nbsp", "");
			projectEditNo = projectEditNo.replace("&nbsp", "");
			markerIcon = markerIcon.replace("&nbsp", "");
			
			param.clear();
			param.put("loginId", loginId);
			param.put("projectName", projectName);
			param.put("shareType", shareType);
			param.put("idx", projectIdx);
			param.put("markerIcon", markerIcon);
			resultIntegerValue = dataDao.updateProject(param);
			
			if(resultIntegerValue == 1){
				if(shareType != null && !"".equals(shareType) && !"null".equals(shareType)){
					HashMap<String, Object> tmpParam = new HashMap<String, Object>();
					tmpParam.put("shareIdx", projectIdx);
					tmpParam.put("shareKind", "GeoProject");
					
					if("2".equals(shareType)){
						if(shareAddUser != null && !"".equals(shareAddUser) && !"null".equals(shareAddUser)){
							String[] shareTList = shareAddUser.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.insertShare(tmpParam);
						}
						if(shareRemoveUser != null && !"".equals(shareRemoveUser) && !"null".equals(shareRemoveUser)){
							String[] shareTList = shareRemoveUser.split(",");
							tmpParam.put("shareTList", shareTList);
							resultIntegerValue = userDao.deleteShare(tmpParam);
						}
						
						if(projectEditYes != null && !"".equals(projectEditYes) && !"null".equals(projectEditYes)){
							String[] editList = projectEditYes.split(",");
							tmpParam.put("editType", "Y");
							tmpParam.put("editList", editList);
							resultIntegerValue = userDao.updateShareEdit(tmpParam);
						}
						
						if(projectEditNo != null && !"".equals(projectEditNo) && !"null".equals(projectEditNo)){
							String[] editList = projectEditNo.split(",");
							tmpParam.put("editType", "N");
							tmpParam.put("editList", editList);
							resultIntegerValue = userDao.updateShareEdit(tmpParam);
						}
					}else{
						resultIntegerValue += userDao.deleteShare(tmpParam);
					}
					
					//image 
					HashMap<String, String> imgTmp = new HashMap<String, String>();
					HashMap<String, Object> imgTmp2 = new HashMap<String, Object>();
					imgTmp.put("projectIdx", projectIdx);
					resultList = dataDao.selectProjectContentList(imgTmp);
					
					if(resultList != null && resultList.size()>0){
						for(int a=0;a<resultList.size();a++){
							HashMap<String, String> tmpMap = (HashMap<String, String>)resultList.get(a);
							if(tmpMap != null){
								//이미지 공유 타입 변경
								tmpMap.put("moveContent", String.valueOf(tmpMap.get("IDX")));
								tmpMap.put("shareType", shareType);
								resultIntegerValue = dataDao.updateImageMove(tmpMap);
//								tmpMap.put("title", tmpMap.get("TITLE"));
//								tmpMap.put("content", tmpMap.get("CONTENT"));
//								tmpMap.put("tabName", tmpMap.get("TABNAME"));
//								tmpMap.put("xmlData", tmpMap.get("XMLDATA"));
//								resultIntegerValue = dataDao.updateImage(tmpMap);
								
								//공유 유저 삭제
								imgTmp2 = new HashMap<String, Object>();
								imgTmp2.put("shareIdx", String.valueOf(tmpMap.get("IDX")));
								imgTmp2.put("shareKind", tmpMap.get("DATAKIND"));
								resultIntegerValue += userDao.deleteShare(imgTmp2);
								
								if("2".equals(shareType)){
									//공유 유저 추가
									imgTmp.put("shareIdx", String.valueOf(tmpMap.get("IDX")));
									imgTmp.put("shareKind", tmpMap.get("DATAKIND"));
									resultIntegerValue = userDao.insertShareFormProject(imgTmp);
								}
								
							}
						}
					}
				}
			}
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/moveProject/{token}/{loginId}/{moveProIS}/{moveContentArr}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String moveProjectService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("moveProIS") String moveProIS
			, @PathVariable("moveContentArr") String moveContentArr
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			if(moveProIS != null && !"".equals(moveProIS) && !"null".equals(moveProIS) && moveContentArr != null){
				HashMap<String, Object> tmpParam = new HashMap<String, Object>();
				String[] tmpArr = moveProIS.split("_");
				String proIdx = tmpArr[0];
				String proShare = tmpArr[1];
				
				String[] moveContentList = moveContentArr.split(",");
				if(moveContentList != null && moveContentList.length > 0 && proIdx != null && proShare != null){
					param.clear();
					param.put("shareType", proShare);
					param.put("projectIdx", proIdx);
					for(int i=0; i<moveContentList.length; i++){
						if(moveContentList[i] != null){
							String[] tmpMv = moveContentList[i].split("_");
							if(tmpMv[1] != null){
								if("GeoPhoto".equals(tmpMv[1])){
									param.put("moveContent", tmpMv[2]);
									resultIntegerValue = dataDao.updateImageMove(param);
									
									//공유 유저 삭제
									tmpParam = new HashMap<String, Object>();
									tmpParam.put("shareIdx", tmpMv[2]);
									tmpParam.put("shareKind","GeoPhoto");
									resultIntegerValue += userDao.deleteShare(tmpParam);
									
									if("2".equals(proShare)){
										//공유 유저 추가
										param.put("shareIdx", String.valueOf(tmpMv[2]));
										param.put("shareKind", "GeoPhoto");
										resultIntegerValue += userDao.insertShareFormProject(param);
									}
								}else{
									//비디오
								}
							}
						}
					}
				}
			}
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/removeProject/{token}/{loginId}/{projectIdx}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String rmoveProjectService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("projectIdx") String projectIdx
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		param = new HashMap<String, String>();
		result = new HashMap<String, String>();
		
		//token
		param.clear();
		param.put("token", token);
		result = userDao.selectUid(param);
		
		if(result != null){
			//update token time
			param.put("uid", String.valueOf(result.get("UID")));
			resultIntegerValue = userDao.updateTokenTime(param);
			
			if(projectIdx != null && !"".equals(projectIdx) && !"null".equals(projectIdx)){
				param.clear();
				param.put("loginId", loginId);
				param.put("projectIdx", projectIdx);
				resultIntegerValue = dataDao.deleteProject(param);
			}
			
			if(resultIntegerValue > 0) {
				resultJSON.put("Code", 100);
				resultJSON.put("Message", Message.code100);
			}else{
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
//	@RequestMapping(value = "/cms/getMyContents/{type}/{contentViewType}/{token}/{loginId}/{pageNum}/{contentNum}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
//	@ResponseBody
//	public String getProjectGroupService(@RequestParam("callback") String callback
//			, @PathVariable("type") String type
//			, @PathVariable("contentViewType") String contentViewType
//			, @PathVariable("token") String token
//			, @PathVariable("loginId") String loginId
//			, @PathVariable("pageNum") String pageNum
//			, @PathVariable("contentNum") String contentNum
//			, Model model, HttpServletRequest request) {
//		JSONObject resultJSON = new JSONObject();
//		
//		param = new HashMap<String, String>();
//		result = new HashMap<String, String>();
//		
//		HashMap<String, String> boardLen = new HashMap<String, String>();
//		HashMap<String, String> imageLen = new HashMap<String, String>();
//		HashMap<String, String> videoLen = new HashMap<String, String>();
//		
//		List<Object> resultList2  = new ArrayList<Object>();
//		
//		//token
//		param.clear();
//		param.put("token", token);
//		result = userDao.selectUid(param);
//		
//		if(result != null){
//			pageNum = pageNum.replace("&nbsp", "");
//			contentNum = contentNum.replace("&nbsp", "");
//			
//			if(type != null && !"".equals(type)){
//				param.put("loginId", loginId);
//				param.put("pageNum", pageNum);
//				param.put("contentNum", contentNum);
//				
//				if("board".equals(type) || "first".equals(type)){
//					resultList = dataDao.selectBoardList(param);
//					boardLen = dataDao.selectBoardListLen(param);
//				}
//				if("image".equals(type) || ("first".equals(type) && ("Both".equals(contentViewType) || "Image".equals(contentViewType)))){
//					resultList2 = dataDao.selectImageList(param);
//					imageLen = dataDao.selectImageListLen(param);
//					
//					if(resultList != null && resultList.size() > 0){
//						ListUtils.union(resultList, resultList2);
//					}else{
//						resultList = resultList2;
//					}
//				}
////				if("video".equals(type) || ("first".equals(type) && ("Both".equals(contentViewType) || "Video".equals(contentViewType)))){
////					resultList2 = dataDao.selectImageList(param);
////					imageLen = dataDao.selectImageListLen(param);
////					
////					if(resultList != null && resultList.size() > 0){
////						ListUtils.union(resultList, resultList2);
////					}else{
////						resultList = resultList2;
////					}
////				}
//			}
//			
//			param.put("type", type);
//			param.put("contentViewType", contentViewType);
//			param.put("loginId", loginId);
//			param.put("pageNum", pageNum);
//			param.put("contentNum", contentNum);
//			
//			if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(pageNum) && !"null".equals(contentNum)){
//				int tmpPage = Integer.valueOf(pageNum);
//				int tmpContent = Integer.valueOf(contentNum);
//				int offset = tmpContent * (tmpPage-1);
//				param.put("offset", String.valueOf(offset));
//			}
//			resultList = dataDao.selectContentList(param);
//			result = dataDao.selectContentListLen(param);
//			
//			if(resultList != null && resultList.size() != 0) {
//				resultJSON.put("Code", 100);
//				resultJSON.put("Message", Message.code100);
//				resultJSON.put("Data", JSONArray.fromObject(resultList));
//				resultJSON.put("DataLen", result.get("TOTAL_CNT"));
//			}
//			else {
//				resultJSON.put("Code", 200);
//				resultJSON.put("Message", Message.code200);
//			}
//		}else{
//			resultJSON.put("Code", 203);
//			resultJSON.put("Message", Message.code203);
//		}
//		
//		return callback + "(" + resultJSON.toString() + ")";
//	}
	
//	@RequestMapping(value = "/cms/getProjectGroup/{token}/{loginId}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
//	@ResponseBody
//	public String getProjectGroupService(@RequestParam("callback") String callback
//			, @PathVariable("token") String token
//			, @PathVariable("loginId") String loginId
//			, Model model, HttpServletRequest request) {
//		JSONObject resultJSON = new JSONObject();
//		
//		param = new HashMap<String, String>();
//		result = new HashMap<String, String>();
//		
//		//token
//		param.clear();
//		param.put("token", token);
//		result = userDao.selectUid(param);
//				
//		if(result != null){
//			param.put("loginId", loginId);
//			resultList = dataDao.selectProjectList(param);
//			
////			param.put("projectType", projectType);
////			param.put("projectGroup", "Y");
////			resultList = dataDao.selectProjectContentList(param);
//		}
//		
//		if(resultList != null && resultList.size() != 0) {
//			resultJSON.put("Code", 100);
//			resultJSON.put("Message", Message.code100);
//			resultJSON.put("Data", JSONArray.fromObject(resultList));
//		}
//		else {
//			resultJSON.put("Code", 200);
//			resultJSON.put("Message", Message.code200);
//		}
//		
//		return callback + "(" + resultJSON.toString() + ")";
//	}
//	
//	@RequestMapping(value = "/cms/getProject/{token}/{loginId}/{projectType}/{projectIdx}/{pageNum}/{contentNum}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
//	@ResponseBody
//	public String getProjectGroupService(@RequestParam("callback") String callback
//			, @PathVariable("token") String token
//			, @PathVariable("loginId") String loginId
//			, @PathVariable("projectType") String projectType
//			, @PathVariable("projectIdx") String projectIdx
//			, @PathVariable("pageNum") String pageNum
//			, @PathVariable("contentNum") String contentNum
//			, Model model, HttpServletRequest request) {
//		JSONObject resultJSON = new JSONObject();
//		
//		param = new HashMap<String, String>();
//		result = new HashMap<String, String>();
//		
//		//token
//		param.clear();
//		param.put("token", token);
//		result = userDao.selectUid(param);
//				
//		if(result != null){
//			pageNum = pageNum.replace("&nbsp", "");
//			contentNum = contentNum.replace("&nbsp", "");
//			
//			param.put("loginId", loginId);
//			param.put("projectType", projectType);
//			param.put("projectIdx", projectIdx);
//			param.put("pageNum", pageNum);
//			param.put("contentNum", contentNum);
//			
//			if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && contentNum != null && !"".equals(pageNum) && !"null".equals(contentNum)){
//				int tmpPage = Integer.valueOf(pageNum);
//				int tmpContent = Integer.valueOf(contentNum);
//				int offset = tmpContent * (tmpPage-1);
//				param.put("offset", String.valueOf(offset));
//			}
//			resultList = dataDao.selectProjectContentList(param);
//			result = dataDao.selectProjectContentListLen(param);
//		}
//		
//		if(resultList != null && resultList.size() != 0) {
//			resultJSON.put("Code", 100);
//			resultJSON.put("Message", Message.code100);
//			resultJSON.put("Data", JSONArray.fromObject(resultList));
//			resultJSON.put("DataLen", result.get("TOTAL_CNT"));
//		}
//		else {
//			resultJSON.put("Code", 200);
//			resultJSON.put("Message", Message.code200);
//		}
//		
//		return callback + "(" + resultJSON.toString() + ")";
//	}
	
//	@RequestMapping(value = "/cms/saveProject/{token}/{loginId}/{projectName}/{share}/{shareUserTxt}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
//	@ResponseBody
//	public String saveProjectService(@RequestParam("callback") String callback
//			, @PathVariable("token") String token
//			, @PathVariable("loginId") String loginId
//			, @PathVariable("projectName") String projectName
//			, @PathVariable("share") String share
//			, @PathVariable("shareUserTxt") String shareUserTxt
//			, Model model, HttpServletRequest request) {
//		JSONObject resultJSON = new JSONObject();
//		param = new HashMap<String, String>();
//		result = new HashMap<String, String>();
//		
//		//token
//		param.clear();
//		param.put("token", token);
//		result = userDao.selectUid(param);
//		
//		if(result != null){
//			//update token time
//			param.put("uid", String.valueOf(result.get("UID")));
//			resultIntegerValue = userDao.updateTokenTime(param);
//			
//			if(shareUserTxt != null && !"".equals(shareUserTxt) && !"null".equals(shareUserTxt)){
//				shareUserTxt = shareUserTxt.replaceAll("&nbsp","/");
//			}
//			
//			param.clear();
//			param.put("loginId", loginId);
//			param.put("projectName", projectName);
//			param.put("share", share);
//			resultIntegerValue = dataDao.insertProject(param);
//			
//			if(share != null && "2".equals(share)){
//				HashMap<String, Object> tmpParam = new HashMap<String, Object>();
//				
//				String[] shareTList = shareUserTxt.split(",");
//				tmpParam.put("shareTList", shareTList);
//				tmpParam.put("shareIdx", param.get("idx"));
//				tmpParam.put("shareKind", "GeoProject");
//				resultIntegerValue = dataDao.insertShare(tmpParam);
//			}
//			
//			if(resultIntegerValue > 1) {
//				resultJSON.put("Code", 100);
//				resultJSON.put("Message", Message.code100);
//			}else{
//				resultJSON.put("Code", 300);
//				resultJSON.put("Message", Message.code300);
//			}
//		}else{
//			resultJSON.put("Code", 203);
//			resultJSON.put("Message", Message.code203);
//		}
//		
//		return callback + "(" + resultJSON.toString() + ")";
//	}
}
