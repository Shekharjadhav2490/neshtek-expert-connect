package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.ConsultationRejectRequest;
import com.neshtek.expertconnect.dto.ConsultationRequestRequest;
import com.neshtek.expertconnect.dto.ConsultationRequestResponse;
import com.neshtek.expertconnect.entity.*;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.ConsultationRequestRepository;
import com.neshtek.expertconnect.repository.CustomerRepository;
import com.neshtek.expertconnect.repository.CustomerRequirementRepository;
import com.neshtek.expertconnect.repository.ExpertRepository;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class ConsultationRequestService {
    private final ConsultationRequestRepository repository;
    private final CustomerRepository customerRepository;
    private final CustomerRequirementRepository requirementRepository;
    private final ExpertRepository expertRepository;
    private final ResourceAuthorizationService authorization;

    public ConsultationRequestService(ConsultationRequestRepository repository, CustomerRepository customerRepository,
                                      CustomerRequirementRepository requirementRepository, ExpertRepository expertRepository,
                                      ResourceAuthorizationService authorization) {
        this.repository=repository; this.customerRepository=customerRepository; this.requirementRepository=requirementRepository;
        this.expertRepository=expertRepository; this.authorization=authorization;
    }

    @Transactional
    public ConsultationRequestResponse create(ConsultationRequestRequest request) {
        if(request.customerId()==null||request.requirementId()==null||request.expertId()==null)
            throw new IllegalArgumentException("customerId, requirementId and expertId are required");
        authorization.assertCanCreateForCustomer(request.customerId());
        Customer customer=customerRepository.findById(request.customerId()).orElseThrow(()->new ResourceNotFoundException("Customer not found: "+request.customerId()));
        CustomerRequirement requirement=requirementRepository.findById(request.requirementId()).orElseThrow(()->new ResourceNotFoundException("Customer requirement not found: "+request.requirementId()));
        Expert expert=expertRepository.findById(request.expertId()).orElseThrow(()->new ResourceNotFoundException("Expert not found: "+request.expertId()));
        if(requirement.getCustomer()==null||!request.customerId().equals(requirement.getCustomer().getId())) throw new IllegalArgumentException("Requirement does not belong to customer");
        if(expert.getStatus()!=ExpertStatus.ACTIVE) throw new IllegalArgumentException("Consultation can only be requested from an active expert");
        ConsultationRequest entity=new ConsultationRequest(); entity.setCustomer(customer);entity.setRequirement(requirement);entity.setExpert(expert);entity.setMessage(request.message());entity.setRequestedStartDate(request.requestedStartDate());entity.setEstimatedHours(request.estimatedHours());entity.setProposedRate(request.proposedRate());entity.setCurrencyCode(request.currencyCode()==null?"USD":request.currencyCode().toUpperCase(Locale.ROOT));entity.setStatus(ConsultationRequestStatus.PENDING);return toResponse(repository.save(entity));
    }

    @Transactional(readOnly=true)
    public ConsultationRequestResponse get(Long id){
        ConsultationRequest request=find(id);
        authorization.assertCanAccess(request);
        return toResponse(request);
    }

    @Transactional(readOnly=true)
    public Page<ConsultationRequestResponse> byCustomer(Long customerId,Pageable pageable){
        authorization.assertCustomerOwns(customerId);
        if(!customerRepository.existsById(customerId))throw new ResourceNotFoundException("Customer not found: "+customerId);
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId,pageable).map(this::toResponse);
    }

    @Transactional(readOnly=true)
    public Page<ConsultationRequestResponse> byExpert(Long expertId,Pageable pageable){
        authorization.assertExpertOwns(expertId);
        if(!expertRepository.existsById(expertId))throw new ResourceNotFoundException("Expert not found: "+expertId);
        return repository.findByExpertIdOrderByCreatedAtDesc(expertId,pageable).map(this::toResponse);
    }

    @Transactional
    public ConsultationRequestResponse accept(Long id){
        ConsultationRequest e=preparePending(id);
        authorization.assertExpertOwns(e.getExpert().getId());
        e.setRejectionReason(null);e.setRespondedAt(LocalDateTime.now());e.setStatus(ConsultationRequestStatus.ACCEPTED);
        return toResponse(repository.save(e));
    }

    @Transactional
    public ConsultationRequestResponse reject(Long id, ConsultationRejectRequest request){
        ConsultationRequest e=preparePending(id);
        authorization.assertExpertOwns(e.getExpert().getId());
        e.setRejectionReason(request.reason().trim());e.setRespondedAt(LocalDateTime.now());e.setStatus(ConsultationRequestStatus.REJECTED);
        return toResponse(repository.save(e));
    }

    private ConsultationRequest preparePending(Long id){
        ConsultationRequest e=find(id);
        if(e.getStatus()!=ConsultationRequestStatus.PENDING)throw new IllegalArgumentException("Only PENDING consultation requests can be changed");
        return e;
    }

    private ConsultationRequest find(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Consultation request not found: "+id));}

    private ConsultationRequestResponse toResponse(ConsultationRequest e){
        String expertName=e.getExpert().getFirstName()+" "+e.getExpert().getLastName();
        return new ConsultationRequestResponse(e.getId(),e.getCustomer().getId(),e.getRequirement().getId(),e.getExpert().getId(),expertName,e.getRequirement().getTitle(),e.getMessage(),e.getRequestedStartDate(),e.getEstimatedHours(),e.getProposedRate(),e.getCurrencyCode(),e.getStatus().name(),e.getRejectionReason(),e.getRespondedAt(),e.getCreatedAt(),e.getUpdatedAt());
    }
}
