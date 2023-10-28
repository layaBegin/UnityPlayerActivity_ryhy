package com.kola;

public class GameGloableData {

	//文件保存路径
	private static String headImagSaveFolderPath;
	public static String getHeadImagSaveFolderPath() {
		return headImagSaveFolderPath;
	}



	public static void setHeadImagSaveFolderPath(String headImagSaveFolderPath) {
		GameGloableData.headImagSaveFolderPath = headImagSaveFolderPath;
	}



	public static String getHeadImagSaveFileName() {
		return headImagSaveFileName;
	}



	public static void setHeadImagSaveFileName(String headImagSaveFileName) {
		GameGloableData.headImagSaveFileName = headImagSaveFileName;
	}



	//文件保存名称
	private static String headImagSaveFileName;
	
	
	//获取头像文件保存路径
	public static String GetHeadImgPath() {
		return headImagSaveFolderPath+"/"+headImagSaveFileName;
	}
	
	
	
	
}
