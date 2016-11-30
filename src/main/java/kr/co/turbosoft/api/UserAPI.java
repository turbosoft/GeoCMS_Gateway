package kr.co.turbosoft.api;

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
import kr.co.turbosoft.util.KeyManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class UserAPI {
	static Logger log = Logger.getLogger(DataAPI.class.getName());

	static UserDao userDao = null;
	static DataDao dataDao = null;
	
	private HashMap<String, String> param, result, result2;
	private List<Object> resultList;
	private int resultIntegerValue;
	private String resultStringValue;

	public void setUserDao(UserDao userDao){
		this.userDao = userDao;
	}
	
	public void setDataDao(DataDao dataDao){
		this.dataDao = dataDao;
	}
	
	//id, email 중복 체크
	@RequestMapping(value = "/cms/userChk/{textType}/{textVal}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String userChkService(@RequestParam("callback") String callback, @PathVariable("textType") String textType, @PathVariable("textVal") String textVal, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		//token
		param = new HashMap<String, String>();
		
		//id check
		param.put("textType", textType);
		param.put("textVal", textVal);
		
		result = userDao.selectUser(param);
		
		if(textType != null){
			if(result != null) {
				if(textType.equalsIgnoreCase("ID")){
					resultJSON.put("Code", 102);
					resultJSON.put("Message", Message.code102);
				}else if(textType.equalsIgnoreCase("EMAIL")){
					resultJSON.put("Code", 104);
					resultJSON.put("Message", Message.code104);
				}
			}else{
				if(textType.equalsIgnoreCase("ID")){
					resultJSON.put("Code", 101);
					resultJSON.put("Message", Message.code101);
				}else if(textType.equalsIgnoreCase("EMAIL")){
					resultJSON.put("Code", 103);
					resultJSON.put("Message", Message.code103);
				}
			}
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	//회원가입
	@RequestMapping(value = "/cms/join/{id}/{pass}/{email}/{iutype}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String joinService(@RequestParam("callback") String callback
			, @PathVariable("id") String id
			, @PathVariable("pass") String pass
			, @PathVariable("email") String email
			, @PathVariable("iutype") String iutype
			, Model model
			, HttpServletRequest reqeust) throws Exception{
		
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		param.clear();
		param.put("id"		, id		);
		param.put("email"	, email		);
		param.put("iutype"	, iutype	);
		
		result = userDao.selectUser(param);
		
		param.put("pass", pass	);
		
		if(result == null) {
			resultIntegerValue = userDao.insertUser(param);
				
			if(resultIntegerValue == 1) {
				resultIntegerValue = userDao.insertToken(param);
				
				if(resultIntegerValue == 1) {
					resultJSON.put("Code", 100);
					resultJSON.put("Message", Message.code100);
				} else {
					resultJSON.put("Code", 202);
					resultJSON.put("Message", Message.code202);
				}
			} else {
				resultJSON.put("Code", 300);
				resultJSON.put("Message", Message.code300);
			}
		} else {
			if("I".equals(iutype)){
				resultJSON.put("Code", 101);
				resultJSON.put("Message", Message.code101);
			}else if("U".equals(iutype)){
				resultIntegerValue = userDao.updateUser(param);
				
				if(resultIntegerValue == 1){
					resultJSON.put("Code", 100);
					resultJSON.put("Message", Message.code100);
				}else{
					resultJSON.put("Code", 300);
					resultJSON.put("Message", Message.code300);
				}
			}
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	//id, pass 찾기
	@RequestMapping(value = "/cms/findUser/{textType}/{id}/{email}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String findUserService(@RequestParam("callback") String callback, 
			@PathVariable("textType") String textType,
			@PathVariable("id") String id,
			@PathVariable("email") String email,
			Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		
		//id check
		param.put("textType", textType);
		param.put("email", email);
		if(textType != null && "FINDPASS".equalsIgnoreCase(textType)){
			param.put("id", id);
		}
		
		result = userDao.selectUser(param);
		
		if(textType != null){
			if(result != null) {
				if("FINDID".equalsIgnoreCase(textType)){
					resultJSON.put("Code", 100);
					resultJSON.put("Message", Message.code100);
					resultJSON.put("Data", result.get("id"));
				}else if("FINDPASS".equalsIgnoreCase(textType)){
					resultJSON.put("Code", 100);
					resultJSON.put("Message", Message.code100);
					resultJSON.put("Data", result.get("password"));
				}
			}else{
				resultJSON.put("Code", 105);
				resultJSON.put("Message", Message.code105);
			}
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	//user login 검사
	@RequestMapping(value = "/cms/login/{id}/{pass}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String loginService(@RequestParam("callback") String callback, @PathVariable("id") String id, @PathVariable("pass") String pass, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		param = new HashMap<String, String>();
		
		//id check
		param.put("id", id);
		param.put("pass", pass);
		
		result = userDao.selectUser(param);
		
		if(result != null) {
			param.put("uid", String.valueOf(result.get("uid")));
			
			KeyManager keyManager = new KeyManager();
			String aes = null;
			
			try {
				aes = keyManager.genKey(id);
				param.put("aes", aes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				resultJSON.put("Code", 202);
				resultJSON.put("Message", Message.code202);
			}
			
			if(aes != null) {
				resultIntegerValue = userDao.updateToken(param);
				if(resultIntegerValue == 1) {
					resultJSON.put("Code", 100);
					resultJSON.put("Message", Message.code100);
					resultJSON.put("Data", result.get("type"));
					resultJSON.put("Token", aes);
				}
			}
		}
		else {
			resultJSON.put("Code", 105);
			resultJSON.put("Message", Message.code105);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	//회원 권한 수정
	@RequestMapping(value = "/cms/typeUpdate/{token}/{data}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String typeUpdateService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("data") List<HashMap<String,String>> data
			, Model model
			, HttpServletRequest reqeust) throws Exception{
		
		JSONObject resultJSON = new JSONObject();
		
		//token
		param = new HashMap<String, String>();
		param.put("token", token);
		
		result = userDao.selectUid(param);
		
		if(result != null){
			if("ADMIN".equals(result.get("TYPE"))){
				if(data != null && data.size() > 0){
					for(int i=0;i<data.size();i++){
						if(data.get(i) != null){
							resultIntegerValue = userDao.updateUser(data.get(i));
							if(resultIntegerValue != 1) {
								resultJSON.put("Code", 300);
								resultJSON.put("Message", Message.code300);
								break;
							}
						}
					}
					resultJSON.put("Code", 100);
					resultJSON.put("Message", Message.code100);
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
	
	@RequestMapping(value = "/cms/searchUser/{token}/{searchType}/{searchText}/{sDate}/{eDate}/{pageNum}/{selUserNum}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchUserService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("searchType") String searchType
			, @PathVariable("searchText") String searchText
			, @PathVariable("sDate") String sDate
			, @PathVariable("eDate") String eDate
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("selUserNum") String selUserNum
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		//token
		param = new HashMap<String, String>();
		param.put("token", token);
		
		result = userDao.selectUid(param);
		
		if(result != null){
			if("ADMIN".equals(result.get("TYPE"))){
				searchText = searchText.replace("&nbsp", "");
				sDate = sDate.replace("&nbsp", "");
				eDate = eDate.replace("&nbsp", "");
				
				param.clear();
				param.put("searchType", searchType);
				param.put("searchText", searchText);
				param.put("sDate", sDate);
				param.put("eDate", eDate);
				param.put("pageNum", pageNum);
				param.put("selUserNum", selUserNum);
				
				int offset = 0;
				if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && selUserNum != null && !"".equals(selUserNum) && !"null".equals(selUserNum)){
					int tmpPage = Integer.valueOf(pageNum);
					int tmpContent = Integer.valueOf(selUserNum);
					offset = tmpContent * (tmpPage-1);
				}
				param.put("offset", String.valueOf(offset));
				
				resultList = userDao.selectAllUser(param);
				
				result = new HashMap<String, String>();
				result = userDao.selectAllUserLen(param);
				
				if(resultList != null && resultList.size() > 0) {
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
	
	@RequestMapping(value = "/cms/searchShareUser/{token}/{loginId}/{type}/{searchText}/{pageNum}/{selUserNum}/{shareIdx}/{shareKind}/{orderText}/{addShare}/{removeShare}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchShareUserService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("type") String type
			, @PathVariable("searchText") String searchText
			, @PathVariable("pageNum") String pageNum
			, @PathVariable("selUserNum") String selUserNum
			, @PathVariable("shareIdx") String shareIdx
			, @PathVariable("shareKind") String shareKind
			, @PathVariable("orderText") String orderText
			, @PathVariable("addShare") String addShare
			, @PathVariable("removeShare") String removeShare
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		HashMap<String,Object> tempHash = new HashMap<String, Object>();
		String[] userArr = null;
		String[] addArr = null;
		String[] removeArr = null;
		
		//token
		param = new HashMap<String, String>();
		param.put("token", token);
		
		result = userDao.selectUid(param);
		
  		if(result != null){
			searchText = searchText.replace("&nbsp", "");
			shareIdx = shareIdx.replace("&nbsp", "");
			shareKind = shareKind.replace("&nbsp", "");
			pageNum = pageNum.replace("&nbsp", "");
			addShare = addShare.replace("&nbsp", "");
			removeShare = removeShare.replace("&nbsp", "");
			
			tempHash.put("loginId", loginId);
			tempHash.put("searchText", searchText);
			tempHash.put("pageNum", pageNum);
			tempHash.put("selUserNum", selUserNum);
			tempHash.put("orderText", orderText);
			
			if(addShare != null && !"".equals(addShare) && !"null".equals(addShare)){
				addArr = addShare.split(",");
				tempHash.put("addArr", addArr);
			}
			
			if(removeShare != null && !"".equals(removeShare) && !"null".equals(removeShare)){
				removeArr = removeShare.split(",");
				tempHash.put("removeArr", removeArr);
			}
			
			if(shareIdx != null && !"".equals(shareIdx) && !"null".equals(shareIdx)){
				param.clear();
				param.put("shareIdx", shareIdx);
				param.put("shareKind", shareKind);
				resultList = userDao.selectShareUserList(param);
				if(resultList != null && resultList.size() > 0){
					userArr = new String[resultList.size()];
					
					for(int i=0;i<resultList.size();i++){
						HashMap<String, String> tmpMap = (HashMap<String, String>)resultList.get(i);
						if(tmpMap != null){
							userArr[i] = String.valueOf(tmpMap.get("UID"));
						}
					}
					tempHash.put("userArr", userArr);
					tempHash.put("shareIdx", shareIdx);
				}
			}
			
			int offset = 0;
			if(pageNum != null && !"".equals(pageNum) && !"null".equals(pageNum) && selUserNum != null && !"".equals(selUserNum) && !"null". equals(selUserNum)){
				int tmpPage = Integer.valueOf(pageNum);
				int tmpContent = Integer.valueOf(selUserNum);
				offset = tmpContent * (tmpPage-1);
			}
			tempHash.put("offset", String.valueOf(offset));
			
			if(userArr == null){
				tempHash.put("searchOff", "Y");
			}
			
			resultList = userDao.selectShareUser(tempHash);
			
			if(type != null && "search".equals(type)){
				if(resultList != null && resultList.size() > 0){
					resultJSON.put("SearchYN", "Y");
				}else{
					tempHash.clear();
					tempHash.put("searchText", searchText);
					tempHash.put("pageNum", pageNum);
					tempHash.put("selUserNum", selUserNum);
					tempHash.put("orderText", orderText);
					resultList = userDao.selectShareUser(tempHash);
					resultJSON.put("SearchYN", "N");
				}
			}
			
			result = new HashMap<String, String>();
			result = userDao.selectShareUserLen(tempHash);
			
//			if((searchText != null && !"".equals(searchText) && !"null".equals(searchText)) || (resultList != null && resultList.size()>0)){
//				resultList = userDao.selectShareUser(tempHash);
//			}else{
//				resultList = null;
//			}
			
//			if(userArr == null && (searchText == null || "" .equals(searchText)) && "first".equals(type)){
//				resultList = null;
//			}
			
			if(resultList != null && resultList.size() > 0) {
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
		}else{
			resultJSON.put("Code", 203);
			resultJSON.put("Message", Message.code203);
		}
		
		return callback + "(" + resultJSON.toString() + ")";
	}
	
	@RequestMapping(value = "/cms/getShareUser/{token}/{loginId}/{shareIdx}/{shareType}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchUserService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("shareIdx") String shareIdx
			, @PathVariable("shareType") String shareType
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		
		//token
		param = new HashMap<String, String>();
		param.put("token", token);
		
		result = userDao.selectUid(param);
		
		if(result != null){
			param.clear();
			param.put("loginId", loginId);
			param.put("shareIdx", shareIdx);
			param.put("shareKind", shareType);
			param.put("editUserChk", "Y");
			
			resultList = userDao.selectShareUserList(param);
			
			if(resultList != null && resultList.size() > 0) {
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
}
