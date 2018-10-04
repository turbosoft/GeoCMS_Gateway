package kr.co.turbosoft.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

public class ExifRead {
	//EXIF Read
	public boolean changeOrientation(String savefullStr) {
		File file = new File(savefullStr);
		String tmpFileName = savefullStr.substring(savefullStr.lastIndexOf("/")+1, savefullStr.length());
		boolean changeImage = false;
		
		File fileDir = null;
		
		//데이터 저장 변수 선언
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<String> data = new ArrayList<String>();
		
		//EXIF 설정
		IImageMetadata metadata = null;
		
		//EXIF 설정
		if(!tmpFileName.contains(".png") && !tmpFileName.contains(".PNG")){
			try {
				if(file.exists()){
					metadata = Sanselan.getMetadata(file);
				}else{
					return changeImage;
				}
			}
			catch(Exception e) {
//				e.printStackTrace();
			}
		}else{
			return changeImage;
		}
		
		//이미지 변경
		if(metadata != null && metadata instanceof JpegImageMetadata) {
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			//일반 정보 + GPS 일부 정보 추출
			TiffField field;
			String tagInfoTxt = "";
			
			TagInfo tagInfo = TiffConstants.EXIF_TAG_ORIENTATION;
			field = jpegMetadata.findEXIFValue(tagInfo);
			if(field != null){
				tagInfoTxt = field.getValueDescription();
				if(tagInfoTxt != null && tagInfoTxt != "" && tagInfoTxt != "null"){
					int tagInfoInt = Integer.valueOf(tagInfoTxt);
					int tagWidth = 0;
					int tagHeight = 0;
					
					BufferedImage sourceImage;
					try {
						sourceImage = ImageIO.read(new File(savefullStr));
						tagWidth = sourceImage.getWidth();
						tagHeight = sourceImage.getHeight();
						
						AffineTransform t = new AffineTransform();

					    switch (tagInfoInt) {
					    case 1:
					        break;
					    case 2: // Flip X
					        t.scale(-1.0, 1.0);
					        t.translate(-tagWidth, 0);
					        break;
					    case 3: // PI rotation 
					        t.translate(tagWidth, tagHeight);
					        t.rotate(Math.PI);
					        break;
					    case 4: // Flip Y
					        t.scale(1.0, -1.0);
					        t.translate(0, -tagHeight);
					        break;
					    case 5: // - PI/2 and Flip X
					        t.rotate(-Math.PI / 2);
					        t.scale(-1.0, 1.0);
					        break;
					    case 6: // -PI/2 and -width
					        t.translate(tagHeight, 0);
					        t.rotate(Math.PI / 2);
					        break;
					    case 7: // PI/2 and Flip
					        t.scale(-1.0, 1.0);
					        t.translate(-tagHeight, 0);
					        t.translate(0, tagWidth);
					        t.rotate(  3 * Math.PI / 2);
					        break;
					    case 8: // PI / 2
					        t.translate(0, tagWidth);
					        t.rotate(  3 * Math.PI / 2);
					        break;
					    }
					    
					    String tmpPrefixa = savefullStr.substring(0, savefullStr.lastIndexOf("."));
					    String tmpLastfixa = savefullStr.substring(savefullStr.lastIndexOf(".")+1);
					    FileInputStream fis = new FileInputStream(new File(savefullStr));
					    FileOutputStream fos = new FileOutputStream(new File(tmpPrefixa + "_BASE_thumbnail."+tmpLastfixa));
					    int readData = 0;
					    while ((readData = fis.read()) != -1) {
							fos.write(readData);
						}
					    fis.close();
					    fos.close();
					    BufferedImage sourceImage11 = ImageIO.read(new File(tmpPrefixa+"_BASE_thumbnail."+tmpLastfixa));
					    
					    AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BICUBIC);

					    BufferedImage destinationImage = op.createCompatibleDestImage(sourceImage11, (sourceImage11.getType() == BufferedImage.TYPE_BYTE_GRAY) ? sourceImage11.getColorModel() : null );
					    Graphics2D g = destinationImage.createGraphics();
					    g.setBackground(Color.WHITE);
					    g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
					    destinationImage = op.filter(sourceImage11, destinationImage);
					    
//					    Graphics g11 = newImage11.getGraphics();
//			        	g11.clearRect(0, 0, sourceImage.getWidth(), sourceImage.getHeight());
//			        	g11.drawImage(scaledImage, 0, 0, null);
//			        	g11.dispose();
					    
						File test1 = new File(tmpPrefixa + "_AAA_thumb_thumbnail.png");
						ImageIO.write(destinationImage, "jpg", test1);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
		//정보 리턴
		return changeImage;
	}
}
