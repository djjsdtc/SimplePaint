package org.cookiestudio.simplepaint;

public class ImageEffect{
	//一些关于图像特效的ARGB数值变换算法集合
	//参考资料：http://dev.10086.cn/cmdn/wiki/index.php?doc-view-3381.html
	
	public static int rgbReverse(int color){
		//ARGB值是一个4字节整数，按16进制表示时，四个字节分别为Alpha、R、G、B值
		//反色的要求：Alpha不变，R、G、B为255减原来的值
		int a = ((color & 0xff000000) >> 24); // alpha channel
		int r =255-((color & 0x00ff0000) >> 16); // red channel
		int g =255-((color & 0x0000ff00) >> 8); // green channel
		int b =255-(color & 0x000000ff); // blue channel
		return ((a << 24) | (r << 16) | (g << 8) | b);
	}
	
	public static int rgbBW(int color){
		//黑白效果
		int a = ((color & 0xff000000) >> 24); // alpha channel
		int r = ((color & 0x00ff0000) >> 16); // red channel
		int g = ((color & 0x0000ff00) >> 8); // green channel
		int b = (color & 0x000000ff); // blue channel
		int temp = (int)(.299*(double)r+.587*(double)g+.114*(double)b);
		r = temp;g = temp;b = temp;
		return ((a << 24) | (r << 16) | (g << 8) | b);
	}
}