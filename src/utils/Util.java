package tum.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public class Util {

	// checkCommandParams
	public static Boolean checkCommandParams(String[] args){
		if (args.length == 0) {
			System.err.println("tile, mokuroku, cocotile の作成/アップロードを実行。");
			System.err.println("Params.xml, AwsCredentials.propertiesが必要\n");
			System.err.println("java –jar –Xmx1024m tum.jar 引数1  引数2");
			System.err.println("引数1: tum | mokuroku | cocotile");
			System.err.println("引数2: フォルダ (例)C:\\work");
			return false;
		}

		if (args.length <= 1 || args.length >= 4){
			System.err.println("引数は２つ必要です。\n");
			System.err.println("引数1: tum | mokuroku | cocotile");
			System.err.println("引数2: フォルダ (例)C:\\work");
			return false;
		}

		String mode = args[0].toLowerCase();
		if (!mode.equals("tum") && !mode.equals("mokuroku") && !mode.equals("cocotile")){
			System.err.println("第一引数はtum, mokuroku, またはcocotileを指定してください。\n");
			return false;
		}

        File file = new File(args[1]);
        if (!file.exists())  {
			System.err.println("第二引数は実存するパスを指定してください。\n");
			return false;
        }

		return true;
	}

	// TumLog.txt
	public static void log(String msg) {
		Path logFile = new File("TumLog.txt").toPath();

		try {
			BufferedWriter bw = Files.newBufferedWriter(logFile, Charset.forName("UTF8")
					, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			bw.write(getDT(System.currentTimeMillis()) + "  ");
			bw.write(msg);
            bw.newLine();
            bw.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	// SimpleDateFormat
	public static String getDT(long mills){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
		return sdf.format(mills);
	}

	// getSysUser
	public static String getSysUser(){
		return System.getProperty("user.name");
	}

	// getElapsedTime
	public static String getElapsedTime(long start, long end){
		Calendar result = Calendar.getInstance();

		long sa = end - start - result.getTimeZone().getRawOffset();
	    result.setTimeInMillis(sa);
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	    return sdf.format(result.getTime());
	}

	// sort
	public static List<String> distinct(List<String> slist) {
		return new ArrayList<String>(new LinkedHashSet<String>(slist));
	}

	// mokuroku mills2sec
	public static long mills2sec(long millsTime){
		return Math.floorDiv(millsTime, 1000L);
	}
}
