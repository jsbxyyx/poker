package io.github.jsbxyyx.poker;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

@Slf4j
public class CardService {

    private static final List<String> cards = new ArrayList() {
        {
            add("1-2");
            add("1-3");
            add("1-4");
            add("1-5");
            add("1-6");
            add("1-7");
            add("1-8");
            add("1-9");
            add("1-10");
            add("1-j");
            add("1-q");
            add("1-k");
            add("1-a");

            add("2-2");
            add("2-3");
            add("2-4");
            add("2-5");
            add("2-6");
            add("2-7");
            add("2-8");
            add("2-9");
            add("2-10");
            add("2-j");
            add("2-q");
            add("2-k");
            add("2-a");

            add("3-2");
            add("3-3");
            add("3-4");
            add("3-5");
            add("3-6");
            add("3-7");
            add("3-8");
            add("3-9");
            add("3-10");
            add("3-j");
            add("3-q");
            add("3-k");
            add("3-a");

            add("4-2");
            add("4-3");
            add("4-4");
            add("4-5");
            add("4-6");
            add("4-7");
            add("4-8");
            add("4-9");
            add("4-10");
            add("4-j");
            add("4-q");
            add("4-k");
            add("4-a");

            add("0-1");
            add("0-2");
        }
    };

    public Stack<String> shuffleCard() {
        ArrayList<String> newCards = new ArrayList<>(cards);
        for (int i = 0; i < 3; i++) {
            Collections.shuffle(newCards);
        }
        Stack<String> cardStack = new Stack<>();
        for (String newCard : newCards) {
            cardStack.add(newCard);
        }
        return cardStack;
    }

    public int compare(List<String> list1, List<String> list2) {
        List<String> card1 = new ArrayList<>(list1);
        List<String> card2 = new ArrayList<>(list2);
        sort(card1);
        sort(card2);
        int r1 = type(card1);
        int r2 = type(card2);
        if (r1 < r2) {
            return 1;
        }
        if (r1 > r2) {
            return 0;
        }
        int c0_0 = convert(card1.get(0).split("-"));
        int c0_1 = convert(card1.get(1).split("-"));
        int c0_2 = convert(card1.get(2).split("-"));
        int c0_s = c0_0 + c0_1 + c0_2;

        int c1_0 = convert(card2.get(0).split("-"));
        int c1_1 = convert(card2.get(1).split("-"));
        int c1_2 = convert(card2.get(2).split("-"));
        int c1_s = c1_0 + c1_1 + c1_2;

        if (c0_s < c1_s) {
            return 1;
        }
        if (c0_s > c1_s) {
            return 0;
        }
        return -1;
    }

    /**
     * 判断牌的类型
     *
     * @param cards
     * @return 1大小 2顺子 3飞机
     */
    private int type(List<String> cards) {
        int c0 = convert(cards.get(0).split("-"));
        int c1 = convert(cards.get(1).split("-"));
        int c2 = convert(cards.get(2).split("-"));
        if (c0 == 15 || c0 == 14) {
            return 1;
        }
        if (c0 - c1 == 1 && c1 - c2 == 1) {
            return 2;
        }
        if (c0 == c1 && c1 == c2) {
            return 3;
        }
        return 1;
    }

    private List<String> sort(List<String> cards) {
        Collections.sort(cards, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] split2 = o2.split("-");
                int i2 = convert(split2);

                String[] split1 = o1.split("-");
                int i1 = convert(split1);

                return Integer.compare(i2, i1);
            }
        });
        return cards;
    }

    private int convert(String[] split) {
        String s0 = split[0];
        String s1 = split[1];
        if ("0".equals(s0) && "1".equals(s1)) {
            return 14;
        } else if ("0".equals(s0) && "2".equals(s1)) {
            return 15;
        } else if ("a".equals(s1)) {
            return 1;
        } else if ("j".equals(s1)) {
            return 11;
        } else if ("q".equals(s1)) {
            return 12;
        } else if ("k".equals(s1)) {
            return 13;
        } else {
            return Integer.parseInt(s1);
        }
    }

}
