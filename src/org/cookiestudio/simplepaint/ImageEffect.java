package org.cookiestudio.simplepaint;

public class ImageEffect{
	//һЩ����ͼ����Ч��ARGB��ֵ�任�㷨����
	//�ο����ϣ�http://dev.10086.cn/cmdn/wiki/index.php?doc-view-3381.html
	
	public static int rgbReverse(int color){
		//ARGBֵ��һ��4�ֽ���������16���Ʊ�ʾʱ���ĸ��ֽڷֱ�ΪAlpha��R��G��Bֵ
		//��ɫ��Ҫ��Alpha���䣬R��G��BΪ255��ԭ����ֵ
		int a = ((color & 0xff000000) >> 24); // alpha channel
		int r =255-((color & 0x00ff0000) >> 16); // red channel
		int g =255-((color & 0x0000ff00) >> 8); // green channel
		int b =255-(color & 0x000000ff); // blue channel
		return ((a << 24) | (r << 16) | (g << 8) | b);
	}
	
	public static int rgbBW(int color){
		//�ڰ�Ч��
		int a = ((color & 0xff000000) >> 24); // alpha channel
		int r = ((color & 0x00ff0000) >> 16); // red channel
		int g = ((color & 0x0000ff00) >> 8); // green channel
		int b = (color & 0x000000ff); // blue channel
		int temp = (int)(.299*(double)r+.587*(double)g+.114*(double)b);
		r = temp;g = temp;b = temp;
		return ((a << 24) | (r << 16) | (g << 8) | b);
	}
}