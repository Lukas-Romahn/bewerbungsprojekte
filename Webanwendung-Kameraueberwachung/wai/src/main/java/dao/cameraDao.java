package dao;

import java.sql.SQLException;
import java.util.List;

import model.Camera;
import utils.QueryStrategy.QueryBuilder;

public interface cameraDao {
    
	public List<Camera> getCameras(QueryBuilder builder);
	public List<Camera> getCamerasWithData(QueryBuilder builder);

	public int getCameraCount(QueryBuilder builder); 
	public int insertDevice(String name, String location, String domain);
	public int deleteDevices(QueryBuilder builder) throws SQLException;
	public int updateDevice(QueryBuilder setStrategy, QueryBuilder whereStrategy);
	public List<String> getStandorte();
}
