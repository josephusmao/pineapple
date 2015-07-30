package learn;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MultiThreadDown {
	public static void main(String[] args) throws Exception {
		String path = new String();
		String targetFile = new String();

		//�������
		Document doc = Jsoup.connect("http://book.zi5.me/archives/book/1032").get();
		Elements links = doc.select("[href]");
		for (Element link : links) {
			if (link.attr("href").toString().endsWith(".mobi")) {
				path = link.attr("href").toString();
				int split = doc.title().toString().indexOf(" | ");	//���鱦�� | �������
				targetFile = doc.title().toString().substring(0, split) + ".mobi";
				break;
			}
		}
		
		DownUtil downUtil = new DownUtil(path, targetFile, 4);
		downUtil.download();
		new Thread() {
			public void run() {
				while (downUtil.getCompleteRate() < 1) {
					System.out.println("����ɣ�" + String.format("%.1f", 100.0 * downUtil.getCompleteRate()) + "%");
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("����ɣ�100%");
			}
		}.run();
	}
}
