package de.adesso.kicker.match;

import de.adesso.kicker.email.EmailService;
import de.adesso.kicker.match.persistence.MatchRepository;
import de.adesso.kicker.notification.matchverificationrequest.persistence.MatchVerificationRequestRepository;
import de.adesso.kicker.notification.matchverificationrequest.service.events.MatchVerificationSentEvent;
import de.adesso.kicker.user.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestPropertySource(locations = "classpath:application-integration-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
class MatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchVerificationRequestRepository matchVerificationRequestRepository;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Assures that adding a match works through all layers")
    @WithMockUser(username = "user1")
    void addMatchWorksThroughAllLayers() throws Exception {
        // given
        willDoNothing().given(emailService).sendVerification(any(MatchVerificationSentEvent.class));
        var user1 = userRepository.findById("user1").orElseThrow();
        var user2 = userRepository.findById("user2").orElseThrow();

        // when
        mockMvc.perform(post("/matches/add").with(csrf())
                .param("date", LocalDate.now().toString())
                .param("teamAPlayer1.userId", user1.getUserId())
                .param("teamAPlayer1.firstName", user1.getFirstName())
                .param("teamAPlayer1.lastName", user1.getLastName())
                .param("teamAPlayer1.email", user1.getEmail())
                .param("teamAPlayer1.statistics.statisticId", user1.getStatistics().getStatisticId())
                .param("teamAPlayer1.statistics.rating", String.valueOf(user1.getStatistics().getRating()))
                .param("teamAPlayer1.statistics.rank", String.valueOf(user1.getStatistics().getRank()))
                .param("teamAPlayer1.wins", String.valueOf(user1.getStatistics().getWins()))
                .param("teamAPlayer1.losses", String.valueOf(user1.getStatistics().getLosses()))
                .param("teamBPlayer1.userId", user2.getUserId())
                .param("teamBPlayer1.firstName", user2.getFirstName())
                .param("teamBPlayer1.lastName", user2.getLastName())
                .param("teamBPlayer1.email", user2.getEmail())
                .param("teamBPlayer1.statistics.statisticId", user2.getStatistics().getStatisticId())
                .param("teamBPlayer1.statistics.rating", String.valueOf(user2.getStatistics().getRating()))
                .param("teamBPlayer1.statistics.rank", String.valueOf(user2.getStatistics().getRank()))
                .param("teamBPlayer1.wins", String.valueOf(user2.getStatistics().getWins()))
                .param("teamBPlayer1.losses", String.valueOf(user2.getStatistics().getLosses()))
                .param("winnerTeamA", "true"));

        // then
        var matches = matchRepository.findAll();
        System.out.println(matches);
        var match = matchRepository.findById("1").orElseThrow();
        var notifications = matchVerificationRequestRepository.getAllByMatch(match);
        assertEquals(user1.getUserId(), match.getTeamAPlayer1().getUserId());
        assertEquals(user2.getUserId(), match.getTeamBPlayer1().getUserId());
        assertNotNull(notifications);
        then(emailService).should(times(1)).sendVerification(any(MatchVerificationSentEvent.class));
    }
}
