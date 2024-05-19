package school.faang.user_service.service.profile_picture;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.S3Client;
import school.faang.user_service.config.ProfilePictureServiceConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Service
@AllArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {
    private final ProfilePictureServiceConfig config;
    private final S3Client s3Client;

    @Override
    public byte[] downloadPicture(String pictureURL) {
        byte[] pictureData = new byte[0];
        try {
            URL url = new URL(pictureURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                pictureData = inputStream.readAllBytes();
            }
        } catch (MalformedURLException e) {
            log.error("Incorrect picture URL: {}", pictureURL);
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Downloading of picture from url: {} was failed", pictureURL);
            e.printStackTrace();
        }
        return pictureData;
    }

    @Override
    public byte[] decreasePicture(byte[] originalPictureData) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            BufferedImage originalPicture = ImageIO.read(new ByteArrayInputStream(originalPictureData));
            Thumbnails.of(originalPicture)
                    .size(config.getWidthOfDecreasedFile(), config.getHeightOfDecreasedFile())
                    .outputFormat("JPEG")
                    .outputQuality(1.0)
                    .toOutputStream(byteArrayOutputStream);
        } catch (IOException e) {
            log.error("Decreasing of picture was failed");
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void assignPictureToUser(User user) {
        Long userId = user.getId();
        byte[] originalPicture = downloadPicture(config.getPictureUrlWithAllSettings());
        byte[] decreasedPicture = decreasePicture(originalPicture);
        String extensionOfOriginalPic = config.getExtensionOfOriginalFile();
        String extensionOfDecreasedPic = config.getExtensionOfDecreasedFile();
        s3Client.uploadProfilePicture(user, originalPicture, extensionOfOriginalPic);
        s3Client.uploadProfilePicture(user, decreasedPicture, extensionOfDecreasedPic);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(s3Client.generatePictureLink(userId, extensionOfOriginalPic));
        userProfilePic.setSmallFileId(s3Client.generatePictureLink(userId, extensionOfDecreasedPic));
        user.setUserProfilePic(userProfilePic);
    }
}