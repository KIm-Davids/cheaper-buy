package com.packages.scraperapi;

import com.microsoft.playwright.CLI;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        CLI.main(new String[]{"install"});
    }
}
