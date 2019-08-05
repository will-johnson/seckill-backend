package com.seen.seckillbackend.starter;

import java.util.Random;

public class AddUser {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            System.out.println(sqlGenerator());
        }
    }

    private static int uidGenerator() {
        Random random = new Random();
        int res = random.nextInt(899999999) + 100000000;
        return res;
    }

    private static String sqlGenerator(){
        int res = uidGenerator();
        String prefix = "insert into user values(13";
        String end = ", 123456);";
        return prefix + res + end;
    }
}
