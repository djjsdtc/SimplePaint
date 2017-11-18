package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public abstract class Shape implements MouseListener,MouseMotionListener{
	protected int Button;
	protected int x1,y1,x2,y2;
	//��ͼ�εĻ��࣬ʵ��������궯���ļ������ӿ�
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
			if(Button==MouseEvent.BUTTON1)g.setColor(PaintProperties.currentColor);		//����ǰ��ɫ
			else if(Button==MouseEvent.BUTTON3)g.setColor(PaintProperties.bgColor);
		}
		else{
			if(Button==MouseEvent.BUTTON1)g.setColor(PaintProperties.bgColor);	
			else if(Button==MouseEvent.BUTTON3)g.setColor(PaintProperties.currentColor);	//���ñ���ɫ
		}
	}
}

class PencilLine extends Shape{
	//Ǧ��
	public void mouseDragged(MouseEvent arg0) {
		PaintProperties.drawing=true;
		x2=arg0.getX();		//�����յ�
		y2=arg0.getY();
		draw(MainFrame.pa.bi.getGraphics());	//��ͼ�񻺳����ϻ�ͼ
		MainFrame.pa.repaint();		//�����ػ�
	}
	public void mousePressed(MouseEvent arg0) {	//������ʼ��
		PaintProperties.drawing=true;
		x1=arg0.getX();
		y1=arg0.getY();
		MainFrame.pa.Record();
		Button=arg0.getButton();
	}
	public void draw(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;	//ֻ��Graphics2D������������ߴ֣�����ǿ������ת��
		g2d.setStroke(new BasicStroke(PaintProperties.LineSize));	//�趨�ߴ�
		setColor(g,Button,false);		//����������ɫ
		g.drawLine(x1, y1, x2, y2);		//����
		x1=x2;y1=y2;	//��ʼ����Ϊ�ղŵ��յ�
	}
	public void mouseReleased(MouseEvent arg0) {
		PaintProperties.drawing=false;
	}
}

class Line extends Shape{
	//ֱ��
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
	//���Σ�ͬʱ��ΪԲ�Ǿ��κ�Բ�εĳ��࣬��Ϊ���ǵĻ��ƶ����Ծ���Ϊ�����ģ�
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
		MainFrame.pa.repaint();		//����ǰ״̬��ͼ��
	}
	public void mousePressed(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		MainFrame.pa.Record();
		Button=arg0.getButton();
		PaintProperties.drawing=true;
	}
	public void mouseReleased(MouseEvent arg0) {
		draw(MainFrame.pa.bi.getGraphics());	//���Ƶ�ͼ�񻺳���
		MainFrame.pa.repaint();		//�ػ滭��
		PaintProperties.drawing=false;
	}
	public void draw(Graphics g) {
		switch(PaintProperties.ShapeState){
		case ShapeStates.BORDER:
			setColor(g,Button,false);	//ǰ��ɫ����
			g.drawRect(x2, y2, width, height);
			break;
		case ShapeStates.BORDER_FILL:
			setColor(g,Button,true);	//����ɫ���
			g.fillRect(x2, y2, width, height);
			setColor(g,Button,false);	//ǰ��ɫ����
			g.drawRect(x2, y2, width, height);
			break;
		case ShapeStates.FILL:
			setColor(g,Button,false);	//ǰ��ɫ���
			g.fillRect(x2, y2, width, height);
		}
	}
}

class RoundRect extends Rect{
	//Բ�Ǿ���
	protected final int arcWidth=10,arcHeight=10;
	public void draw(Graphics g) {
		switch(PaintProperties.ShapeState){
		case ShapeStates.BORDER:
			setColor(g,Button,false);
			g.drawRoundRect(x2,y2,width,height,arcWidth,arcHeight);
			break;
		case ShapeStates.BORDER_FILL:
			setColor(g,Button,true);	//����ɫ���
			g.fillRoundRect(x2,y2,width,height,arcWidth,arcHeight);
			setColor(g,Button,false);	//ǰ��ɫ����
			g.drawRoundRect(x2,y2,width,height,arcWidth,arcHeight);
			break;
		case ShapeStates.FILL:
			setColor(g,Button,false);	//ǰ��ɫ���
			g.fillRoundRect(x2,y2,width,height,arcWidth,arcHeight);
		}
	}
}

