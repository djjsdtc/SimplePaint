package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class MainFrame {
	public static PaintingArea pa;					//����
	public static JFrame jf=new JFrame(){
		protected void processWindowEvent(WindowEvent e) {  
	        if (e.getID() == WindowEvent.WINDOW_CLOSING){ 
	        	if(MainFrame.close()) System.exit(0);
	        	else return;
	        }
	        super.processWindowEvent(e); //������ִ�д����¼���Ĭ�϶���(�磺����)  
	    }
	};				//����
	public static ToolBox tb;						//����壨�����䣩
	private ColorPicker cp=new ColorPicker();			//����壨��ɫʰȡ����
	private StatusBar stb=new StatusBar();				//״̬��
	public static ScrollPane sp;						//��������ӹ�����
	public static File currentFile=null;
	public static boolean modified=false;
	
	public MainFrame(){
		jf.setTitle("δ���� - Simple Painting Application");
		ImageIcon imgIcon = new ImageIcon("images//logo.jpg");
		jf.setIconImage(imgIcon.getImage());			//������ͼ��
		jf.setJMenuBar(new MenuBar());					//�����˵���
		pa=new CanvasCreator(null).getCanvas();
		tb=new ToolBox();
		jf.setLayout(null);
		jf.setBounds(50,50,660,550);
		jf.setMinimumSize(new Dimension(370,400));
		jf.add(tb);
		jf.add(cp);
		sp=new ScrollPane();
		sp.add(pa);
		jf.add(sp);
		jf.add(stb);
		jf.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){		//���������Сʱ˳�����������С
				int compWidth=jf.getWidth()-15,compHeight=jf.getHeight()-140;
				tb.setBounds(1, 0, 55, compHeight);
				cp.setBounds(1, jf.getHeight()-140,compWidth, 50);
				stb.setBounds(0,jf.getHeight()-87,compWidth,25);
				pa.setSize(pa.Width, pa.Height);
				sp.setBounds(60,0,compWidth-60,compHeight);
			}
		});
		jf.setVisible(true);
		
	}

	public static boolean close() {
		((Polygon)PaintProperties.shapes[6]).clear();
		if(modified)
			if(!MenuBar.SaveFile("modified")) return false;
		return true;
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}

