package com.ch.mytesttools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageColler {
	static int size=32;
//	static float height =32;
//	static float width =32;
	public static void main(String args[]) throws FileNotFoundException, IOException{
		ImageColler test = new ImageColler();
		
//		changeImge(new File("E:/ch.jpg"));
//		changeImge(new File("E:/test.jpg"));
		
		test.alterImageRGB(new File("E:/1ch.jpg"));
		test.alterImageRGB(new File("E:/1test.jpg"));
		
//		cutImage("E:/1ch.jpg",5,5);
//		cutImage("E:/1test.jpg",5,5);
		
		
		
		BufferedImage image = ImageIO.read(new FileInputStream(new File("E:/1ch.jpg")));
		BufferedImage image1 = ImageIO.read(new FileInputStream(new File("E:/1test.jpg")));
//		float size =image.getHeight()*image.getWidth();
//		float size1 =image1.getHeight()*image1.getWidth();
		
//		if(size<=size1){
//			System.out.println(test.operationRGB(image1));
//			System.out.println(test.operationRGB(image,image1));
//		}
		

//		image = test.grayscale(image);
//		image1 = test.grayscale(image1);
		System.out.println(test.operationRGB1(image));
		System.out.println(test.operationRGB1(image1));
	}
	
	public float operationRGB1(BufferedImage image) throws IOException{
		float height=image.getHeight();
		float width=image.getWidth();
		
//		image=resize(image,(int)width,(int)height);
		
//		image = grayscale(image);

		String img = "";
		float rgbsum=0;
		int statussum = 0;
	    for (int i = 0; i < width; i++) {
	    	for (int j = 0; j < height; j++) {
	    		if((image.getRGB(i, j)  & 0xff)<160){
	    			statussum++;
	    			rgbsum+=(image.getRGB(i, j)  & 0xff);
//	    			System.out.println(image.getRGB(i, j)  & 0xff);
	    		}
			    
//	    		statussum++;
//    			rgbsum+=(image.getRGB(i, j)  & 0xff);
    			
//			    System.out.println(image.getRGB(i, j)  & 0xff);
			}
	    }
	    
//	    float avg = rgbsum/(height*width);
	    float avg = rgbsum/statussum;
	    
//	    System.out.println(rgbsum);
	    
	    System.out.println(image.getRGB(0, 1)  & 0xff);
//	    System.out.println(image.getRGB(7, 11)  & 0xff);
	    
	    return statussum;
	}
	
	//
	public float operationRGB(BufferedImage image){
		float height=image.getHeight();
		float width=image.getWidth();
		
//		image=resize(image,(int)width,(int)height);

		int imagefirstRGB=0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(imagefirstRGB != (image.getRGB(0, 0) & 0xff)){
					imagefirstRGB = image.getRGB(0, 0) & 0xff;
					if(i>0 && j>0){
						break;
					}
				}
			}
		}
		
		System.out.println(imagefirstRGB);
		
		String img = "";
		float rgbsum=0;
		int status = 0;
//	    for (int i = 0; i < width; i++) {
//	    	for (int j = 0; j < height; j++) {
//			    if(status==0){
//	    			if(imagefirstRGB == (image.getRGB(i, j)  & 0xff)){
//		    			rgbsum+=(image.getRGB(i, j)  & 0xff);
//		    			status=1;
//		    		}
//	    		}
//	    		if(status==1){
//	    			rgbsum+=(image.getRGB(i, j)  & 0xff);
//	    		}
//			}
//	    }
	    
