package ist.meic.cmu.server.storage;

public class GPSLocation extends Location{
	private int radius;
	private double longitude;
	private double latitude;
	public GPSLocation(String name, double latitude, double longitude, int radius){
		super(name);
		this.radius=radius;
		this.latitude=latitude;
		this.longitude=longitude;
	}

	public int getRadius() {
		return radius;
	}
	
	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}
}
