package learn;

public class User {
	private String email;
	private String bookName;
	private int bookNum;

	public User() {

	}

	public User(String email, String bookName, int bookNum) {
		this.email = email;
		this.bookName = bookName;
		this.bookNum = bookNum;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public void setBookNum(int bookNum) {
		this.bookNum = bookNum;
	}

	public String getEmail() {
		return this.email;
	}

	public String getBookName() {
		return this.bookName;
	}

	public int getBookNum() {
		return this.bookNum;
	}
}
