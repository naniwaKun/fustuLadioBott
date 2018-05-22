package futsuladiobot;
import static spark.Spark.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import java.lang.Thread.UncaughtExceptionHandler;

public class Main {
	public static String domain = "";
	public static String audio_html = "";
	public static String tweet = "";
	public static String liveId = "0";
	public static String saisei = "0";
	public static String ita = "https://mao.5ch.net/livevenus/subback.html";

	public static void main(String[] args) {
	//	secure(keystoreFilePath, keystorePassword, truststoreFilePath, truststorePassword);
		port(8090);

		staticFiles.location("/public");
		get("/live/audio_html", (req, res) -> { return  new String( audio_html .getBytes("UTF-8"), "UTF-8");});
		get("/live/card/*/*/", (req, res) -> {
			String html = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<meta charset=\"utf-8\">\n";
			String[] splat = req.splat();

			String para1 = URLDecoder.decode(splat[0], "UTF-8");
			String para2 = URLDecoder.decode(splat[1], "UTF-8");

			html = html + "<meta name=\"twitter:card\" content=\"summary\">\n"
					+ "<meta name=\"twitter:site\" content=\"@nytimes\">\n"
					+ "<meta name=\"twitter:creator\" content=\"@fustuu_no_ladio \">\n"
					+ "<meta name=\"twitter:title\" content=\"" + para1 + "\">\n"
					+ "<meta name=\"twitter:description\" content=\"" + para2 + "\">\n"
					+ "<meta name=\"twitter:image:src\" content=\"http://live.cfw4.tk/live/image/icon.jpg\">"
					+ "</head>";
				html = html + "<body>\n" + "<meta http-equiv=\"refresh\" content=\"0;URL=/live/\">" + "</body>"
						+ "</html>";
			
			return html;
		});

		get("/", (req, res) -> {
			return "helloworld";
			});

		new Thread(new Runnable() {
			@Override
			public void run() {
				
				System.out.println("thread runninng");
				String id = "0";
				while (true) {
					System.out.println("I'm not dead ..");
					try {
						TimeUnit.SECONDS.sleep(30);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					List<HashMap<String, String>> list;
					try {
						list = getFtsuList();
					} catch (IOException e) {
						list = new ArrayList<HashMap<String, String>>();
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (list.size() == 0) {
						id = "0";
						saisei = "0";
						tweet = "リレーが終わりました！新たな次ＤＪさんにwktk！wktk！ " + liveId;
						audio_html = "<div class=\"demo-card-image mdl-card mdl-shadow--2dp\">\n"
								+ "<div class=\"mdl-card__title mdl-card--expand\"></div>\n"
								+ "<div class=\"mdl-card__actions\">\n" + "<span class=\"demo-card-image__filename\">\n"
								+ "<style>\n" + "    .demo-card-image.mdl-card {\n" + "    width: 400px;\n"
								+ "    background: url(\"./image/off.jpg\") center / cover;\n" + "    }\n"
								+ "</style>\n"
								+ "<p><a href=\"http://mao.2ch.net/livevenus/subback.html\" target=\"_blank\" >Off Air... まだ見ぬDJさんがまだお見えにならない!!</a></p></span>\n"
								+ "</div>\n" + "</div><a class=\"twitter-timeline\" data-lang=\"ja\" data-width=\"400\" data-height=\"470\" data-dnt=\"true\" data-theme=\"dark\" data-link-color=\"#E95F28\" href=\"https://twitter.com/fustuu_no_ladio\">Tweets by fustuu_no_ladio</a> <script async src=\"//platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>";

					} else if (list.size() == 1) {
						id = list.get(0).get("SURL");
						ita = list.get(0).get("URL");
						saisei = "http://" + list.get(0).get("SRV") + ":" + list.get(0).get("PRT")
								+ list.get(0).get("MNT");
						String dj = list.get(0).get("DJ");
						String title = list.get(0).get("NAM");
						if (dj.equals("")) {
							dj = title.split("@|＠")[1];
						}
						String desc = list.get(0).get("GNL") + " " + list.get(0).get("DESC") + " " + dj + "さんの放送";

						String url = "";
						try {
							url = "http://live.cfw4.tk/live/card/" + URLEncoder.encode(title, "UTF-8") + "/"
									+ URLEncoder.encode(desc, "UTF-8") + "/";
						} catch (UnsupportedEncodingException e) {
							url = "http://live.cfw4.tk/live/";
						}

						if (liveId.equals("0")) {
							tweet = "リレーが始まりました！ " + title + " " + url;
						} else {
							tweet = "バトンがつながりました！ " + title + " " + url;
						}
						audio_html = "<div class=\"demo-card-image mdl-card mdl-shadow--2dp\">\n"
								+ "<div class=\"mdl-card__title mdl-card--expand\"></div>\n"
								+ "<div class=\"mdl-card__actions\">\n" + "<span class=\"demo-card-image__filename\">\n"
								+ "<style>\n" + "    .demo-card-image.mdl-card {\n" + "    width: 400px;\n"
								+ "    background: url(\"./image/on.jpg\") center / cover;\n" + "    }\n" + "</style>\n"
								 +   "<audio src=\"http://" + list.get(0).get("SRV") + ":" + list.get(0).get("PRT")
								+ list.get(0).get("MNT") + "\" autoplay controls>\n" + "</audio> " + "<script>\n" + "var instance = new Notification(\n" + "        \""
								+ title + "\"," + "        {\n" + "            body: \"「" + desc + "」\" ,"
								+ "            icon: \"http://ladio.fam.cx/live/image/icon.jpg\", " + "        }\n"
								+ "    );\n" + "    instance .config({autoClose: 30000});" + "</script></span>\n"
								+ "</div>\n"
								+"</div>"
										+ "<div class=\"movieWraper\">\n" + 
										"            <div class=\"iframeWrap\">\n" + 
										"                <iframe width=\"100%\" height=\"100%\" src=\""+ list.get(0).get("URL") + " \" frameborder=\"0\" allowfullscreen></iframe>\n" + 
										"            </div>\n" + 
										"        </div>";

					}
					if (!id.equals(liveId)) {
						tweet(tweet);
						//System.out.println(tweet);
						try {
							TimeUnit.SECONDS.sleep(10);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

					}
					liveId = id;
//break;
				}
				
				
			}
		}).start();

	}

	public static void tweet(String str) {
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			Status status = twitter.updateStatus(str);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<HashMap<String, String>> getFtsuList() throws IOException {
		URL url = new URL("http://yp.ladio.net/stats/list.v2.dat");
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setRequestMethod("GET");
		http.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream(), "SJIS"));
		List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		String line = "";
		boolean flag = false;
		HashMap<String, String> hash = new HashMap<String, String>();
		while ((line = reader.readLine()) != null) {
			String str = new String(line.getBytes(), "UTF-8");
			// str = line;
			if (str.equals("")) {
				if (flag) {
					ret.add(hash);
				}
				flag = false;
				hash = new HashMap<String, String>();
			} else {
				String[] strs = str.split("=", 2);
				hash.put(strs[0], strs[1]);
				if (strs[0].equals("NAM") && strs[1].matches(".*普通にねとらじリレー.*")) {
				//if (strs[0].equals("NAM") && strs[1].matches(".*おつかれ.*")) {
					flag = true;
				}
			}
		}
		reader.close();
		return ret;
	}
}
