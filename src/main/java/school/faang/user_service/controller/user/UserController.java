package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.image.ImageProcessor;

import static school.faang.user_service.exception.ExceptionMessage.AVATAR_FILE_SIZE_EXCEPTION;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final double MAX_AVATAR_SIZE = 5_242_880L;
    private final UserService userService;
    private final ImageProcessor imageProcessor;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PostMapping("/{userId}/avatar")
    public UserDto uploadUserPic(@PathVariable Long userId, MultipartFile file) {
        if (file.getSize() > MAX_AVATAR_SIZE) {
            log.error(AVATAR_FILE_SIZE_EXCEPTION.getMessage() + "(userId = " + userId + ")");
            throw new DataValidationException(AVATAR_FILE_SIZE_EXCEPTION.getMessage());
        }

        log.info("Uploading avatar for user with id = " + userId);
        return userService.uploadUserAvatar(userId, imageProcessor.getBufferedImage(file));
    }

    @GetMapping("/{userId}/pic")
    public ResponseEntity<byte[]> downloadUserAvatar(@PathVariable Long userId) {
        log.info("Downloading avatar for user with id = " + userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(userService.downloadUserAvatar(userId), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/pic")
    public void deleteUserAvatar(@PathVariable Long userId) {
        log.info("Deleting avatar for user with id = " + userId);

        userService.deleteUserAvatar(userId);
    }
}
