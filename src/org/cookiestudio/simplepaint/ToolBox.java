package org.cookiestudio.simplepaint;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class ToolBox extends JPanel {
	//���Ĺ������
	private PropertiesArea[] p_area={new LineArea(),new RubberArea(),new SprayArea(),new ShapeArea(),new PropertiesArea()};
	private final String[] tooltips=		//��ť�Ĺ�����ʾ����
		{"��Ƥ/��ɫ��Ƥ��","Ǧ��","��ǹ","ֱ��","Բ�Ǿ���","����","�����","��Բ","����ɫ���","�������","ȡɫ"};
	public static final String[] hints={		//״̬����ʾ����
			"�õ�ǰ���õı���ɫ����ͼ������",
			"�����������ߡ�",
			"������ǹͼ����",
			"����һ��ֱ�ߣ�",
			"��ѡ�������ģʽ����Բ�Ǿ��Ρ�",
			"��ѡ�������ģʽ���ƾ��Ρ�",
			"��ѡ�������ģʽ���ƶ���Ρ�",
			"��ѡ�������ģʽ������Բ��",
			"��ѡ������ɫ���һ�������򡣣�����ʵ���У�",
			"�����ı���",
			"��ͼ��������ѡȡһ����ɫ��"
	};
	
	public ToolBox(){
		this.setLayout(null);
		ToolButton tools[]=new ToolButton[11];
		for(int i=0;i<11;i++){			//��ӹ��߰�ť
			tools[i]=new ToolButton(tools,i);
			tools[i].setBounds(2+i%2*27, 2+i/2*27, 27, 27);
			tools[i].setToolTipText(tooltips[i]);		//������ʾ
			add(tools[i]);
		}
		tools[1].setClicked(true);		//Ĭ��ѡ��Ǧ��
		for(PropertiesArea p:p_area){	//���ȫ����������ȫ����ʾ��
			p.setBounds(PropertiesArea.x,PropertiesArea.y,PropertiesArea.width,PropertiesArea.height);
			p.setBorder(BorderFactory.createLoweredBevelBorder());
			add(p);
		}
		setPropertiesArea(1);			//Ĭ����ʾ������ϸ��������
	}
	
	public void setPropertiesArea(int currentState){
		for(PropertiesArea p:p_area) p.setVisible(false);		//ȫ������
		switch(currentState){
		case 0:				//��Ƥ
			p_area[1].setVisible(true);		//��Ƥ��С
			break;
		case 1:case 3:		//Ǧ�ʡ�����
			p_area[0].setVisible(true);		//������ϸ
			break;
		case 2:				//��ǹ
			p_area[2].setVisible(true);		//��ǹ��С
			break;
		case 4:case 5:case 6:case 7:			//ͼ����
			p_area[3].setVisible(true);		//��䷽ʽ
			break;
		case 8:case 9:case 10:			//��ѡ����
			p_area[4].setVisible(true);		//�հ����
		}
	}
}

class ToolButton extends JButton implements ActionListener{
	//���߰�ť��
	ImageIcon firstPic,finalPic;		//ѡ��ǰ��ѡ���ͼ��
	boolean isClicked=false;			//�Ƿ�ѡ��
	int toolNumber;						//���߱��
	ToolButton tools[];					//�����������������
	public ToolButton(ToolButton[] tools,int number){
		toolNumber=number;
		this.tools=tools;
		firstPic=new ImageIcon("images/"+number+"0.gif");
		finalPic=new ImageIcon("images/"+number+"1.gif");
		this.setIcon(firstPic);			//Ĭ�ϲ�ѡ��
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.addMouseListener(new HintAdapter(ToolBox.hints[toolNumber]));		//״̬����ʾ��
		this.addActionListener(this);
	}
	public void actionPerformed(ActionEvent arg0) {
		if(PaintProperties.getCurrentState()!=toolNumber){
			ToolButton.this.tools[PaintProperties.getCurrentState()].setClicked(false);		//ȡ���������е�ѡ��
			ToolButton.this.setClicked(true);			//ѡ��
			MainFrame.tb.setPropertiesArea(PaintProperties.getCurrentState());	//������������
		}
	}
	public void setClicked(boolean isClicked) {
		if(!isClicked) this.setIcon(firstPic);		//��ʾδѡ��ͼƬ
		else{
			this.setIcon(finalPic);					//��ʾѡ�к�ͼƬ
			PaintProperties.setCurrentState(toolNumber);	//���ĵ�ǰ״̬
		}
		this.isClicked=isClicked;
	}
}

class PropertiesArea extends JPanel implements ActionListener{
	//��������ĳ��࣬����Ҳ�ǿհ���������
	public static final int x=4,y=170,width=50,height=90;
	protected PropertiesArea(){
		setLayout(new GridLayout(5,1));		//5��1��
	}
	public void actionPerformed(ActionEvent arg0) {}
}

