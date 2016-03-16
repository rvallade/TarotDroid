package fr.commun.utils;

public class Logs {
	private static int level = 0;
	private static int debug = 0;
	private static int info = 1;
	private static int error = 2;
	
	public static void debug(String message){
		if (level<=debug)
			System.out.println(message);
	}
	public static void info(String message){
		if (level<=info)
			System.out.println(message);
	}
	public static void error(String message){
		if (level<=error)
			System.out.println(message);
	}
}
