package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PaintingArea extends Canvas {
	public int Height,Width;
	public BufferedImage bi;
	public BufferedImage prevBi;		//撤销用
	public BufferedImage nextBi;		//恢复用
	private Image offImage=null;					//第二缓冲区
	public PaintingArea(int w,int h){
		Height=h;Width=w;
		MenuBar.redoItem.setEnabled(false);
		MenuBar.undoItem.setEnabled(false);
		//图像缓冲区用全白方块填满
		bi=new BufferedImage(Width,Height,BufferedImage.TYPE_INT_RGB);
		//大小为Width*Height（画布大小），颜色类型为RGB的图像缓冲区
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
		g.drawImage(bi,0,0,this);		//将图像缓冲区的图像绘制出来
	}
	public void update(Graphics g){
		//用双缓冲绘制图形、直线、橡皮的当前状态
		//参考资料：http://zhidao.baidu.com/question/241783477.html
		int currentState=PaintProperties.getCurrentState();
		if(offImage==null) offImage=createImage(Width,Height);
		Graphics offG=offImage.getGraphics();
		offG.drawImage(bi,0,0,this);
		if(PaintProperties.drawing) PaintProperties.shapes[currentState].draw(offG);
		g.drawImage(offImage,0,0,this);
	}
	public void Record(){
		//记录应该撤销到哪一步
		MainFrame.modified=true;
		prevBi.getGraphics().drawImage(MainFrame.pa.bi,0,0,null);
		MenuBar.redoItem.setEnabled(false);
		MenuBar.undoItem.setEnabled(true);
	}
	public static void setClipboardImage(final Image image) {
		//将图像image传入剪贴板中
		//参考资料：http://danker-dai.iteye.com/blog/627050
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
		//将剪贴板中的图像传给返回值
		Transferable trans=Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			return ((Image)trans.getTransferData(trans.getTransferDataFlavors()[0]));
		} catch (Exception e) {
			//出现异常说明剪贴板中无图像或剪贴板不可访问，返回null。
			return null;
		}
	}
}