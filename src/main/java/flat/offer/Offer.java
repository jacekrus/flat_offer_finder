package flat.offer;

import java.time.LocalDate;

public class Offer {
	
	private final String id;
	private final String link;
	private boolean isPromo;
	private LocalDate date;
	
	public Offer(String id, String link, boolean isPromo) {
		super();
		this.link = link;
		this.id = id;
		this.isPromo = isPromo;
	}
	
	public Offer(String id, String link, LocalDate date) {
		super();
		this.link = link;
		this.id = id;
		this.date = date;
	}
	
	public String getId() {
		return id;
	}
	
	public String getLink() {
		return link;
	}

	public boolean isPromo() {
		return isPromo;
	}

	public LocalDate getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Offer other = (Offer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Offer [id=" + id + "]";
	}
	
}
