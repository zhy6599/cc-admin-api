package cc.admin.modules.jp.util;

import cn.hutool.core.io.FileUtil;
import com.google.common.base.Charsets;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WordUtil {

	/**
	 * 将目录中所有文件整合成word
	 * @param mergePath
	 * @param aimPath
	 * @param maxNum 最大文件序号，从0开始
	 * @throws IOException
	 */
	public static void writeWord(String mergePath,String aimPath,int maxNum) throws IOException{
		try {
			Rectangle rectPageSize = new Rectangle(PageSize.A4);
			rectPageSize = rectPageSize.rotate();
			Document document = new Document(rectPageSize,80, 20, 20,20); //建立文档

			RtfWriter2.getInstance(document, new FileOutputStream(aimPath));
			document.open();

			HeaderFooter header = new HeaderFooter(new Phrase("家谱"), false);
			header.setAlignment(Rectangle.ALIGN_CENTER);
			header.setTop(100);
			document.setHeader(header);


			BaseFont bfChinese = null;
			try {
				bfChinese = BaseFont.createFont("C:\\WINDOWS\\Fonts\\SIMSUN.TTC,0", BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			} catch (Exception e) {
				bfChinese = BaseFont.createFont("/opt/SIMSUN.TTC,0", BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			}
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);

			for(int i=0;i<maxNum;i++){
				if(i>0)
					document.newPage();

				List<String> contents = FileUtil.readLines(String.format(mergePath+"/%d.txt", i), Charsets.UTF_8);
				for(String content : contents){
					document.add(new Paragraph(content,contextFont));
				}

			}
			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			Rectangle rectPageSize = new Rectangle(PageSize.A4);
			rectPageSize = rectPageSize.rotate();
			Document document = new Document(rectPageSize,60, 20, 20,20); //建立文档

			RtfWriter2.getInstance(document, new FileOutputStream("E:/word.doc"));
			document.open();

			HeaderFooter header = new HeaderFooter(new Phrase("家谱"), false);
			header.setAlignment(Rectangle.ALIGN_CENTER);
			header.setTop(100);
			document.setHeader(header);


			document.add(new Paragraph("用java生成word文件"));
			document.newPage();
			document.add(new Paragraph("用java生成word文件2"));

			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