class MenuBar extends JMenuBar{
	private static JFileChooser fc=new JFileChooser();			//�򿪱����ļ��Ի���
	public static JMenuItem undoItem,redoItem;			//����������������MenuItem
	static{
		//fc.setFileFilter(new FileNameExtensionFilter("JPEG ͼ���ļ�","jpg"));	//�ļ�����ɸѡ
		fc.setFileFilter(new FileFilter(){
			public boolean accept(File f){
				return f.getName().toLowerCase().endsWith(".jpg")|| f.isDirectory();
			}
			public String getDescription() {
				return "JPEG ͼ���ļ�";
			}
		});
		fc.setAcceptAllFileFilterUsed(false);			//���á������ļ�(*.*)��
	}
	public MenuBar(){
		JMenu  menuFile = new JMenu("�ļ�(F)");
		menuFile.setMnemonic('F');
		add(menuFile);

		JMenuItem createItem = new JMenuItem("�½�(N)...",'N');
		createItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+N
		createItem.addMouseListener(new HintAdapter("�½�һ�ſհ׵�ͼƬ��"));
		createItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				((Polygon)PaintProperties.shapes[6]).clear();
				if(MainFrame.modified)
					if(!SaveFile("modified")) return;
				PaintingArea oldCanvas=MainFrame.pa;
				MainFrame.pa=new CanvasCreator(MainFrame.pa).getCanvas();
				if(MainFrame.pa!=oldCanvas){
					MainFrame.sp.remove(oldCanvas);
					MainFrame.pa.setSize(MainFrame.pa.Width, MainFrame.pa.Height);
					MainFrame.sp.add(MainFrame.pa);
					MainFrame.sp.validate();
					MainFrame.currentFile=null;
					MainFrame.jf.setTitle("δ���� - Simple Painting Application");
				}
				PaintProperties.setCurrentState(PaintProperties.getCurrentState());
			}
		});
		menuFile.add(createItem);

		JMenuItem openItem = new JMenuItem("��(O)...",'O');
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+S
		openItem.addMouseListener(new HintAdapter("���������е�ͼƬ����ǰͼƬ���С�"));
		openItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				((Polygon)PaintProperties.shapes[6]).clear();
				if(MainFrame.modified)
					if(!SaveFile("modified")) return;
				fc.setDialogTitle("��");			//�Ի������
				if(fc.showOpenDialog(MainFrame.jf)==JFileChooser.APPROVE_OPTION){		//������
					try {
						MainFrame.modified=false;
						File file=fc.getSelectedFile();
						Image img=ImageIO.read(file);
						MainFrame.sp.remove(MainFrame.pa);
						MainFrame.pa=new PaintingArea(img.getWidth(null),img.getHeight(null));
						MainFrame.pa.setSize(MainFrame.pa.Width, MainFrame.pa.Height);
						MainFrame.sp.add(MainFrame.pa);
						MainFrame.sp.validate();
						MainFrame.pa.bi.getGraphics().drawImage(img,0,0,null);
						MainFrame.currentFile=file;
						MainFrame.jf.setTitle(file.getName()+" - Simple Painting Application");
						PaintProperties.setCurrentState(PaintProperties.getCurrentState());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					MainFrame.pa.repaint();
				}
			}
		});
		menuFile.add(openItem);

		JMenuItem saveItem = new JMenuItem("����(S)",'S');
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+S
		saveItem.addMouseListener(new HintAdapter("���浱ǰͼƬ���е����ݡ�"));
		saveItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(MainFrame.currentFile==null)SaveFile("saveas");
				else SaveFile("save");
			}
		});
		menuFile.add(saveItem);
		
		JMenuItem saveasItem = new JMenuItem("���Ϊ(A)...",'A');
		saveasItem.addMouseListener(new HintAdapter("���浱ǰͼƬ���е����ݵ��µ��ļ��С�"));
		saveasItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				SaveFile("saveas");
			}
		});
		menuFile.add(saveasItem);

		menuFile.addSeparator();

		JMenuItem printItem = new JMenuItem("��ӡ(P)...",'P');
		printItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+P
		printItem.addMouseListener(new HintAdapter("��ӡ��ǰͼ�����ô�ӡѡ�"));
		printItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new PrintTool();
			}
		});
		menuFile.add(printItem);

		menuFile.addSeparator();

		JMenuItem exitItem = new JMenuItem("�˳�(X)",'X');
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,ActionEvent.ALT_MASK));	//��ݼ�Alt+F4
		exitItem.addMouseListener(new HintAdapter("�˳�������ʾ�����ļ���"));
		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(MainFrame.close()) System.exit(0);
			}
		});
		menuFile.add(exitItem);

		JMenu  menuEdit = new JMenu("�༭(E)");
		menuEdit.setMnemonic('E');
		add(menuEdit);

		undoItem = new JMenuItem("����(U)",'U');
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+Z
		undoItem.addMouseListener(new HintAdapter("������һ�εĲ�����"));
		undoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.pa.nextBi.getGraphics().drawImage(MainFrame.pa.bi,0,0,null);		//�浽��һ��ͼ
				MainFrame.pa.bi.getGraphics().drawImage(MainFrame.pa.prevBi,0,0,null);			//����һ��ͼ
				MainFrame.pa.repaint();
				undoItem.setEnabled(false);
				redoItem.setEnabled(true);
			}
		});
		menuEdit.add(undoItem);

		redoItem = new JMenuItem("����(R)",'R');
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+Y
		redoItem.addMouseListener(new HintAdapter("�ָ���һ�α������Ĳ����� "));
		redoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.pa.Record();			//�浽��һ��ͼ
				MainFrame.pa.bi.getGraphics().drawImage(MainFrame.pa.nextBi,0,0,null);			//����һ��ͼ
				MainFrame.pa.repaint();
			}
		});
		menuEdit.add(redoItem);

		menuEdit.addSeparator();

		JMenuItem cutItem = new JMenuItem("����(T)",'T');
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+X
		cutItem.addMouseListener(new HintAdapter("����ǰͼ�������Ƶ��������У�����յ�ǰͼ��"));
		cutItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				PaintingArea.setClipboardImage(MainFrame.pa.bi);
				MainFrame.pa.Record();
				MainFrame.pa.bi.getGraphics().setColor(Color.white);			//����
				MainFrame.pa.bi.getGraphics().fillRect(0, 0, 574, 400);
				MainFrame.pa.repaint();
			}
		});
		menuEdit.add(cutItem);

		JMenuItem copyItem = new JMenuItem("����(C)",'C');
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+C
		copyItem.addMouseListener(new HintAdapter("����ǰͼ�������Ƶ��������С�"));
		copyItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				PaintingArea.setClipboardImage(MainFrame.pa.bi);
			}
		});
		menuEdit.add(copyItem);

		JMenuItem pasteItem = new JMenuItem("ճ��(P)",'P');
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));	//��ݼ�Ctrl+V
		pasteItem.addMouseListener(new HintAdapter("����������е�ͼƬ����ǰͼƬ���С�"));
		pasteItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Image clip_image=PaintingArea.getClipboardImage();
				MainFrame.pa.Record();
				MainFrame.pa.bi.getGraphics().drawImage(clip_image, 0, 0, null);
				MainFrame.pa.repaint();
			}
		});
		menuEdit.add(pasteItem);

		menuEdit.addSeparator();

		JMenuItem insertItem = new JMenuItem("ճ�����ļ�(F)...",'F');
		insertItem.addMouseListener(new HintAdapter("��������ͼƬ����ǰͼƬ���С�"));
		insertItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				fc.setDialogTitle("ճ�����ļ�");			//�Ի������
				if(fc.showOpenDialog(MainFrame.jf)==JFileChooser.APPROVE_OPTION){		//������
					try {
						File file=fc.getSelectedFile();
						((Polygon)PaintProperties.shapes[6]).clear();
						MainFrame.pa.Record();
						MainFrame.pa.bi.getGraphics().drawImage(ImageIO.read(file),0,0,null);		//���ļ�����ͼ�񻺳���
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					MainFrame.pa.repaint();
				}
			}
		});
		menuEdit.add(insertItem);

		JMenu  menuGraphic = new JMenu("ͼ��(G)");
		menuGraphic.setMnemonic('G');
		add(menuGraphic);

		JMenuItem cleanItem = new JMenuItem("����(C)",'C');
		cleanItem.addMouseListener(new HintAdapter("���ͼƬ��"));
		cleanItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				((Polygon)PaintProperties.shapes[6]).clear();					//��ն����û�������
				MainFrame.pa.Record();
				int Width=MainFrame.pa.Width,Height=MainFrame.pa.Height;
				MainFrame.pa.bi.getGraphics().setColor(Color.white);			//����
				MainFrame.pa.bi.getGraphics().fillRect(0, 0, Width, Height);
				MainFrame.pa.repaint();
			}	
		});
		menuGraphic.add(cleanItem);
		
		JMenu  menuEffect = new JMenu("Ч��(E)");
		menuEffect.setMnemonic('E');
		menuGraphic.add(menuEffect);
		
		JMenuItem bwItem = new JMenuItem("�ڰ�(B)",'B');
		bwItem.addMouseListener(new HintAdapter("��ͼ�������еĵ�ǰͼ���Ϊ�ڰס�"));
		bwItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.pa.Record();
				int Width=MainFrame.pa.Width,Height=MainFrame.pa.Height;
				for(int i=0;i<Width;i++)
					for(int j=0;j<Height;j++)
						MainFrame.pa.bi.setRGB(i,j,ImageEffect.rgbBW(MainFrame.pa.bi.getRGB(i,j)));
				MainFrame.pa.repaint();
			}
		});
		menuEffect.add(bwItem);
		
		JMenuItem reverseItem = new JMenuItem("��ɫ(R)",'R');
		reverseItem.addMouseListener(new HintAdapter("��ͼ�������еĵ�ǰͼ��ɫ��"));
		reverseItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.pa.Record();
				int Width=MainFrame.pa.Width,Height=MainFrame.pa.Height;
				for(int i=0;i<Width;i++)
					for(int j=0;j<Height;j++)
						MainFrame.pa.bi.setRGB(i,j,ImageEffect.rgbReverse(MainFrame.pa.bi.getRGB(i,j)));
				MainFrame.pa.repaint();
			}
		});
		menuEffect.add(reverseItem);
		
		menuGraphic.addSeparator();
		
		JMenuItem infoItem = new JMenuItem("ͼ����Ϣ(I)...",'I');
		infoItem.addMouseListener(new HintAdapter("��ʾ��ǰͼ��Ĵ�С��Ϣ��"));
		infoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String str="�ļ���";
				if(MainFrame.currentFile==null) str+="�µ�δ�����ļ�";
				else str+=MainFrame.currentFile.getAbsolutePath();
				str+="\n��ȣ�"+MainFrame.pa.Width+"\n�߶ȣ�"+MainFrame.pa.Height;
				JOptionPane.showMessageDialog(MainFrame.jf,str,"ͼ����Ϣ",JOptionPane.INFORMATION_MESSAGE);
			}	
		});
		menuGraphic.add(infoItem);
		
		JMenu  menuColor = new JMenu("��ɫ(C)");
		menuColor.setMnemonic('C');
		add(menuColor);

		JMenuItem colorItem = new JMenuItem("�༭ǰ��ɫ(F)...",'F');
		ColorChanger cc=new ColorChanger(true);
		colorItem.addMouseListener(cc);
		colorItem.addActionListener(cc);
		menuColor.add(colorItem);

		JMenuItem bgItem = new JMenuItem("�༭����ɫ(B)...",'B');
		ColorChanger bc=new ColorChanger(false);
		bgItem.addMouseListener(bc);
		bgItem.addActionListener(bc);
		menuColor.add(bgItem);

		JMenu  menuHelp = new JMenu("����(H)");
		menuHelp.setMnemonic('H');
		add(menuHelp);

		JMenuItem aboutItem = new JMenuItem("����(A)...",'A');
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));			//��ݼ�F1
		aboutItem.addMouseListener(new HintAdapter("��ʾ������Ϣ�Ͱ�Ȩ��"));
		aboutItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				final String str="Simple Painting Application\nһ�����׵Ļ���С����\n��Ȩ����(C) 2013 ���ɹ����ҡ���������Ȩ����\n\n" +
						"���棺�����������������Ȩ���͹��ʹ�Լ�ı�����δ����Ȩ����\n" +
						"���ƻ򴫲�������Ĳ��ֻ�ȫ���������ܵ����������»������Ʋã�\n" +
						"�����ڷ�����ɵķ�Χ���ܵ������ܵ����ߡ�";
				//���ڶԻ��������
				JOptionPane.showMessageDialog(MainFrame.jf,str,"����",JOptionPane.INFORMATION_MESSAGE);
				//��ʾ���ڶԻ��򣬶Ի�������Ϊ��Ϣ�Ի��򣨴�i��־��
			}	
		});
		menuHelp.add(aboutItem);
	}
	public static boolean SaveFile(String state){
		((Polygon)PaintProperties.shapes[6]).clear();
		File file = MainFrame.currentFile;
		if(state.equals("modified")){
			int result=JOptionPane.showConfirmDialog(MainFrame.jf,"�ļ��Ѿ��޸ģ��Ƿ񱣴棿","ѯ��",JOptionPane.YES_NO_CANCEL_OPTION);
			if(result==JOptionPane.YES_OPTION)
				if(file==null) state="saveas";
				else state="save";
			else if(result==JOptionPane.CANCEL_OPTION) return false;
			else return true;
		}
		if(state.equals("saveas")){
			fc.setDialogTitle("���Ϊ");			//�Ի������
			if(fc.showSaveDialog(MainFrame.jf)==JFileChooser.APPROVE_OPTION){
				file=fc.getSelectedFile();
				state="save";
			}
			else return false;
		}
		if(state.equals("save")){
			try {
				if(!file.getAbsolutePath().toLowerCase().endsWith(".jpg"))
					file=new File(file.getAbsolutePath()+".jpg");		//���������ļ���������չ����Ҫ�Զ����
				ImageIO.write(MainFrame.pa.bi,"jpg",file);		//��ͼ�񻺳���ͼ����JPG��ʽ����
				MainFrame.currentFile=file;
				MainFrame.jf.setTitle(file.getName()+" - Simple Painting Application");
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}
}

class StatusBar extends JToolBar{
	public static final String str="����ð�Ȩ��Ϣ���뵥��������>���ڡ���F1��";
	//״̬����Ĭ���ַ���
	static JLabel information=new JLabel(str);
	static JLabel xoy=new JLabel("");
	public StatusBar(){
		setFloatable(false);
		setLayout(new BorderLayout());
		add(information,BorderLayout.WEST);		//��ʾ��Ϣ�����
		add(xoy,BorderLayout.EAST);				//�����������ұ�
	}
}

class CanvasCreator extends WindowAdapter implements ActionListener{
	private String command;
	private PaintingArea oldCanvas;
	private JTextField tfWidth=new JTextField("574"),tfHeight=new JTextField("400");
	private JDialog newDlg=new JDialog(MainFrame.jf,"�½�",true);

	public CanvasCreator(PaintingArea oldCanvas){
		this.oldCanvas=oldCanvas;

		newDlg.setLayout(null);
		newDlg.setBounds(150,150,250,110);
		newDlg.setResizable(false);
		newDlg.addWindowListener(this);

		JLabel lb1=new JLabel("��ȣ�"),lb2=new JLabel("�߶ȣ�");

		lb1.setBounds(10, 10, 40, 20);
		newDlg.add(lb1);
		tfWidth.setBounds(50, 10, 60, 20);
		newDlg.add(tfWidth);

		lb2.setBounds(130, 10, 40, 20);
		newDlg.add(lb2);
		tfHeight.setBounds(170, 10, 60, 20);
		newDlg.add(tfHeight);

		JButton okButton=new JButton("ȷ��");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		okButton.setBounds(40, 40, 60, 30);
		newDlg.add(okButton);

		JButton cancelButton=new JButton("ȡ��");
		cancelButton.setActionCommand("CANCEL");
		cancelButton.addActionListener(this);
		cancelButton.setBounds(150, 40, 60, 30);
		newDlg.add(cancelButton);

		newDlg.setVisible(true);
	}
	public void actionPerformed(ActionEvent arg0) {
		command=arg0.getActionCommand();
		newDlg.dispose();
	}
	public PaintingArea getCanvas(){
		try{
			if(command.equals("OK")){
				int width=Integer.parseInt(tfWidth.getText());
				int height=Integer.parseInt(tfHeight.getText());
				MainFrame.modified=false;
				return new PaintingArea(width,height);
			}
			else throw new NumberFormatException("Cancelled");
		}catch(NumberFormatException e){
			if(!e.getMessage().equals("Cancelled")){
				JOptionPane.showMessageDialog(MainFrame.jf, "�������벻�Ϸ������������롣", "�½�ʧ��", JOptionPane.INFORMATION_MESSAGE);
			}
			if(oldCanvas == null) System.exit(0);
			return oldCanvas;
		}
	}
	public void windowClosing(WindowEvent arg0) {
		if(oldCanvas == null) System.exit(0);
	}
}