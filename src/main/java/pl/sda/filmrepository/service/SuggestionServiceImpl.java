package pl.sda.filmrepository.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sda.filmrepository.repository.SuggestioRepository;
import pl.sda.filmrepository.model.Suggestion;
import pl.sda.filmrepository.event.SuggestionCreatedEvent;
import pl.sda.filmrepository.dto.CreateSuggestionDTO;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SuggestionServiceImpl implements SuggestionService {
    private SuggestioRepository suggestioRepository;
    private ApplicationEventPublisher eventPublisher;
    private ConversionService conversionService;

    public SuggestionServiceImpl(SuggestioRepository suggestioRepository, ApplicationEventPublisher eventPublisher, ConversionService conversionService) {
        this.suggestioRepository = suggestioRepository;
        this.eventPublisher = eventPublisher;
        this.conversionService = conversionService;
    }

    @Override
    public Suggestion addSuggestion(CreateSuggestionDTO createSuggestionDTO) {
        Suggestion suggestion = conversionService.convert(createSuggestionDTO, Suggestion.class);
        suggestion.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
        Suggestion createdSuggestion = suggestioRepository.save(suggestion);
        eventPublisher.publishEvent(new SuggestionCreatedEvent(Instant.now(), createdSuggestion));
        return createdSuggestion;
    }

    @Override
    public Iterable<Suggestion> findSuggestionByAuthor(String author) {
        return suggestioRepository.findByAuthor(author);
    }

    @Override
    public Optional<Suggestion> findById(Long id) {
        return suggestioRepository.findById(id);
    }

    @Override
    public void deleteSuggestionById(Long id) {
        suggestioRepository.deleteById(id);
    }

    @Override
    public Iterable<Suggestion> getAllSuggestions() {
        return suggestioRepository.findAll();
    }


    @Override
    @Transactional
    public void addAll(List<CreateSuggestionDTO> suggestions) {
        for (CreateSuggestionDTO sugestion : suggestions) {
            addSuggestion(sugestion);
        }
    }
}
