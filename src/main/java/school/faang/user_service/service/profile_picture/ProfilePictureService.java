package school.faang.user_service.service.profile_picture;

import school.faang.user_service.entity.User;

public interface ProfilePictureService {
    byte[] downloadPicture(String pictureURL);
    byte[] decreasePicture(byte[] originalPictureData);
    void assignPictureToUser(User user);
}