class Circle extends Rect{
	//Բ��
	public void draw(Graphics g) {
		switch(PaintProperties.ShapeState){
		case ShapeStates.BORDER:
			setColor(g,Button,false);	//ǰ��ɫ����
			g.drawOval(x2, y2, width, height);
			break;
		case ShapeStates.BORDER_FILL:
			setColor(g,Button,true);	//����ɫ���
			g.fillOval(x2, y2, width, height);
			setColor(g,Button,false);	//ǰ��ɫ����
			g.drawOval(x2, y2, width, height);
			break;
		case ShapeStates.FILL:
			setColor(g,Button,false);	//ǰ��ɫ���
			g.fillOval(x2, y2, width, height);
		}
	}
}

class Rubber extends Shape{
	//��Ƥ
	public void mouseDragged(MouseEvent arg0) {
		x1=arg0.getX();
		y1=arg0.getY();
		perform(MainFrame.pa.bi.getGraphics());	//����ͼ�񻺳�������Ӧ������
		MainFrame.pa.repaint();					//����Ƥ��ָ��
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
		MainFrame.pa.repaint();					//����Ƥ��ָ��
		StatusBar.xoy.setText("("+arg0.getX()+","+arg0.getY()+")");
	}
	public void perform(Graphics g){			//�����Ĵ���
		int size=PaintProperties.RubberSize;
		g.setColor(PaintProperties.bgColor);
		g.fillRect(x1-size/2, y1-size/2, size, size);
	}
	public void draw(Graphics g) {				//����Ƥָ��Ĵ���
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
	//��ǹ
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
			switch(i%4){				//���ĸ������ڻ���
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
	/* ��λ��ƶ���Σ�
	 * >>���ƹ��̣�
	 *   ����Ӧ����һ��ֱ�ߣ��й켣����
	 *   Ȼ��������ƶ���ʱ����������ʾ���ڵĹ켣��
	 *   �ƶ���ĳ������ʱ������һ����������߶Σ�
	 *   �����Ǽ����϶�������һ��ֱ�ߣ��й켣����
	 * >>����������
	 *   ����ƶ�����ʼ�㵥��������ξͻ����ˣ�
	 *   �������������ĳ��˫����꣬���Զ���ȫ��
	 */
	protected Vector<Point> points=new Vector<Point>();
	protected boolean isFirstPoint=true;
	
	public void mouseClicked(MouseEvent arg0) {
		if(isFirstPoint){			//�����ǲ����õ�
			clear();
			return;
		}
		else if(arg0.getClickCount()==2){		//���������ĳ��˫����꣬���Զ���ȫ��
			int x=points.elementAt(0).x;
			int y=points.elementAt(0).y;
			points.add(new Point(x,y));
		}
		else{			//�ƶ���ĳ������ʱ������һ����������߶�
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
		if(Math.abs(offset1)<=5 && Math.abs(offset2)<=5){		//ģ��ƥ�䣬ģ����Ϊ5
			MainFrame.pa.Record();
			int size=points.size();
			int[] pointX=new int[size];			//���ƶ���εĲ����ǵ�������������
			int[] pointY=new int[size];
			for(int i=0;i<size;i++){				//�ӵ�������������
				pointX[i]=points.elementAt(i).x;
				pointY[i]=points.elementAt(i).y;
			}
			switch(PaintProperties.ShapeState){
			case ShapeStates.BORDER:
				setColor(g,Button,false);	//ǰ��ɫ����
				g.drawPolygon(pointX,pointY,size);
				break;
			case ShapeStates.BORDER_FILL:			
				setColor(g,Button,true);;	//����ɫ���
				g.fillPolygon(pointX,pointY,size);
				setColor(g,Button,false);	//ǰ��ɫ����
				g.drawPolygon(pointX,pointY,size);
				break;
			case ShapeStates.FILL:
				setColor(g,Button,false);	//ǰ��ɫ���
				g.fillPolygon(pointX,pointY,size);
			}
			clear();
			MainFrame.pa.repaint();
		}
	}
	public void draw(Graphics g) {
		if(!isFirstPoint){
			if(Button==MouseEvent.BUTTON1)g.setColor(PaintProperties.currentColor);
			else if(Button==MouseEvent.BUTTON3)g.setColor(PaintProperties.bgColor);	//����������ɫ
			Point pt1=points.elementAt(0),pt2=points.elementAt(0);
			for(int i=0;i<points.size()-1;i++){
				pt1=points.elementAt(i);pt2=points.elementAt(i+1);
				g.drawLine(pt1.x,pt1.y,pt2.x,pt2.y);	//�����Ѿ�������ֱ��
			}
			g.drawLine(x1,y1,x2,y2);	//���ƴ����һ�㵽��굱ǰ����λ�õ�ֱ��
		}
	}
	public void clear(){
		PaintProperties.drawing=false;
		isFirstPoint=true;
		points.clear();
	}
}