package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.RecommendationPeriodIsNotCorrect;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.SkillOfferMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationChecker;
import school.faang.user_service.validator.SkillChecker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Spy
    private RecommendationMapperImpl recommendationMapper;
    @Spy
    private SkillOfferMapperImpl skillOfferMapper;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;

    String str = "2023-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    private RecommendationService recommendationService;
    @Mock
    private SkillChecker skillChecker;
    private RecommendationChecker recommendationChecker;
    private Skill systemSkill;
    private Skill failedSkill;
    List<SkillOffer> doubleSkillOffers = new ArrayList<>();
    List<SkillOffer> noSystemSkillOffers = new ArrayList<>();
    List<SkillOffer> receiverSkillOfferToCreate = new ArrayList<>();
    private SkillOffer skillOffer1;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;
    private SkillOffer skillOffer4;

    @BeforeEach
    void setUp() {
        skillChecker = new SkillChecker(skillRepository);
        recommendationChecker = new RecommendationChecker(recommendationRepository);
        recommendationService = new RecommendationService(recommendationRepository,
                skillRepository,
                skillOfferRepository,
                userRepository,
                recommendationMapper,
                userSkillGuaranteeRepository,
                skillChecker,
                recommendationChecker);

        systemSkill = Skill.builder().id(1L).build();
        failedSkill = Skill.builder().id(148L).build();

        skillOffer1 = SkillOffer
                .builder()
                .id(1L)
                .skill(systemSkill)
                .build();
        skillOffer2 = SkillOffer
                .builder()
                .id(2L)
                .skill(systemSkill)
                .build();
        skillOffer3 = SkillOffer
                .builder()
                .id(3L)
                .skill(systemSkill)
                .build();
        skillOffer4 = SkillOffer
                .builder()
                .id(4L)
                .skill(failedSkill)
                .build();

        doubleSkillOffers.addAll(List.of(skillOffer1, skillOffer2, skillOffer1, skillOffer4));
        noSystemSkillOffers.addAll(List.of(skillOffer1, skillOffer4));
    }

    @Test
    void testCreateRecommendation_notValidRecommendationPeriod() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .content("anyText")
                .authorId(1L)
                .receiverId(2L)
                .build();

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(RECOMMENDATION_PERIOD_IN_MONTH - 1));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId())
        )
                .thenReturn(java.util.Optional.of(recommendation));

        RecommendationPeriodIsNotCorrect ex = assertThrows(RecommendationPeriodIsNotCorrect.class, () -> recommendationChecker.check(recommendationDto));
        assertEquals("Date of new recommendation should be after "
                + RECOMMENDATION_PERIOD_IN_MONTH
                + " months of the last recommendation", ex.getMessage());
    }

    @Test
    @DisplayName("SkillChecker - the same skill in offered skills")
    void testCreateRecommendation_NotUniqueSkillsInOfferedSkills() {
        RecommendationDto badRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(recommendationMapper.toListOfDto(doubleSkillOffers))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        when(recommendationRepository.create(
                badRecommendationDto.getAuthorId(),
                badRecommendationDto.getReceiverId(),
                badRecommendationDto.getContent()))
                .thenReturn(1L);
        Optional<Recommendation> entity = Optional.of(recommendationMapper.toEntity(badRecommendationDto));
        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(entity);

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(badRecommendationDto));
    }

    @Test
    @DisplayName("SkillChecker - unknown skill in offered skills")
    void testCreateRecommendation_NoSystemSkillsInOfferedSkills() {
        RecommendationDto validRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(recommendationMapper.toListOfDto(noSystemSkillOffers))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = Recommendation
                .builder()
                .receiver(User.builder()
                        .id(2L)
                        .build())
                .author(User.builder()
                        .id(1L)
                        .build())
                .skillOffers(noSystemSkillOffers)
                .build();
        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));

        when(skillRepository.countExisting(anyList())).thenReturn(1);

        assertThrows(DataValidationException.class, () -> recommendationService.create(validRecommendationDto));
    }

    @Test
    void testCreateRecommendation_SkillExists_Positive() {
        receiverSkillOfferToCreate.add(skillOffer4);

        RecommendationDto validRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(recommendationMapper.toListOfDto(receiverSkillOfferToCreate))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        User actualGuarantor = User
                .builder()
                .id(1L)
                .build();

        User receiver = User
                .builder()
                .id(2L)
                .skills(List.of(failedSkill))
                .build();

        User guarantorToFailedSkill = User
                .builder()
                .id(7L)
                .build();

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = Recommendation
                .builder()
                .author(actualGuarantor)
                .receiver(receiver)
                .build();

        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));
        when(skillRepository.countExisting(anyList())).thenReturn(1);

        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        guarantees.add(UserSkillGuarantee
                .builder()
                .guarantor(guarantorToFailedSkill)
                .build());
        failedSkill.setGuarantees(guarantees);

        when(skillRepository.findAllById(anyList())).thenReturn(List.of(failedSkill));
        when(skillOfferRepository.findAllById(anyList())).thenReturn(receiverSkillOfferToCreate);

        recommendationService.create(validRecommendationDto);
        verify(recommendationRepository).create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent());
        verify(recommendationRepository).findById(anyLong());
        verify(userSkillGuaranteeRepository).saveAll(anyList());
        verify(skillOfferRepository).create(148L, 1L);
        verify(recommendationRepository).save(entity);
    }

    @Test
    void testCreateRecommendation_SaveValidRecommendation() {
        receiverSkillOfferToCreate.add(skillOffer1);

        RecommendationDto validRecommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .skillOffers(recommendationMapper.toListOfDto(receiverSkillOfferToCreate))
                .authorId(1L)
                .receiverId(2L)
                .content("anyText")
                .build();

        User actualGuarantor = User
                .builder()
                .id(1L)
                .build();

        User receiver = User
                .builder()
                .id(2L)
                .skills(new ArrayList<>())
                .build();
        User firstGuarantor = User
                .builder()
                .id(5L)
                .build();
        User secondGuarantor = User
                .builder()
                .id(6L)
                .build();

        List<User> authors = List.of(firstGuarantor, secondGuarantor);

        when(recommendationRepository.create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent()))
                .thenReturn(1L);

        Recommendation entity = Recommendation
                .builder()
                .author(actualGuarantor)
                .receiver(receiver)
                .build();

        when(recommendationRepository.findById(
                anyLong()))
                .thenReturn(Optional.of(entity));

        when(skillRepository.countExisting(anyList())).thenReturn(1);

        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        authors.forEach(o -> guarantees.add(UserSkillGuarantee
                .builder()
                .skill(Skill
                        .builder()
                        .id(1L)
                        .build())
                .guarantor(o)
                .user(receiver)
                .build()));

        guarantees.add(UserSkillGuarantee
                .builder()
                .skill(Skill
                        .builder()
                        .id(1L)
                        .build())
                .guarantor(actualGuarantor)
                .user(receiver)
                .build());

        when(skillOfferRepository.findAllAuthorsBySkillIdAndReceiverId(1L, receiver.getId()))
                .thenReturn(authors);
        when(skillRepository.findAllById(anyList())).thenReturn(List.of(systemSkill));
        when(skillOfferRepository.findAllById(anyList())).thenReturn(receiverSkillOfferToCreate);

        recommendationService.create(validRecommendationDto);
        verify(recommendationRepository).create(
                validRecommendationDto.getAuthorId(),
                validRecommendationDto.getReceiverId(),
                validRecommendationDto.getContent());

        verify(recommendationRepository).findById(anyLong());
        verify(skillRepository).assignSkillToUser(1L, receiver.getId());
        verify(userSkillGuaranteeRepository).saveAll(guarantees);
        verify(skillOfferRepository).create(1L, 1L);
        verify(recommendationRepository).save(entity);
    }

    @Test
    void testUpdateRecommendation_positive() {
        RecommendationDto recommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .skillOffers(List.of(new SkillOfferDto[]{}))
                .content("someText")
                .build();


        Recommendation oldRecommendation = recommendationMapper.toEntity(recommendationDto);
        when(recommendationRepository.findById(recommendationDto.getId())).thenReturn(Optional.ofNullable(oldRecommendation));

        RecommendationUpdateDto recommendationUpdateDto = RecommendationUpdateDto
                .builder()
                .content("anotherText")
                .id(1L)
                .updatedAt(dateTime)
                .build();

        oldRecommendation.setContent(recommendationUpdateDto.getContent());
        oldRecommendation.setUpdatedAt(recommendationUpdateDto.getUpdatedAt());

        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        Recommendation expectedRecommendation = Recommendation
                .builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .updatedAt(dateTime)
                .skillOffers(List.of(new SkillOffer[]{}))
                .content("anotherText")
                .build();

        when(recommendationRepository.save(any())).thenReturn(expectedRecommendation);

        RecommendationDto result = recommendationService.update(recommendationUpdateDto);
        verify(recommendationRepository).save(oldRecommendation);
        assertEquals(recommendationMapper.toDto(expectedRecommendation), result);
    }
}