/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pers.hw.chinesechess;

/**
 *
 * @author HW
 */
public class Rule {

    private int[][] point;
    private int xs, ys, xd, yd;//源地址（src），目的地址（dst）

    public Rule(int[][] point) {
        this.point = point;
    }

    //外部调用，更新指向的point对象
    public void setJudgePoint(int[][] point) {
        this.point = point;
    }

    public boolean judge(int xs, int ys, int xd, int yd) {
        if (xd < 0 || yd < 0) {
            return false;//目的地址无效则返回
        }
        int idx = point[xs][ys];
        this.xs = xs;
        this.ys = ys;
        this.xd = xd;
        this.yd = yd;
        if (isSameFamily()) {
            return false;//是同一类棋子则返回
        }
        //根据棋子索引判断走步规则
        if (idx == 0 || idx == 8 || idx == 16 || idx == 24) {
            return judgeJu();
        }
        if (idx == 1 || idx == 7 || idx == 17 || idx == 23) {
            return judgeMa();
        }
        if (idx == 2 || idx == 6 || idx == 18 || idx == 22) {
            return judgeXiang();
        }
        if (idx == 3 || idx == 5 || idx == 19 || idx == 21) {
            return judgeShi();
        }
        if (idx == 4 || idx == 20) {
            return judgeWang();
        }
        if (idx == 9 || idx == 10 || idx == 25 || idx == 26) {
            return judgePao();
        }
        if (idx >= 11 && idx < 16) {
            return judgeZu();
        }
        if (idx >= 27) {
            return judgeBing();
        }
        return false;
    }

    private boolean isSameFamily() {
        if (point[xd][yd] < 0) {
            return false;
        }
        return point[xs][ys] < 16 ? point[xd][yd] < 16 : point[xd][yd] >= 16;
    }

    //索引非负则有棋子
    private boolean hasPiece(int i, int j) {
        return point[i][j] >= 0;
    }

    //比较沿Y轴方向的两个棋子间棋子数量与给定值是否相等
    private boolean checkY(int i, int cmpValue) {
        int cnt = 0;
        int m = ys, n = yd;
        if (ys > yd) {
            m = yd;
            n = ys;
        }
        for (int j = m + 1; j < n; j++) {
            if (hasPiece(i, j)) {
                cnt++;
            }
        }
        return cnt == cmpValue;
    }

    private boolean checkX(int j, int cmpValue) {
        int cnt = 0;
        int m = xs, n = xd;
        if (xs > xd) {
            m = xd;
            n = xs;
        }
        for (int i = m + 1; i < n; i++) {
            if (hasPiece(i, j)) {
                cnt++;
            }
        }
        return cnt == cmpValue;
    }

    private boolean judgeJu() {
        return xs == xd && checkY(xs, 0) || ys == yd && checkX(ys, 0);
    }

    private boolean judgeMa() {
        return Math.abs(xs - xd) == 2 && Math.abs(ys - yd) == 1 && !hasPiece((xs + xd) / 2, ys)
                || Math.abs(ys - yd) == 2 && Math.abs(xs - xd) == 1 && !hasPiece(xs, (ys + yd) / 2);
    }

    private boolean judgePao() {
        return xs == xd && checkY(xs, 0) && !hasPiece(xd, yd)
                || xs == xd && checkY(xs, 1) && hasPiece(xd, yd)
                || ys == yd && checkX(ys, 0) && !hasPiece(xd, yd)
                || ys == yd && checkX(ys, 1) && hasPiece(xd, yd);
    }

    private boolean judgeXiang() {
        return Math.abs(xs - xd) == 2 && Math.abs(ys - yd) == 2 && !hasPiece((xs + xd) / 2, (ys + yd) / 2);
    }

    private boolean judgeShi() {
        return Math.abs(xs - xd) == 1 && Math.abs(ys - yd) == 1 && xd > 2 && xd < 6 && (yd > 6 || yd < 3);
    }

    private boolean judgeZu() {
        return yd - ys == 1 && xs == xd
                || ys > 4 && Math.abs(xs - xd) == 1 && ys == yd;
    }

    private boolean judgeBing() {
        return yd - ys == -1 && xs == xd
                || ys < 5 && Math.abs(xs - xd) == 1 && ys == yd;
    }

    private boolean judgeWang() {
        return (Math.abs(xs - xd) == 1 && ys == yd
                || Math.abs(ys - yd) == 1 && xs == xd) && xd > 2 && xd < 6 && (yd > 6 || yd < 3)
                || (xs == xd && checkY(xs, 0) && (point[xd][yd] == 4 || point[xd][yd] == 20));
    }

}
