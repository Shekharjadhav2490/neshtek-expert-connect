package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.EngagementHistoryResponse;
import com.neshtek.expertconnect.entity.AppUser;
import com.neshtek.expertconnect.entity.Engagement;
import com.neshtek.expertconnect.entity.EngagementHistory;
import com.neshtek.expertconnect.repository.EngagementHistoryRepository;
import com.neshtek.expertconnect.repository.EngagementRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EngagementHistoryService {
    private final EngagementHistoryRepository repository;
    private final EngagementRepository engagementRepository;
    private final ResourceAuthorizationService authorization;

    public EngagementHistoryService(EngagementHistoryRepository repository,
                                    EngagementRepository engagementRepository,
                                    ResourceAuthorizationService authorization) {
        this.repository = repository;
        this.engagementRepository = engagementRepository;
        this.authorization = authorization;
    }

    @Transactional
    public void record(Engagement engagement, String action, String fromStatus, String toStatus, String reason) {
        AppUser user = authorization.currentUser();
        EngagementHistory history = new EngagementHistory();
        history.setEngagement(engagement);
        history.setAction(action);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setActorUserId(user.getId());
        history.setActorRole(user.getRole().name());
        history.setActorName(actorName(user));
        history.setReason(reason);
        repository.save(history);
    }

    @Transactional(readOnly = true)
    public List<EngagementHistoryResponse> list(Long engagementId) {
        Engagement engagement = engagementRepository.findWithDetailsById(engagementId)
                .orElseThrow(() -> new IllegalArgumentException("Engagement not found: " + engagementId));
        authorization.assertCanAccess(engagement);
        return repository.findByEngagementIdOrderByOccurredAtDesc(engagementId).stream()
                .map(h -> new EngagementHistoryResponse(
                        h.getId(), h.getAction(), h.getFromStatus(), h.getToStatus(),
                        h.getActorUserId(), h.getActorRole(), h.getActorName(), h.getReason(), h.getOccurredAt()))
                .toList();
    }

    private String actorName(AppUser user) {
        if (user.getExpert() != null) {
            return user.getExpert().getFirstName() + " " + user.getExpert().getLastName();
        }
        if (user.getCustomer() != null) {
            return user.getCustomer().getFirstName() + " " + user.getCustomer().getLastName();
        }
        return user.getEmail();
    }
}
