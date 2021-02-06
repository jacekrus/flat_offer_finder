package flat.offer;

public enum Site {
	
	OTODOM("https://www.otodom.pl/"),
	MORIZON("https://www.morizon.pl/");
	
	private final String url;
	
	private Site(String name) {
		this.url = name;
	}
	
	public String getUrl() {
		return url;
	}
	
}
