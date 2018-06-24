package kr.co.turbosoft.api;

import kr.co.turbosoft.dao.DataDao;
import kr.co.turbosoft.dao.UserDao;

public class Message {
//	public static String code100 = "����ó�� �Ǿ����ϴ�.";
//	public static String code101 = "������ �� �ִ� ���̵��Դϴ�.";
//	public static String code102 = "�̹� ���Ե� ���̵��Դϴ�.";
//	public static String code103 = "������ �� �ִ� �̸����Դϴ�.";
//	public static String code104 = "�̹� ���Ե� �̸����Դϴ�.";
//	public static String code105 = "ȸ�� ������ �������� �ʽ��ϴ�.";
//	public static String code106 = "�̹� ���Ե� ���̵�� �̸��� �Դϴ�.";
//	public static String code107 = "ȸ�������� �Ϸ� �Ǿ����ϴ�.";
//	public static String code108 = "������ ������ ���� ������ �����մϴ�.";
//	public static String code200 = "�����Ͱ� �������� �ʽ��ϴ�.";
//	public static String code201 = "��й�ȣ�� �ٸ��ϴ�.";
//	
//	public static String code202 = "Session Token ó�� �����Դϴ�.";
//	public static String code203 = "Session Token�� ����Ǿ����ϴ�.";
//	public static String code204 = "Session Token ������ �����ϴ�.";
//	public static String code205 = "Session Token ������ �α��� ������ ��ġ���� �ʽ��ϴ�.";
//	public static String code206 = "�ش� ������ ���� �� �� �����ϴ�.";
//	
//	public static String code300 = "�����ͺ��̽� ó�� �����Դϴ�.";
//	public static String code400 = "���� ó�� �����Դϴ�.";
//	public static String code500 = "�����ڸ� ��밡���� �޴��Դϴ�.";
//	public static String code600 = "���ǿ� ���� �ʴ� �����Ͱ� �ֽ��ϴ�.";
//	public static String code700 = "�ش� �����Ϳ� ���� ������ �����ϴ�.";
//	
//	public static String code800 = "�ý��� �����Դϴ�. �����޽����� Ȯ���� �ּ���.";
	
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