//	    float avg = rgbsum/(height*width);
//	    System.out.println(rgbsum);
	    
	    return rgbsum;
	}
	
	public float operationRGB(BufferedImage image,BufferedImage image1){
		float height=image.getHeight();
		float width=image.getWidth();
		
		float height1=image1.getHeight();
		float width1=image1.getWidth();
		
//		image1=resize(image1,(int)width,(int)height);

		int imagefirstRGB=0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(imagefirstRGB != (image.getRGB(0, 0) & 0xff)){
					imagefirstRGB = image.getRGB(0, 0) & 0xff;
					if(i>0 && j>0){
						break;
					}
				}
			}
		}
		
		String img = "";
		float rgbsum=0;
		int status = 0;
	    for (int i = 0; i < width; i++) {
	    	for (int j = 0; j < height; j++) {
	    		if(status==0){
	    			if(imagefirstRGB == (image1.getRGB(i, j)  & 0xff)){
		    			rgbsum+=(image1.getRGB(i, j)  & 0xff);
		    			status=1;
		    		}
	    		}
	    		if(status==1){
	    			rgbsum+=(image1.getRGB(i, j)  & 0xff);
	    		}
			}
	    }
	    
//	    float avg = rgbsum/(height*width);
//	    System.out.println(rgbsum);
	    
	    return rgbsum;
	}
	
	
	//重绘图片
	private BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}
	
	//灰度模式
	private ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
	private BufferedImage grayscale(BufferedImage img) throws IOException {
		colorConvert.filter(img, img);
		return img;
	}
	
	/** 
     * * 转换图片 * * 
     */  
    public static void changeImge(File img) {  
        try {  
            Image image = ImageIO.read(img);  
            int srcH = image.getHeight(null);  
            int srcW = image.getWidth(null);  
            BufferedImage bufferedImage = new BufferedImage(srcW, srcH,BufferedImage.TYPE_3BYTE_BGR);  
            bufferedImage.getGraphics().drawImage(image, 0,0, srcW, srcH, null);  
            bufferedImage=new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null).filter (bufferedImage,null);   
            FileOutputStream fos = new FileOutputStream("E:/1"+img.getName());  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);  
            encoder.encode(bufferedImage);  
            fos.close();  
            System.out.println("转换成功...");  
        } catch (IOException e) {  
            e.printStackTrace();  
            throw new IllegalStateException("图片转换出错！", e);  
        }  
    } 
    
    /** 
     * 图片裁剪 
     * @param srcImageFile 图片裁剪地址 
     * @param result 图片输出文件夹 
     * @param destWidth 图片裁剪宽度 
     * @param destHeight 图片裁剪高度 
     */  
    public final static void cutImage(String srcImageFile, 
            int destWidth, int destHeight) {  
        try {  
            Iterator iterator = ImageIO.getImageReadersByFormatName("JPEG");/*PNG,BMP*/     
            ImageReader reader = (ImageReader)iterator.next();/*获取图片尺寸*/  
            InputStream inputStream = new FileInputStream(srcImageFile);    
            ImageInputStream iis = ImageIO.createImageInputStream(inputStream);     
            reader.setInput(iis, true);     
            ImageReadParam param = reader.getDefaultReadParam();     
            Rectangle rectangle = new Rectangle(0,0, destWidth, destHeight);/*指定截取范围*/      
            param.setSourceRegion(rectangle);     
            BufferedImage bi = reader.read(0,param);   
            ImageIO.write(bi, "JPEG", new File(srcImageFile));  
            System.out.println("图片裁剪成功。。。。");
        } catch (Exception e) {  
            System.out.println("图片裁剪出现异常:"+e);
        }  
    }  
    
    public void alterImageRGB(File img) throws IOException{
    	BufferedImage image = ImageIO.read(new FileInputStream(img));
    	
    	int width=image.getWidth();
    	int height=image.getHeight();
    	
    	for (int i = 0; i < width; i++) {
	    	for (int j = 0; j < height; j++) {
	    		image.setRGB(i, j, 255);
//	    		if((image.getRGB(i, j)  & 0xff)!=0){
//	    			image.setRGB(i, j, 0);
//	    		}
			}
	    }
    	
		 FileOutputStream fos = new FileOutputStream("E:/"+img.getName());  
	     JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);  
	     encoder.encode(image);  
	     fos.close();  
	     System.out.println("转换成功...");  
    }
}
