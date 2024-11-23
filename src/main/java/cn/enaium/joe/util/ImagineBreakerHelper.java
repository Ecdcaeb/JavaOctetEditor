package cn.enaium.joe.util;

import zone.rong.imaginebreaker.ImagineBreaker;

public class ImagineBreakerHelper {
    private static boolean isBooted = false;

    public static void boot(){
        if (isBooted){
            ImagineBreaker.openBootModules();
            ImagineBreaker.wipeFieldFilters();
            ImagineBreaker.wipeMethodFilters();
            isBooted = true;
        }
    }
}
