package kr.co.turbosoft.dao.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import kr.co.turbosoft.api.Message;
import kr.co.turbosoft.dao.DataDao;

public class DataDaoImpl extends SqlSessionDaoSupport implements DataDao {
	private HashMap<String, String> result;
	private List<Object> resultList;
	private int resultIntegerValue;
	private String resultStringValue;
	
	@Override
	public void thisRollback() {
		// TODO Auto-generated method stub
		getSqlSession().rollback();
	}
	
	@Override
	public HashMap<String, String> selectBase() {
		// TODO Auto-generated method stub
		result = getSqlSession().selectOne("data.selectBase");
		
		return result;
	}
	
	@Override
	public int updateBase(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateBase", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateTabNameBoard(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateTabNameBoard", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateTabNameImage(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateTabNameImage", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateTabNameVideo(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateTabNameVideo", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public List<Object> selectBoardList(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		resultList = getSqlSession().selectList("data.selectBoardList", param);
		
		return resultList;
	}
	
	@Override
	public HashMap<String, String> selectBoardListLen(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		result = getSqlSession().selectOne("data.selectBoardListLen", param);
		
		return result;
	}
	
	@Override
	public int insertBoard(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().insert("data.insertBoard", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateBoard(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateBoard", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int deleteBoard(HashMap<String, String> param) {
		resultIntegerValue = getSqlSession().delete("data.deleteBoard", param);
		return resultIntegerValue;
	}
	
	@Override
	public List<Object> selectContentList(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		resultList = getSqlSession().selectList("data.selectContentList", param);
		
		return resultList;
	}
	
	@Override
	public HashMap<String, String> selectContentListLen(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		result = getSqlSession().selectOne("data.selectContentListLen", param);
		
		return result;
	}
	
	@Override
	public List<Object> selectImageList(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		resultList = getSqlSession().selectList("data.selectImageList", param);
		
		return resultList;
	}
	
	@Override
	public HashMap<String, String> selectImageListLen(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		result = getSqlSession().selectOne("data.selectImageListLen", param);
		
		return result;
	}
	
	@Override
	public int insertImage(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().insert("data.insertImage", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateImage(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateImage", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int deleteImage(HashMap<String, String> param) {
		resultIntegerValue = getSqlSession().delete("data.deleteImage", param);
		return resultIntegerValue;
	}
	
	@Override
	public int updateXmlData(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateXmlData", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateImageMove(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateImageMove", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public List<Object> selectVideoList(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		resultList = getSqlSession().selectList("data.selectVideoList", param);
		
		return resultList;
	}
	
	@Override
	public HashMap<String, String> selectVideoListLen(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		result = getSqlSession().selectOne("data.selectVideoListLen", param);
		
		return result;
	}
	
	@Override
	public int insertVideo(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().insert("data.insertVideo", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateVideo(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateVideo", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int deleteVideo(HashMap<String, String> param) {
		resultIntegerValue = getSqlSession().delete("data.deleteVideo", param);
		return resultIntegerValue;
	}
	
	@Override
	public int updateVideoMove(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateVideoMove", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public List<Object> selectProjectList(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		resultList = getSqlSession().selectList("data.selectProjectList", param);
		
		return resultList;
	}
	
	@Override
	public List<Object> selectProjectContentList(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		resultList = getSqlSession().selectList("data.selectProjectContentList", param);
		
		return resultList;
	}
	
	@Override
	public HashMap<String, String> selectProjectContentListLen(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		result = getSqlSession().selectOne("data.selectProjectContentListLen", param);
		
		return result;
	}
	
	@Override
	public int insertProject(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().insert("data.insertProject", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int updateProject(HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
		resultIntegerValue = getSqlSession().update("data.updateProject", param);
		
		return resultIntegerValue;
	}
	
	@Override
	public int deleteProject(HashMap<String, String> param) {
		resultIntegerValue = getSqlSession().delete("data.deleteProject", param);
		return resultIntegerValue;
	}
	
}
