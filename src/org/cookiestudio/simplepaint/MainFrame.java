package org.cookiestudio.simplepaint;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class MainFrame {
	public static PaintingArea pa;					//画布
	public static JFrame jf=new JFrame(){
		protected void processWindowEvent(WindowEvent e) {  
	        if (e.getID() == WindowEvent.WINDOW_CLOSING){ 
	        	if(MainFrame.close()) System.exit(0);
	        	else return;
	        }
	        super.processWindowEvent(e); //该语句会执行窗口事件的默认动作(如：隐藏)  
	    }
	};				//窗口
	public static ToolBox tb;						//左面板（工具箱）
	private ColorPicker cp=new ColorPicker();			//下面板（颜色拾取器）
	private StatusBar stb=new StatusBar();				//状态栏
	public static ScrollPane sp;						//给画布添加滚动条
	public static File currentFile=null;
	public static boolean modified=false;
	
	public MainFrame(){
		jf.setTitle("未命名 - Simple Painting Application");
		ImageIcon imgIcon = new ImageIcon("images//logo.jpg");
		jf.setIconImage(imgIcon.getImage());			//标题栏图标
		jf.setJMenuBar(new MenuBar());					//创建菜单栏
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
			public void componentResized(ComponentEvent e){		//调整窗体大小时顺带调整组件大小
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
	private static JFileChooser fc=new JFileChooser();			//打开保存文件对话框
	public static JMenuItem undoItem,redoItem;			//其它类会调用这两个MenuItem
	static{
		//fc.setFileFilter(new FileNameExtensionFilter("JPEG 图像文件","jpg"));	//文件类型筛选
		fc.setFileFilter(new FileFilter(){
			public boolean accept(File f){
				return f.getName().toLowerCase().endsWith(".jpg")|| f.isDirectory();
			}
			public String getDescription() {
				return "JPEG 图像文件";
			}
		});
		fc.setAcceptAllFileFilterUsed(false);			//禁用“所有文件(*.*)”
	}
	public MenuBar(){
		JMenu  menuFile = new JMenu("文件(F)");
		menuFile.setMnemonic('F');
		add(menuFile);

		JMenuItem createItem = new JMenuItem("新建(N)...",'N');
		createItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));	//快捷键Ctrl+N
		createItem.addMouseListener(new HintAdapter("新建一张空白的图片。"));
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
					MainFrame.jf.setTitle("未命名 - Simple Painting Application");
				}
				PaintProperties.setCurrentState(PaintProperties.getCurrentState());
			}
		});
		menuFile.add(createItem);

		JMenuItem openItem = new JMenuItem("打开(O)...",'O');
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));	//快捷键Ctrl+S
		openItem.addMouseListener(new HintAdapter("导入计算机中的图片到当前图片区中。"));
		openItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				((Polygon)PaintProperties.shapes[6]).clear();
				if(MainFrame.modified)
					if(!SaveFile("modified")) return;
				fc.setDialogTitle("打开");			//对话框标题
				if(fc.showOpenDialog(MainFrame.jf)==JFileChooser.APPROVE_OPTION){		//单击打开
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

		JMenuItem saveItem = new JMenuItem("保存(S)",'S');
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));	//快捷键Ctrl+S
		saveItem.addMouseListener(new HintAdapter("保存当前图片区中的内容。"));
		saveItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(MainFrame.currentFile==null)SaveFile("saveas");
				else SaveFile("save");
			}
		});
		menuFile.add(saveItem);
		
		JMenuItem saveasItem = new JMenuItem("另存为(A)...",'A');
		saveasItem.addMouseListener(new HintAdapter("保存当前图片区中的内容到新的文件中。"));
		saveasItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				SaveFile("saveas");
			}
		});
		menuFile.add(saveasItem);

		menuFile.addSeparator();

		JMenuItem printItem = new JMenuItem("打印(P)...",'P');
		printItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));	//快捷键Ctrl+P
		printItem.addMouseListener(new HintAdapter("打印当前图像并设置打印选项。"));
		printItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new PrintTool();
			}
		});
		menuFile.add(printItem);

		menuFile.addSeparator();

		JMenuItem exitItem = new JMenuItem("退出(X)",'X');
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,ActionEvent.ALT_MASK));	//快捷键Alt+F4
		exitItem.addMouseListener(new HintAdapter("退出程序，提示保存文件。"));
		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(MainFrame.close()) System.exit(0);
			}
		});
		menuFile.add(exitItem);

		JMenu  menuEdit = new JMenu("编辑(E)");
		menuEdit.setMnemonic('E');
		add(menuEdit);

		undoItem = new JMenuItem("撤销(U)",'U');
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));	//快捷键Ctrl+Z
		undoItem.addMouseListener(new HintAdapter("撤销上一次的操作。"));
		undoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.pa.nextBi.getGraphics().drawImage(MainFrame.pa.bi,0,0,null);		//绘到下一张图
				MainFrame.pa.bi.getGraphics().drawImage(MainFrame.pa.prevBi,0,0,null);			//绘上一张图
				MainFrame.pa.repaint();
				undoItem.setEnabled(false);
				redoItem.setEnabled(true);
			}
		});
		menuEdit.add(undoItem);

		redoItem = new JMenuItem("重做(R)",'R');
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK));	//快捷键Ctrl+Y
		redoItem.addMouseListener(new HintAdapter("恢复上一次被撤销的操作。 "));
		redoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.pa.Record();			//绘到上一张图
				MainFrame.pa.bi.getGraphics().drawImage(MainFrame.pa.nextBi,0,0,null);			//绘下一张图
				MainFrame.pa.repaint();
			}
		});
		menuEdit.add(redoItem);

		menuEdit.addSeparator();

		JMenuItem cutItem = new JMenuItem("剪切(T)",'T');
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));	//快捷键Ctrl+X
		cutItem.addMouseListener(new HintAdapter("将当前图像区域复制到剪贴板中，并清空当前图像。"));
		cutItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				PaintingArea.setClipboardImage(MainFrame.pa.bi);
				MainFrame.pa.Record();
				MainFrame.pa.bi.getGraphics().setColor(Color.white);			//清屏
				MainFrame.pa.bi.getGraphics().fillRect(0, 0, 574, 400);
				MainFrame.pa.repaint();
			}
		});
		menuEdit.add(cutItem);

		JMenuItem copyItem = new JMenuItem("复制(C)",'C');
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));	//快捷键Ctrl+C
		copyItem.addMouseListener(new HintAdapter("将当前图像区域复制到剪贴板中。"));
		copyItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				PaintingArea.setClipboardImage(MainFrame.pa.bi);
			}
		});
		menuEdit.add(copyItem);

		JMenuItem pasteItem = new JMenuItem("粘贴(P)",'P');
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));	//快捷键Ctrl+V
		pasteItem.addMouseListener(new HintAdapter("导入剪贴板中的图片到当前图片区中。"));
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

		JMenuItem insertItem = new JMenuItem("粘贴自文件(F)...",'F');
		insertItem.addMouseListener(new HintAdapter("导入现有图片到当前图片区中。"));
		insertItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				fc.setDialogTitle("粘贴自文件");			//对话框标题
				if(fc.showOpenDialog(MainFrame.jf)==JFileChooser.APPROVE_OPTION){		//单击打开
					try {
						File file=fc.getSelectedFile();
						((Polygon)PaintProperties.shapes[6]).clear();
						MainFrame.pa.Record();
						MainFrame.pa.bi.getGraphics().drawImage(ImageIO.read(file),0,0,null);		//把文件绘入图像缓冲区
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					MainFrame.pa.repaint();
				}
			}
		});
		menuEdit.add(insertItem);

		JMenu  menuGraphic = new JMenu("图像(G)");
		menuGraphic.setMnemonic('G');
		add(menuGraphic);

		JMenuItem cleanItem = new JMenuItem("清屏(C)",'C');
		cleanItem.addMouseListener(new HintAdapter("清除图片。"));
		cleanItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				((Polygon)PaintProperties.shapes[6]).clear();					//清空多边形没画完的线
				MainFrame.pa.Record();
				int Width=MainFrame.pa.Width,Height=MainFrame.pa.Height;
				MainFrame.pa.bi.getGraphics().setColor(Color.white);			//清屏
				MainFrame.pa.bi.getGraphics().fillRect(0, 0, Width, Height);
				MainFrame.pa.repaint();
			}	
		});
		menuGraphic.add(cleanItem);
		
		JMenu  menuEffect = new JMenu("效果(E)");
		menuEffect.setMnemonic('E');
		menuGraphic.add(menuEffect);
		
		JMenuItem bwItem = new JMenuItem("黑白(B)",'B');
		bwItem.addMouseListener(new HintAdapter("将图像区域中的当前图像变为黑白。"));
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
		
		JMenuItem reverseItem = new JMenuItem("反色(R)",'R');
		reverseItem.addMouseListener(new HintAdapter("将图像区域中的当前图像反色。"));
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
		
		JMenuItem infoItem = new JMenuItem("图像信息(I)...",'I');
		infoItem.addMouseListener(new HintAdapter("显示当前图像的大小信息。"));
		infoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String str="文件：";
				if(MainFrame.currentFile==null) str+="新的未保存文件";
				else str+=MainFrame.currentFile.getAbsolutePath();
				str+="\n宽度："+MainFrame.pa.Width+"\n高度："+MainFrame.pa.Height;
				JOptionPane.showMessageDialog(MainFrame.jf,str,"图像信息",JOptionPane.INFORMATION_MESSAGE);
			}	
		});
		menuGraphic.add(infoItem);
		
		JMenu  menuColor = new JMenu("颜色(C)");
		menuColor.setMnemonic('C');
		add(menuColor);

		JMenuItem colorItem = new JMenuItem("编辑前景色(F)...",'F');
		ColorChanger cc=new ColorChanger(true);
		colorItem.addMouseListener(cc);
		colorItem.addActionListener(cc);
		menuColor.add(colorItem);

		JMenuItem bgItem = new JMenuItem("编辑背景色(B)...",'B');
		ColorChanger bc=new ColorChanger(false);
		bgItem.addMouseListener(bc);
		bgItem.addActionListener(bc);
		menuColor.add(bgItem);

		JMenu  menuHelp = new JMenu("帮助(H)");
		menuHelp.setMnemonic('H');
		add(menuHelp);

		JMenuItem aboutItem = new JMenuItem("关于(A)...",'A');
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));			//快捷键F1
		aboutItem.addMouseListener(new HintAdapter("显示程序信息和版权。"));
		aboutItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				final String str="Simple Painting Application\n一个简易的画板小程序\n版权所有(C) 2013 饼干工作室。保留所有权利。\n\n" +
						"警告：本计算机程序受著作权法和国际公约的保护。未经授权擅自\n" +
						"复制或传播本程序的部分或全部，可能受到严厉的民事或刑事制裁，\n" +
						"并将在法律许可的范围内受到最大可能的起诉。";
				//关于对话框的内容
				JOptionPane.showMessageDialog(MainFrame.jf,str,"关于",JOptionPane.INFORMATION_MESSAGE);
				//显示关于对话框，对话框类型为信息对话框（带i标志）
			}	
		});
		menuHelp.add(aboutItem);
	}
	public static boolean SaveFile(String state){
		((Polygon)PaintProperties.shapes[6]).clear();
		File file = MainFrame.currentFile;
		if(state.equals("modified")){
			int result=JOptionPane.showConfirmDialog(MainFrame.jf,"文件已经修改，是否保存？","询问",JOptionPane.YES_NO_CANCEL_OPTION);
			if(result==JOptionPane.YES_OPTION)
				if(file==null) state="saveas";
				else state="save";
			else if(result==JOptionPane.CANCEL_OPTION) return false;
			else return true;
		}
		if(state.equals("saveas")){
			fc.setDialogTitle("另存为");			//对话框标题
			if(fc.showSaveDialog(MainFrame.jf)==JFileChooser.APPROVE_OPTION){
				file=fc.getSelectedFile();
				state="save";
			}
			else return false;
		}
		if(state.equals("save")){
			try {
				if(!file.getAbsolutePath().toLowerCase().endsWith(".jpg"))
					file=new File(file.getAbsolutePath()+".jpg");		//如果输入的文件名不带扩展名，要自动添加
				ImageIO.write(MainFrame.pa.bi,"jpg",file);		//将图像缓冲区图像以JPG格式存盘
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
	public static final String str="欲获得版权信息，请单击“帮助>关于”或按F1。";
	//状态栏的默认字符串
	static JLabel information=new JLabel(str);
	static JLabel xoy=new JLabel("");
	public StatusBar(){
		setFloatable(false);
		setLayout(new BorderLayout());
		add(information,BorderLayout.WEST);		//提示信息在左边
		add(xoy,BorderLayout.EAST);				//画布坐标在右边
	}
}

class CanvasCreator extends WindowAdapter implements ActionListener{
	private String command;
	private PaintingArea oldCanvas;
	private JTextField tfWidth=new JTextField("574"),tfHeight=new JTextField("400");
	private JDialog newDlg=new JDialog(MainFrame.jf,"新建",true);

	public CanvasCreator(PaintingArea oldCanvas){
		this.oldCanvas=oldCanvas;

		newDlg.setLayout(null);
		newDlg.setBounds(150,150,250,110);
		newDlg.setResizable(false);
		newDlg.addWindowListener(this);

		JLabel lb1=new JLabel("宽度："),lb2=new JLabel("高度：");

		lb1.setBounds(10, 10, 40, 20);
		newDlg.add(lb1);
		tfWidth.setBounds(50, 10, 60, 20);
		newDlg.add(tfWidth);

		lb2.setBounds(130, 10, 40, 20);
		newDlg.add(lb2);
		tfHeight.setBounds(170, 10, 60, 20);
		newDlg.add(tfHeight);

		JButton okButton=new JButton("确定");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		okButton.setBounds(40, 40, 60, 30);
		newDlg.add(okButton);

		JButton cancelButton=new JButton("取消");
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
				JOptionPane.showMessageDialog(MainFrame.jf, "您的输入不合法，请重新输入。", "新建失败", JOptionPane.INFORMATION_MESSAGE);
			}
			if(oldCanvas == null) System.exit(0);
			return oldCanvas;
		}
	}
	public void windowClosing(WindowEvent arg0) {
		if(oldCanvas == null) System.exit(0);
	}
}