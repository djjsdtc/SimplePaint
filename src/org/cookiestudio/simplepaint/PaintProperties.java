package org.cookiestudio.simplepaint;

import java.awt.Color;

public class PaintProperties {
	public static Color currentColor=Color.BLACK;		//��ʼ��ɫ����ɫ
	public static Color bgColor=Color.WHITE;			//��ʼ��������ɫ
	private static int currentState=1;				//��ʼ״̬��Ǧ��
	public static int RubberSize=RubberSizes.SMALL;	//��ʼ��Ƥ��С��С��
	public static int SpraySize=SpraySizes.SMALL;		//��ʼ��ǹ��С��С��
	public static float LineSize=LineSizes.SMALL;		//��ʼ������ϸ��С��
	public static int ShapeState=ShapeStates.BORDER;	//��ʼ���״̬�����߿�
	public static boolean drawing=false;
	
	public static final Shape[] shapes={
		new Rubber(),					//��Ƥ
		new PencilLine(),				//Ǧ��
		new Spray(),					//��ǹ
		new Line(),						//ֱ��
		new RoundRect(),				//Բ�Ǿ���
		new Rect(),						//����
		new Polygon(),					//�����
		new Circle(),					//Բ
		new AreaFiller(),				//��ɫ���
		new Text(),						//����
		new ColorReader()				//��ɫ����
	};
	
	public static void setCurrentState(int currentState) {
		int lastState=PaintProperties.currentState;					//��һ��״̬
		if(lastState==0 || lastState==6) MainFrame.pa.repaint();	//�����Ƥ�����ָ��Ͷ����û�������
		PaintProperties.currentState = currentState;				//������״̬
		MainFrame.pa.removeMouseListener(shapes[lastState]);		//�����һ��״̬�ļ�����
		MainFrame.pa.removeMouseMotionListener(shapes[lastState]);
		MainFrame.pa.addMouseListener(shapes[currentState]);		//������״̬�ļ�����
		MainFrame.pa.addMouseMotionListener(shapes[currentState]);
	}
	
	public static int getCurrentState(){return currentState;}
}

interface RubberSizes{		//��Ƥ��С�ĳ���
	int SMALL=10,NORMAL=20,BIG=30,HUGE=40;
}

interface SpraySizes{		//��ǹ��С�ĳ���
	int SMALL=5,NORMAL=10,BIG=15;
}

interface LineSizes{		//������ϸ�ĳ���
	float SMALL=1.0f,NORMAL=2.0f,BIG=3.0f,HUGE=4.0f,EXTRA=5.0f;
}

interface ShapeStates{		//ͼ�α߿����״̬�ĳ���
	int BORDER=0,BORDER_FILL=1,FILL=2;
	//0�����߿�1���ڱ߿�+��䣨����β����ã���2�������
}