package gov.ca.fppc.fppcgifttracker.model;

public class ExcelOutput {
	private Source src;
	private Gift gft;
	private double amount;
	
	public ExcelOutput (Source s, Gift g, double amount) {
		this.src = s;
		this.gft = g;
		this.amount = amount;
	}
	
	public Source getSource() { return this.src;}
	public Gift getGift() {return this.gft;}
	public double getAmount() {return this.amount;}	
	public void setSource(Source s) {this.src=s;}
	public void setGift(Gift g) {this.gft=g;}
	public void setAmount(double a) {this.amount=a;}
}
