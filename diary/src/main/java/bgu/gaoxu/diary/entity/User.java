package bgu.gaoxu.diary.entity;

public class User {

	private int user_id;
	private String user_name;
	private String user_psd;
	private String user_photo;
	private String user_friends;
	private int erro_code;
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getUser_id() {
		return user_id;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_name() {
		return user_name;
	}

	public void setUser_psd(String user_psd) {
		this.user_psd = user_psd;
	}
	public String getUser_psd() {
		return user_psd;
	}

	public void setUser_photo(String user_photo) {
		this.user_photo = user_photo;
	}
	public String getUser_photo() {
		return user_photo;
	}

	public void setUser_friends(String user_friends) {
		this.user_friends = user_friends;
	}
	public String getUser_friends() {
		return user_friends;
	}

	public void setErro_code(int erro_code) {
		this.erro_code = erro_code;
	}
	public int getErro_code() {
		return erro_code;
	}

}