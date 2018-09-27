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
//				String[] message = new String[] {
//						ffmpegSetting.getFfmpeg_dir_and_file_name(),
//						"-i",
//						file_name,
//						ffmpegSetting.getSrc_no_ext(file_name)+"_mp4.mp4"
//				};
//				
				String[] message = new String[] {
						ffmpegSetting.getFfmpeg_dir_and_file_name(),
						"-i",
						file_name,
						"-acodec",
						"aac",
						"-ab",
						"192k",
						"-ar",
						"48000",
						"-ac","2","-b:a","300k","-vcodec","libx264","-level","30","-b:v","3000k","-r","29.97","-s","1280:720","-threads","0","-strict","-2",

//						"-deinterlace -r 24 -acodec libfaac -ab 96k -ac 2 -ar 44100 -crf 24 -s 1024x576 -vcodec libx264 -vpre normal -threads 0 -f mp4",

//						"-deinterlace",
//						"-r",
//						"24",
//						"-acodec",
////						"libfaac",
//						"aac",
////						"-c:a","aac", "-strict", "experimental",
////						"-c:a", "libfdk_aac",
//						"-ab",
//						"96k",
//						"-ac",
//						"2",
//						"-ar",
//						"44100",
//						"-crf",
//						"24",
//						"-s",
//						"1024x576",
//						"-vcodec",
//						"libx264",
////						"-vpre",
////						"normal",
////						"-threads",
////						"0",
//						"-preset", "ultrafast", "-crf", "0",
//						"-f",
//						"mp4",
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
//				String dirPath = file_name.substring(0,file_name.lastIndexOf("/upload/GeoVideo")) + "/ffmpeg/bin/ffmpeg";
//				System.out.println("convertToOgg : " + file_name);
//				System.out.println("convertToOgg dirPath : " + dirPath);
				
				String[] message = new String[] {
//						dirPath,
						"ffmpeg",
						"-i",
						file_name,
						"-acodec",
						"aac",
						"-ab",
						"192k",
						"-ar",
						"48000",
						"-ac","2","-b:a","300k","-vcodec","libx264","-level","30","-b:v","3000k","-r","29.97","-s","1280:720","-threads","0","-strict","-2",
						ffmpegSetting.getSrc_no_ext(file_name)+"_mp4.mp4"
				};
				
				FFmpeg ffmpeg = new FFmpeg();
//				String value = ffmpeg.runFFmpeg_linux(file_name, ffmpegSetting.getSrc_dir(file_name), message, "encoding");
				String value = ffmpeg.runFFmpeg_linux(file_name, message, "encoding");
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
		
		System.out.println("convertToGpx osffmpeg : " + osffmpeg);
		if(osffmpeg != null && !"".equals(osffmpeg)){
			System.out.println("convertToGpx file_name : " + file_name);
			if("win".equals(osffmpeg))
			{
				System.out.println("convertToGpx win");
				file_name = file_name.replaceAll("/","\\");
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
				System.out.println("convertToGpx linux");
//				file_name = file_name.replaceAll("/","\\");
				file_name = file_name.replaceAll("\\+", "\\");
				
//				String dirPath = file_name.substring(0,file_name.lastIndexOf("/upload/GeoVideo")) + "/ffmpeg/bin/ffmpeg";
//				System.out.println("convertToGpx : " + file_name);
//				System.out.println("convertToGpx dirPath : " + dirPath);
				
				String[] message = new String[] {
						"exiftool",
						"-ee",
						file_name
//						,
//						" > ",
//						ffmpegSetting.getSrc_no_ext(file_name)+".gpx"
				};
				
				FFmpeg ffmpeg = new FFmpeg();
				resGpsDataArr = ffmpeg.runExiftool_linux(file_name, ffmpegSetting.getSrc_dir(file_name), message, "encoding");
				System.out.println("종료 정보 : "+ resGpsDataArr);
			}
		}
		
		return resGpsDataArr;
	}
}
