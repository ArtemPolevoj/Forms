package org.forms;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageForm {
    private final Map<String, String> mapAnswer;
    private final String folder;

    public ImageForm(Map<String, String> mapAnswer, String folder) {
        this.mapAnswer = mapAnswer;
        this.folder = folder;
    }

    public Map<String, String> getMapAnswer() {
        setImageAnswer();
        return new LinkedHashMap<>(mapAnswer);
    }

    private void setImageAnswer() {
        String number;
        String[] arrImage;
        String url = "https://storage.yandexcloud.net/art-forms-baket/";
        for (String key : mapAnswer.keySet()) {
            if (key.contains("Фото") || key.equals("Общий вид машины")) {
                if (key.contains("Фото")) {
                    number = key.substring(0, 3);
                } else {
                    number = "O";
                }
                StringBuilder value = new StringBuilder();
                arrImage = mapAnswer.get(key).split(", ");
                for (int i = 0; i < arrImage.length; i++) {
                    String name = arrImage[i];
                    String fileUrl = url + arrImage[i];
                    String imageFileName = folder + "/" + number + "." + (i + 1) + ".jpeg";
                    File temp = new File(imageFileName);
                    if (!temp.exists()) {

                        try (InputStream in = new URI(fileUrl).toURL().openStream();
                             OutputStream out = Files.newOutputStream(Paths.get(imageFileName))) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                            if (i == 0) {
                                value = new StringBuilder(imageFileName);
                            } else {
                                value.append(", ").append(imageFileName);
                            }
                            mapAnswer.put(key, value.toString());

                        } catch (Exception e) {
                          //  System.out.println("Не удалось вставить " + name);
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (!mapAnswer.get(key).contains(imageFileName)) {
                            if (i == 0) {
                                value = new StringBuilder(imageFileName);
                            } else {
                                value.append(", ").append(imageFileName);
                            }
                            mapAnswer.put(key, value.toString());
                        }
                    }
                }
            }
        }
    }
}
