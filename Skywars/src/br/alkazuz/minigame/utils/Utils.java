package br.alkazuz.minigame.utils;

import java.io.*;
import java.util.Random;

public class Utils
{
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            if (file.listFiles().length == 0) {
                file.delete();
            }
            else {
                File[] listFiles;
                for (int length = (listFiles = file.listFiles()).length, i = 0; i < length; ++i) {
                    deleteFile(listFiles[i]);
                }
            }
        }
        else {
            file.delete();
        }
    }
    
    public static float randomRange(final float min, final float max) {
        return min + (float)Math.random() * (max - min);
    }
    
    public static boolean percent(int percent) {
    	if(percent == 100) return true;
    	return (double)(new Random().nextInt(100) + 1) < percent;
    }
    private static Random rand = new Random();
	public static int randInt(int min, int max) {
		if(min == max)return min;
	    int randomNum = rand.nextInt((max - min) + 1) + min;
		//System.out.println(randomNum);
	    return randomNum;
	}
    
    public static void copyDirectory(File file, File file2) {
        if (file.isDirectory()) {
            if (!file2.exists()) {
                file2.mkdir();
            }
            String[] list;
            for (int length = (list = file.list()).length, i = 0; i < length; ++i) {
                String s = list[i];
                copyDirectory(new File(file, s), new File(file2, s));
            }
        }
        else {
            if (file.getName().equals("uid.dat") || file.getName().equals("session.dat")) {
                return;
            }
            try {
                copyFile(new FileInputStream(file), file2);
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void copyFile(InputStream inputStream, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] array = new byte[710];
            int read;
            while ((read = inputStream.read(array)) > 0) {
                fileOutputStream.write(array, 0, read);
            }
            fileOutputStream.close();
            inputStream.close();
        }
        catch (Exception ex) {}
    }
}
