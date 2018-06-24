package kr.co.turbosoft.util;

import java.util.ArrayList;
import java.util.List;

import kr.co.turbosoft.util.FFmpeg;
import kr.co.turbosoft.util.FFmpegSetting;

public class VideoEncoding {
	
	//동영상 인코딩
	public int convertToOgg(String file_name) {
		FFmpegSetting ffmpegSetting = new FFmpegSetting();
		String osName = System.getProperty("os.name").toLowerCase();
		String osffmpeg = "";
		int resInt = 0;
		
		if(osName.indexOf("win") >= 0){
			osffmpeg = "win";
		}else if(osName.indexOf("mac") >= 0){
			osffmpeg = "mac";
		}else if(osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0 ){
			osffmpeg = "linux";
		}else if(osName.indexOf("sunos") >= 0){
			osffmpeg = "sunos";
		}
		
		if(osffmpeg != null && !"".equals(osffmpeg)){
			if("win".equals(osffmpeg))
			{
				String[] message = new String[] {
						ffmpegSetting.getFfmpeg_dir_and_file_name(),
						"-i",
						file_name,
						ffmpegSetting.getSrc_no_ext(file_name)+"_mp4.mp4"
				};
				
				FFmpeg ffmpeg = new FFmpeg();
				String value = ffmpeg.runFFmpeg(file_name, ffmpegSetting.getSrc_dir(file_name), message, "encoding");
				System.out.println("종료 정보 : "+value);
				if(value != null && !"".equals(value)){
					resInt = 1;
				}
			}
			else if("linux".equals(osffmpeg))
			{
				String dirPath = file_name.substring(0,file_name.lastIndexOf("/upload/GeoVideo")) + "/ffmpeg/bin/ffmpeg";
				System.out.println("convertToOgg : " + file_name);
				System.out.println("convertToOgg dirPath : " + dirPath);
				
				String[] message = new String[] {
//						dirPath,
						"ffmpeg",
						"-i",
						file_name,
						ffmpegSetting.getSrc_no_ext(file_name)+"_mp4.mp4"
				};
				
				FFmpeg ffmpeg = new FFmpeg();
				String value = ffmpeg.runFFmpeg_linux(file_name, ffmpegSetting.getSrc_dir(file_name), message, "encoding");
				System.out.println("종료 정보 : "+ value);
				if(value != null && "complete".equals(value)){
					resInt = 1;
				}
				System.out.println("종료 정보  resInt : "+resInt);
			}
		}
		
		return resInt;
	}
	
	//동영상 gpx 인코딩
	public List<String> convertToGpx(String file_name) {
		FFmpegSetting ffmpegSetting = new FFmpegSetting();
		String osName = System.getProperty("os.name").toLowerCase();
		String osffmpeg = "";
		List<String> resGpsDataArr = new ArrayList<String>();
		
		if(osName.indexOf("win") >= 0){
			osffmpeg = "win";
		}else if(osName.indexOf("mac") >= 0){
			osffmpeg = "mac";
		}else if(osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0 ){
			osffmpeg = "linux";
		}else if(osName.indexOf("sunos") >= 0){
			osffmpeg = "sunos";
		}
		
		if(osffmpeg != null && !"".equals(osffmpeg)){
			file_name = file_name.replaceAll("/","\\");
			if("win".equals(osffmpeg))
			{
				String[] message = new String[] {
						ffmpegSetting.getExiftool_file_name(),
						"-ee",
						file_name ," > ",
						ffmpegSetting.getSrc_no_ext(file_name)+".gpx"
				};
				
				FFmpeg ffmpeg = new FFmpeg();
				resGpsDataArr = ffmpeg.runExiftool(file_name, ffmpegSetting.getSrc_dir(file_name), message, "encoding");
				System.out.println("종료 정보 : "+ resGpsDataArr);
			}
			else if("linux".equals(osffmpeg))
			{
//				String dirPath = file_name.substring(0,file_name.lastIndexOf("/upload/GeoVideo")) + "/ffmpeg/bin/ffmpeg";
//				System.out.println("convertToOgg : " + file_name);
//				System.out.println("convertToOgg dirPath : " + dirPath);
//				
//				String[] message = new String[] {
//						"ffmpeg",
//						"-i",
//						file_name,
//						ffmpegSetting.getSrc_no_ext(file_name)+"_ogg.ogg"
//				};
//				
//				FFmpeg ffmpeg = new FFmpeg();
//				String value = ffmpeg.runFFmpeg_linux(file_name, ffmpegSetting.getSrc_dir(file_name), message, "encoding");
//				System.out.println("종료 정보 : "+ value);
//				if(value != null && "complete".equals(value)){
//					resInt = 1;
//				}
//				System.out.println("종료 정보  resInt : "+resInt);
			}
		}
		
		return resGpsDataArr;
	}
}
