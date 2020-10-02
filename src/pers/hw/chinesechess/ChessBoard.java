/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pers.hw.chinesechess;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author HW
 */
public class ChessBoard extends JPanel {

    private ChessPiece[] piece;//棋子类
    private Rule rule;  //规则类
    private Stack stack; //堆栈，实现悔棋
    private BoardMouseListener mouseListener;

    private int chessState;//对局状态 0：未开始  1：对局中 2：结束
    private boolean isRedTurn;//回合判断
    private int[][] point;//棋盘交叉点

    private Image boardImg;//棋盘图像
    private Image imgbuf;//更新缓冲图像
    private Graphics gbuf;
    private JButton startBtn;
    private Timer timer;
    private int timeCnt;

    private final int CELL_W = 60;//单元格宽度
    private final int OFFSET_X = 40, OFFSET_Y = 40;//棋盘左上角偏移量
    private final int PIECE_R = ChessPiece.DIAM / 2;//棋子半径
    private final int TOTAL_W = OFFSET_X + CELL_W * 8 + 230;//画布总宽度
    private final int TOTAL_H = OFFSET_Y + CELL_W * 9 + 80;//画布总高度
    private final String[] blackChessName = {"車", "馬", "象", "士", "將", "炮", "卒"};
    private final String[] redChessName = {"車", "馬", "相", "仕", "帥", "砲", "兵"};

