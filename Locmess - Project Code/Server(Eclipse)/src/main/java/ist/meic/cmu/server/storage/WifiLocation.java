package ist.meic.cmu.server.storage;

public class WifiLocation extends Location{
	private String ssid;
	public WifiLocation(String name, String ssid){
		super(name);
		this.ssid=ssid;
	}
	public String getSsid() {
		return ssid;
	}
}