class LineArea extends PropertiesArea{
	//����������ʱ��ѡ��������ϸ����������
	private JButton[] LineSize=new JButton[5];
	public LineArea(){
		for(int i=0;i<5;i++){
			LineSize[i]=new JButton(new ImageIcon("images/3"+i+"0.gif"));
			LineSize[i].setBorder(BorderFactory.createEmptyBorder());
			LineSize[i].addActionListener(this);
			add(LineSize[i]);
		}
		LineSize[0].setIcon(new ImageIcon("images/301.gif"));
		//��������ʱĬ�������ϸ�ߣ����Է���ϸ�ߵ�ͼ��
	}
	public void actionPerformed(ActionEvent arg0) {
		for(int i=0;i<5;i++) LineSize[i].setIcon(new ImageIcon("images/3"+i+"0.gif"));
		if(arg0.getSource()==LineSize[0]){
			PaintProperties.LineSize=LineSizes.SMALL;
			LineSize[0].setIcon(new ImageIcon("images/301.gif"));
		}
		else if(arg0.getSource()==LineSize[1]){
			PaintProperties.LineSize=LineSizes.NORMAL;
			LineSize[1].setIcon(new ImageIcon("images/311.gif"));
		}
		else if(arg0.getSource()==LineSize[2]){
			PaintProperties.LineSize=LineSizes.BIG;
			LineSize[2].setIcon(new ImageIcon("images/321.gif"));
		}
		else if(arg0.getSource()==LineSize[3]){
			PaintProperties.LineSize=LineSizes.HUGE;
			LineSize[3].setIcon(new ImageIcon("images/331.gif"));
		}
		else{
			PaintProperties.LineSize=LineSizes.EXTRA;
			LineSize[4].setIcon(new ImageIcon("images/341.gif"));
		}
	}
}

class ShapeArea extends PropertiesArea{
	//����ͼ�ε�ʱ��ѡ��ͼ�εı߿�������������
	private JButton[] ShapeState=new JButton[3];
	public ShapeArea(){
		for(int i=0;i<3;i++){
			ShapeState[i]=new JButton(new ImageIcon("images/5"+i+"0.gif"));
			ShapeState[i].setBorder(BorderFactory.createEmptyBorder());
			ShapeState[i].addActionListener(this);
			add(ShapeState[i]);
		}
		ShapeState[0].setIcon(new ImageIcon("images/501.gif"));
		//��������ʱĬ����ֻ���Ʊ߿򣬷��Դ˰�ť
	}
	public void actionPerformed(ActionEvent arg0) {
		for(int i=0;i<3;i++) ShapeState[i].setIcon(new ImageIcon("images/5"+i+"0.gif"));
		if(arg0.getSource()==ShapeState[0]){
			PaintProperties.ShapeState=ShapeStates.BORDER;
			ShapeState[0].setIcon(new ImageIcon("images/501.gif"));
		}
		else if(arg0.getSource()==ShapeState[1]){
			PaintProperties.ShapeState=ShapeStates.BORDER_FILL;
			ShapeState[1].setIcon(new ImageIcon("images/511.gif"));
		}
		else{
			PaintProperties.ShapeState=ShapeStates.FILL;
			ShapeState[2].setIcon(new ImageIcon("images/521.gif"));
		}
	}
}

class RubberArea extends PropertiesArea{
	//��Ƥ����Сѡ�����������
	private JButton[] RubberSize=new JButton[4];
	public RubberArea(){
		for(int i=0;i<4;i++){
			RubberSize[i]=new JButton(new ImageIcon("images/0"+i+"0.gif"));
			RubberSize[i].setBorder(BorderFactory.createEmptyBorder());
			RubberSize[i].addActionListener(this);
			add(RubberSize[i]);
		}
		RubberSize[0].setIcon(new ImageIcon("images/001.gif"));
		//��������ʱĬ����С��Ƥ�������Դ�ѡ��
	}
	public void actionPerformed(ActionEvent arg0) {
		for(int i=0;i<4;i++) RubberSize[i].setIcon(new ImageIcon("images/0"+i+"0.gif"));
		if(arg0.getSource()==RubberSize[0]){
			PaintProperties.RubberSize=RubberSizes.SMALL;
			RubberSize[0].setIcon(new ImageIcon("images/001.gif"));
		}
		else if(arg0.getSource()==RubberSize[1]){
			PaintProperties.RubberSize=RubberSizes.NORMAL;
			RubberSize[1].setIcon(new ImageIcon("images/011.gif"));
		}
		else if(arg0.getSource()==RubberSize[2]){
			PaintProperties.RubberSize=RubberSizes.BIG;
			RubberSize[2].setIcon(new ImageIcon("images/021.gif"));
		}
		else{
			PaintProperties.RubberSize=RubberSizes.HUGE;
			RubberSize[3].setIcon(new ImageIcon("images/031.gif"));
		}
	}
}

class SprayArea extends PropertiesArea{
	//��ǹ��Сѡ�����������
	private JButton[] SpraySize=new JButton[3];
	public SprayArea(){
		for(int i=0;i<3;i++){
			SpraySize[i]=new JButton(new ImageIcon("images/2"+i+"0.gif"));
			SpraySize[i].setBorder(BorderFactory.createEmptyBorder());
			SpraySize[i].addActionListener(this);
			add(SpraySize[i]);
		}
		SpraySize[0].setIcon(new ImageIcon("images/201.gif"));
		//��������ʱĬ����С��ǹ�����Դ�ѡ��
	}
	public void actionPerformed(ActionEvent arg0) {
		for(int i=0;i<3;i++) SpraySize[i].setIcon(new ImageIcon("images/2"+i+"0.gif"));
		if(arg0.getSource()==SpraySize[0]){		//С��ǹ
			PaintProperties.SpraySize=SpraySizes.SMALL;
			SpraySize[0].setIcon(new ImageIcon("images/201.gif"));
		}
		else if(arg0.getSource()==SpraySize[1]){		//����ǹ
			PaintProperties.SpraySize=SpraySizes.NORMAL;
			SpraySize[1].setIcon(new ImageIcon("images/211.gif"));
		}
		else{		//����ǹ
			PaintProperties.SpraySize=SpraySizes.BIG;
			SpraySize[2].setIcon(new ImageIcon("images/221.gif"));
		}
	}
}