package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ColorPicker extends JPanel {
	static JButton cbButton = new JButton();			//ǰ����ɫ
	static JButton bgButton = new JButton();			//������ɫ
	
	public ColorPicker(){
		this.setLayout(null);
		cbButton.setBackground(Color.black);	
		cbButton.setBounds(7, 17, 20, 20);
		cbButton.setBorder(BorderFactory.createLoweredBevelBorder());
		cbButton.addActionListener(new ColorChanger(true));
		this.add(cbButton);
		
		bgButton.setBackground(Color.white);	
		bgButton.setBounds(15, 25, 20, 20);
		bgButton.setBorder(BorderFactory.createLoweredBevelBorder());
		bgButton.addActionListener(new ColorChanger(false));
		this.add(bgButton);
		
		JButton []colorJP =  new JButton[36];
		int n = 0;
		for(int i = 1 ; i <= 255 ;i += 127){
			for(int j = 1 ; j <= 255 ;j += 127){
				for(int m = 1 ; m <= 255 ;m += 64){
					colorJP[n] = new JButton();
					colorJP[n].setBackground(new Color(i-1,j-1,m-1));				
					colorJP[n].setBounds(40+n/2*17+1, 15+n%2*18, 15, 15);
					colorJP[n].setBorder(BorderFactory.createLoweredBevelBorder());
					colorJP[n].addMouseListener(new MouseAdapter(){
						public void mouseClicked(MouseEvent e) {
							JButton b=(JButton) e.getSource();
							if(e.getButton()==MouseEvent.BUTTON1)setColor(b.getBackground());
							else if(e.getButton()==MouseEvent.BUTTON3)setBg(b.getBackground());
						}
					});
					//����ť������岢��n++
					add(colorJP[n++]);
				}
			}
		}
	}
	public static void setColor(Color newcolor){
		PaintProperties.currentColor=newcolor;
		cbButton.setBackground(newcolor);
		//��ȡ��ǰ����ɫ������PaintProperties����ɫ���ı䵱ǰ��ɫ���
	}
	public static void setBg(Color newcolor){
		PaintProperties.bgColor=newcolor;
		bgButton.setBackground(newcolor);
	}
}

class ColorChanger extends HintAdapter implements ActionListener{
	boolean foreground;
	public ColorChanger(final boolean isForeground)
	{
		super(isForeground?"�༭ǰ��ɫ��":"�༭����ɫ��");
		foreground=isForeground;
	}	
	public void actionPerformed(ActionEvent arg0) {
		final String title=foreground?"�༭��ɫ - ǰ��ɫ":"�༭��ɫ - ����ɫ";
		Color chosenColor=JColorChooser.showDialog(MainFrame.jf, title, PaintProperties.currentColor);
		//��ɫѡȡ����
		if(chosenColor!=null){
			if(foreground) ColorPicker.setColor(chosenColor);
			else ColorPicker.setBg(chosenColor);
		}
	}
}