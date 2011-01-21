package BHEngine;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
 
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
 
public class CropImage {
	
	String sourceDirPath=""; 
	String targetDirPath="";
	
	public CropImage(String sourceDirPath,String targetDirPath)
	{
   
   	 this.sourceDirPath=sourceDirPath;
   	 this.targetDirPath=targetDirPath;
	}
	public CropImage()
	{
		
	}
	/*getSubimage(int x, int y, int w, int h) 
			  Returns a subimage defined by a specified rectangular region.
	*/
	public void cropImage(String sourceFileName,String targetFileName,int x, int y, int w, int h)
	{
		String sourcePath=sourceDirPath+"\\"+sourceFileName;
		
		try
		{
			File tempFile=new File(targetDirPath+"\\"+targetFileName);
			OutputStream tmp = new FileOutputStream(tempFile);
		
			Image image = new ImageIcon(sourcePath).getImage();
			BufferedImage  outImage =toBufferedImage(image);
			BufferedImage img=outImage.getSubimage(x,y,w,h); 
			ImageIO.write(img,"png",tempFile);
			tmp.close();
		}
		catch(Exception e)
		{
           e.printStackTrace();			 
		}
}
	
	
	public static BufferedImage toBufferedImage(Image image) {
			 if (image instanceof BufferedImage) {
				 return (BufferedImage)image;
			 }
    
			 // This code ensures that all the pixels in the image are loaded
			 image = new ImageIcon(image).getImage();
    
			 // Determine if the image has transparent pixels; for this method's
			 // implementation, see e661 Determining If an Image Has Transparent Pixels
			 boolean hasAlpha = hasAlpha(image);
 
			 //boolean hasAlpha = false;
    
			 // Create a buffered image with a format that's compatible with the screen
			 BufferedImage bimage = null;
			 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			 try {
				 // Determine the type of transparency of the new buffered image
				 int transparency = Transparency.OPAQUE;
				 if (hasAlpha) {
					 transparency = Transparency.BITMASK;
				 }
    
				 // Create the buffered image
				 GraphicsDevice gs = ge.getDefaultScreenDevice();
				 GraphicsConfiguration gc = gs.getDefaultConfiguration();
				 bimage = gc.createCompatibleImage(
					 image.getWidth(null), image.getHeight(null), transparency);
			 } catch (HeadlessException e) {
				 // The system does not have a screen
			 }
    
			 if (bimage == null) {
				 // Create a buffered image using the default color model
				 int type = BufferedImage.TYPE_INT_RGB;
				 if (hasAlpha) {
					 type = BufferedImage.TYPE_INT_ARGB;
				 }
				 bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
			 }
    
			 // Copy image to buffered image
			 Graphics g = bimage.createGraphics();
    
			 // Paint the image onto the buffered image
			 g.drawImage(image, 0, 0, null);
			 g.dispose();
    
			 return bimage;
		 }
 
//		This method returns true if the specified image has transparent pixels
		 public static boolean hasAlpha(Image image) {
			 // If buffered image, the color model is readily available
			 if (image instanceof BufferedImage) {
				 BufferedImage bimage = (BufferedImage)image;
				 return bimage.getColorModel().hasAlpha();
			 }
    
			 // Use a pixel grabber to retrieve the image's color model;
			 // grabbing a single pixel is usually sufficient
			  PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
			 try {
				 pg.grabPixels();
			 } catch (InterruptedException e) {
			 }
    
			 // Get the image's color model
			 ColorModel cm = pg.getColorModel();
			 return cm.hasAlpha();
		 }
	
	
	/**
	 * @return
	 */
	public String getSourceDirPath() {
		return sourceDirPath;
	}
 
	/**
	 * @return
	 */
	public String getTargetDirPath() {
		return targetDirPath;
	}
 
	/**
	 * @param string
	 */
	public void setSourceDirPath(String string) {
		sourceDirPath = string;
	}
 
	/**
	 * @param string
	 */
	public void setTargetDirPath(String string) {
		targetDirPath = string;
	}
 
}
 