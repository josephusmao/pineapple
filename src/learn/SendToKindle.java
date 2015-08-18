package learn;

import java.io.File;
import java.io.FilenameFilter;

public class SendToKindle {
	private String to;
	private String bookName;
	private int bookNum;

	public SendToKindle(String to, String bookName, int bookNum) {
		this.to = to;
		this.bookName = bookName;
		this.bookNum = bookNum;
	}

	public void run() throws Exception {
		// 搜索与下载
		SearchAndDown down = new SearchAndDown(bookName, bookNum);
		down.run();

		// 推送
		File file = new File(".");
		String[] fileList = file.list(new Filter(".mobi"));
		for (int i = 0; i < fileList.length && i < bookNum; i++) { // 控制发送书的数目
			MailUtil mail = new MailUtil("466629332@qq.com", "smtp.qq.com", "466629332@qq.com", "HAN921013");
			mail.setTo(to);
			mail.setFilename(fileList[i]);
			mail.sendMail();
		}
	}

	private static class Filter implements FilenameFilter {
		private String type;

		public Filter(String type) {
			this.type = type;
		}

		public boolean accept(File dir, String name) {
			return name.endsWith(type);
		}
	}
}
