package com.littlehorizons.erp;

import com.littlehorizons.erp.controller.MainController;

/**
 * Entry point for Little Horizons Educational ERP System.
 */
public class Main {
    public static void main(String[] args) {
        MainController controller = new MainController();
        controller.start();
    }
}
