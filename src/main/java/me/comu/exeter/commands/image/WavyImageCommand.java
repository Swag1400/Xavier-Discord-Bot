package me.comu.exeter.commands.image;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import me.comu.exeter.utility.Config;
import me.comu.exeter.core.Core;
import me.comu.exeter.interfaces.ICommand;
import me.comu.exeter.utility.Utility;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WavyImageCommand implements ICommand {


    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        if (Utility.beingProcessed) {
            event.getChannel().sendMessage("An image is already being processed, please wait.").queue();
            return;
        }
            if (event.getMessage().getAttachments().isEmpty()) {
                if (args.isEmpty()) {
                    event.getChannel().sendMessage("Please insert an image link to manipulate").queue();
                    return;
                }
                Utility.beingProcessed = true;
                event.getChannel().sendMessage("`Processing Image...`").queue(message -> {
                    try {
                        int random = new Random().nextInt(1000);
                        int newRandom = new Random().nextInt(1000);
                        Utility.saveImage(args.get(0), "cache", "image" + random);
                        File file = new File("cache/image" + random + ".png");
                        BufferedImage image = ImageIO.read(file);
                        CompletableFuture.supplyAsync(() -> image)
                                .thenApply(this::wavy)
                                .completeOnTimeout(null, 10, TimeUnit.SECONDS)
                                .thenAccept(processedImage -> {
                                    if (processedImage == null) {
                                        message.editMessage("Processing thread timed out.").queue();
                                        Config.clearCacheDirectory();
                                        Utility.beingProcessed = false;
                                    } else {
                                        try {
                                            File newFilePNG = new File("cache/image" + newRandom + ".png");
                                            ImageIO.write(processedImage, "png", newFilePNG);
                                            message.delete().queue();
                                            event.getChannel().sendFile(newFilePNG, "swag.png").queue(lol -> Config.clearCacheDirectory());
                                            Utility.beingProcessed = false;
                                        } catch (Exception ignored) {
                                            message.editMessage("Something went wrong with processing the image").queue();
                                            Utility.beingProcessed = false;
                                        }
                                    }
                                });
                    } catch (Exception ignored) {
                        message.editMessage("Something went wrong with processing the image").queue();
                        Utility.beingProcessed = false;
                    }
                });
            } else {
                Utility.beingProcessed = true;
                event.getChannel().sendMessage("`Processing Image...`").queue(message -> {
                    try {
                        int random = new Random().nextInt(1000);
                        int newRandom = new Random().nextInt(1000);
                        Utility.saveImage(args.get(0), "cache", "image" + random);
                        File file = new File("cache/image" + random + ".png");
                        BufferedImage image = ImageIO.read(file);
                        CompletableFuture.supplyAsync(() -> image)
                                .thenApply(this::wavy)
                                .completeOnTimeout(null, 10, TimeUnit.SECONDS)
                                .thenAccept(processedImage -> {
                                    if (processedImage == null) {
                                        message.editMessage("Processing thread timed out.").queue();
                                        Config.clearCacheDirectory();
                                        Utility.beingProcessed = false;
                                    } else {
                                        try {
                                            File newFilePNG = new File("cache/image" + newRandom + ".png");
                                            ImageIO.write(processedImage, "png", newFilePNG);
                                            message.delete().queue();
                                            event.getChannel().sendFile(newFilePNG, "swag.png").queue(lol -> Config.clearCacheDirectory());
                                            Utility.beingProcessed = false;
                                        } catch (Exception ignored) {
                                            message.editMessage("Something went wrong with processing the image").queue();
                                            Utility.beingProcessed = false;
                                        }
                                    }
                                });
                    } catch (Exception ex) {
                        message.editMessage("Something went wrong with processing the image").queue();
                        Utility.beingProcessed = false;
                    }

                });
            }
        Config.clearCacheDirectory();
    }

    private BufferedImage wavy(BufferedImage image) {
        new JFXPanel();

        final BufferedImage[] imageContainer = new BufferedImage[1];

        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            int width = image.getWidth();
            int height = image.getHeight();
            Canvas canvas = new Canvas(width, height);
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));


            FloatMap floatMap = new FloatMap();
            floatMap.setWidth(width);
            floatMap.setHeight(height);

            for (int i = 0; i < width; i++) {
                double v = (Math.sin(i / 180.0 * Math.PI) - 0.5) / 40.0;
                for (int j = 0; j < height; j++) {
                    floatMap.setSamples(i, j, 0.0f, (float) v);
                }
            }

            DisplacementMap displacementMap = new DisplacementMap();
            displacementMap.setMapData(floatMap);

            imageView.setEffect(displacementMap);


            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            Image newImage = imageView.snapshot(params, null);
            graphicsContext.drawImage(newImage, 0, 0);

            imageContainer[0] = SwingFXUtils.fromFXImage(newImage, image);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.exit();
        return imageContainer[0];
    }


    @Override
    public String getHelp() {
        return "Adds a wavy-filter the specified image\n`" + Core.PREFIX + getInvoke() + " [image]`\nAliases: `" + Arrays.deepToString(getAlias()) + "`";
    }

    @Override
    public String getInvoke() {
        return "wavy";
    }

    @Override
    public String[] getAlias() {
        return new String[]{"wavyfilter", "wavyimage", "wavyimg"};
    }

    @Override
    public Category getCategory() {
        return Category.IMAGE;
    }

    @Override
    public boolean isPremium() {
        return false;
    }
}