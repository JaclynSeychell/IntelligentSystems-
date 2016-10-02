package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Problem implements Concept {
	private int num;
	private String msg;
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int pNum) {
		num = pNum;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String pMsg) {
		msg = pMsg;
	}
	
	public String toString() {
		return "Problem {num:" + num + ", msg:" + msg + "}";
	}
}
