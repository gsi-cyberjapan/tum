package tum.main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import tum.aws.S3;
import tum.utils.LocalFile;
import tum.utils.Util;

public class Cocotile {

	// exec
	public static void exec(String localPath) {
		try {
			File dir = new File(localPath);
			dir.mkdirs();

			List<String> typeList = LocalFile.getTypeList(localPath);

			String typeExtListPath = localPath + "\\typeList.txt";
			File file = new File(typeExtListPath);

			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			typeList = Util.distinct(typeList);
			Collections.sort(typeList);

			for (String t : typeList) {
				pw.println(t);
			}

			pw.close();

			String str = "";
			Scanner scan1 = new Scanner(file);

			while (scan1.hasNext()) {
				str = scan1.next();
				String filePath = localPath + "\\" + str;

				dir = new File(filePath);
				dir.mkdirs();

				System.out.println("getFile " + filePath + "mokuroku.csv.gz");
				S3.getFileFromS3(str + "mokuroku.csv.gz", filePath + "mokuroku.csv.gz");

				readGzipFile(filePath + "mokuroku.csv.gz", localPath);
			} // end of make cocotile process

			scan1.close();

			System.out.println("end of make cocotile process");

			CocotileTask.exec(localPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// readGzipFile
	private static String readGzipFile(String filePath, String localPath){
		System.out.println("read GZIP" + filePath);

		try {
			File mokuroku = new File(filePath);

			Scanner scan = new Scanner(new InputStreamReader(
					new GZIPInputStream(new BufferedInputStream(
						new FileInputStream(mokuroku)))));

			String regex = "mokuroku";
			String regex2 = "style";

			Pattern p;
			Matcher m;

			Pattern p2;
			Matcher m2;

			String str = "", type = "";

			String coordinate = "";
			int index = 0;

			while (scan.hasNext()) {
				str = scan.next();

				p = Pattern.compile(regex);
				m = p.matcher(str);

				p2 = Pattern.compile(regex2);
				m2 = p2.matcher(str);

				if (!m.find() && !m2.find()) {
					String[] temp1 = str.split(",", 0);
					index = temp1[0].indexOf(".");

					if (index != -1) {
						coordinate = temp1[0];
						coordinate = coordinate.substring(0, coordinate.lastIndexOf("."));
						String[] temp2 = coordinate.split("/", 0);

						// z temp2[0]
						// x temp2[1]
						// y temp2[2]

						type = filePath;
						index = type.indexOf("xyz");
						type = type.substring(index + 4).trim();
						type = type.substring(0, type.length() - mokuroku.getName().length() - 1);

						makeCocotile(temp2[0], temp2[1], temp2[2], localPath, type);
					}
				}
			} // end of make cocotile process

			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Exception folder " + filePath);
		}

		return "success";
	}

	// makeCocotile
	private static void makeCocotile(String z, String x, String y, String localPath, String type) throws Exception {
		File cocotileDir = new File(localPath + "\\cocotile\\" + z + "\\" + x + "\\");
		cocotileDir.mkdirs();

		File cocotile = new File(localPath + "\\cocotile\\" + z + "\\" + x + "\\" + y + ".csv");

		if (!cocotile.exists()) { // first time
			FileOutputStream fos = new FileOutputStream(cocotile);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);
			pw.print(type);

			pw.close();
			osw.close();
			fos.close();
		} else { // else
			FileOutputStream fos = new FileOutputStream(cocotile, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);
			pw.print("," + type);

			pw.close();
			osw.close();
			fos.close();
		}
	}
}
