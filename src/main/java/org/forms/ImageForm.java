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
        for (String key : map.keySet()) {
            if (key.contains("Фото") || key.equals("Общий вид машины")) {
                if (key.contains("Фото")) {
                    number = key.substring(0, 3);
                } else {
                    number = "O";
                }
                String[] arrImage = map.get(key).split(", ");
                if (map.get(key).contains("https://")) {
                    String valueTemp = "";
                    map.put(key, valueTemp);
                }

                for (int i = 0; i < arrImage.length; i++) {

                    try {
                        Desktop d = Desktop.getDesktop();
                        d.browse(new URI(arrImage[i]));
                        TimeUnit.SECONDS.sleep(4);
                        BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                        BufferedImage screen = image.getSubimage(200, 110, 1200, 700);
                        String imageFileName = folder + "/" + number + "." + (i + 1) + ".jpeg";
                        ImageIO.write(screen, "jpeg", new File(imageFileName));
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
