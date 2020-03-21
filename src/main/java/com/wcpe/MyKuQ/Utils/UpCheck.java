package com.wcpe.MyKuQ.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.nukkit.utils.Config;
import com.wcpe.MyKuQ.Main;




public class UpCheck {

	public static Config upCheckVersion() {
		Config load = new Config();
		try {
			load.load(WxysUtil.getImageStream("https://wcpe.github.io/NukkitMyKuQ.yml"));
		} catch (Exception e) {
			System.out.println("§4检查更新失败！！！");
		}
		return load;
	}
	public static boolean isLatestVersion(Config conf) {
		boolean isLatest = false;
		double latest = Double.valueOf(conf.getString("NewVersion"));
		double current = Main.Version;
		if(current < latest) {
			isLatest = true;
		}
		return isLatest;
	}
}
