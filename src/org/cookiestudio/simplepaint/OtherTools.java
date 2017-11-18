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
	//���������Ĺ��ߵĻ��࣬�̳�Shape���Ա�ǰ���������ֱ������
	public void draw(Graphics g) {}
	//�������߲���draw()������
}

class AreaFiller extends OtherTools{
	//������������ɫ
	public void mouseClicked(MouseEvent e) {
		MainFrame.pa.Record();
		FloodFill(MainFrame.pa.bi,e.getX(),e.getY(),e.getButton());
		MainFrame.pa.repaint();
	}
	public static void FloodFill(BufferedImage img,int i,int j,int Button){
		//�����ݽṹ��P114��118������ͨ����㷨������ע����������㷨��д
		//�ݹ��ע����������㷨�����StackOverflowError���ݹ���ȹ���
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
				x=p.x;y=p.y+1;		//��
				s.push(new Point(x,y));
				x=p.x;y=p.y-1;		//��
				s.push(new Point(x,y));
				x=p.x-1;y=p.y;		//��
				s.push(new Point(x,y));
				x=p.x+1;y=p.y;		//��
				s.push(new Point(x,y));
			}
		}
	}
}

class Text extends OtherTools implements ActionListener{
	//�������
	private JComboBox fontBox=new JComboBox();		//����ѡ��
	private JComboBox sizeBox=new JComboBox();		//�ֺ�ѡ��
	private JCheckBox bold=new JCheckBox("����");
	private JCheckBox italic=new JCheckBox("б��");
	private JCheckBox underline=new JCheckBox("�»���");
	private JTextField tf=new JTextField();						//����
	private JDialog textdlg=new JDialog(MainFrame.jf,"�������",true);
	private Graphics g=MainFrame.pa.bi.getGraphics();
	
	public void mouseClicked(MouseEvent e) {
		x1=e.getX();y1=e.getY();
		Perform();
	}
	private void Perform(){
		textdlg.setLayout(null);
		textdlg.setBounds(150,150,415,130);
		textdlg.setResizable(false);
		
		String allfonts[]=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();	//�����б�
		for(int i=0;i<allfonts.length;i++)fontBox.addItem(allfonts[i]);			//��ʾ��ComboBox
		fontBox.setBounds(5, 5, 150, 25);
		textdlg.add(fontBox);
		
		int allSizes[]={10,14,18,22,26,30,40,50,70,90};										//�ֺ��б�
		for(int i=0;i<allSizes.length;i++)sizeBox.addItem(Integer.toString(allSizes[i]));
		sizeBox.setBounds(160, 5, 60, 25);
		sizeBox.setSelectedItem("22");														//Ĭ�ϣ�22��
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
		
		JButton okButton=new JButton("ȷ��");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		okButton.setBounds(120, 65, 60, 30);
		textdlg.add(okButton);
		
		JButton cancelButton=new JButton("ȡ��");		
		cancelButton.setActionCommand("CANCEL");
		cancelButton.addActionListener(this);
		cancelButton.setBounds(240, 65, 60, 30);
		textdlg.add(cancelButton);
		
		textdlg.setVisible(true);
	}
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("OK")){			//���ȷ�ϣ����������
			HashMap<TextAttribute, Object> hm = new HashMap<TextAttribute, Object>();
			//HashMap��������ķ����ο����ϣ�JDK�ĵ�
			if(underline.isSelected()) hm.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);  //�»���
			if(bold.isSelected()) hm.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);		//����
			if(italic.isSelected()) hm.put(TextAttribute.POSTURE,TextAttribute.POSTURE_OBLIQUE);	//б��
			hm.put(TextAttribute.SIZE, Integer.valueOf((String)sizeBox.getSelectedItem()));    //�ֺ� 
			hm.put(TextAttribute.FAMILY, (String)fontBox.getSelectedItem());    //����
			Font font = new Font(hm);			//��HashMap�����������
			
			MainFrame.pa.Record();
			g.setColor(PaintProperties.currentColor);
			g.setFont(font);
			g.drawString(tf.getText(), x1, y1);
			MainFrame.pa.repaint();
		}
		textdlg.dispose();			//�رնԻ���
	}
}

class ColorReader extends OtherTools{
	//��ɫʰȡ
	public void mouseClicked(MouseEvent e) {
		//�����������ǰ��ɫ���Ҽ��������뱳��ɫ
		if(e.getButton()==MouseEvent.BUTTON1)ColorPicker.setColor(new Color(MainFrame.pa.bi.getRGB(e.getX(),e.getY())));
		else if(e.getButton()==MouseEvent.BUTTON3)ColorPicker.setBg(new Color(MainFrame.pa.bi.getRGB(e.getX(),e.getY())));
	}
}

class HintAdapter extends MouseAdapter{
	//״̬���ϵ�������ʾ
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
		//��ӡ
		//�ο����ϣ�http://bbs.csdn.net/topics/350169483
		
		PrintService service=null;
		DocFlavor psInFormat = DocFlavor.INPUT_STREAM.JPEG;
		//��ӡʱ�õ�MIME������JPEG
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		try {
		    ImageIO.write(MainFrame.pa.bi, "jpg", outstream);
		    //�ѻ���ͼ��������ֽ���
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
			JOptionPane.showMessageDialog(MainFrame.jf, "��ǰϵͳû�����Ӵ�ӡ�����ӡ������ʱ�����á�", "��ӡʧ��", JOptionPane.INFORMATION_MESSAGE);
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