package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.*;

public abstract class OtherTools extends Shape{
	//后面新增的工具的基类，继承Shape类以便前面的引用上直接增加
	public void draw(Graphics g) {}
	//新增工具不用draw()函数。
}

class AreaFiller extends OtherTools{
	//封闭区域填充颜色
	public void mouseClicked(MouseEvent e) {
		MainFrame.pa.Record();
		FloodFill(MainFrame.pa.bi,e.getX(),e.getY(),e.getButton());
		MainFrame.pa.repaint();
	}
	public static void FloodFill(BufferedImage img,int i,int j,int Button){
		//《数据结构》P114～118的四连通填充算法，根据注入填充区域算法改写
		//递归的注入填充区域算法会造成StackOverflowError（递归深度过大）
		int currentcolor=img.getRGB(i, j);
		Stack<Point> s=new Stack<Point>();
		Point p=new Point(i,j);
		s.push(p);
		while(!s.empty()){
			p=s.pop();
			int x=p.x,y=p.y;
			if(x<0 || y<0 | x>=574 || y>=400) continue;
			if(img.getRGB(x,y)==currentcolor){
				if(Button==MouseEvent.BUTTON1)img.setRGB(x, y, PaintProperties.currentColor.getRGB());
				else if(Button==MouseEvent.BUTTON3)img.setRGB(x, y, PaintProperties.bgColor.getRGB());
				else break;
				x=p.x;y=p.y+1;		//上
				s.push(new Point(x,y));
				x=p.x;y=p.y-1;		//下
				s.push(new Point(x,y));
				x=p.x-1;y=p.y;		//左
				s.push(new Point(x,y));
				x=p.x+1;y=p.y;		//右
				s.push(new Point(x,y));
			}
		}
	}
}

class Text extends OtherTools implements ActionListener{
	//添加文字
	private JComboBox fontBox=new JComboBox();		//字体选择
	private JComboBox sizeBox=new JComboBox();		//字号选择
	private JCheckBox bold=new JCheckBox("粗体");
	private JCheckBox italic=new JCheckBox("斜体");
	private JCheckBox underline=new JCheckBox("下划线");
	private JTextField tf=new JTextField();						//打字
	private JDialog textdlg=new JDialog(MainFrame.jf,"添加文字",true);
	private Graphics g=MainFrame.pa.bi.getGraphics();
	
	public void mouseClicked(MouseEvent e) {
		x1=e.getX();y1=e.getY();
		Perform();
	}
	private void Perform(){
		textdlg.setLayout(null);
		textdlg.setBounds(150,150,415,130);
		textdlg.setResizable(false);
		
		String allfonts[]=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();	//字体列表
		for(int i=0;i<allfonts.length;i++)fontBox.addItem(allfonts[i]);			//显示到ComboBox
		fontBox.setBounds(5, 5, 150, 25);
		textdlg.add(fontBox);
		
		int allSizes[]={10,14,18,22,26,30,40,50,70,90};										//字号列表
		for(int i=0;i<allSizes.length;i++)sizeBox.addItem(Integer.toString(allSizes[i]));
		sizeBox.setBounds(160, 5, 60, 25);
		sizeBox.setSelectedItem("22");														//默认：22磅
		textdlg.add(sizeBox);
		
		bold.setBounds(225,5,55,20);
		textdlg.add(bold);
		
		italic.setBounds(280,5,55,20);
		textdlg.add(italic);
		
		underline.setBounds(335,5,65,20);
		textdlg.add(underline);
		
		tf.setText("");
		tf.setBounds(5,35,400,25);
		textdlg.add(tf);
		
		JButton okButton=new JButton("确定");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		okButton.setBounds(120, 65, 60, 30);
		textdlg.add(okButton);
		
		JButton cancelButton=new JButton("取消");		
		cancelButton.setActionCommand("CANCEL");
		cancelButton.addActionListener(this);
		cancelButton.setBounds(240, 65, 60, 30);
		textdlg.add(cancelButton);
		
		textdlg.setVisible(true);
	}
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("OK")){			//如果确认，就输出文字
			HashMap<TextAttribute, Object> hm = new HashMap<TextAttribute, Object>();
			//HashMap构建字体的方法参考资料：JDK文档
			if(underline.isSelected()) hm.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);  //下划线
			if(bold.isSelected()) hm.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);		//粗体
			if(italic.isSelected()) hm.put(TextAttribute.POSTURE,TextAttribute.POSTURE_OBLIQUE);	//斜体
			hm.put(TextAttribute.SIZE, Integer.valueOf((String)sizeBox.getSelectedItem()));    //字号 
			hm.put(TextAttribute.FAMILY, (String)fontBox.getSelectedItem());    //字体
			Font font = new Font(hm);			//用HashMap构建输出字体
			
			MainFrame.pa.Record();
			g.setColor(PaintProperties.currentColor);
			g.setFont(font);
			g.drawString(tf.getText(), x1, y1);
			MainFrame.pa.repaint();
		}
		textdlg.dispose();			//关闭对话框
	}
}

class ColorReader extends OtherTools{
	//颜色拾取
	public void mouseClicked(MouseEvent e) {
		//左键单击放入前景色，右键单击放入背景色
		if(e.getButton()==MouseEvent.BUTTON1)ColorPicker.setColor(new Color(MainFrame.pa.bi.getRGB(e.getX(),e.getY())));
		else if(e.getButton()==MouseEvent.BUTTON3)ColorPicker.setBg(new Color(MainFrame.pa.bi.getRGB(e.getX(),e.getY())));
	}
}

class HintAdapter extends MouseAdapter{
	//状态栏上的文字提示
	String Hint;
	public HintAdapter(String hint){Hint=hint;}
	public void mouseEntered(MouseEvent arg0) {
		StatusBar.information.setText(Hint);
	}
	public void mouseExited(MouseEvent arg0) {
		StatusBar.information.setText(StatusBar.str);
	}
}

class PrintTool{
	public PrintTool(){
		//打印
		//参考资料：http://bbs.csdn.net/topics/350169483
		
		PrintService service=null;
		DocFlavor psInFormat = DocFlavor.INPUT_STREAM.JPEG;
		//打印时用的MIME类型是JPEG
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		try {
		    ImageIO.write(MainFrame.pa.bi, "jpg", outstream);
		    //把画板图像输出给字节流
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
		byte[] buf = outstream.toByteArray();
		InputStream stream = new ByteArrayInputStream(buf);
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.JPEG, null);
		PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
		if (services.length > 0) {
			service =  ServiceUI.printDialog(null,50,50,services,services[0],psInFormat,attributes);
		}else{
			JOptionPane.showMessageDialog(MainFrame.jf, "当前系统没有连接打印机或打印服务暂时不可用。", "打印失败", JOptionPane.INFORMATION_MESSAGE);
		}
		Doc myDoc = new SimpleDoc(stream, psInFormat, null);
		if(service!=null){
			DocPrintJob job = service.createPrintJob();
			try {
			    job.print(myDoc, attributes);
			}
			catch (PrintException pe) {
			    pe.printStackTrace();
			}
		}
	}
}