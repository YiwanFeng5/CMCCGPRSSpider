package cn.fywspring.spdierdemo.china10086.module;

public class GPRS {
	private String gprs_time;
	private String gprs_addr;
	private String gprs_way;
	private String gprs_long;
	private String gprs_sum;
	private String gprs_tcyh;
	private String gprs_fee;
	
	public GPRS() {
	}
	
	public GPRS(String gprs_time, String gprs_addr, String gprs_way, String gprs_long, String gprs_sum,
			String gprs_tcyh, String gprs_fee) {
		this.gprs_time = gprs_time;
		this.gprs_addr = gprs_addr;
		this.gprs_way = gprs_way;
		this.gprs_long = gprs_long;
		this.gprs_sum = gprs_sum;
		this.gprs_tcyh = gprs_tcyh;
		this.gprs_fee = gprs_fee;
	}




	public String getGprs_time() {
		return gprs_time;
	}




	public void setGprs_time(String gprs_time) {
		this.gprs_time = gprs_time;
	}




	public String getGprs_addr() {
		return gprs_addr;
	}




	public void setGprs_addr(String gprs_addr) {
		this.gprs_addr = gprs_addr;
	}




	public String getGprs_way() {
		return gprs_way;
	}




	public void setGprs_way(String gprs_way) {
		this.gprs_way = gprs_way;
	}




	public String getGprs_long() {
		return gprs_long;
	}




	public void setGprs_long(String gprs_long) {
		this.gprs_long = gprs_long;
	}




	public String getGprs_sum() {
		return gprs_sum;
	}




	public void setGprs_sum(String gprs_sum) {
		this.gprs_sum = gprs_sum;
	}




	public String getGprs_tcyh() {
		return gprs_tcyh;
	}




	public void setGprs_tcyh(String gprs_tcyh) {
		this.gprs_tcyh = gprs_tcyh;
	}




	public String getGprs_fee() {
		return gprs_fee;
	}




	public void setGprs_fee(String gprs_fee) {
		this.gprs_fee = gprs_fee;
	}




	@Override
	public String toString() {
		return "GPRS [gprs_time=" + gprs_time + ", gprs_addr=" + gprs_addr + ", gprs_way=" + gprs_way + ", gprs_long="
				+ gprs_long + ", gprs_sum=" + gprs_sum + ", gprs_tcyh=" + gprs_tcyh + ", gprs_fee=" + gprs_fee + "]";
	}
	
	
	
}
