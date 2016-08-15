package tum.main;

import tum.utils.Param;
import tum.utils.Util;

public class Main {

	public static void main(String[] args) {
		if(!Util.checkCommandParams(args)) System.exit(1);

		long start = System.currentTimeMillis();
		System.out.println("user:" + Util.getSysUser());
		System.out.println(Util.getDT(start) + "  Start");

		Param.setParams();

		String mode = args[0].toLowerCase();
		String localPath = args[1];

		if(mode.equals("tum")){
			Util.log("  Tile Upload Start");
			TileTask.exec(localPath);
			Util.log("  Tile Upload End\n");
		}
		else if(mode.equals("mokuroku")){
			Util.log("  Mokuroku Upload Start");
			Mokuroku.exec(localPath);
			Util.log("  Mokuroku Upload End\n");
		}
		else if(mode.equals("cocotile")){
			Util.log("  Cocotile Upload Start");
			if (args.length == 3) {
				if (args[2].toLowerCase().equals("upload")) {
					CocotileTask.exec(localPath);
				}
			} else {
				Cocotile.exec(localPath);
			}
			Util.log("  Cocotile Upload End\n");
		}

		long end = System.currentTimeMillis();
		System.out.println(Util.getDT(end) + "  End");
		System.out.println("Elapsed time:" + Util.getElapsedTime(start, end));

		System.exit(0);
	}
}
