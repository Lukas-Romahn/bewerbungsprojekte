package utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import dao.DaoFactory;
import dao.cameraDao;
import dao.PictureDao;
import model.Camera;
import utils.QueryStrategy.*;

public class ImageDownloadJob implements Job{
	
	private static Logger jlog = LogManager.getLogger(ImageDownloadJob.class);
	Document doc;
	cameraDao camDao = DaoFactory.getInstance().getCameraDao("psql");
	PictureDao bildDao = DaoFactory.getInstance().getPictureDao("psql");
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		jlog.info("ImageDownloadJob startet");
	    String savePath, thumbnailPath;

	    try {
	        savePath = JNDIFactory.getInstance().getEnvironmentAsString("imageSavePath");
	        thumbnailPath = JNDIFactory.getInstance().getEnvironmentAsString("thumbnailSavePath");
	        jlog.info("ImageSavePath: " + savePath + " | ThumbnailSavePath: " + thumbnailPath);
	    } catch (NamingException e) {
	        jlog.error("Fehler beim Path holen", e);
	        throw new JobExecutionException(e);
	    }
	    
	    List<Camera> cameras=new ArrayList<>();
	    QueryBuilder builder = new QueryBuilder();
	    try {
	        cameras = camDao.getCameras(builder);
	        jlog.info("Gefundene Kameras: " + (cameras == null ? "null" : cameras.size()));
	    } catch (Exception e) {
	        jlog.error("Fehler beim Laden der Kameras", e);
	    }
	    
	    for(Camera cam : cameras) {
	    	String imageUrl = cam.getDomain();
	    	String camName = cam.getName();
	    	int camID = cam.getKid();
	    	String camStatus = cam.getStatus();
	    	
	    	jlog.info("Kamera: " + camName + " | URL: " + imageUrl);
	    	
	    	if(camStatus.equals("aktiv")) {
	    		try {
					downloadImage(imageUrl, savePath, thumbnailPath, camName, camID);
				} catch (Exception e) {
					jlog.error("Fehler Beim downloadImage, der Kamera " + camName + ": " + e.getMessage());
				}
	    	}
	    }
	  
	    
	}
	
	private void downloadImage(String imageURL, String savePath, String thumbnailPath,  String camName, int camID) throws Exception {
		Date now = new Date();
		Timestamp timestampDB = new Timestamp(now.getTime());
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(now);
        String fileName = camName + "_" + timestamp + ".jpg";
        savePath = savePath + "/" + camID;
        thumbnailPath = thumbnailPath + "/" + camID;
        File targetFile = new File(savePath, fileName);
        File thumbFile = new File(thumbnailPath, "thumb_" + fileName);
        
        //Ordner anlegen wenn nötig
        File imageDir = new File(savePath);
        if(!imageDir.exists()) {
        	imageDir.mkdirs();
        }
        
        File thumbDir = new File(thumbnailPath);
        if(!thumbDir.exists()) {
        	thumbDir.mkdirs();
        }
        
        //Image Download
        try (InputStream in = new URL(imageURL).openStream();
             FileOutputStream out = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            jlog.info("Bild erfolgreich heruntergeladen: " + fileName);
            
            createThumbnail(targetFile, thumbFile, 200, 150);
            jlog.info("Thumbnail erstellt: " + thumbFile.getAbsolutePath());
            
            bildDao.insertBild(camID, thumbFile.getAbsolutePath(), targetFile.getAbsolutePath(), timestampDB);

        } catch (Exception e) {
        	jlog.error("Fehler Beim downloadImage, der Kamera " + camName + ": " + e.getMessage());

            // Kamera auf "passiv" setzen
            QueryBuilder setStrat = new QueryBuilder("SET");
            setStrat.addStrategy(new EqualsStrategy<>("status", "passiv"));

            QueryBuilder whereStrat = new QueryBuilder();
            whereStrat.addStrategy(new SimpleStrategy("kid = ?", camID, "WHERE"));

            int result = camDao.updateDevice(setStrat, whereStrat);

            if (result > 0) {
                jlog.warn("Kamera " + camName + " (ID: " + camID + ") auf 'passiv' gesetzt.");
            } else {
                jlog.error("Kamera " + camName + " konnte nicht auf 'passiv' gesetzt werden.");
            }
        }
    }
	
	public static void createThumbnail(File inputFile, File outputFile, int thumbWidth, int thumbHeight) throws Exception{
		BufferedImage originalImage = ImageIO.read(inputFile);
		
		BufferedImage thumbnail = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = thumbnail.createGraphics();
		g.drawImage(originalImage,  0, 0, thumbWidth, thumbHeight, null);
		g.dispose();
		
		ImageIO.write(thumbnail, "jpg", outputFile);
	}
}

