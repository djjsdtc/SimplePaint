package org.cookiestudio.simplepaint;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class ToolBox extends JPanel {
	//左侧的工具面板
	private PropertiesArea[] p_area={new LineArea(),new RubberArea(),new SprayArea(),new ShapeArea(),new PropertiesArea()};
	private final String[] tooltips=		//按钮的工具提示文字
		{"橡皮/彩色橡皮擦","铅笔","喷枪","直线","圆角矩形","矩形","多边形","椭圆","用颜色填充","添加文字","取色"};
	public static final String[] hints={		//状态栏提示文字
			"用当前设置的背景色擦除图像区域。",
			"绘制自由曲线。",
			"绘制喷枪图案。",
			"绘制一条直线，",
			"用选定的填充模式绘制圆角矩形。",
			"用选定的填充模式绘制矩形。",
			"用选定的填充模式绘制多边形。",
			"用选定的填充模式绘制椭圆。",
			"用选定的颜色填充一块封闭区域。（功能实验中）",
			"插入文本。",
			"在图像区域中选取一种颜色。"
	};
	
	public ToolBox(){
		this.setLayout(null);
		ToolButton tools[]=new ToolButton[11];
		for(int i=0;i<11;i++){			//添加工具按钮
			tools[i]=new ToolButton(tools,i);
			tools[i].setBounds(2+i%2*27, 2+i/2*27, 27, 27);
			tools[i].setToolTipText(tooltips[i]);		//工具提示
			add(tools[i]);
		}
		tools[1].setClicked(true);		//默认选中铅笔
		for(PropertiesArea p:p_area){	//添加全部属性区域（全不显示）
			p.setBounds(PropertiesArea.x,PropertiesArea.y,PropertiesArea.width,PropertiesArea.height);
			p.setBorder(BorderFactory.createLoweredBevelBorder());
			add(p);
		}
		setPropertiesArea(1);			//默认显示线条粗细属性区域
	}
	
	public void setPropertiesArea(int currentState){
		for(PropertiesArea p:p_area) p.setVisible(false);		//全部隐藏
		switch(currentState){
		case 0:				//橡皮
			p_area[1].setVisible(true);		//橡皮大小
			break;
		case 1:case 3:		//铅笔、线条
			p_area[0].setVisible(true);		//线条粗细
			break;
		case 2:				//喷枪
			p_area[2].setVisible(true);		//喷枪大小
			break;
		case 4:case 5:case 6:case 7:			//图形类
			p_area[3].setVisible(true);		//填充方式
			break;
		case 8:case 9:case 10:			//无选项类
			p_area[4].setVisible(true);		//空白面板
		}
	}
}

class ToolButton extends JButton implements ActionListener{
	//工具按钮类
	ImageIcon firstPic,finalPic;		//选择前、选择后图标
	boolean isClicked=false;			//是否被选中
	int toolNumber;						//工具编号
	ToolButton tools[];					//对整个工具箱的引用
	public ToolButton(ToolButton[] tools,int number){
		toolNumber=number;
		this.tools=tools;
		firstPic=new ImageIcon("images/"+number+"0.gif");
		finalPic=new ImageIcon("images/"+number+"1.gif");
		this.setIcon(firstPic);			//默认不选中
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.addMouseListener(new HintAdapter(ToolBox.hints[toolNumber]));		//状态栏提示用
		this.addActionListener(this);
	}
	public void actionPerformed(ActionEvent arg0) {
		if(PaintProperties.getCurrentState()!=toolNumber){
			ToolButton.this.tools[PaintProperties.getCurrentState()].setClicked(false);		//取消其它所有的选中
			ToolButton.this.setClicked(true);			//选中
			MainFrame.tb.setPropertiesArea(PaintProperties.getCurrentState());	//更改属性区域
		}
	}
	public void setClicked(boolean isClicked) {
		if(!isClicked) this.setIcon(firstPic);		//显示未选中图片
		else{
			this.setIcon(finalPic);					//显示选中后图片
			PaintProperties.setCurrentState(toolNumber);	//更改当前状态
		}
		this.isClicked=isClicked;
	}
}

class PropertiesArea extends JPanel implements ActionListener{
	//属性区域的超类，另外也是空白属性区域
	public static final int x=4,y=170,width=50,height=90;
	protected PropertiesArea(){
		setLayout(new GridLayout(5,1));		//5行1列
	}
	public void actionPerformed(ActionEvent arg0) {}
}

class LineArea extends PropertiesArea{
	//绘制线条的时候选择线条粗细的属性区域
	private JButton[] LineSize=new JButton[5];
	public LineArea(){
		for(int i=0;i<5;i++){
			LineSize[i]=new JButton(new ImageIcon("images/3"+i+"0.gif"));
			LineSize[i].setBorder(BorderFactory.createEmptyBorder());
			LineSize[i].addActionListener(this);
			add(LineSize[i]);
		}
		LineSize[0].setIcon(new ImageIcon("images/301.gif"));
		//程序启动时默认情况是细线，所以反显细线的图标
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
	//绘制图形的时候选择图形的边框填充的属性区域
	private JButton[] ShapeState=new JButton[3];
	public ShapeArea(){
		for(int i=0;i<3;i++){
			ShapeState[i]=new JButton(new ImageIcon("images/5"+i+"0.gif"));
			ShapeState[i].setBorder(BorderFactory.createEmptyBorder());
			ShapeState[i].addActionListener(this);
			add(ShapeState[i]);
		}
		ShapeState[0].setIcon(new ImageIcon("images/501.gif"));
		//程序启动时默认是只绘制边框，反显此按钮
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
	//橡皮擦大小选择的属性区域
	private JButton[] RubberSize=new JButton[4];
	public RubberArea(){
		for(int i=0;i<4;i++){
			RubberSize[i]=new JButton(new ImageIcon("images/0"+i+"0.gif"));
			RubberSize[i].setBorder(BorderFactory.createEmptyBorder());
			RubberSize[i].addActionListener(this);
			add(RubberSize[i]);
		}
		RubberSize[0].setIcon(new ImageIcon("images/001.gif"));
		//程序启动时默认是小橡皮擦，反显此选项
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
	//喷枪大小选择的属性区域
	private JButton[] SpraySize=new JButton[3];
	public SprayArea(){
		for(int i=0;i<3;i++){
			SpraySize[i]=new JButton(new ImageIcon("images/2"+i+"0.gif"));
			SpraySize[i].setBorder(BorderFactory.createEmptyBorder());
			SpraySize[i].addActionListener(this);
			add(SpraySize[i]);
		}
		SpraySize[0].setIcon(new ImageIcon("images/201.gif"));
		//程序启动时默认是小喷枪，反显此选项
	}
	public void actionPerformed(ActionEvent arg0) {
		for(int i=0;i<3;i++) SpraySize[i].setIcon(new ImageIcon("images/2"+i+"0.gif"));
		if(arg0.getSource()==SpraySize[0]){		//小喷枪
			PaintProperties.SpraySize=SpraySizes.SMALL;
			SpraySize[0].setIcon(new ImageIcon("images/201.gif"));
		}
		else if(arg0.getSource()==SpraySize[1]){		//中喷枪
			PaintProperties.SpraySize=SpraySizes.NORMAL;
			SpraySize[1].setIcon(new ImageIcon("images/211.gif"));
		}
		else{		//大喷枪
			PaintProperties.SpraySize=SpraySizes.BIG;
			SpraySize[2].setIcon(new ImageIcon("images/221.gif"));
		}
	}
}