package kr.co.turbosoft.dao;

import java.util.HashMap;
import java.util.List;

public interface DataDao {
	public void thisRollback();
	public HashMap<String, String> selectBase();
	public int updateBase(HashMap<String, String> param);
	
	public int updateTabNameBoard(HashMap<String, String> param);
	public int updateTabNameImage(HashMap<String, String> param);
	public int updateTabNameVideo(HashMap<String, String> param);
	
	public List<Object> selectBoardList(HashMap<String, String> param);
	public HashMap<String, String> selectBoardListLen(HashMap<String, String> param);
	public int insertBoard(HashMap<String, String> param);
	public int updateBoard(HashMap<String, String> param);
	public int deleteBoard(HashMap<String, String> param);
	
	public List<Object> selectContentList(HashMap<String, String> param);
	public HashMap<String, String> selectContentListLen(HashMap<String, String> param);
	
	public List<Object> selectImageList(HashMap<String, String> param);
	public HashMap<String, String> selectImageListLen(HashMap<String, String> param);
	public int insertImage(HashMap<String, String> param);
	public int updateImage(HashMap<String, String> param);
	public int deleteImage(HashMap<String, String> param);
	public int updateXmlData(HashMap<String, String> param);
	public int updateImageMove(HashMap<String, String> param);
	
	public List<Object> selectVideoList(HashMap<String, String> param);
	public HashMap<String, String> selectVideoListLen(HashMap<String, String> param);
	public int insertVideo(HashMap<String, String> param);
	public int updateVideo(HashMap<String, String> param);
	
	public List<Object> selectProjectList(HashMap<String, String> param);
	public List<Object> selectProjectContentList(HashMap<String, String> param);
	public HashMap<String, String> selectProjectContentListLen(HashMap<String, String> param);
	public int insertProject(HashMap<String, String> param);
	public int updateProject(HashMap<String, String> param);
	public int deleteProject(HashMap<String, String> param);
}
