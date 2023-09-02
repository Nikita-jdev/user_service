package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSkillGuaranteeDto {
    private Long id;
    private Long userId;
    private Long skillId;
    private Long guarantorId;
}
