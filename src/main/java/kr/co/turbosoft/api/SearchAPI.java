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

import kr.co.turbosoft.dao.SearchDao;
import kr.co.turbosoft.dao.UserDao;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class SearchAPI {
	static Logger log = Logger.getLogger(DataAPI.class.getName());

	static SearchDao searchDao = null;
	static UserDao userDao = null;
	
	private HashMap<String, String> param, result, result2;
	private List<Object> resultList;
	private int resultIntegerValue;
	private String resultStringValue;

	public void setSearchDao(SearchDao searchDao){
		this.searchDao = searchDao;
	}
	
	public void setUserDao(UserDao userDao){
		this.userDao = userDao;
	}
	
	@RequestMapping(value = "/cms/searchList/{token}/{loginId}/{text}/{boardChk}/{imageChk}/{videoChk}/{check}/{display}/{projectImage}/{projectVideo}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchListService(@RequestParam("callback") String callback
			, @PathVariable("token") String token
			, @PathVariable("loginId") String loginId
			, @PathVariable("text") String text
			, @PathVariable("boardChk") String boardChk
			, @PathVariable("imageChk") String imageChk
			, @PathVariable("videoChk") String videoChk
			, @PathVariable("check") String check
			, @PathVariable("display") String display
			, @PathVariable("projectImage") String projectImage
			, @PathVariable("projectVideo") String projectVideo
			, Model model, HttpServletRequest request) {
		JSONObject resultJSON = new JSONObject();
		result = new HashMap<String, String>();
		
		token = token.replace("&nbsp", "");
		loginId = loginId.replace("&nbsp", "");
		
		if(loginId != null && !"".equals(loginId) &&  !"null".equals(loginId)){
			//token
			param = new HashMap<String, String>();
			param.put("token", token);
			result = userDao.selectUid(param);
			if(result == null){
				resultJSON.put("Code", 203);
				resultJSON.put("Message", Message.code203);
				return callback + "(" + resultJSON.toString() + ")";
			}
		}
		
		param = new HashMap<String, String>();
		param.put("loginId", loginId);
		param.put("text", text);
		param.put("boardChk", boardChk);
		param.put("imageChk", imageChk);
		param.put("videoChk", videoChk);
		param.put("check", check);
		param.put("display", display);
		param.put("projectImage", projectImage);
		param.put("projectVideo", projectVideo);
		
		//get Base
		resultList = searchDao.selectSearchList(param);
		
		if(resultList != null && resultList.size() > 0){
			for(int i=0;i<resultList.size();i++){
				String searchKind = "";
				result2 = (HashMap<String, String>)resultList.get(i);
				String title = (String)result2.get("TITLE");
				String content = (String)result2.get("CONTENT");
				String xmlData = (String)result2.get("XMLDATA");
				
				if("content".equals(check) || "all".equals(check)){
					if(title.indexOf(text) > -1){
						searchKind = "Title";
					}
					if(content.indexOf(text) > -1){
						if(searchKind != null && searchKind.equals("Title")){
							searchKind += "/";
						}
						searchKind += "Content";
					}
				}
				if(("anno".equals(check) || "all".equals(check)) && xmlData != null && xmlData.indexOf(text) > -1){
					if(check.equals("all") && !"".equals(searchKind)){
						searchKind += " and ";
					}
					searchKind += "Annotation";
				}
				result2.put("SEARCHKIND", searchKind);
			}
		}
		
		resultJSON.put("Code", 100);
		resultJSON.put("Message", Message.code100);
		resultJSON.put("Data", JSONArray.fromObject(resultList));
		
		return callback + "(" + resultJSON.toString() + ")";
	}
}
