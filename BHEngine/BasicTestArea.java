package BHEngine;

public class BasicTestArea {
public static void main(String args[]){
	 String commaCount = new String("bf.returnString(34,343,343)");
	 int i = 0;
	 while(commaCount.contains(",")) {
		 
		 commaCount = commaCount.substring(commaCount.indexOf(",")+1,commaCount.length());
		 i++;
	 }
	 System.out.println(i);
	 
	
}
}
