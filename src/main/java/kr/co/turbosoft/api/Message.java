package kr.co.turbosoft.api;

import kr.co.turbosoft.dao.DataDao;
import kr.co.turbosoft.dao.UserDao;

public class Message {
//	public static String code100 = "정상처리 되었습니다.";
//	public static String code101 = "가입할 수 있는 아이디입니다.";
//	public static String code102 = "이미 가입된 아이디입니다.";
//	public static String code103 = "가입할 수 있는 이메일입니다.";
//	public static String code104 = "이미 가입된 이메일입니다.";
//	public static String code105 = "회원 정보가 존재하지 않습니다.";
//	public static String code106 = "이미 가입된 아이디와 이메일 입니다.";
//	public static String code107 = "회원가입이 완료 되었습니다.";
//	public static String code108 = "동일한 정보가 서버 정보가 존재합니다.";
//	public static String code200 = "데이터가 존재하지 않습니다.";
//	public static String code201 = "비밀번호가 다릅니다.";
//	
//	public static String code202 = "Session Token 처리 오류입니다.";
//	public static String code203 = "Session Token이 만료되었습니다.";
//	public static String code204 = "Session Token 정보가 없습니다.";
//	public static String code205 = "Session Token 정보와 로그인 정보가 일치하지 않습니다.";
//	public static String code206 = "해당 서버에 연결 할 수 없습니다.";
//	
//	public static String code300 = "데이터베이스 처리 오류입니다.";
//	public static String code400 = "파일 처리 오류입니다.";
//	public static String code500 = "관리자만 사용가능한 메뉴입니다.";
//	public static String code600 = "조건에 맞지 않는 데이터가 있습니다.";
//	public static String code700 = "해당 데이터에 대한 권한이 없습니다.";
//	
//	public static String code800 = "시스템 오류입니다. 오류메시지를 확인해 주세요.";
	
	public static String code100 = "Successfully processed.";
	public static String code101 = "This is the ID you can sign up for.";
	public static String code102 = "This is the ID already registered.";
	public static String code103 = "You can sign up for this email.";
	public static String code104 = "This is an already signed email.";
	public static String code105 = "Member information does not exist.";
	public static String code106 = "Already registered ID and email.";
	public static String code107 = "Sign up is complete.";
	public static String code108 = "The same information exists in the server information.";
	public static String code200 = "The data does not exist.";
	public static String code201 = "The password is different.";
	
	public static String code202 = "Session Token processing error.";
	public static String code203 = "The Session Token has expired.";
	public static String code204 = "There is no Session Token information.";
	public static String code205 = "Session Token information and login information do not match.";
	public static String code206 = "Unable to connect to that server";
	
	public static String code300 = "Database processing error.";
	public static String code400 = "File processing error.";
	public static String code500 = "This menu is available only for administrators.";
	public static String code600 = "There is data that does not meet the criteria.";
	public static String code700 = "You are not authorized for this data.";
	
	public static String code800 = "System error. Please check the error message.";
	
	
	
	static DataDao dataDao = null;
	static UserDao userDao = null;
}
