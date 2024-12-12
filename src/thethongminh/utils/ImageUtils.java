/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thethongminh.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author nlcuong
 */
public class ImageUtils {
    public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        // Create a new BufferedImage with the specified width and height
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());

        // Create a Graphics2D object to perform the image resizing
        Graphics2D g2d = resizedImage.createGraphics();
        
        // Perform the resizing (scaling) of the original image
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        
        // Dispose of the graphics object to release resources
        g2d.dispose();
        
        return resizedImage;
    }
    
    public static byte[] bufferedImageToByteArray(BufferedImage image) throws IOException {
        // Create a ByteArrayOutputStream to hold the byte data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        // Write the BufferedImage to the ByteArrayOutputStream as a PNG (can be changed to JPEG, BMP, etc.)
        ImageIO.write(image, "png", byteArrayOutputStream);
        
        // Return the byte array
        return byteArrayOutputStream.toByteArray();
    }
    
    public static BufferedImage byteArrayToBufferedImage(byte[] imageBytes) throws IOException {
        // Create a ByteArrayInputStream from the byte array
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        
        // Use ImageIO.read() to convert the byte array to a BufferedImage
        return ImageIO.read(byteArrayInputStream);
    }
}
