package com.softserveinc.ita.homeproject.homeservice.service.poll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.softserveinc.ita.homeproject.homedata.cooperation.Cooperation;
import com.softserveinc.ita.homeproject.homedata.cooperation.CooperationRepository;
import com.softserveinc.ita.homeproject.homedata.cooperation.house.House;
import com.softserveinc.ita.homeproject.homedata.poll.Poll;
import com.softserveinc.ita.homeproject.homedata.poll.PollRepository;
import com.softserveinc.ita.homeproject.homedata.poll.enums.PollQuestionType;
import com.softserveinc.ita.homeproject.homedata.poll.enums.PollStatus;
import com.softserveinc.ita.homeproject.homedata.poll.enums.PollType;
import com.softserveinc.ita.homeproject.homedata.poll.question.AnswerVariant;
import com.softserveinc.ita.homeproject.homedata.poll.question.AnswerVariantRepository;
import com.softserveinc.ita.homeproject.homedata.poll.question.DoubleChoiceQuestion;
import com.softserveinc.ita.homeproject.homedata.poll.results.ResultQuestion;
import com.softserveinc.ita.homeproject.homedata.poll.results.ResultQuestionRepository;
import com.softserveinc.ita.homeproject.homedata.poll.votes.Vote;
import com.softserveinc.ita.homeproject.homedata.poll.votes.VoteRepository;
import com.softserveinc.ita.homeproject.homedata.user.User;
import com.softserveinc.ita.homeproject.homeservice.dto.cooperation.house.HouseDto;
import com.softserveinc.ita.homeproject.homeservice.dto.poll.PollDto;
import com.softserveinc.ita.homeproject.homeservice.dto.poll.enums.PollStatusDto;
import com.softserveinc.ita.homeproject.homeservice.exception.BadRequestHomeException;
import com.softserveinc.ita.homeproject.homeservice.exception.NotFoundHomeException;
import com.softserveinc.ita.homeproject.homeservice.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@PropertySource("classpath:home-service.properties")
public class PollServiceImpl implements PollService {

    private static final String COMPLETION_DATE_VALIDATION_MESSAGE =
        "Completion date of the poll has not to be less than %s days after creation";

    private static final String NOT_FOUND_MESSAGE = "%s with 'id: %s' is not found";

    private static final String POLL_STATUS_VALIDATION_MESSAGE = "Can't update or delete poll with status: '%s'";

    @Value("${home.service.min.poll.duration.in.days:2}")
    private int minPollDurationInDays;

    private final PollRepository pollRepository;

    private final CooperationRepository cooperationRepository;

    private final AnswerVariantRepository answerVariantRepository;

    private final VoteRepository voteRepository;

    private final ResultQuestionRepository resultQuestionRepository;

    private final ServiceMapper mapper;

    @Override
    @Transactional
    public PollDto create(Long cooperationId, PollDto pollDto) {
        pollDto.getPolledHouses().forEach(houseDto -> validateHouse(cooperationId, houseDto));
        pollDto.setCompletionDate(pollDto.getCreationDate().plusDays(15));
        Poll poll = mapper.convert(pollDto, Poll.class);
        Cooperation cooperation = getCooperationById(cooperationId);
        validateCompletionDate(poll.getCompletionDate(), poll.getCreationDate());
        poll.setCooperation(cooperation);
        poll.setStatus(PollStatus.DRAFT);
        poll.setEnabled(true);
        pollRepository.save(poll);
        return mapper.convert(poll, PollDto.class);
    }

    @Override
    @Transactional
    public PollDto update(Long cooperationId, Long id, PollDto pollDto) {
        Poll poll = pollRepository.findById(id)
            .filter(Poll::getEnabled)
            .filter(poll1 -> poll1.getCooperation().getId().equals(cooperationId))
            .orElseThrow(() -> new NotFoundHomeException(String.format(NOT_FOUND_MESSAGE, "Poll", id)));
        validatePollStatus(poll, pollDto.getStatus());

        if (pollDto.getHeader() != null) {
            poll.setHeader(pollDto.getHeader());
        }

        if (pollDto.getCompletionDate() != null) {
            LocalDateTime completionDate = pollDto.getCompletionDate();
            validateCompletionDate(completionDate, poll.getCreationDate());
            poll.setCompletionDate(completionDate);
        }

        if (pollDto.getStatus() != null) {
            poll.setStatus(PollStatus.valueOf(pollDto.getStatus().name()));
        }

        poll.setUpdateDate(LocalDateTime.now());
        pollRepository.save(poll);
        return mapper.convert(poll, PollDto.class);
    }

    @Override
    public void deactivate(Long id) {
        Poll poll = pollRepository.findById(id).filter(Poll::getEnabled)
            .orElseThrow(() -> new NotFoundHomeException(String.format(NOT_FOUND_MESSAGE, "Poll", id)));
        validatePollStatus(poll, null);
        poll.setEnabled(false);
        pollRepository.save(poll);
    }

