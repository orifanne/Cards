package ori.cards;

public class Card {
	
	private String name;
	private String color;
	private int picture;
	private int sPicture;
	private String descr = "";

	public Card(String name, String color, int picture, int sPicture, String descr) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.color = color;
		this.picture = picture;
		this.sPicture = sPicture;
		this.descr = descr;
	}
	
	public String getName() {
		return name;
	}
	
	public String getColor() {
		return color;
	}
	
	public int getPicture() {
		return picture;
	}
	
	public int getSPicture() {
		return sPicture;
	}
	
	public String getDescr() {
		return descr;
	}

}
