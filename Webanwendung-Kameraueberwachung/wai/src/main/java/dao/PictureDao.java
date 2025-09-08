package dao;

import java.sql.SQLException;
import java.util.List;

import model.Bild;
import utils.QueryStrategy.QueryBuilder;

public interface PictureDao {
    
    public List<String> getLocationMetaData(QueryBuilder builder);
    public List<Bild> getBilder(QueryBuilder builder);
    public List<String> getTimestamps(QueryBuilder builder, String format) throws SQLException;
    public List<Bild> getLastestPictures(QueryBuilder builder);
    public void insertBild(int kid, String thumbnailUrl, String ablageUrl, java.sql.Timestamp timestamp);
    public int getPictureCount(QueryBuilder builder);
}
