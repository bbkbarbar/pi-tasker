package hu.barbar.util;

import java.util.ArrayList;

public class HtmlJoiner {
	
	public static final String INPUT_FILE = "htmlPart.list";

	private static HtmlJoiner joiner = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		joiner = new HtmlJoiner();
		joiner.run();
		
	}

	private void run() {
		
		ArrayList<String> fileList = FileHandler.readLines(INPUT_FILE, true);
		
		if(fileList == null || fileList.size() == 0){
			System.out.println("Please define part-list of html file in " + INPUT_FILE);
			return;
		}
		
		ArrayList<String> content = new ArrayList<>();
		for(int i = 0; i < fileList.size(); i++){
			content.addAll( FileHandler.readLines(fileList.get(i)) );
		}
		
		
		if( !FileHandler.writeToFile("output.html", content) ){
			System.out.println("Can not write output.");
		}
		
	}
	
	

}
