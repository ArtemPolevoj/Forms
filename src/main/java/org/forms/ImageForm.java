package org.forms;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ImageForm {
    private final Report report;
    private final String folder;

    public ImageForm(Report report, String folder) {
        this.report = report;
        this.folder = folder;
    }

    public void setImageAnswer() {
        Map<String, String> map = report.getAnswer();
        String number;
        String[] arrImage;
        for (String key : map.keySet()) {
            if (key.contains("Фото") || key.equals("Общий вид машины")) {
                if (key.contains("Фото")) {
                    number = key.substring(0, 3);
                } else {
                    number = "O";
                }
                if (map.get(key).contains(",")){
                    arrImage = map.get(key).split(", ");
                }else {
                    arrImage = map.get(key).split(" ");
                }

                if (map.get(key).contains("https://")) {
                    String valueTemp = "";
                    map.put(key, valueTemp);
                }

                for (int i = 0; i < arrImage.length; i++) {

                    try {
                        String imageFileName = folder + "/" + number + "." + (i + 1) + ".jpeg";
                        File file = new File(imageFileName);
                        if (!file.exists()) {
                            Desktop desktop = Desktop.getDesktop();
                            desktop.browse(URI.create(arrImage[i]));
                            TimeUnit.SECONDS.sleep(5);
                            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                            BufferedImage screen = image.getSubimage(200, 110, 1200, 700);

                         //   BufferedImage screen = image.getSubimage(610, 350, 300, 200);// for Egor

                            ImageIO.write(screen, "jpeg", new File(imageFileName));
                        }

                        if (map.get(key).isEmpty()) {
                            map.put(key, imageFileName);
                        } else {
                            String value = map.get(key);
                            value = value + ", " + imageFileName;
                            map.put(key, value);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        report.setAnswer(map);
    }
}
