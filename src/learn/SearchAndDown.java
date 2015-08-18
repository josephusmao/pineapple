package learn;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SearchAndDown {
	private String bookName;
	private int bookNum;

	public SearchAndDown(String bookName, int bookNum) {
		this.bookName = bookName;	// 书名
		this.bookNum = bookNum;		// 最多下载
	}

	public void run() throws Exception {
		List<String> paths = new ArrayList<String>();

		// 子乌书简
		Document doc = Jsoup.connect("http://book.zi5.me/?s=" + URLEncoder.encode(bookName, "UTF-8")).get();
		Elements links = doc.select("[href]");
		for (Element link : links) {	// 得到所有下载链接
			if (link.attr("href").toString().startsWith("http://book.zi5.me/archives/book/")) {
				paths.add(link.attr("href").toString());
			}
		}

		for (int i = 0; i < paths.size() && i < bookNum * 2; i += 2) {	// 下载链接在paths中连续出现2次
			String path = paths.get(i);
			String targetFile = "";
			
			doc = Jsoup.connect(path).get();
			links = doc.select("[href]");
			for (Element link : links) {
				if (link.attr("href").toString().endsWith(".mobi")) {
					path = link.attr("href").toString();
					
					int split = doc.title().toString().indexOf(" | "); // 精灵宝钻 | 子乌书简
					targetFile = doc.title().toString().substring(0, split) + ".mobi";					
					break;
				}
			}
			System.out.println(path);
			System.out.println(targetFile);

			DownUtil downUtil = new DownUtil(path, targetFile, 4);
			downUtil.download();
			new Thread() {
				public void run() {
					while (downUtil.getCompleteRate() < 1) {
						System.out.println("已完成：" + String.format("%.1f", 100.0 * downUtil.getCompleteRate()) + "%");
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					System.out.println("已完成：100%");
				}
			}.run();
		}
	}
}
