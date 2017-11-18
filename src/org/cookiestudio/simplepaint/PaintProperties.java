package org.cookiestudio.simplepaint;

import java.awt.Color;

public class PaintProperties {
	public static Color currentColor=Color.BLACK;		//初始颜色：黑色
	public static Color bgColor=Color.WHITE;			//初始背景：白色
	private static int currentState=1;				//初始状态：铅笔
	public static int RubberSize=RubberSizes.SMALL;	//初始橡皮大小：小号
	public static int SpraySize=SpraySizes.SMALL;		//初始喷枪大小：小号
	public static float LineSize=LineSizes.SMALL;		//初始线条粗细：小号
	public static int ShapeState=ShapeStates.BORDER;	//初始填充状态：仅边框
	public static boolean drawing=false;
	
	public static final Shape[] shapes={
		new Rubber(),					//橡皮
		new PencilLine(),				//铅笔
		new Spray(),					//喷枪
		new Line(),						//直线
		new RoundRect(),				//圆角矩形
		new Rect(),						//矩形
		new Polygon(),					//多边形
		new Circle(),					//圆
		new AreaFiller(),				//颜色填充
		new Text(),						//文字
		new ColorReader()				//颜色吸管
	};
	
	public static void setCurrentState(int currentState) {
		int lastState=PaintProperties.currentState;					//上一个状态
		if(lastState==0 || lastState==6) MainFrame.pa.repaint();	//清除橡皮的鼠标指针和多边形没画完的线
		PaintProperties.currentState = currentState;				//设置新状态
		MainFrame.pa.removeMouseListener(shapes[lastState]);		//清除上一个状态的监听器
		MainFrame.pa.removeMouseMotionListener(shapes[lastState]);
		MainFrame.pa.addMouseListener(shapes[currentState]);		//设置新状态的监听器
		MainFrame.pa.addMouseMotionListener(shapes[currentState]);
	}
	
	public static int getCurrentState(){return currentState;}
}

interface RubberSizes{		//橡皮大小的常量
	int SMALL=10,NORMAL=20,BIG=30,HUGE=40;
}

interface SpraySizes{		//喷枪大小的常量
	int SMALL=5,NORMAL=10,BIG=15;
}

interface LineSizes{		//线条粗细的常量
	float SMALL=1.0f,NORMAL=2.0f,BIG=3.0f,HUGE=4.0f,EXTRA=5.0f;
}

interface ShapeStates{		//图形边框填充状态的常量
	int BORDER=0,BORDER_FILL=1,FILL=2;
	//0：仅边框；1：黑边框+填充（多边形不适用）；2、仅填充
}