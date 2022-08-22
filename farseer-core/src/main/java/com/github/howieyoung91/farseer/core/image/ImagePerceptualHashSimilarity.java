package com.github.howieyoung91.farseer.core.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class ImagePerceptualHashSimilarity {
    public boolean perceptualHashSimilarity(BufferedImage src1, BufferedImage src2) {
        BitSet fingerprint1 = fingerprint(src1);
        BitSet fingerprint2 = fingerprint(src2);
        int    diffCount    = 0;
        for (int i = 0; i < 64; i++) {
            if (fingerprint1.get(i) != fingerprint2.get(i)) {
                diffCount++;
            }
        }
        return diffCount <= 5;
    }

    public BitSet fingerprint(BufferedImage src) {
        int           width    = 8;
        int           height   = 8;
        BufferedImage image    = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics      graphics = image.createGraphics();
        graphics.drawImage(src, 0, 0, 8, 8, null);
        int total = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);
                total += gray(rgb);
            }
        }

        int grayAvg = total / (width * height); // 计算平均灰度
        // 二值化
        BitSet set = new BitSet(64);
        int    k   = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb  = image.getRGB(j, i);
                int gray = gray(rgb);
                if (gray >= grayAvg) {
                    set.set(k++);
                }
                else {
                    set.clear(k++);
                }
            }
        }
        return set;
    }

    private static int gray(int rgb) {
        int a = rgb & 0xff000000;   // 将最高位（24-31）的信息（alpha通道）存储到a变量
        int r = (rgb >> 16) & 0xff; // 取出次高位（16-23）红色分量的信息
        int g = (rgb >> 8) & 0xff;  // 取出中位（8-15）绿色分量的信息
        int b = rgb & 0xff;         // 取出低位（0-7）蓝色分量的信息
        rgb = (r * 77 + g * 151 + b * 28) >> 8;    // NTSC luma，算出灰度值
        return a | (rgb << 16) | (rgb << 8) | rgb; // 将灰度值送入各个颜色分量
    }
}