    @Override
    public Page<PollDto> findAll(Integer pageNumber, Integer pageSize, Specification<Poll> specification) {
        specification = specification.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
            .equal(root.get("cooperation").get("enabled"), true));

        Page<Poll> pollsPage = pollRepository.findAll(specification, PageRequest.of(pageNumber - 1, pageSize));

        List<ResultQuestion> resultQuestions = calculateCompletedPollsResults(pollsPage.getContent());
        List<Poll> p = resultQuestions.stream().map(ResultQuestion::getPoll).collect(Collectors.toList());

        return pollsPage.map(poll -> {
            PollDto dto = mapper.convert(poll, PollDto.class);
            if (p.contains(poll)) {
                dto.setResult(resultQuestions.get(p.indexOf(poll)).getPercentVotes());
            }
            return dto;
        });
    }


    private Cooperation getCooperationById(Long id) {
        return cooperationRepository.findById(id).filter(Cooperation::getEnabled)
            .orElseThrow(() -> new NotFoundHomeException(String.format(NOT_FOUND_MESSAGE, "Cooperation", id)));
    }

    private void validateCompletionDate(LocalDateTime completionDate, LocalDateTime creationDate) {
        long days = ChronoUnit.DAYS.between(creationDate, completionDate);
        if (days < minPollDurationInDays) {
            throw new BadRequestHomeException(
                String.format(COMPLETION_DATE_VALIDATION_MESSAGE, minPollDurationInDays));
        }
    }

    private void validateHouse(Long cooperationId, HouseDto houseDto) {
        Long id = houseDto.getId();
        Cooperation cooperation = getCooperationById(cooperationId);
        boolean isHousePresentInCooperation = cooperation.getHouses()
            .stream()
            .filter(House::getEnabled)
            .map(House::getId)
            .anyMatch(houseId -> houseId.equals(id));

        if (!isHousePresentInCooperation) {
            throw new NotFoundHomeException(String.format(NOT_FOUND_MESSAGE, "House", id));
        }
    }

    private void validatePollStatus(Poll poll, PollStatusDto pollStatus) {
        if (!poll.getStatus().equals(PollStatus.DRAFT)) {
            throw new BadRequestHomeException(
                String.format(POLL_STATUS_VALIDATION_MESSAGE, poll.getStatus().toString()));
        } else if (pollStatus != null && pollStatus.equals(PollStatusDto.COMPLETED)) {
            throw new BadRequestHomeException(
                "Poll status can't be changed to 'completed'");
        }
    }


    /**
     * Calculates and saves the result and status to the database
     * only for the votes specified below. Two possible scenarios
     * for counting: by the number of voters and by the area of
     * ownership of those who voted. Does not save the result for
     * polls that do not have enough votes.
     */
    public List<ResultQuestion> calculateCompletedPollsResults(List<Poll> polls) {
        List<ResultQuestion> results = new ArrayList<>();

        polls.stream()
            .filter(poll -> poll.getResult() == null)
            .filter(poll -> poll.getStatus().equals(PollStatus.ACTIVE))
            .filter(poll -> poll.getCompletionDate().isBefore(LocalDateTime.now()))
            .filter(poll -> poll.getEnabled().equals(true))
            .filter(poll -> poll.getPollQuestions().size() == 1)
            .filter(poll -> poll.getPollQuestions().get(0) instanceof DoubleChoiceQuestion)
            .forEach(poll -> results.addAll(calculatePollResult(poll)));

        return results;
    }

    private List<ResultQuestion> calculatePollResult(Poll poll) {
        List<AnswerVariant> answerVariants = answerVariantRepository.findAllByQuestion(poll.getPollQuestions().get(0));
        List<Vote> votes = voteRepository.findAllByPoll(poll);
        List<ResultQuestion> results = new ArrayList<>();
        Map<User, BigDecimal> ownedAreaByUser = getAllAreaOwnedByUser(poll);
        Map<AnswerVariant, List<Vote>> answerVariantsToVotes = new HashMap<>();
        int votesCount = votes.size();
        final int[] totalOwnershipsQuantity = {0};
        double amountOfNeededPeople = 0.0;

        initializeMapOfAnswerVariantIdToVotes(answerVariants, votes, answerVariantsToVotes);

        poll.getPolledHouses().get(0).getApartments().forEach(apartment ->
            apartment.getOwnerships().forEach(ownership -> totalOwnershipsQuantity[0]++)
        );

        if (poll.getType().equals(PollType.MAJOR)) {
            amountOfNeededPeople = totalOwnershipsQuantity[0] / 3.0 * 2;
        } else if (poll.getType().equals(PollType.SIMPLE)) {
            amountOfNeededPeople = totalOwnershipsQuantity[0] / 2.0 + 1;
        }

        poll.setStatus(PollStatus.COMPLETED);

        if (votesCount >= amountOfNeededPeople) {
            if (isOverHalfAreaOwner(ownedAreaByUser, poll)) {
                results = saveResultByVotesQuantity(answerVariantsToVotes, poll);
            } else {
                results = saveResultByOwnershipArea(answerVariantsToVotes, ownedAreaByUser, poll);
            }
        }

        return results;
    }

    private void initializeMapOfAnswerVariantIdToVotes(List<AnswerVariant> answerVariants, List<Vote> votes,
                                                       Map<AnswerVariant, List<Vote>> answerVariantsIdToVotes) {
        answerVariants.forEach(answerVariant ->
            votes.forEach(vote ->
                vote.getVoteAnswerVariants().forEach(voteAnswerVariant -> {
                    if (answerVariant.equals(voteAnswerVariant.getAnswerVariant())) {

                        if (!answerVariantsIdToVotes.containsKey(answerVariant)) {
                            List<Vote> votes1 = new ArrayList<>();
                            votes1.add(vote);
                            answerVariantsIdToVotes.put(answerVariant, votes1);
                        } else {
                            answerVariantsIdToVotes.get(answerVariant).add(vote);
                        }

                    }
                })
            )
        );
    }

    private Map<User, BigDecimal> getAllAreaOwnedByUser(Poll poll) {
        Map<User, BigDecimal> ownedAreaByUser = new HashMap<>();

        poll.getPolledHouses().forEach(
            house -> house.getApartments().forEach(
                apartment -> apartment.getOwnerships().forEach(
                    ownership -> {
                        if (ownedAreaByUser.containsKey(ownership.getUser())) {
                            ownedAreaByUser.replace(ownership.getUser(),
                                ownedAreaByUser.get(ownership.getUser())
                                    .add(ownership.getApartment()
                                        .getApartmentArea())
                            );
                        } else {
                            ownedAreaByUser.put(ownership.getUser(),
                                (ownership.getOwnershipPart()).multiply(ownership.getApartment().getApartmentArea()));
                        }
                    }
                )
            )
        );

        return ownedAreaByUser;
    }

    private boolean isOverHalfAreaOwner(Map<User, BigDecimal> totalOwnershipsArea, Poll poll) {
        double halfHouseArea = poll.getPolledHouses().get(0).getHouseArea() / 2;

        for (Map.Entry<User, BigDecimal> entry : totalOwnershipsArea.entrySet()) {
            if (entry.getValue().doubleValue() >= halfHouseArea) {
                return true;
            }
        }

        return false;
    }

    private List<ResultQuestion> saveResultByOwnershipArea(Map<AnswerVariant, List<Vote>> answerVariantsToVotes,
                                                           Map<User, BigDecimal> ownedAreaByUser, Poll poll) {
        List<ResultQuestion> results = new ArrayList<>();
        BigDecimal votedHouseArea = BigDecimal.valueOf(poll.getPolledHouses().get(0).getHouseArea());

        for (Map.Entry<AnswerVariant, List<Vote>> entry : answerVariantsToVotes.entrySet()) {
            ResultQuestion resultQuestion = new ResultQuestion();
            AtomicReference<BigDecimal> votedArea = new AtomicReference<>(new BigDecimal(0));

            entry.getValue().forEach(vote -> {
                BigDecimal area = votedArea.get();
                area = area.add(ownedAreaByUser.get(vote.getUser()));
                votedArea.set(area);
            });

            resultQuestion.setAnswerVariant(entry.getKey());
            resultQuestion.setPoll(poll);
            resultQuestion.setType(PollQuestionType.MULTIPLE_CHOICE);//TODO:needs to be removed in task #417
            resultQuestion.setVoteCount(entry.getValue().size());
            resultQuestion.setPercentVotes(String.valueOf(
                votedArea.get()
                    .multiply(new BigDecimal(100))
                    .divide(votedHouseArea, 10, RoundingMode.CEILING)
            ));

            results.add(resultQuestionRepository.save(resultQuestion));
        }

        return results;
    }

    private List<ResultQuestion> saveResultByVotesQuantity(Map<AnswerVariant, List<Vote>> answerVariantsToVotes,
                                                           Poll poll) {
        int voteQuantity = 0;
        List<ResultQuestion> results = new ArrayList<>();

        for (Map.Entry<AnswerVariant, List<Vote>> entry : answerVariantsToVotes.entrySet()) {
            voteQuantity += entry.getValue().size();
        }
        for (Map.Entry<AnswerVariant, List<Vote>> entry : answerVariantsToVotes.entrySet()) {
            ResultQuestion resultQuestion = new ResultQuestion();
            resultQuestion.setAnswerVariant(entry.getKey());
            resultQuestion.setType(PollQuestionType.MULTIPLE_CHOICE);//TODO:needs to be removed in task #417
            resultQuestion.setPoll(poll);
            resultQuestion.setVoteCount(entry.getValue().size());
            resultQuestion.setPercentVotes(String.valueOf(
                new BigDecimal(resultQuestion.getVoteCount())
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(voteQuantity), 10, RoundingMode.CEILING)
            ));

            results.add(resultQuestionRepository.save(resultQuestion));
        }

        return results;
    }
}
