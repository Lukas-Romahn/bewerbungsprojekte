package dao;

public class DaoFactory {
	
	private static DaoFactory instance;
	
	private DaoFactory() {
		
		
		
	}
	
	public static DaoFactory getInstance() {
		if(instance==null) {
			instance=new DaoFactory();
		}
			return instance;
		
	}
	public UserDao getUserDao(String dbType) {
		if(dbType.equals("psql")) {
			return new UserDaoImpl();
		}else {
			return null;
		}
	}
	
	public cameraDao getCameraDao(String dbType) {
		if(dbType.equals("psql")) {
			return new CameraDaoImpl();
		}else {
			return null;
		}
	}
	
	public locationDao getLocationDao(String dbType) {
		if(dbType.equals("psql")) {
			return new locationDaoImpl();
		}else {
			return null;
		}
	}

	public PictureDao getPictureDao(String dbType) {
		if(dbType.equals("psql")) {
			return new PictureDaoImpl();
		}else {
			return null;
		}
	}
	

}
