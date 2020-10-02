/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pers.hw.chinesechess;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author HW
 */
public class ChessPiece {

    public static final int DIAM = 50;//棋子直径
    private final int family;//棋子类别，0:黑  1:红
    private final String name;
    private final int xindex, yindex;//棋子初始位置
    private final Image img;//棋子图像

    public ChessPiece(int family, String name, int xindex, int yindex) {
        this.family = family;
        this.name = name;
        this.xindex = xindex;
        this.yindex = yindex;
        this.img = createImage();
    }

    //绘制棋子图像
    private Image createImage() {
        BufferedImage bimg = new BufferedImage(DIAM, DIAM, BufferedImage.TYPE_INT_ARGB);//样式：带透明色
        Graphics2D g2d = (Graphics2D) bimg.getGraphics();

        Color fillColor, borderColor, fontColor;//填充颜色，边框颜色，字体颜色
        if (family == 0) {
            fillColor = new Color(255, 239, 188);
            borderColor = new Color(215, 173, 15);
            fontColor = Color.BLACK;
        } else {
            fillColor = new Color(255, 230, 223);
            borderColor = new Color(231, 140, 59);
            fontColor = Color.RED;
        }
        g2d.setColor(fillColor);
        g2d.fillOval(0, 0, DIAM, DIAM);

        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(borderColor);
        g2d.drawOval(1, 1, DIAM - 2, DIAM - 2);

        g2d.setFont(new Font("楷体", Font.BOLD, (int) (38.0 * DIAM / 50)));
        g2d.setColor(fontColor);
        g2d.drawString(name, (int) (DIAM / 2 - 20.0 * DIAM / 50), (int) (DIAM / 2 + 14.0 * DIAM / 50));
        return bimg;
    }

    public int getXindex() {
        return xindex;
    }

    public int getYindex() {
        return yindex;
    }

    public Image getImage() {
        return img;
    }

}
