package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public abstract class Shape implements MouseListener,MouseMotionListener{
	protected int Button;
	protected int x1,y1,x2,y2;
	//各图形的基类，实现鼠标和鼠标动作的监听器接口
	public abstract void draw(Graphics g);
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {
		StatusBar.xoy.setText("");
	}
	public void mouseMoved(MouseEvent arg0) {
		StatusBar.xoy.setText("("+arg0.getX()+","+arg0.getY()+")");
	}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseDragged(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void setColor(Graphics g,int Button,boolean isBackground){
		if(!isBackground){
			if(Button==MouseEvent.BUTTON1)g.setColor(PaintProperties.currentColor);		//设置前景色
			else if(Button==MouseEvent.BUTTON3)g.setColor(PaintProperties.bgColor);
		}
		else{
			if(Button==MouseEvent.BUTTON1)g.setColor(PaintProperties.bgColor);	
			else if(Button==MouseEvent.BUTTON3)g.setColor(PaintProperties.currentColor);	//设置背景色
		}
	}
}

class PencilLine extends Shape{
	//铅笔
	public void mouseDragged(MouseEvent arg0) {
		PaintProperties.drawing=true;
		x2=arg0.getX();		//设置终点
		y2=arg0.getY();
		draw(MainFrame.pa.bi.getGraphics());	//在图像缓冲区上绘图
		MainFrame.pa.repaint();		//画布重绘
	}
	public void mousePressed(MouseEvent arg0) {	//设置起始点
		PaintProperties.drawing=true;
		x1=arg0.getX();
		y1=arg0.getY();
		MainFrame.pa.Record();
		Button=arg0.getButton();
	}
	public void draw(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;	//只有Graphics2D对象才能设置线粗，故做强制类型转换
		g2d.setStroke(new BasicStroke(PaintProperties.LineSize));	//设定线粗
		setColor(g,Button,false);		//设置线条颜色
		g.drawLine(x1, y1, x2, y2);		//画线
		x1=x2;y1=y2;	//起始点设为刚才的终点
	}
	public void mouseReleased(MouseEvent arg0) {
		PaintProperties.drawing=false;
	}
}

class Line extends Shape{
	//直线
	public void mouseDragged(MouseEvent arg0) {
		x2=arg0.getX();
		y2=arg0.getY();
		MainFrame.pa.repaint();
	}
	public void mousePressed(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		MainFrame.pa.Record();
		Button=arg0.getButton();
		PaintProperties.drawing=true;
	}
	public void mouseReleased(MouseEvent arg0) {
		draw(MainFrame.pa.bi.getGraphics());
		MainFrame.pa.repaint();
		PaintProperties.drawing=false;
	}
	public void draw(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		g2d.setStroke(new BasicStroke(PaintProperties.LineSize));
		setColor(g,Button,false);
		g.drawLine(x1, y1, x2, y2);
	}
}

class Rect extends Shape{
	//矩形（同时作为圆角矩形和圆形的超类，因为它们的绘制都是以矩形为基础的）
	protected int width,height;
	public void mouseDragged(MouseEvent arg0) {
		x2=x1;y2=y1;
		width=arg0.getX()-x1;
		height=arg0.getY()-y1;
		if(width<0){
			x2=x1+width;
			width=-width;
		}
		if(height<0){
			y2=y1+height;
			height=-height;
		}
		MainFrame.pa.repaint();		//出当前状态的图形
	}
	public void mousePressed(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		MainFrame.pa.Record();
		Button=arg0.getButton();
		PaintProperties.drawing=true;
	}
	public void mouseReleased(MouseEvent arg0) {
		draw(MainFrame.pa.bi.getGraphics());	//绘制到图像缓冲区
		MainFrame.pa.repaint();		//重绘画面
		PaintProperties.drawing=false;
	}
	public void draw(Graphics g) {
		switch(PaintProperties.ShapeState){
		case ShapeStates.BORDER:
			setColor(g,Button,false);	//前景色画框
			g.drawRect(x2, y2, width, height);
			break;
		case ShapeStates.BORDER_FILL:
			setColor(g,Button,true);	//背景色填充
			g.fillRect(x2, y2, width, height);
			setColor(g,Button,false);	//前景色画框
			g.drawRect(x2, y2, width, height);
			break;
		case ShapeStates.FILL:
			setColor(g,Button,false);	//前景色填充
			g.fillRect(x2, y2, width, height);
		}
	}
}

class RoundRect extends Rect{
	//圆角矩形
	protected final int arcWidth=10,arcHeight=10;
	public void draw(Graphics g) {
		switch(PaintProperties.ShapeState){
		case ShapeStates.BORDER:
			setColor(g,Button,false);
			g.drawRoundRect(x2,y2,width,height,arcWidth,arcHeight);
			break;
		case ShapeStates.BORDER_FILL:
			setColor(g,Button,true);	//背景色填充
			g.fillRoundRect(x2,y2,width,height,arcWidth,arcHeight);
			setColor(g,Button,false);	//前景色画框
			g.drawRoundRect(x2,y2,width,height,arcWidth,arcHeight);
			break;
		case ShapeStates.FILL:
			setColor(g,Button,false);	//前景色填充
			g.fillRoundRect(x2,y2,width,height,arcWidth,arcHeight);
		}
	}
}

class Circle extends Rect{
	//圆形
	public void draw(Graphics g) {
		switch(PaintProperties.ShapeState){
		case ShapeStates.BORDER:
			setColor(g,Button,false);	//前景色画框
			g.drawOval(x2, y2, width, height);
			break;
		case ShapeStates.BORDER_FILL:
			setColor(g,Button,true);	//背景色填充
			g.fillOval(x2, y2, width, height);
			setColor(g,Button,false);	//前景色画框
			g.drawOval(x2, y2, width, height);
			break;
		case ShapeStates.FILL:
			setColor(g,Button,false);	//前景色填充
			g.fillOval(x2, y2, width, height);
		}
	}
}

class Rubber extends Shape{
	//橡皮
	public void mouseDragged(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		perform(MainFrame.pa.bi.getGraphics());	//擦除图像缓冲区中相应的内容
		MainFrame.pa.repaint();					//绘橡皮的指针
	}
	public void mousePressed(MouseEvent arg0) {
		PaintProperties.drawing=true;
		Button=arg0.getButton();
		MainFrame.pa.Record();
		mouseDragged(arg0);
	}
	public void mouseMoved(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		MainFrame.pa.repaint();					//绘橡皮的指针
		StatusBar.xoy.setText("("+arg0.getX()+","+arg0.getY()+")");
	}
	public void perform(Graphics g){			//擦除的代码
		int size=PaintProperties.RubberSize;
		g.setColor(PaintProperties.bgColor);
		g.fillRect(x1-size/2, y1-size/2, size, size);
	}
	public void draw(Graphics g) {				//绘橡皮指针的代码
		perform(g);
		int size=PaintProperties.RubberSize;
		g.setColor(Color.black);
		g.drawRect(x1-size/2, y1-size/2, size, size);
	}
	public void mouseReleased(MouseEvent arg0){
		PaintProperties.drawing=false;
	}
}

class Spray extends Shape{
	//喷枪
	public void mousePressed(MouseEvent arg0) {
		Button=arg0.getButton();
		MainFrame.pa.Record();
		mouseDragged(arg0);
		PaintProperties.drawing=true;
	}
	public void mouseDragged(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		perform(MainFrame.pa.bi.getGraphics());
		MainFrame.pa.repaint();
	}
	public void perform(Graphics g){
		int size=PaintProperties.SpraySize;
		int randx=0,randy=0;
		Random rand=new Random();
		setColor(g,Button,false);
		for(int i=0;i<4*10;i++){
			switch(i%4){				//在四个象限内画点
			case 0:
				randx=x1+rand.nextInt(size);
				randy=y1+rand.nextInt(size);
				break;
			case 1:
				randx=x1-rand.nextInt(size);
				randy=y1+rand.nextInt(size);
				break;
			case 2:
				randx=x1+rand.nextInt(size);
				randy=y1-rand.nextInt(size);
				break;
			case 3:
				randx=x1-rand.nextInt(size);
				randy=y1-rand.nextInt(size);
				break;
			}
			if((randx-x1)*(randx-x1)+(randy-y1)*(randy-y1)<=size*size)
				g.drawLine(randx, randy, randx, randy);
		}
	}
	public void draw(Graphics g) {}
	public void mouseReleased(MouseEvent arg0){
		PaintProperties.drawing=false;
	}
}

class Polygon extends Shape{
	/* 如何绘制多边形？
	 * >>绘制过程：
	 *   首先应该拖一条直线（有轨迹）。
	 *   然后在鼠标移动的时候它不会显示现在的轨迹，
	 *   移动到某处单击时，出现一条到这里的线段；
	 *   或者是继续拖动，出现一条直线（有轨迹）。
	 * >>结束条件：
	 *   如果移动到开始点单击，多边形就画好了；
	 *   或者如果在任意某处双击鼠标，就自动补全。
	 */
	protected Vector<Point> points=new Vector<Point>();
	protected boolean isFirstPoint=true;
	
	public void mouseClicked(MouseEvent arg0) {
		if(isFirstPoint){			//单击是不管用的
			clear();
			return;
		}
		else if(arg0.getClickCount()==2){		//如果在任意某处双击鼠标，就自动补全。
			int x=points.elementAt(0).x;
			int y=points.elementAt(0).y;
			points.add(new Point(x,y));
		}
		else{			//移动到某处单击时，出现一条到这里的线段
			x2=arg0.getX();
			y2=arg0.getY();
			points.add(new Point(x2,y2));
			MainFrame.pa.repaint();
		}
		perform(MainFrame.pa.bi.getGraphics());
	}
	public void mousePressed(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		points.add(new Point(x1,y1));
		Button=arg0.getButton();
		PaintProperties.drawing=true;
	}
	public void mouseReleased(MouseEvent arg0){
		points.add(new Point(x2,y2));
		x1=x2;y1=y2;
		perform(MainFrame.pa.bi.getGraphics());
	}
	public void mouseDragged(MouseEvent arg0) {
		isFirstPoint=false;
		x2=arg0.getX();
		y2=arg0.getY();
		MainFrame.pa.repaint();
	}
	public void perform(Graphics g){
		int offset1=points.elementAt(points.size()-1).x-points.elementAt(0).x;
		int offset2=points.elementAt(points.size()-1).y-points.elementAt(0).y;
		if(Math.abs(offset1)<=5 && Math.abs(offset2)<=5){		//模糊匹配，模糊度为5
			MainFrame.pa.Record();
			int size=points.size();
			int[] pointX=new int[size];			//绘制多边形的参数是点横纵坐标的数组
			int[] pointY=new int[size];
			for(int i=0;i<size;i++){				//从点向量生成数组
				pointX[i]=points.elementAt(i).x;
				pointY[i]=points.elementAt(i).y;
			}
			switch(PaintProperties.ShapeState){
			case ShapeStates.BORDER:
				setColor(g,Button,false);	//前景色画框
				g.drawPolygon(pointX,pointY,size);
				break;
			case ShapeStates.BORDER_FILL:			
				setColor(g,Button,true);;	//背景色填充
				g.fillPolygon(pointX,pointY,size);
				setColor(g,Button,false);	//前景色画框
				g.drawPolygon(pointX,pointY,size);
				break;
			case ShapeStates.FILL:
				setColor(g,Button,false);	//前景色填充
				g.fillPolygon(pointX,pointY,size);
			}
			clear();
			MainFrame.pa.repaint();
		}
	}
	public void draw(Graphics g) {
		if(!isFirstPoint){
			if(Button==MouseEvent.BUTTON1)g.setColor(PaintProperties.currentColor);
			else if(Button==MouseEvent.BUTTON3)g.setColor(PaintProperties.bgColor);	//设置线条颜色
			Point pt1=points.elementAt(0),pt2=points.elementAt(0);
			for(int i=0;i<points.size()-1;i++){
				pt1=points.elementAt(i);pt2=points.elementAt(i+1);
				g.drawLine(pt1.x,pt1.y,pt2.x,pt2.y);	//绘制已经画出的直线
			}
			g.drawLine(x1,y1,x2,y2);	//绘制从最后一点到鼠标当前所在位置的直线
		}
	}
	public void clear(){
		PaintProperties.drawing=false;
		isFirstPoint=true;
		points.clear();
	}
}