package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PaintingArea extends Canvas {
	public int Height,Width;
	public BufferedImage bi;
	public BufferedImage prevBi;		//������
	public BufferedImage nextBi;		//�ָ���
	private Image offImage=null;					//�ڶ�������
	public PaintingArea(int w,int h){
		Height=h;Width=w;
		MenuBar.redoItem.setEnabled(false);
		MenuBar.undoItem.setEnabled(false);
		//ͼ�񻺳�����ȫ�׷�������
		bi=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
		//��СΪWidth*Height��������С������ɫ����ΪRGB��ͼ�񻺳���
		bi.getGraphics().setColor(Color.white);
		bi.getGraphics().fillRect(0, 0, Width, Height);
		prevBi=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
		prevBi.getGraphics().setColor(Color.white);
		prevBi.getGraphics().fillRect(0, 0, Width, Height);
		nextBi=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
		nextBi.getGraphics().setColor(Color.white);
		nextBi.getGraphics().fillRect(0, 0, Width, Height);
	}
	public void paint(Graphics g){
		g.drawImage(bi,0,0,this);		//��ͼ�񻺳�����ͼ����Ƴ���
	}
	public void update(Graphics g){
		//��˫�������ͼ�Ρ�ֱ�ߡ���Ƥ�ĵ�ǰ״̬
		//�ο����ϣ�http://zhidao.baidu.com/question/241783477.html
		int currentState=PaintProperties.getCurrentState();
		if(offImage==null) offImage=createImage(Width,Height);
		Graphics offG=offImage.getGraphics();
		offG.drawImage(bi,0,0,this);
		if(PaintProperties.drawing) PaintProperties.shapes[currentState].draw(offG);
		g.drawImage(offImage,0,0,this);
	}
	public void Record(){
		//��¼Ӧ�ó�������һ��
		MainFrame.modified=true;
		prevBi.getGraphics().drawImage(MainFrame.pa.bi,0,0,null);
		MenuBar.redoItem.setEnabled(false);
		MenuBar.undoItem.setEnabled(true);
	}
	public static void setClipboardImage(final Image image) {
		//��ͼ��image�����������
		//�ο����ϣ�http://danker-dai.iteye.com/blog/627050
		Transferable trans = new Transferable(){
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if(isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}

		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}
	public static Image getClipboardImage() {
		//���������е�ͼ�񴫸�����ֵ
		Transferable trans=Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			return ((Image)trans.getTransferData(trans.getTransferDataFlavors()[0]));
		} catch (Exception e) {
			//�����쳣˵������������ͼ�������岻�ɷ��ʣ�����null��
			return null;
		}
	}
}