    public ChessBoard() {
        JFrame f = new JFrame("象棋");
        f.setBounds(400, 30, TOTAL_W, TOTAL_H);

        initVar();
        initButton();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        setLayout(null);//绝对布局
        setBackground(new Color(206, 230, 214));
        f.getContentPane().add(this, BorderLayout.CENTER);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initButton() {
        startBtn = new JButton("开始游戏");
        startBtn.setFont(new Font("", Font.BOLD, 18));
        startBtn.setBounds(TOTAL_W - 170, 220, 110, 50);
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chessState != 1) {
                    chessState = 1;//进入对局
                    isRedTurn = true;//红色方先手
                    resetTimer();
                    initPoint();//棋盘点恢复初始状态
                    startBtn.setText("结束游戏");
                } else {
                    chessState = 0;
                    cancelTimer();
                    stack.clear();
                    initPoint();
                    startBtn.setText("开始游戏");
                }
                update();
            }

        });
        add(startBtn);

        JButton recallBtn = new JButton("悔 棋");
        recallBtn.setFont(new Font("", Font.BOLD, 18));
        recallBtn.setBounds(TOTAL_W - 170, 290, 110, 50);
        recallBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stack.isEmpty() || chessState != 1) {
                    return;
                }
                point = stack.getmap();//从堆栈读取上个状态
                rule.setJudgePoint(point);
                isRedTurn = !isRedTurn;//变换回合
                stack.pop();
                resetTimer();
                update();
            }

        });
        add(recallBtn);
    }

    private void initVar() {
        piece = new ChessPiece[32];//共32个棋子
        point = new int[9][10];//x轴方向9个点，y轴方向10个点
        rule = new Rule(point);
        stack = new Stack();
        boardImg = createChessBoard();
        mouseListener = new BoardMouseListener();
        initPiece();
        initPoint();
    }

    private void initPiece() {
        for (int i = 0; i < 9; i++) {
            int nameIdx = (i < 5) ? i : (8 - i);
            piece[i] = new ChessPiece(0, blackChessName[nameIdx], i, 0);
            piece[i + 16] = new ChessPiece(1, redChessName[nameIdx], i, 9);
        }
        piece[9] = new ChessPiece(0, blackChessName[5], 1, 2);
        piece[10] = new ChessPiece(0, blackChessName[5], 7, 2);
        piece[25] = new ChessPiece(1, redChessName[5], 1, 7);
        piece[26] = new ChessPiece(1, redChessName[5], 7, 7);
        for (int i = 0; i < 5; i++) {
            piece[11 + i] = new ChessPiece(0, blackChessName[6], 2 * i, 3);
            piece[27 + i] = new ChessPiece(1, redChessName[6], 2 * i, 6);
        }
    }

    private void initPoint() {
        //-1代表无棋子
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                point[i][j] = -1;
            }
        }
        //根据棋子初始坐标更新相应的点，赋索引值
        for (int i = 0; i < 32; i++) {
            point[piece[i].getXindex()][piece[i].getYindex()] = i;
        }
    }

    //绘制棋盘图像
    private Image createChessBoard() {
        BufferedImage bimg = new BufferedImage(CELL_W * 8, CELL_W * 9, BufferedImage.TYPE_INT_ARGB);//样式：带透明色
        Graphics2D g2d = (Graphics2D) bimg.getGraphics();

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));
        //画横线
        for (int j = 1; j < 9; j++) {
            g2d.drawLine(0, CELL_W * j, CELL_W * 8, CELL_W * j);
        }
        //画竖线
        for (int i = 1; i < 8; i++) {
            g2d.drawLine(CELL_W * i, 0, CELL_W * i, CELL_W * 4);
            g2d.drawLine(CELL_W * i, CELL_W * 5, CELL_W * i, CELL_W * 9);
        }
        //边框
        g2d.drawRect(1, 1, CELL_W * 8 - 2, CELL_W * 9 - 2);
        //九宫格斜线
        g2d.drawLine(CELL_W * 3, 0, CELL_W * 5, CELL_W * 2);
        g2d.drawLine(CELL_W * 3, CELL_W * 2, CELL_W * 5, 0);
        g2d.drawLine(CELL_W * 3, CELL_W * 7, CELL_W * 5, CELL_W * 9);
        g2d.drawLine(CELL_W * 3, CELL_W * 9, CELL_W * 5, CELL_W * 7);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("隶书", Font.BOLD, (int) (45.0 * CELL_W / 60)));
        g2d.drawString("楚" + " 河", (int) (CELL_W * 4 - 180.0 * CELL_W / 60), (int) (CELL_W * 4.5 + 15.0 * CELL_W / 60));
        g2d.drawString("漢" + " 界", (int) (CELL_W * 4 + 60.0 * CELL_W / 60), (int) (CELL_W * 4.5 + 15.0 * CELL_W / 60));
        return bimg;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        //画棋盘
        g2d.drawImage(boardImg, OFFSET_X, OFFSET_Y, null);
        //棋盘外边框
        g2d.setStroke(new BasicStroke(4.0f));
        g2d.drawRect(OFFSET_X - 5, OFFSET_Y - 5, boardImg.getWidth(this) + 10, boardImg.getHeight(this) + 10);

        int selIdx = mouseListener.getIndex();//鼠标选中棋子索引
        int idx, xpos, ypos;
        //画棋子
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                idx = point[i][j];
                if (idx >= 0 && idx != selIdx) {
                    xpos = OFFSET_X + CELL_W * i - PIECE_R;
                    ypos = OFFSET_Y + CELL_W * j - PIECE_R;
                    g2d.drawImage(piece[idx].getImage(), xpos, ypos, null);
                }
            }
        }
        paintTips(g2d);//写提示信息 

        //画鼠标拖动的棋子
        if (selIdx >= 0) {
            paintComponent(g2d, mouseListener.getSelx(), mouseListener.getSely());
            g2d.drawImage(piece[selIdx].getImage(), mouseListener.getPieceXpos(), mouseListener.getPieceYpos(), null);
        }
    }

    private void paintTips(Graphics2D g2d) {

        g2d.setFont(new Font("", Font.BOLD, 23));

        switch (chessState) {
            case 0://初始状态
                g2d.setColor(Color.BLACK);
                g2d.drawString("等待开局", TOTAL_W - 165, 100);
                break;
            case 1://游戏状态
                if (isRedTurn) {
                    g2d.setColor(Color.red);
                    g2d.drawString("红棋回合", TOTAL_W - 165, 100);
                } else {
                    g2d.setColor(Color.black);
                    g2d.drawString("黑棋回合", TOTAL_W - 165, 100);
                }
                g2d.drawString("倒计时：" + timeCnt + " s", TOTAL_W - 185, 140);
                break;
            case 2://结束状态
                if (isRedTurn) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("游戏结束", TOTAL_W - 165, 100);
                    g2d.drawString("红色方获胜！", TOTAL_W - 180, 140);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("游戏结束", TOTAL_W - 165, 100);
                    g2d.drawString("黑色方获胜！", TOTAL_W - 180, 140);
                }
                break;
            default:
                break;
        }
    }

    //坐标提示图案
    public void paintComponent(Graphics2D g2d, int i, int j) {
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(Color.red);
        int x = OFFSET_X + CELL_W * i, y = OFFSET_Y + CELL_W * j;
        final int d1 = 15, d2 = 5;
        g2d.drawLine(x - d1, y - d2, x - d2, y - d2);
        g2d.drawLine(x - d1, y + d2, x - d2, y + d2);
        g2d.drawLine(x + d1, y - d2, x + d2, y - d2);
        g2d.drawLine(x + d1, y + d2, x + d2, y + d2);
        g2d.drawLine(x - d2, y - d2, x - d2, y - d1);
        g2d.drawLine(x + d2, y - d2, x + d2, y - d1);
        g2d.drawLine(x - d2, y + d2, x - d2, y + d1);
        g2d.drawLine(x + d2, y + d2, x + d2, y + d1);
    }

    //双缓冲更新显示
    public void update() {
        if (imgbuf == null) {
            imgbuf = createImage(this.getSize().width, this.getSize().height);
            gbuf = imgbuf.getGraphics();
        }
        gbuf.setColor(getBackground());
        gbuf.fillRect(0, 0, this.getSize().width, this.getSize().height);
        paint(gbuf);//在缓冲图像上绘制
        this.getGraphics().drawImage(imgbuf, 0, 0, this);
        if (chessState == 2) {
            cancelTimer();
            stack.clear();
            startBtn.setText("重新开始");
            if (isRedTurn) {
                JOptionPane.showMessageDialog(null, "红色方获胜！", "游戏结束", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "黑色方获胜！", "游戏结束", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void resetTimer() {
        cancelTimer();
        timer = new Timer();
        //每秒执行一次
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                timeCnt--;
                if (timeCnt == 0) {
                    isRedTurn = !isRedTurn;
                    chessState = 2;
                }
                update();
            }
        }, 1000, 1000);
        timeCnt = 30;//定时30s
    }

    private class BoardMouseListener implements MouseListener, MouseMotionListener {

        private int pieceIdx = -1;//选中棋子索引，-1代表无棋子
        private int selx, sely; //选中棋子的坐标索引
        private int xpos, ypos; //鼠标坐标

        @Override
        public void mousePressed(MouseEvent me) {
            pieceIdx = -1;
            selx = posToIndex(me.getX(), OFFSET_X - PIECE_R, boardImg.getWidth(null) + 2 * PIECE_R);
            sely = posToIndex(me.getY(), OFFSET_Y - PIECE_R, boardImg.getHeight(null) + 2 * PIECE_R);

            //选中的坐标无效
            if (selx == -1 || sely == -1) {
                return;
            }
            int idx = point[selx][sely];
            //坐标无棋子，或非对局状态
            if (idx < 0 || chessState != 1) {
                return;
            }
            //是否选中对方棋子
            if (isRedTurn ? idx < 16 : idx >= 16) {
                return;
            }
            pieceIdx = idx;
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (pieceIdx < 0) {
                return;
            }
            xpos = me.getX();
            ypos = me.getY();
            update();//显示棋子的拖动图像
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (pieceIdx < 0) {
                return;
            }
            pieceIdx = -1;
            int xd = posToIndex(me.getX(), OFFSET_X - PIECE_R, boardImg.getWidth(null) + 2 * PIECE_R);
            int yd = posToIndex(me.getY(), OFFSET_Y - PIECE_R, boardImg.getHeight(null) + 2 * PIECE_R);
            //根据拟定的规则判断
            if (rule.judge(selx, sely, xd, yd)) {
                stack.push(point);//存储当前状态
                int dstIdx = point[xd][yd];//目标地址的棋子索引
                point[xd][yd] = point[selx][sely];
                point[selx][sely] = -1;
                if (dstIdx == 4 || dstIdx == 20) {
                    chessState = 2;//将军被吃，对局结束
                } else {
                    isRedTurn = !isRedTurn;//变换回合
                    resetTimer();
                }
            }
            update();
        }

        //绝对坐标转化为棋子坐标索引，ref：参考点，range：坐标范围
        private int posToIndex(int pos, int ref, int range) {
            if (pos < ref || pos > ref + range) {
                return -1;
            }
            int m = (pos - ref) / CELL_W;
            int r = (pos - ref) % CELL_W;
            if (r < 2 * PIECE_R) {
                return m;
            }
            return -1;
        }

        public int getSelx() {
            return selx;
        }

        public int getSely() {
            return sely;
        }

        public int getIndex() {
            return pieceIdx;
        }

        public int getPieceXpos() {
            return xpos - PIECE_R;
        }

        public int getPieceYpos() {
            return ypos - PIECE_R;
        }

        @Override
        public void mouseClicked(MouseEvent me) {

        }

        @Override
        public void mouseEntered(MouseEvent me) {

        }

        @Override
        public void mouseExited(MouseEvent me) {

        }

        @Override
        public void mouseMoved(MouseEvent me) {

        }
    }

}
